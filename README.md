# EarnReels ⏳✨

> **Turn healthy real-world habits into intentional Reels & Shorts time.**

EarnReels is a local-first, privacy-focused Android app that creates a balanced relationship with short-form video. Instead of relying on infinite willpower or punitive app lockers, EarnReels gives you a limited daily allowance of Instagram Reels and YouTube Shorts time that you earn through positive routines: deep focus sessions, daily steps, workouts, and a consistent sleep routine.

---

## 💡 How It Works

1. **Build Positive Habits**: Complete a Pomodoro focus block, hit your step goal, log your daily workout, or sleep on time.
2. **Earn Reels Minutes**: Every completed habit rewards you with actual minutes stored in your **Reels Wallet**.
3. **Enjoy Guilt-Free Video Time**: Open Instagram Reels or YouTube Shorts; your allowance ticks down in real time.
4. **Calm Restraint**: When your wallet hits zero, a calm, respectful blocker guides you back to EarnReels to recharge your balance.

---

## 🎨 Visual Identity & Philosophy

- **Dark Charcoal Surface**: `#0E1116` background and `#161B22` card surfaces create a calming, premium atmosphere.
- **Vibrant Lime Accent**: `#B7F542` highlights earned time and primary actions with high legibility.
- **Soft Violet Sleep Accent**: `#8B7CF6` provides restful visual hierarchy for sleep data.
- **Zero Gamification Noise**: No loud badges, no XP points, no social feeds, and no manipulative streaks. Just clear, honest metrics.

---

## 💎 Features (MVP)

- ⏱️ **Focus Timer**: Configurable 15, 25, 45, or 60-minute Pomodoro sessions with persistent background timing.
- 👟 **Steps Tracking**: Reads daily steps via Health Connect aggregate queries (default 8,000 steps).
- 🏋️ **Workout Logging**: Quick one-tap workout logging (Gym, Running, Home, Yoga, Other).
- 🌙 **Sleep Routine**: Reads your previous night's sleep duration and bedtime adherence without intrusive sleep-stage scoring.
- 💳 **Reels Wallet**: Real-time remaining allowance ring (daily default cap: 30 minutes, resets at midnight).
- 🛡️ **Intelligent Screen Blocker**: Detects only Reels and Shorts via an on-device `AccessibilityService` without blocking the rest of Instagram or YouTube.

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

*Note: Daily allowance is capped at 30 minutes by default and unused allowance expires at midnight to prevent binge hoarding.*

---

## 🔒 Privacy & Safety First

- **100% Offline**: The app contains **no internet permission**, ensuring your personal habits never leave your phone.
- **No Cloud Sync or Accounts**: Zero registration, no tracking IDs, and no third-party analytics.
- **On-Device Detection**: The Accessibility Service only inspects the active window title/class to identify Reels/Shorts surfaces. It never reads keystrokes, messages, or screen contents.

---

## 📱 Permissions Explained

| Permission | Reason |
|---|---|
| `health.READ_STEPS` | Reads daily aggregate steps from Google Health Connect. |
| `health.READ_SLEEP` | Checks last night's sleep session start/end time. |
| `FOREGROUND_SERVICE` | Ensures the focus timer completes accurately when the screen is off. |
| `POST_NOTIFICATIONS` | Alerts you when your focus session finishes. |
| `SYSTEM_ALERT_WINDOW` | Renders the calm blocker overlay when Reels/Shorts time is exhausted. |
| `BIND_ACCESSIBILITY_SERVICE` | Detects when Instagram Reels or YouTube Shorts are actively in view. |

---

## 🛠️ Project Architecture & Tech Stack

```
app/src/main/kotlin/com/earnreels/app/
├── ui/
│   ├── theme/           # Color, Type, Shape, Spacing, Theme
│   ├── components/      # ReelsWalletRing, HabitCard, RewardCard, PrimaryButton, BottomNavBar, StreakBadge
│   └── navigation/      # Nav destinations and bottom tab routing
├── features/
│   ├── today/           # Main focus dashboard
│   ├── focus/           # Pomodoro timer & foreground service
│   ├── rewards/         # Wallet breakdown & reward rules
│   ├── history/         # 7-day retrospective
│   └── settings/        # Goals, bedtime, and blocking modes
├── domain/              # Clean architecture models & use cases
├── data/
│   ├── local/           # Room Database, DAOs & Entities
│   ├── preferences/     # DataStore User Preferences
│   └── repository/      # Repository implementations
├── health/              # Health Connect client & queries
├── blocking/            # AccessibilityService & Overlay manager
└── worker/              # WorkManager midnight reset task
```

- **Kotlin & Coroutines/Flow**
- **Jetpack Compose & Material 3**
- **Room Database** (local persistence)
- **DataStore Preferences**
- **Health Connect SDK**
- **WorkManager**

---

## 🚀 Building & Testing

### Prerequisites
- JDK 21
- Android SDK 36 (Minimum SDK: 29)

### Commands
```bash
# Run unit tests
./gradlew test

# Run Android lint
./gradlew lint

# Build debug APK
./gradlew assembleDebug
```
Output APK is located at: `app/build/outputs/apk/debug/app-debug.apk`

---

## 🗺️ Roadmap
- [x] Phase 1: Architecture, Gradle 9.1 & Material 3 Dark Design System
- [x] Phase 2: Local Database (Room), DAOs, Entities, Preferences & Use Cases
- [x] Phase 3: Pomodoro Focus Timer, Foreground Service & Focus Rewards
- [ ] Phase 4: Reels Wallet & Reward Transaction Engine
- [ ] Phase 5: Health Connect Steps Integration
- [ ] Phase 6: Health Connect Sleep Routine Integration
- [ ] Phase 7: Workout Logging & Daily Habit Streaks
- [ ] Phase 8: Accessibility Service & Reels/Shorts Blocker
- [ ] Phase 9: Settings, Strict Mode & History View
