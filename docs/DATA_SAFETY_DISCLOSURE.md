# Google Play Console Data Safety Questionnaire Cheat Sheet
**App Name:** Sparking Pathshala  
**Package:** `com.sparkingpathshala.app`  
**Target SDK:** 35 (Android 15) | **Min SDK:** 26  

Use the exact declarations below when completing the **App Content ➔ Data safety** questionnaire in the Google Play Console:

---

### Section 1: Data Collection & Sharing Overview
- **Does your app collect or share any of the required user data types?**  
  👉 **YES**
- **Is all of the user data collected by your app encrypted in transit?**  
  👉 **YES** (All network communication uses TLS 1.3 with Certificate Pinning against `api.sparkingpathshala.in`).
- **Do you provide a way for users to request that their data is deleted?**  
  👉 **YES** (Both in-app via Profile screen and via web URL `https://sparkingpathshala.in/delete-account`).

---

### Section 2: Data Types Collected

#### 1. Personal Info
- **Name:**  
  - Collected? **YES**
  - Shared? **NO**
  - Ephemeral? **NO**
  - Required or Optional? **Required**
  - Purpose: **App functionality** (Account management, student identification in batches).
- **Email Address:**  
  - Collected? **YES**
  - Shared? **NO**
  - Purpose: **App functionality** (Account login, OTP recovery, faculty notifications).
- **Phone Number:**  
  - Collected? **YES**
  - Shared? **NO**
  - Purpose: **App functionality** (Parent/guardian verification, emergency alerts).

#### 2. Financial Info
- **User payment info (Transaction records & receipts):**  
  - Collected? **YES** (Fee status, transaction ref number, receipt dates).
  - Shared? **NO**
  - Purpose: **App functionality** (Tuition payment tracking, student ledger).
  - *Note: Credit card numbers / CVV / UPI MPINs are NOT collected by the app.*

#### 3. Photos and Videos / Files and Docs
- **Files & Documents:**  
  - Collected? **YES**
  - Shared? **NO**
  - Purpose: **App functionality** (Uploading student homework, assignments, and doubt screenshots).

#### 4. App Activity & Performance
- **App Interactions:**  
  - Collected? **YES** (Quiz answer submissions, attendance records).
  - Shared? **NO**
  - Purpose: **Analytics / App functionality** (Calculating student grades and syllabus progress).
- **Crash Logs:**  
  - Collected? **YES**
  - Shared? **NO**
  - Purpose: **Analytics / Diagnostics** (App reliability and debugging).

#### 5. Device or Other IDs
- **Device or other IDs (AAID / Advertising ID):**  
  👉 **NO** (Sparking Pathshala does NOT collect advertising IDs).

---

### Section 3: Target Audience & Content (Families Policy)
- **Target Age Groups:** Select **13–15, 16–17, and 18 and older** (or **Under 13, 13–15, 16–17** if coaching Classes 1 to 5 as well).
- **Does your app use the Google Advertising ID (AAID)?**  
  👉 **NO**.
- **Does your app transmit data to ad SDKs?**  
  👉 **NO**.
- **Families Policy Compliance:**  
  👉 **YES**, compliant with Google Play Families Policy and India DPDP Section 9 (no behavioral tracking, strict parental consent).
