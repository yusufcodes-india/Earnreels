# EarnReels ⏳✨

> **Turn healthy real-world habits into intentional Reels & Shorts time.**

EarnReels is a local-first, privacy-focused native Android app that builds a healthier relationship with short-form video. Instead of relying on brute willpower or punitive blockers, EarnReels gives you a limited daily allowance of Instagram Reels and YouTube Shorts that you earn through positive everyday routines: deep focus blocks, step goals, workouts, and a consistent sleep routine.

---

## 💡 How It Works

1. **Build Positive Habits**: Complete an uninterrupted focus session, hit your daily step goal, log your daily workout, or maintain your sleep routine.
2. **Earn Reels Minutes**: Every completed habit rewards you with actual minutes deposited into your **Reels Wallet**.
3. **Enjoy Guilt-Free Video Time**: Open Instagram Reels or YouTube Shorts; your allowance ticks down in real time while you watch.
4. **Calm Restraint**: When your wallet hits zero, a calm, respectful full-screen reminder guides you back to EarnReels to recharge your balance.

---

## 🎨 Visual Identity & Philosophy

- **Dark Charcoal Canvas**: `#0E1116` background and `#161B22` card surfaces create a distraction-free, restful dashboard.
- **Vibrant Lime Accent**: `#B7F542` highlights earned time and primary actions with crisp legibility.
- **Soft Violet Sleep Accent**: `#8B7CF6` provides restful visual hierarchy for sleep data.
- **Zero Gamification Clutter**: No intrusive badges, no XP points, no social feeds, and no manipulative streaks. Just clear, honest metrics.

---

## 💎 Features (MVP)

### ⏱️ Focus Timer & Custom Study Goals
- Pomodoro-style focus timer with preset lengths: **15, 25, 45, or 60 minutes** (default: 25 minutes).
- Reliable background execution powered by an Android Foreground Service with live notification progress.
- Tracks daily completed focus minutes and daily focus streaks.
- Rewards: `+3m` (15m), `+5m` (25m), `+8m` (45m), `+10m` (60m).
- **Custom Study & Lecture Goals**:
  - Add your own daily goals with custom titles and categories: **Lecture** (🎓), **Study** (📚), **Lesson** (📖), **Assignment** (✍️), or **Revision** (🔄).
  - Set target durations (25m, 45m, 60m, 90m, 120m).
  - Launch a focused session directly attached to any goal via the `▶ Focus on this` action.
  - Live countdown tracks remaining study and lecture time (e.g. *35m remaining of 60m*).
  - Completing focus blocks updates the goal's progress bar in real time and deposits Reels minutes into your wallet.

### 👟 Daily Steps (Health Connect)
- Reads daily steps using Health Connect aggregate queries to avoid double-counting.
- Customizable daily step goal (default: 8,000 steps).
- Progress indicator on the home dashboard.
- Awards `+10m` Reels allowance once per day when the goal is reached.
- Graceful fallback when Health Connect is unavailable or permissions are denied.

### 🏋️ Workout Logging
- Simple one-tap workout logging with optional types: Gym, Running, Home workout, Yoga, Other.
- Can be logged once per day to earn `+15m` Reels allowance.
- Maintains a daily workout streak.

### 🌙 Sleep Routine (Health Connect)
- Reads the previous night's sleep session from Health Connect.
- Customizable target bedtime (default: 11:00 PM) and minimum sleep duration (default: 7 hours).
- Awards `+5m` when falling asleep within 30 minutes of target bedtime.
- Awards `+5m` when total sleep duration meets the minimum goal.
- Zero sleep-stage judging, scoring, or clutter on the main screen.

### 💳 Reels Wallet
- Circular countdown ring showing remaining Reels/Shorts minutes on the home screen.
- Default daily cap of 30 minutes to prevent binge hoarding.
- Earned-but-unused allowance resets every night at midnight via WorkManager.
- Full breakdown of today's earnings and transactions.

### 🛡️ Smart Reels & Shorts Blocker
- Uses an on-device Android `AccessibilityService` to detect when Instagram Reels or YouTube Shorts are actively in view.
- Screen-level blocking: keeps the rest of Instagram and YouTube accessible, only intercepting the short-form video surfaces.
- Optional **Strict Mode** in Settings to block the entire app when allowance is zero.
- Calm blocker screen: "No Reels time left" with next suggested action and a button to return to EarnReels.

---

## 🎁 Reward Economics

| Habit | Requirement | Reward |
|---|---|---|
| **Focus (Quick)** | 15 minutes of uninterrupted work | `+3 min` |
| **Focus (Standard)** | 25 minutes of uninterrupted work | `+5 min` |
| **Focus (Deep)** | 45 minutes of uninterrupted work | `+8 min` |
| **Focus (Ultra)** | 60 minutes of uninterrupted work | `+10 min` |
| **Daily Steps** | Reach 8,000 steps (or custom goal) | `+10 min` |
| **Workout** | Complete and log 1 workout per day | `+15 min` |
| **Sleep Bedtime** | Sleep within 30 min of target bedtime | `+5 min` |
| **Sleep Duration** | Meet minimum duration (e.g., 7 hours) | `+5 min` |
| **3-Day Streak** | All habits completed 3 days in a row | `+10 min bonus` |
| **7-Day Streak** | All habits completed 7 days in a row | `+20 min bonus` |

---

## 🔒 Privacy & Safety Guarantee

- **100% On-Device & Offline**: The app declares **no internet permission**, ensuring your personal habits never leave your phone.
- **No Accounts or Cloud Storage**: Zero sign-ups, no tracking IDs, and no third-party analytics.
- **Targeted Accessibility Inspection**: The Accessibility Service only checks window metadata for Reels/Shorts keywords. It never reads keystrokes, personal messages, passwords, or contacts.

---

## 📱 Permissions Explained

| Permission | Type | Reason |
|---|---|---|
| `health.READ_STEPS` | Health Connect | Reads daily aggregate steps. |
| `health.READ_SLEEP` | Health Connect | Reads previous night's sleep duration and bedtime. |
| `FOREGROUND_SERVICE` | Normal | Keeps focus timer active when screen is turned off. |
| `POST_NOTIFICATIONS` | Runtime (API 33+) | Displays focus timer progress notification. |
| `SYSTEM_ALERT_WINDOW` | Special | Displays calm blocker overlay when allowance reaches zero. |
| `BIND_ACCESSIBILITY_SERVICE` | System | Detects Instagram Reels and YouTube Shorts on screen. |
| `RECEIVE_BOOT_COMPLETED` | Normal | Re-schedules midnight reset worker after device reboot. |

---

## 📲 How to Install the Debug APK

1. Connect your Android phone via USB and enable **USB Debugging** in Developer Options.
2. Install the built debug APK directly using adb:
   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```
3. Open **EarnReels** on your phone.
4. Go to **Settings** in the app:
   - Tap **Accessibility Permission** → Enable **EarnReels** in your phone's Accessibility settings.
   - Tap **Health Connect Status** → Grant read permissions for Steps and Sleep.

---

## 🛠️ Build & Development Setup

### Prerequisites
- macOS, Linux, or Windows
- JDK 21 (configured as `JAVA_HOME`)
- Android SDK 36 (Minimum SDK: 29)

### Build Commands
```bash
# Run unit test suite
./gradlew test

# Run Android Lint
./gradlew lintDebug

# Build debug APK
./gradlew assembleDebug
```
The output APK is generated at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 🗺️ Roadmap & Implementation Status
- [x] Phase 1: Architecture, Gradle 9.1 & Material 3 Dark Design System
- [x] Phase 2: Local Database (Room), DAOs, Entities, Preferences & Use Cases
- [x] Phase 3: Pomodoro Focus Timer, Foreground Service & Focus Rewards
- [x] Phase 4: Reels Wallet, Midnight Reset Worker & Rewards Screen
- [x] Phase 5: Health Connect Steps Integration
- [x] Phase 6: Health Connect Sleep Routine Integration
- [x] Phase 7: Workout Logging & Daily Habit Streaks
- [x] Phase 8: Accessibility Service & Reels/Shorts Blocker
- [x] Phase 9: Settings, Strict Mode, Navigation & History View
- [x] Phase 10: Custom Study & Lecture Goals with Goal-Linked Focus Timer
