# Rozana Tasks Personalization (अपना प्रोफ़ाइल व कस्टमाइज़ेशन)

Personalize the daily task management experience by adding user profile settings, custom name greeting, avatar icon selection, personalized daily targets, and export/backup tools.

### User Review & Critical Decisions

> [!IMPORTANT]
> The clarifying questions were skipped. Based on "Apna bhi apna kar dena chahie application" (Personalizing the application for the user), the plan adopts sensible, user-friendly defaults:
> - **Personal User Profile & Name Greeting**: Lets the user set their name (e.g. "नमस्ते, प्रकाश जी" or custom name) which appears in the daily progress header with dynamic morning/afternoon/evening greetings.
> - **Avatar & Motivation Quote**: Selection of personal avatar emoji/badge and custom daily focus quote.
> - **Daily Target Goal**: Customizable daily target (e.g., 5 tasks per day) for goal tracking and celebratory rewards.
> - **Language Flexibility**: Retains natural Hindi & Hinglish support for intuitive readability.

---

### 1. Overview & Core Concept

- **What It Does**: Transforms the task manager into a personalized daily workspace ("मेरी रोज़ाना कार्यशाला"). Users can open a sleek Profile & Customization sheet to configure their name, choose an avatar/badge, set daily productivity goals, and manage app preferences.
- **Target Audience**: Anyone seeking an organized, encouraging daily routine with a personalized touch tailored to their habits and goals.
- **Key Value**: Increases motivation and daily accountability with custom greetings, personal milestone tracking, and a sense of ownership.

---

### 2. User Experience & Visual Design

- **Key User Flows**:
  1. **Personal Header**: The top bar and progress card proudly greet the user by name with their chosen avatar (e.g., "नमस्ते, राहुल! 🌅 सुप्रभात • आज के 4 कार्य बाकी हैं").
  2. **Profile & Customization Modal**: Accessible via a profile badge in the top bar. Users can:
     - Update their display name (उदा. "ओमप्रकाश", "राहुल", "प्रिया")
     - Choose an avatar/personality badge (⚡ Action Hero, 🧘 Mindful, 📚 Scholar, 💼 Achiever, 🌱 Growth)
     - Set a personal Daily Task Target (e.g., 3, 5, or 8 tasks per day)
     - Customize default time slots and language preferences
  3. **Milestone Celebrations**: When the user hits their personalized daily target, a celebratory card and streak animation congratulates them by name.

- **Visual Identity & Theme**:
  - *Color Palette*: Refined Emerald Teal (`#0F766E`) with warm Amber highlights (`#D97706`) and slate background (`#F8FAFC`).
  - *Components*: Material 3 cards with soft elevation, rounded chips (12-16dp), and smooth toggle animations.

---

### 3. Key Product Decisions & Trade-Offs

- **Decision 1: Local Storage for Profile via SharedPreferences/DataStore or Room User Preferences**:
  - *Chosen Approach*: Store profile preferences locally using structured SharedPreferences / Room Key-Value table alongside tasks.
  - *Why*: Instant loading on startup, zero account/network friction, and full privacy on-device.
  - *Alternatives Considered*: Firebase Auth login gate (postponed until user explicitly requests multi-device cloud sync).

- **Decision 2: Seamless Integration into Existing Task Flows**:
  - *Chosen Approach*: The profile seamlessly connects to `DailyProgressCard` and `MainActivity`, making the greeting and motivation dynamic without cluttering the task list.
  - *Why*: Keeps the main screen clean, focused, and fast for everyday task checkoffs.

---

### 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────┐
│                      MainActivity                      │
│   ┌────────────────────────────────────────────────┐   │
│   │ TopAppBar: Logo + User Avatar Button (Profile) │   │
│   └────────────────────────────────────────────────┘   │
│   ┌────────────────────────────────────────────────┐   │
│   │ DailyProgressCard: Dynamic Personalized Greeting│  │
│   │ "नमस्ते, [UserName]! • [Progress]% Done"         │   │
│   └────────────────────────────────────────────────┘   │
│   ┌────────────────────────────────────────────────┐   │
│   │ Filter Tabs (Today, Upcoming, Habits, Done)    │   │
│   └────────────────────────────────────────────────┘   │
│   ┌────────────────────────────────────────────────┐   │
│   │ LazyColumn: TaskCards + Expandable Subtasks    │   │
│   └────────────────────────────────────────────────┘   │
└────────────────────────────────────────────────────────┘
                           │
                           ▼
┌────────────────────────────────────────────────────────┐
│                     TaskViewModel                      │
│  • TaskUiState (tasks, progress, streak)               │
│  • UserProfileState (name, avatar, dailyTarget, quote) │
│  • CRUD Handlers + Profile update handlers             │
└────────────────────────────────────────────────────────┘
                           │
                           ▼
┌────────────────────────────────────────────────────────┐
│             TaskRepository + AppDatabase               │
│  • TaskDao (Room SQLite persistent storage)            │
│  • UserPreferencesRepository (Persistent Profile Info) │
└────────────────────────────────────────────────────────┘
```

- **Interactive State Mapping**:
  - Tapping the avatar badge in `TopAppBar` opens `UserProfileSheet`.
  - Updating name/target immediately recomposes the `DailyProgressCard` with personalized greeting and progress calculations.
  - Reset / Clear options available for full control.
