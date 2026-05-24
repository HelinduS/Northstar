# NorthStar — Personal Finance Management App

A native Android application built for SE3092 Platform Based Development Assignment 01 (2026 Semester 01). NorthStar helps users track income, manage expenses, set savings goals, and gain financial insights — designed around the personal finance scenario of Kavindu, a young professional managing monthly income, recurring expenses, and long-term savings targets.

---

## Project Overview

NorthStar is a full-stack Android application that allows users to:

- **Register and authenticate** securely using Firebase Authentication
- **Track income** from multiple sources with currency support (LKR)
- **Record expenses** by category, payment method, and expense type
- **Set and monitor savings goals** with progress tracking
- **View transaction history** with filtering and detail views
- **Export financial reports** as PDF documents
- **Manage account settings** including PIN lock security

The app is built with modern Android development practices including Jetpack Compose, MVVM architecture, Hilt dependency injection, and Firebase backend services.

---

## Tech Stack

| Layer | Technology |
|---|---|
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM (Model-View-ViewModel) |
| Dependency Injection | Hilt |
| Local Database | Room |
| Remote Database | Firebase Firestore |
| Authentication | Firebase Auth |
| Navigation | Jetpack Navigation Compose |
| Language | Kotlin |
| Min SDK | Android 8.0 (API 26) |
| Target SDK | Android 15 (API 35) |

---

## Firebase Configuration Steps

### 1. Create a Firebase Project
- Go to [https://console.firebase.google.com](https://console.firebase.google.com)
- Click **Add project** and follow the setup wizard
- Enable **Google Analytics** (optional)

### 2. Register Your Android App
- In Firebase console, click **Add app → Android**
- Enter package name: `com.example.northstar`
- Download the `google-services.json` file

### 3. Add google-services.json
- Place the downloaded `google-services.json` file in the `app/` directory:
```
app/
├── google-services.json   ← place here
├── src/
└── build.gradle.kts
```

### 4. Enable Firebase Authentication
- In Firebase console → **Authentication → Sign-in method**
- Enable **Email/Password** provider

### 5. Set Up Firestore Database
- In Firebase console → **Firestore Database → Create database**
- Start in **production mode**
- Apply the following security rules:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId}/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

### 6. Firestore Collections Structure
```
users/{userId}/
├── incomes/{incomeId}
│   ├── sourceType: String
│   ├── lkrAmount: Long (stored in paisa, amount × 100)
│   ├── originalCurrency: String
│   ├── exchangeRate: Double
│   ├── date: Timestamp
│   └── notes: String
│
├── expenses/{expenseId}
│   ├── amount: Long (stored in paisa, amount × 100)
│   ├── category: String
│   ├── expenseType: String
│   ├── paymentMethod: String
│   ├── description: String
│   └── date: Timestamp
│
└── goals/{goalId}
    ├── name: String
    ├── targetAmount: Long
    ├── savedAmount: Long
    ├── targetDate: Timestamp
    └── isActive: Boolean
```

---

## Build Instructions

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK with API 35
- Git

### Steps

**1. Clone the repository**
```bash
git clone https://github.com/HelinduS/Northstar.git
cd Northstar
```

**2. Add Firebase configuration**
- Follow the Firebase Configuration Steps above
- Place `google-services.json` in the `app/` directory

**3. Open in Android Studio**
- Open Android Studio
- Select **File → Open** and navigate to the cloned directory
- Wait for Gradle sync to complete

**4. Build the project**
```bash
./gradlew assembleDebug
```
Or in Android Studio: **Build → Make Project (Ctrl+F9)**

**5. Run on device/emulator**
- Connect a physical device with USB debugging enabled, or start an emulator running Android 8.0+
- Click **Run → Run 'app'** or press **Shift+F10**

**6. Generate APK**
- In Android Studio: **Build → Build Bundle(s)/APK(s) → Build APK(s)**
- APK will be generated at: `app/build/outputs/apk/debug/app-debug.apk`

---

## Architecture Summary

NorthStar follows the **MVVM (Model-View-ViewModel)** architecture pattern recommended by Google for Android development.

```
┌─────────────────────────────────────────────┐
│                   UI Layer                   │
│         (Jetpack Compose Screens)            │
│  LoginScreen │ DashboardScreen │ etc.        │
└──────────────────┬──────────────────────────┘
                   │ observes state
┌──────────────────▼──────────────────────────┐
│               ViewModel Layer                │
│  AuthViewModel │ DashboardViewModel │ etc.   │
│  - Holds UI state (StateFlow)                │
│  - Handles business logic                    │
│  - Calls repositories                        │
└──────────────────┬──────────────────────────┘
                   │ calls
┌──────────────────▼──────────────────────────┐
│             Repository Layer                 │
│  AuthRepository │ ExpenseRepository │ etc.  │
│  - Single source of truth                    │
│  - Coordinates local + remote data           │
└────────┬─────────────────────┬──────────────┘
         │                     │
┌────────▼────────┐   ┌────────▼────────────┐
│   Local (Room)  │   │  Remote (Firebase)  │
│  NorthStarDB    │   │  Firestore + Auth   │
│  - IncomeDao    │   │  - users collection │
│  - ExpenseDao   │   │  - incomes          │
│  - GoalDao      │   │  - expenses         │
└─────────────────┘   │  - goals            │
                      └─────────────────────┘
```

### Key Architecture Decisions

**MVVM Pattern**
Separates UI logic from business logic. ViewModels survive configuration changes and expose state via `StateFlow`, which Compose collects reactively.

**Repository Pattern**
Each data domain (Auth, Income, Expense, Goal) has a dedicated repository that abstracts the data source — whether local Room database or remote Firestore. This makes testing and data source switching straightforward.

**Hilt Dependency Injection**
All ViewModels, Repositories, and data sources are injected via Hilt, reducing boilerplate and improving testability.

**Dual Storage (Room + Firestore)**
Room provides offline capability and fast local reads. Firestore provides cloud sync and persistence across devices. The repository layer coordinates between both.

**Amounts in Paisa**
All monetary amounts are stored as `Long` in paisa (1 LKR = 100 paisa) to avoid floating-point precision issues per NFR09.

---

## Project Structure

```
app/src/main/java/com/example/northstar/
├── MainActivity.kt
├── NavGraph.kt
├── Screen.kt
├── NorthStarApp.kt
├── data/
│   ├── local/
│   │   ├── NorthStarDatabase.kt
│   │   ├── dao/
│   │   └── entity/
│   ├── remote/
│   │   └── FirestoreConstants.kt
│   └── repository/
│       ├── AuthRepository.kt
│       ├── ExpenseRepositoryImpl.kt
│       └── ...
├── di/
│   └── AppModule.kt
├── domain/model/
│   ├── User.kt
│   ├── Income.kt
│   ├── Expense.kt
│   └── Goal.kt
├── ui/
│   ├── auth/
│   ├── dashboard/
│   ├── expense/
│   ├── income/
│   ├── goals/
│   ├── analytics/
│   ├── history/
│   ├── profile/
│   ├── settings/
│   ├── components/
│   ├── lock/
│   └── theme/
└── util/
    └── ThemePreferenceManager.kt
```

---

## Features Implemented

- ✅ Firebase Authentication (Register, Login, Forgot Password)
- ✅ Expense tracking with category, type, and payment method
- ✅ Income tracking with multi-currency support
- ✅ Savings goals with progress tracking
- ✅ Transaction history with detail view and delete
- ✅ Dashboard with balance summary and recent transactions
- ✅ PDF export of financial report
- ✅ Clear all data functionality
- ✅ App PIN lock security
- ✅ Profile management (name, email update)
- ✅ Settings screen with Privacy Policy and Terms of Service
- ✅ Light and Dark theme support

---

## Student Information

| Field | Details |
|---|---|
| Module | SE3092 — Platform Based Development |
| Assignment | Assignment 01 — 2026 Semester 01 |
| University | SLIIT |

---

## License

This project is submitted as academic coursework for SE3092 at SLIIT. All rights reserved.
