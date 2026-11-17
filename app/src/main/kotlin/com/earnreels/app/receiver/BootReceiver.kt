package com.earnreels.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.earnreels.app.worker.MidnightResetWorker

class BootReceiver : BroadcastReceiver {
    constructor() : super()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED
        ) {
            MidnightResetWorker.scheduleMidnightReset(context)
        }
    }
}
