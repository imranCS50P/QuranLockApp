# QuranLock (قرآن لاک)

> **500 Common Words of the Quran — Screen Unlock Learning Android Application**

QuranLock helps users systematically memorize the 500 most frequent words of the Holy Quran (accounting for ~80% of the Quranic vocabulary) effortlessly throughout the day by displaying an interactive, animated flashcard each time the phone screen is unlocked.

---

## 🌟 Core Features

1. **Screen Unlock Detection (`ACTION_USER_PRESENT`)**:
   - Automatically detects when you turn on and unlock your phone.
   - Triggers the next Quranic word card immediately.

2. **Display Modes**:
   - **Floating Popup Card (Overlay Window)**: Beautiful, modern Material 3 dialog with Arabic word in large Uthmani script, Urdu and English meanings, frequency in the Quran, and the Qur'anic Ayah reference.
   - **Heads-Up Notification Banner**: Subtle, high-priority notification banner for non-intrusive learning.

3. **Smart Learning Management**:
   - **Mark as Memorized (⭐)**: Save words you've learned. Track learning progress directly from the dashboard.
   - **Next Word (➡️)**: Cycle through words without closing the popup.
   - **Auto-Dismiss Timer**: Configure popup to automatically close after 5s, 8s, 15s, or require manual dismiss.
   - **Unlock Interval Filter**: Choose to show on every unlock, or at least 5, 15, or 30 minutes apart.

4. **Directory of 500 Words**:
   - Searchable by Arabic, Urdu, or English.
   - Filter by All, Memorized, or Pending words.

5. **Battery-Friendly & Lightweight**:
   - Runs a lightweight background foreground service (<10MB RAM).
   - Designed for low-spec devices and aggressive Android battery managers.

---

## 🛠️ Project Structure

```
QuranLockApp/
├── .github/workflows/
│   └── build_apk.yml             # Cloud CI/CD build for Android APK
├── app/
│   ├── src/main/
│   │   ├── assets/
│   │   │   └── quran_words_500.json   # 500 Words Database
│   │   ├── java/com/xedlabs/quranlock/
│   │   │   ├── model/
│   │   │   │   └── QuranWord.kt
│   │   │   ├── preferences/
│   │   │   │   └── PreferenceManager.kt
│   │   │   ├── repository/
│   │   │   │   └── WordRepository.kt
│   │   │   ├── receiver/
│   │   │   │   ├── BootReceiver.kt
│   │   │   │   └── ScreenUnlockReceiver.kt
│   │   │   ├── service/
│   │   │   │   ├── QuranForegroundService.kt
│   │   │   │   └── WordOverlayService.kt
│   │   │   ├── ui/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── WordsListActivity.kt
│   │   │   │   ├── SettingsActivity.kt
│   │   │   │   └── adapter/
│   │   │   │       └── WordAdapter.kt
│   │   │   └── QuranLockApp.kt
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   ├── activity_main.xml
│   │   │   │   ├── activity_words_list.xml
│   │   │   │   ├── activity_settings.xml
│   │   │   │   ├── overlay_word_card.xml
│   │   │   │   └── item_word.xml
│   │   │   └── values/
│   │   │       ├── colors.xml
│   │   │       ├── strings.xml
│   │   │       └── themes.xml
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

---

## ☁️ How to Build APK Without Freezing Low-Spec Laptops

Because compiling Android apps locally consumes high CPU and RAM, this repository includes **GitHub Actions Cloud Build**:

1. Push this `QuranLockApp` folder to a GitHub repository:
   ```bash
   git init
   git add .
   git commit -m "Initial QuranLock app release"
   git remote add origin https://github.com/<your-username>/QuranLock.git
   git push -u origin main
   ```
2. Go to the **Actions** tab on your GitHub repository.
3. The **"Build QuranLock APK"** workflow runs automatically on GitHub's cloud servers (16GB RAM).
4. In under 2 minutes, download the ready-to-install **`QuranLock-v1.0.0-debug.apk`** under the workflow **Artifacts**!
