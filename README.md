# NorthStar — Personal Finance Management App

> SE3092 Platform Based Development | Assignment 01 | 2026 | SLIIT

NorthStar is a native Android personal finance management application built for Kavindu — a young professional who needs to track monthly income from multiple sources, manage recurring and one-off expenses, set and monitor savings goals, and gain analytical insight into his spending patterns. The app provides a secure, offline-capable, cloud-synced financial companion built on modern Android architecture.

---

## Project Overview

### The Problem — Kavindu's Scenario
Kavindu earns income from multiple sources (salary, freelance, investments) in different currencies, has recurring monthly expenses across categories (food, transport, utilities), and wants to save towards specific goals (vehicle, vacation, emergency fund). He needs a single app that:
- Converts all income to LKR automatically using live exchange rates
- Tracks expenses by category and payment method
- Shows whether he is on track with savings goals
- Gives monthly analytics so he can adjust spending habits

### Solution — NorthStar Features
| Feature | Description |
|---|---|
| Authentication | Firebase Email/Password with forgot-password email reset flow |
| Income Tracking | Multi-currency income entry with live exchange rate conversion to LKR |
| Expense Tracking | Category-based expense recording with type and payment method |
| Savings Goals | Create goals with target amounts and deadlines, track progress |
| Dashboard | Balance summary, recent transactions, quick actions |
| Transaction History | Full history with detail view and delete |
| Analytics | Monthly breakdown charts by category and income vs expense |
| PDF Export | One-tap export of full financial report as a downloadable PDF |
| Clear Data | Permanently wipe all Firestore records from within the app |
| PIN Lock | App-level 4-digit PIN security |
| Profile Management | Update display name and email from within the app |
| Settings | Notifications, export, clear data, privacy policy, terms |
| Light/Dark Theme | Full Material 3 theming with DataStore persistence |

---

## Tech Stack

| Layer | Technology | Version |
|---|---|---|
| Language | Kotlin | 2.0 |
| UI Framework | Jetpack Compose + Material Design 3 | BOM 2024.x |
| Architecture | MVVM (Model-View-ViewModel) | — |
| Dependency Injection | Hilt (Dagger) | 2.51 |
| Local Database | Room | 2.6.x |
| Remote Database | Firebase Firestore | BoM managed |
| Authentication | Firebase Auth | BoM managed |
| Navigation | Jetpack Navigation Compose | 2.7.x |
| HTTP Client | Retrofit 2 + Gson | 2.9.0 |
| Currency API | fawazahmed0 Currency API (CDN) | latest/v1 |
| Preferences | DataStore Preferences | 1.1.1 |
| Font | Inter (Google Fonts via Compose) | — |
| Min SDK | Android 8.0 | API 26 |
| Target SDK | Android 15 | API 35 |
| Compile SDK | 36 | — |

---

## Firebase Configuration Steps

### 1. Create a Firebase Project
- Go to [https://console.firebase.google.com](https://console.firebase.google.com)
- Click **Add project** → follow the wizard

### 2. Register the Android App
- In Firebase console → **Add app → Android**
- Package name: `com.example.northstar`
- Download `google-services.json`

### 3. Place google-services.json
```
Northstar/
├── app/
│   ├── google-services.json    ← place here
│   ├── src/
│   └── build.gradle.kts
├── build.gradle.kts
└── settings.gradle.kts
```

### 4. Enable Firebase Authentication
- Firebase Console → **Authentication → Sign-in method**
- Enable: **Email/Password**

### 5. Create Firestore Database
- Firebase Console → **Firestore Database → Create database**
- Start in **production mode**
- Region: `asia-south1` recommended for Sri Lanka

### 6. Apply Firestore Security Rules
```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId}/{document=**} {
      allow read, write: if request.auth != null
                         && request.auth.uid == userId;
    }
  }
}
```

### 7. Firestore Data Schema
```
users/{userId}
  ├── displayName: String
  ├── email: String
  ├── phone: String
  ├── address: String
  ├── currency: String          // default "LKR"
  ├── createdAt: Timestamp
  └── updatedAt: Timestamp

users/{userId}/incomes/{incomeId}
  ├── sourceType: String        // "Salary", "Freelance", etc.
  ├── projectName: String?
  ├── lkrAmount: Long           // stored in paisa (× 100)
  ├── originalCurrency: String
  ├── exchangeRate: Double
  ├── date: Timestamp
  ├── month: String             // "2026-05"
  ├── notes: String?
  ├── createdAt: Timestamp
  └── updatedAt: Timestamp

users/{userId}/expenses/{expenseId}
  ├── amount: Long              // stored in paisa (× 100)
  ├── currency: String
  ├── category: String          // "Food", "Transport", etc.
  ├── expenseType: String       // "Fixed" | "Variable"
  ├── paymentSource: String     // "Cash" | "Card" | etc.
  ├── note: String?
  ├── date: Timestamp
  ├── month: String
  ├── createdAt: Timestamp
  └── updatedAt: Timestamp

users/{userId}/goals/{goalId}
  ├── name: String
  ├── targetAmount: Long        // in paisa
  ├── savedAmount: Long         // in paisa
  ├── targetDate: Timestamp
  ├── currency: String
  ├── isActive: Boolean
  └── createdAt: Timestamp

users/{userId}/monthlySummaries/{summaryId}
  ├── month: String
  ├── totalIncome: Long
  ├── totalExpenses: Long
  └── netSaved: Long
```

---

## Build Instructions

### Prerequisites
| Tool | Version |
|---|---|
| Android Studio | Meerkat (2024.3.1) or later |
| JDK | 11+ |
| Android SDK | API 35+ |
| Git | Any recent version |

### Steps

**1. Clone the repository**
```bash
git clone https://github.com/HelinduS/Northstar.git
cd Northstar
```

**2. Add Firebase configuration**
- Complete Firebase setup above
- Copy `google-services.json` into `app/`

**3. Open in Android Studio**
```
File → Open → select the Northstar folder
```
Wait for Gradle sync to complete.

**4. Build**
```bash
# Command line
./gradlew assembleDebug

# Or in Android Studio
Build → Make Project   (Ctrl + F9)
```

**5. Run**
- Connect a physical Android device (API 26+) with USB debugging ON
- Or start an AVD in Android Studio
- Press **Shift + F10**

**6. Generate APK**
```
Build → Build Bundle(s) / APK(s) → Build APK(s)
```
Output: `app/build/outputs/apk/debug/app-debug.apk`

---

## Architecture Summary

NorthStar follows **MVVM (Model-View-ViewModel)** combined with a **Repository pattern** for data access abstraction.

```
╔══════════════════════════════════════════════════════════╗
║                      UI LAYER                            ║
║              Jetpack Compose Screens                     ║
║  LoginScreen · DashboardScreen · IncomeScreen            ║
║  ExpenseScreen · GoalsScreen · AnalyticsScreen           ║
║  HistoryScreen · ProfileScreen · SettingsScreen          ║
╚══════════════════╦═══════════════════════════════════════╝
                   ║  observes StateFlow / collectAsState
╔══════════════════╩═══════════════════════════════════════╗
║                  VIEWMODEL LAYER                         ║
║              Hilt-injected · viewModelScope              ║
║  AuthViewModel · DashboardViewModel · IncomeViewModel    ║
║  ExpenseViewModel · GoalViewModel · AnalyticsViewModel   ║
║  ProfileViewModel · SettingsViewModel                    ║
╚══════════════════╦═══════════════════════════════════════╝
                   ║  calls suspend functions
╔══════════════════╩═══════════════════════════════════════╗
║                 REPOSITORY LAYER                         ║
║          Single source of truth · IO dispatcher          ║
║  AuthRepository · IncomeRepositoryImpl                   ║
║  ExpenseRepositoryImpl · GoalRepositoryImpl              ║
╚════════════╦══════════════════════╦═══════════════════════╝
             ║                      ║
╔════════════╩════════╗  ╔══════════╩══════════════════════╗
║    ROOM DATABASE    ║  ║        FIREBASE                 ║
║   (Local Cache)     ║  ║  Auth + Firestore               ║
║  IncomeEntity       ║◄─►  users/{uid}/incomes            ║
║  ExpenseEntity      ║  ║  users/{uid}/expenses           ║
║  GoalEntity         ║  ║  users/{uid}/goals              ║
╚═════════════════════╝  ╚═════════════════════════════════╝
```

### Architecture Decisions

**MVVM Pattern**
ViewModels hold UI state as `StateFlow<T>` and survive configuration changes. Composables collect state reactively. Business logic never appears in Composables.

**Repository Pattern**
Each domain (Auth, Income, Expense, Goal) has an interface and an implementation. The ViewModel depends on the interface, enabling easy testing and future data source swaps.

**Hilt Dependency Injection**
`AppModule` provides Firebase instances, Room database, DAOs, repositories, `PinLockManager`, and `ThemePreferenceManager` as `@Singleton` scoped dependencies.

**Dual Storage Strategy**
Room provides instant offline reads and local caching. Firestore provides real-time sync and cross-device persistence. Repositories write to both and coordinate sync.

**Paisa Storage**
All monetary amounts are stored as `Long` in paisa (1 LKR = 100 paisa) to eliminate floating-point precision errors in financial calculations.

**DataStore for Preferences**
Theme preference is persisted via `DataStore<Preferences>` in `ThemePreferenceManager`, replacing deprecated `SharedPreferences`.

---

## Project Structure

```
app/src/main/java/com/example/northstar/
│
├── MainActivity.kt
├── NavGraph.kt
├── Screen.kt
├── NorthStarApp.kt
│
├── data/
│   ├── local/
│   │   ├── NorthStarDatabase.kt
│   │   ├── dao/AppDao.kt              IncomeDao, ExpenseDao, GoalDao
│   │   └── entity/AppEntities.kt
│   ├── remote/
│   │   ├── FirestoreConstants.kt
│   │   └── CurrencyApiService.kt
│   └── repository/
│       ├── AuthRepository.kt
│       ├── RepositoryInterfaces.kt
│       ├── IncomeRepositoryImpl.kt
│       ├── ExpenseRepositoryImpl.kt
│       └── GoalRepositoryImpl.kt
│
├── di/
│   └── AppModule.kt
│
├── domain/model/
│   ├── User.kt · Income.kt · Expense.kt · Goal.kt
│   ├── AnalyticsModels.kt
│   └── ComparisonData.kt
│
├── ui/
│   ├── auth/          Login · Register · ForgotPassword · Reset
│   ├── dashboard/     DashboardScreen + components
│   ├── income/        IncomeScreen + IncomeViewModel
│   ├── expense/       ExpenseScreen + ExpenseViewModel
│   ├── goals/         GoalsScreen + GoalViewModel + components
│   ├── analytics/     AnalyticsScreen + AnalyticsViewModel
│   ├── history/       TransactionHistoryScreen
│   ├── profile/       ProfileScreen + ProfileViewModel
│   ├── settings/      SettingsScreen + SettingsViewModel
│   │                  PrivacyPolicyScreen + TermsScreen
│   ├── notifications/ NotificationViewModel + helpers
│   ├── lock/          PinScreen + PinLockManager
│   ├── navigation/    BottomNavBar
│   ├── components/    Shared composables
│   └── theme/         Color.kt · Type.kt · Theme.kt
│
└── util/
    ├── ThemePreferenceManager.kt
    └── Constants.kt
```

---

## Features Implemented

- ✅ Firebase Authentication — register, login, forgot password (email reset)
- ✅ Multi-currency income tracking with live LKR conversion
- ✅ Expense tracking — category, type (fixed/variable), payment source
- ✅ Savings goals — create, track progress, target date
- ✅ Dashboard — balance card, income/expense summary, recent transactions
- ✅ Transaction history — detail dialog, delete
- ✅ Analytics — monthly charts, category breakdown
- ✅ PDF financial report export
- ✅ Clear all data (Firestore wipe)
- ✅ PIN lock — setup, unlock, change, remove
- ✅ Profile management — display name, email update
- ✅ Settings — notification toggles, export, clear data, privacy policy, terms
- ✅ Light and Dark theme with DataStore persistence
- ✅ Bottom navigation bar (5 tabs)
- ✅ Dual storage — Room (offline) + Firestore (cloud)
- ✅ Hilt dependency injection throughout

---

## Student Information

| Field | Details |
|---|---|
| Module | SE3092 — Platform Based Development |
| Assignment | Assignment 01 — 2026 |
| University | Sri Lanka Institute of Information Technology (SLIIT) |

---

## License

Submitted as academic coursework for SE3092 at SLIIT. All rights reserved.
