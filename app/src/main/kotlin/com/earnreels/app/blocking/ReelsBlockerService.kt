package com.earnreels.app.blocking

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.earnreels.app.data.local.EarnReelsDatabase
import com.earnreels.app.data.preferences.UserPreferences
import com.earnreels.app.data.repository.RewardsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

class ReelsBlockerService : AccessibilityService() {

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var tickerJob: Job? = null

    private lateinit var db: EarnReelsDatabase
    private lateinit var rewardsRepo: RewardsRepository
    private lateinit var preferences: UserPreferences

    private var isCurrentlyOnReelsOrShorts = false
    private var lastBlockTriggerTime = 0L

    override fun onCreate() {
        super.onCreate()
        db = EarnReelsDatabase.getInstance(applicationContext)
        rewardsRepo = RewardsRepository(db.reelsAllowanceDao(), db.rewardTransactionDao())
        preferences = UserPreferences(applicationContext)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val packageName = event.packageName?.toString() ?: return
        if (packageName != INSTAGRAM_PACKAGE && packageName != YOUTUBE_PACKAGE) {
            if (isCurrentlyOnReelsOrShorts) {
                stopWatchTicker()
            }
            return
        }

        serviceScope.launch(Dispatchers.IO) {
            val settings = preferences.appSettingsFlow.first()
            if (!settings.blockingEnabled) {
                if (isCurrentlyOnReelsOrShorts) stopWatchTicker()
                return@launch
            }

            val today = LocalDate.now().toString()
            val currentAllowance = rewardsRepo.getAllowanceForDateSync(today, settings.dailyReelsCap)

            val isTargetScreen = if (settings.strictModeAppBlocking) {
                // Strict mode: blocks entire app when allowance is 0
                true
            } else {
                // Screen-level detection
                isReelsOrShortsScreen(event)
            }

            if (isTargetScreen) {
                if (currentAllowance.currentAllowanceMinutes <= 0) {
                    // Allowance is 0 -> Trigger blocker!
                    triggerBlocker()
                } else {
                    // Allowance > 0 -> User is allowed to watch, start ticker
                    if (!isCurrentlyOnReelsOrShorts) {
                        startWatchTicker(today)
                    }
                }
            } else {
                if (isCurrentlyOnReelsOrShorts) {
                    stopWatchTicker()
                }
            }
        }
    }

    private fun isReelsOrShortsScreen(event: AccessibilityEvent): Boolean {
        val rootNode = rootInActiveWindow ?: return false
        val pkg = event.packageName?.toString() ?: ""

        return try {
            val isMatch = if (pkg == INSTAGRAM_PACKAGE) {
                findTextOrDescription(rootNode, listOf("reels", "reel", "watch reels", "audio for this reel", "clips_viewer"))
            } else if (pkg == YOUTUBE_PACKAGE) {
                findTextOrDescription(rootNode, listOf("shorts", "youtube shorts", "remix this video", "shorts_container", "reel_recycler"))
            } else {
                false
            }
            rootNode.recycle()
            isMatch
        } catch (e: Exception) {
            false
        }
    }

    private fun findTextOrDescription(node: AccessibilityNodeInfo?, keywords: List<String>): Boolean {
        if (node == null) return false

        val text = node.text?.toString()?.lowercase()
        val desc = node.contentDescription?.toString()?.lowercase()
        val viewId = node.viewIdResourceName?.lowercase()

        for (kw in keywords) {
            if (text?.contains(kw) == true || desc?.contains(kw) == true || viewId?.contains(kw) == true) {
                return true
            }
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val found = findTextOrDescription(child, keywords)
            child.recycle()
            if (found) {
                return true
            }
        }
        return false
    }

    private fun triggerBlocker() {
        val now = System.currentTimeMillis()
        if (now - lastBlockTriggerTime < 2000L) {
            // Cooldown active to prevent infinite loop or OS event flooding
            return
        }
        lastBlockTriggerTime = now

        stopWatchTicker()
        // Take user back safely
        performGlobalAction(GLOBAL_ACTION_BACK)

        val intent = Intent(this, BlockerActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
    }

    private fun startWatchTicker(date: String) {
        isCurrentlyOnReelsOrShorts = true
        tickerJob?.cancel()
        tickerJob = serviceScope.launch(Dispatchers.IO) {
            while (isCurrentlyOnReelsOrShorts) {
                // Wait 60 seconds of continuous watching
                delay(60_000L)
                if (!isCurrentlyOnReelsOrShorts) break

                val hasRemaining = rewardsRepo.deductMinutes(date, 1)
                if (!hasRemaining) {
                    // Allowance reached zero while watching!
                    serviceScope.launch(Dispatchers.Main) {
                        triggerBlocker()
                    }
                    break
                }
            }
        }
    }

    private fun stopWatchTicker() {
        isCurrentlyOnReelsOrShorts = false
        tickerJob?.cancel()
        tickerJob = null
    }

    override fun onInterrupt() {
        stopWatchTicker()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopWatchTicker()
        serviceScope.cancel()
    }

    companion object {
        const val INSTAGRAM_PACKAGE = "com.instagram.android"
        const val YOUTUBE_PACKAGE = "com.google.android.youtube"
    }
}
