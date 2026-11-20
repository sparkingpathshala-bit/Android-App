# 🚀 Sparking Pathshala — Google Play Store Deployment Kit
**Release Version:** `3.1` (Build `31`)  
**Package Name:** `com.sparkingpathshala.app`  
**Target SDK:** `36` (Android 16) | **Min SDK:** `26` (Android 8.0)  
**Keystore:** Signed with official `sparkingpathshala.jks` (Valid until 2053)

---

## 📁 Pre-Packaged Assets Checklist

All required files are prepared and organized in this directory:

| Asset | File Location | Specifications |
| :--- | :--- | :--- |
| **Production App Bundle** | [`app-release.aab`](file:///Users/sajjanmallik/Desktop/Android%20App/playstore_assets/app-release.aab) | **6.9 MB** signed with official keystore |
| **Side-Load Test APK** | [`app-release.apk`](file:///Users/sajjanmallik/Desktop/Android%20App/playstore_assets/app-release.apk) | **3.07 MB** signed APK for direct testing |
| **Store App Icon** | [`icon_512x512.png`](file:///Users/sajjanmallik/Desktop/Android%20App/playstore_assets/icon_512x512.png) | 512 × 512 PNG, 32-bit |
| **Feature Graphic Banner** | [`feature_graphic_1024x500.png`](file:///Users/sajjanmallik/Desktop/Android%20App/playstore_assets/feature_graphic_1024x500.png) | 1024 × 500 PNG (Flame emblem + luxury gradient) |
| **Phone Screenshots** | [`screenshots/`](file:///Users/sajjanmallik/Desktop/Android%20App/playstore_assets/screenshots/) | 8 flagship screenshots (1080 × 2338 FHD+) |

---

## 📝 1. Store Listing Details (Copy & Paste)

### App Name (18 / 30 characters)
```text
Sparking Pathshala
```

### Short Description (78 / 80 characters)
```text
CBSE Class 9-12 coaching: live classes, practice quizzes, homework & notes.
```

### Full Description (Formatted for Google Play)
```text
Sparking Pathshala (॥ इतः सम्भवति ॥) is the official academic and coaching portal dedicated to empowering CBSE Class 9, 10, 11, and 12 students with premier coaching, live interaction, and comprehensive test preparation.

Designed with an ultra-responsive Material 3 architecture, the app provides an all-in-one digital companion for students, parents, and faculty.

🔥 KEY FEATURES:

🎯 CBSE 2026-27 SPEED QUIZ ARENA
• Practice chapter-wise and subject-wise MCQs aligned with the latest CBSE board curriculum.
• Class-isolated quizzes for Class 9, 10, 11 (PCM/PCB), and 12 (Board + JEE Foundation).
• Live timers, instant scoring, positive/negative marking analytics, and detailed question review.

📚 DIGITAL STUDY MATERIAL & LIBRARY
• Instant access to verified lecture notes, formula handbooks, NCERT solutions, and sample papers.
• Filter materials by your enrolled class and subject.
• Offline download support for registered scholars.

📝 HOMEWORK & ASSIGNMENT TRACKER
• View upcoming and submitted assignments with teacher deadlines.
• Track verification status, faculty feedback, and submission dates.

📊 ACADEMIC VITALS & FEE TRACKING
• Real-time attendance percentage with date-wise present/absent logs.
• Exam marksheet breakdown with rank and percentage.
• Transparent fee status with receipt history and upcoming installment dates.

🛡️ GUEST EXPLORER MODE
• Prospective students can preview curriculum materials across all grades before enrolling.

🔒 PRIVACY & STATUTORY COMPLIANCE:
• Full compliance with the Digital Personal Data Protection (DPDP) Act 2023.
• Designated Grievance Redressal Officer: Sajjan Kumar Mallik (sajjanmallik@sparkingpathshala.in).
• 256-bit encrypted data transit with zero third-party advertisement tracking.

Join Sparking Pathshala today and ignite your academic journey!
॥ इतः सम्भवति ॥
```

---

## ⚙️ 2. App Content Declarations (Play Console Setup)

### A. Privacy Policy
- **URL**: `https://sparkingpathshala.in/privacy-policy` (or your GitHub Pages URL hosting `docs/privacy-policy.html`)
- **Designated Grievance Officer**: Sajjan Kumar Mallik (`sajjanmallik@sparkingpathshala.in`)
- **Resolution Timeline**: 24-hour statutory acknowledgment, 15-day maximum grievance redressal.

### B. App Access (For Google Reviewers)
*Google Play requires test credentials so reviewers can log into the app.*
- Select: **"All or some functionality in my app is restricted"** -> Click **"Add instructions"**:
  - **Account Name**: `Google Play Reviewer`
  - **Username / Mobile**: `+91 98765 43210` (or `SP-2026-X`)
  - **Password**: `Password@123`
  - **Notes**: *"Reviewers can also tap 'Explore App as Guest' on the login screen to enter the app without entering credentials."*

### C. Ads Declaration
- Select: **"No, my app does not contain ads"**

### D. Content Rating Questionnaire
- **Category**: Consumer / Utility / Education
- **Violence, Blood, Sexual Content, Offensive Language, Controlled Substances**: Select **"No"** to all.
- **Calculated Rating**: **PEGI 3 / Everyone (3+)**

### E. Target Audience & Content
- **Target Age Groups**: Select **13-15**, **16-17**, and **18 and over**.
- **Appeal to Children**: Select **"No"** (unintentionally appealing elements: None).

### F. Data Safety Form
| Category | Question | Answer |
| :--- | :--- | :--- |
| **Data Collection** | Does your app collect or share user data? | **Yes** (Account & Academic functionality) |
| **Encryption** | Is all user data encrypted in transit? | **Yes** (HTTPS / TLS 1.3) |
| **Account Deletion** | Do you provide a way for users to request account deletion? | **Yes** (Via Grievance Officer email `sajjanmallik@sparkingpathshala.in`) |
| **Data Types** | • Name, Phone, Email<br>• Academic activity (quizzes, homework) | **Collected**: Yes<br>**Shared**: **No** (Zero 3rd-party sharing)<br>**Purpose**: App functionality & Account management |

### G. Financial, Health & Government Declarations
- **Financial Features**: No (Coaching fee tracker only displays institution receipts).
- **Government App**: No.
- **Health / Medical**: No.

---

## 📲 3. Step-by-Step Play Console Upload Guide

1. Log into **[Google Play Console](https://play.google.com/console)**.
2. Click **"Create App"**:
   - **App name**: `Sparking Pathshala`
   - **Default language**: `English (United States)` or `English (India)`
   - **App or Game**: `App`
   - **Free or Paid**: `Free`
3. Under **"Grow > Store presence > Main store listing"**:
   - Paste **Short Description** and **Full Description**.
   - Upload **App Icon**: [`icon_512x512.png`](file:///Users/sajjanmallik/Desktop/Android%20App/playstore_assets/icon_512x512.png)
   - Upload **Feature Graphic**: [`feature_graphic_1024x500.png`](file:///Users/sajjanmallik/Desktop/Android%20App/playstore_assets/feature_graphic_1024x500.png)
   - Upload **Phone Screenshots**: All 8 PNGs from [`playstore_assets/screenshots/`](file:///Users/sajjanmallik/Desktop/Android%20App/playstore_assets/screenshots/)
4. Under **"Policy and programs > App content"**:
   - Complete all questionnaires using Section 2 above (Privacy Policy, App Access, Content Rating, Data Safety, Target Audience).
5. Under **"Release > Production"** (or **"Testing > Internal testing"** to test with your internal team first):
   - Click **"Create new release"**.
   - Drag and drop [`app-release.aab`](file:///Users/sajjanmallik/Desktop/Android%20App/playstore_assets/app-release.aab).
   - Enter Release Name: `3.1 (Build 31 - CBSE 2026-27 Edition)`.
   - Release notes:
     ```text
     - CBSE Board 2026-27 curriculum questions for Class 9-12
     - New speed quiz arena with top-bar counter and timer
     - 7-day secure auto-logout session management
     - Guest mode multi-class library preview
     - Performance optimizations for Android 15
     ```
6. Click **"Save"**, then **"Review release"**, and **"Start rollout"**!
