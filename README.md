# EarnReels

> **Turn healthy real-world habits into intentional Reels & Shorts time.**

EarnReels is a local-first, privacy-focused native Android app that cultivates an intentional, healthy relationship with short-form video. Instead of relying on brute willpower or punitive blockers, EarnReels provides a limited, capped daily allowance of Instagram Reels and YouTube Shorts time that you earn through positive everyday routines: uninterrupted focus blocks, step goals, workouts, custom study sessions, and a consistent sleep routine.

---

## How It Works

1. **Build Positive Habits**: Complete an uninterrupted focus session, hit your daily step goal, log your daily workout, or maintain your target sleep routine.
2. **Earn Reels Minutes**: Every completed habit rewards you with actual minutes deposited into your **Reels Wallet**.
3. **Enjoy Guilt-Free Video Time**: Open Instagram Reels or YouTube Shorts; your allowance ticks down in real time while you watch.
4. **Calm Restraint**: When your wallet hits zero, a calm, respectful reminder guides you back to EarnReels to recharge your balance.

---

## Design Principles

EarnReels is built on a minimal, calm, premium dark **"Calm Graphite"** design language using Material 3 and Jetpack Compose. It intentionally avoids loud neons, excessive gamification, bright cards, badges, levels, and fake urgency.

- **Background (`#101113`)**: Deep graphite matte black that feels calm on OLED screens without harsh contrast.
- **Card Surface (`#17191C`)**: Subtle charcoal surface to organize habit cards and wallet components.
- **Elevated Surface (`#1E2124`)**: Elevated containers for interactive selectors and input panels.
- **Primary Text (`#EDEDED`)**: Soft off-white typography for crisp, glare-free readability at night.
- **Secondary Text (`#A1A6AB`)**: Muted slate gray for secondary metrics, timestamps, and subtitles.
- **Borders & Dividers (`#26292D`)**: Ultra-subtle flat dividers defining clean card edges.
- **Progress Accent (`#8AB4F8`)**: Calm periwinkle blue for the Reels Wallet ring and active progress bars.
- **Success & Completed (`#7FB89A`)**: Soft sage green indicating achieved goals and earned rewards.
- **Sleep Accent (`#9A8FBF`)**: Soft muted violet dedicated strictly to sleep routine metrics.
- **Blocked State (`#D98B80`)**: Gentle terracotta rose for exhausted allowance and blocked reminders.
- **Typography & Touch**: Strict 8dp grid spacing system, 20–24dp screen margins, 16–20dp card padding, 52–56dp primary buttons, and minimum 48×48dp touch targets following WCAG 2.1 AA accessibility guidelines.

---

## Features

### Reels Wallet (Home Centerpiece)
- The home screen focuses entirely on one question: *"How much Reels time do I have, and how do I earn more?"*
- Features a large, thin circular progress ring displaying remaining Reels/Shorts minutes (e.g. `12 min` and `Reels time left`).
- One primary button: **"Start Focus"**.
- Three compact habit cards: **Focus**, **Steps**, and **Sleep**.
- One **"Today's reward"** card indicating the next achievable action.
- Bottom navigation: **Today**, **Rewards**, and **History**.

### Focus Timer & Custom Study Goals
- Pomodoro-style focus timer with preset lengths: **15, 25, 45, or 60 minutes** (default: 25 minutes).
- Grounded in `SystemClock.elapsedRealtime()` within an Android Foreground Service to prevent background countdown drift when the device is locked or in Doze mode.
- Tracks daily completed focus minutes and daily focus streaks.
- Canceled sessions earn 0 minutes to prevent reward farming.
- Rewards: `+3m` (15m), `+5m` (25m), `+8m` (45m), `+10m` (60m).
- **Custom Study & Lecture Goals**:
  - Located cleanly on the dedicated **Focus Screen**.
  - Add your own daily goals with custom titles and categories: **Lecture**, **Study**, **Lesson**, **Assignment**, or **Revision**.
  - Set target durations (25m, 45m, 60m, 90m, 120m).
  - Launch a focused session directly attached to any goal via the `Focus on this` action.
  - Live progress tracking toward your study time with auto-updating progress indicators.

### Daily Steps (Health Connect)
- Reads daily steps using Health Connect aggregate queries to avoid duplicate counting across multiple fitness trackers.
- Customizable daily step goal (default: 8,000 steps).
- Awards `+10m` Reels allowance once per day when the goal is reached.
- Graceful empty states when Health Connect data is pending or permission is revoked.

### Workout Logging
- Simple one-tap workout logging: Gym, Running, Home workout, Yoga, or Other.
- Can be logged once per day to earn `+15m` Reels allowance.
- Maintains a continuous daily workout streak.

### Sleep Routine (Health Connect)
- Reads the previous night's sleep session from Health Connect.
- Calculates sleep duration from session start and end timestamps.
- Bedtime adherence evaluated against your target bedtime with a 30-minute tolerance window.
- Awards `+5m` for falling asleep on time and `+5m` for meeting minimum duration (e.g. 7 hours).
- Does not judge or display sleep stages, REM cycles, heart rate, calories, or weight.

---

## Best-Effort Reels and Shorts Blocking

### How Blocking Works
EarnReels uses an on-device Android `AccessibilityService` (`ReelsBlockerService`) that observes window and content change events (`TYPE_WINDOW_STATE_CHANGED`, `TYPE_WINDOW_CONTENT_CHANGED`).

1. **Targeted Surface Detection**: When Instagram or YouTube is in the foreground, the service inspects view hierarchies for short-form video identifiers (such as `reel_viewer`, `shorts_container`, and specific content descriptions).
2. **Debounce & Event Limiting**: A 2,000 ms cooldown timestamp prevents event floods, UI freezes, or infinite launch loops.
3. **Calm Blocker Presentation**: When your Reels Wallet balance reaches 0 minutes, `BlockerActivity` is launched with a clear message, next recommended healthy action, and safe navigation options back to EarnReels or your launcher.
4. **Active Allowance Consumption**: While short-form video is actively playing and you have remaining time, a background coroutine decrements your allowance minute-by-minute until exhausted.

### Strict Mode
For users who want zero distractions or when third-party app updates change internal view IDs, **Strict Mode** can be enabled in Settings. In Strict Mode, EarnReels blocks the entire Instagram or YouTube application whenever your allowance is 0 minutes.

### Known Limitations
- **Best-Effort Detection**: Instagram and YouTube frequently update their layouts, obfuscated class names, and view hierarchies without notice. Screen-level detection is inherently best-effort.
- **Accessibility Reliance**: Android power management or task killers may occasionally pause background accessibility event delivery.
- **Browser Playback**: EarnReels monitors the official native Instagram and YouTube applications; web browser playback (e.g. via Chrome) is not intercepted.

---

## Reward System

The reward system operates on a single currency: **Reels minutes**. Unused minutes reset every night at midnight to promote daily consistency.

| Habit | Requirement | Reward |
|---|---|---|
| **Focus (Quick)** | 15 minutes uninterrupted | `+3 min` |
| **Focus (Standard)** | 25 minutes uninterrupted | `+5 min` |
| **Focus (Deep)** | 45 minutes uninterrupted | `+8 min` |
| **Focus (Ultra)** | 60 minutes uninterrupted | `+10 min` |
| **Daily Steps** | Reach 8,000 steps (or custom goal) | `+10 min` |
| **Workout** | Complete and log 1 workout per day | `+15 min` |
| **Sleep Bedtime** | Sleep within 30 min of target bedtime | `+5 min` |
| **Sleep Duration** | Meet minimum duration (e.g. 7 hours) | `+5 min` |
| **3-Day Streak** | All habits completed 3 consecutive days | `+10 min bonus` |
| **7-Day Streak** | All habits completed 7 consecutive days | `+20 min bonus` |

### Reliability & Integrity Guardrails
- **Duplicate Prevention**: Steps, workouts, and sleep checks record calendar date timestamps and cannot be claimed multiple times in the same day.
- **Anti-Farming**: Canceled focus sessions yield 0 minutes. Rewards are granted exclusively upon legitimate timer completion.
- **Midnight Reset**: Unused minutes expire at 00:00 local time via an idempotent WorkManager periodic job.
- **Daily Cap**: Enforces a customizable upper cap (default: 30 minutes) to eliminate binge hoarding.
- **Streak Normalization**: Streaks verify calendar day continuity; missed days dynamically reset active streak counters to 0.

---

## Privacy

EarnReels is built strictly on local-first and privacy-respecting foundations:

- **Zero Network Traffic**: The app declares **no internet permission** (`android.permission.INTERNET` is omitted from `AndroidManifest.xml`).
- **No Cloud, No Accounts**: No account creation, login, cloud sync, or external databases.
- **Zero Telemetry**: No third-party SDKs, tracking IDs, advertising trackers, or analytics libraries.
- **Privacy Disclosures**: Prominent in-app disclosures precede all system permission requests for Accessibility and Health Connect.
- **No Screen Recording or Keystroke Logging**: The Accessibility Service never inspects text input, passwords, messages, contacts, photos, or general app navigation.

---

## Permissions Explained

| Permission | Type | Reason |
|---|---|---|
| `health.READ_STEPS` | Health Connect | Reads daily aggregate step count. |
| `health.READ_SLEEP` | Health Connect | Reads previous night's sleep duration and bedtime. |
| `FOREGROUND_SERVICE` | Normal | Maintains focus timer execution while the device screen is off. |
| `FOREGROUND_SERVICE_SPECIAL_USE` | Special Use | Declares dedicated foreground timer functionality per Android 14+ requirements. |
| `POST_NOTIFICATIONS` | Runtime (API 33+) | Displays live focus timer countdown in the notification shade. |
| `SYSTEM_ALERT_WINDOW` | Special | Displays calm blocker overlay when allowance reaches zero. |
| `BIND_ACCESSIBILITY_SERVICE` | System | Detects Instagram Reels and YouTube Shorts surfaces on screen. |
| `RECEIVE_BOOT_COMPLETED` | Normal | Re-schedules midnight reset worker after device reboot. |

---

## Testing

The codebase includes an extensive suite of unit tests validating core business rules:

- **Duplicate Reward Prevention**: Verifies single daily award constraints for steps, workouts, and sleep.
- **Streak Calculation & Missed Days**: Verifies consecutive increments, same-day no-ops, 1-day gaps, and dynamic active streak normalization.
- **Daily Allowance Reset**: Verifies midnight allowance expiration, 0-minute resets, and fresh daily transactions.
- **Focus Timer Integrity**: Verifies canceled focus sessions award zero minutes and prevent reward farming.

To execute the test suite:
```bash
./gradlew test
```

---

## Build & Installation

### Prerequisites
- macOS, Linux, or Windows
- JDK 21 (configured as `JAVA_HOME`)
- Android SDK 36 (Minimum SDK: 29)

### Build Commands
```bash
# Run unit tests
./gradlew test

# Run Android Lint
./gradlew lintDebug

# Build debug APK
./gradlew assembleDebug
```

The output APK is generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```

### Installation via ADB
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```
