# 📱 Product Requirement Document (PRD): HisaabMate
**Version:** 2.1 (Notification + Native Compose)
**Architecture:** MVVM + Clean Architecture
**UI Framework:** Jetpack Compose (Native)
**Core Constraint:** Zero-Server Cost

---

## 1. Executive Summary
**HisaabMate** is a privacy-first Pakistani finance manager. It uses **Notification Listening** to automate transaction tracking without invasive SMS permissions. It is built entirely in **Native Kotlin (Jetpack Compose)** for maximum performance ("Flawless & Lightweight"), using the provided Stitch designs as a visual style guide.

---

## 2. Technical Stack
* **Platform:** Android (Min SDK: 26, Target: 34).
* **UI:** **Jetpack Compose**.
    * *Agent Instruction:* "Analyze the visual style (colors, roundness, spacing) of the Stitch Zips, but write clean, native Compose code from scratch. Do not try to port React code."
* **Database:** Room (SQLite) + SQLCipher (Encryption).
* **Automation:** `NotificationListenerService`.
    * *Logic:* Intercepts notifications from targeted packages (e.g., `com.telenor.pakistan.easypaisa`, `com.techlogix.mobilinkcustomer`).
* **Network:** `Jsoup` (for HTML Scraping) + Retrofit (Optional).

---

## 3. Automation Strategy: Notification Listener
Instead of `READ_SMS`, we use `NotificationListenerService`.
* **How it works:** When JazzCash/EasyPaisa posts a notification ("You sent Rs. 500..."), the app captures the title and body text.
* **Privacy:** The app filters *only* financial apps defined in a `WhiteList`. All other notifications are ignored immediately.
* **Fallback:** If the user dismisses the notification before we capture it, we miss the transaction. (Acceptable trade-off for privacy/compliance).

---

## 4. The "Zero-Cost" Data Engine
To ensure $0 server costs, the app performs **Client-Side Scraping** on the user's device.
* **Gold/Silver Rates:**
    * *Target:* A lightweight, public Pakistani finance page (e.g., `hamariweb` or `urdupoint` gold rates section).
    * *Tech:* **Jsoup** library.
    * *Logic:* Background worker fetches the HTML page once every 24 hours, parses the table for "24K Gold" and "Silver", and saves the rate locally.
* **Currency Conversion:**
    * *Logic:* Scrape a lightweight forex page or use a free-tier API (e.g., ExchangeRate-API) with local caching to stay within free limits.

---

## 5. UI/UX Strategy (Compose)
* **Theme A (Playful):**
    * *Palette:* extracted from Stitch mockups.
    * *Components:* Rounded Cards (`RoundedCornerShape(16.dp)`), Pastel backgrounds.
* **Theme B (Minimalist):**
    * *Palette:* Monochrome / High Contrast.
    * *Components:* Outlined Borders, standard padding, dense lists.
* **Performance:** All lists must use `LazyColumn` and `LazyRow`.

---

## 6. Development Checklist (Agent Instructions)
1.  [ ] **Project Scaffold:** Setup Hilt, Room, Compose.
2.  [ ] **Service:** Implement `HisaabNotificationListener` extending `NotificationListenerService`.
3.  [ ] **Parsing:** Integrate **Gemini Nano** (if available) or Regex to parse the *Notification Text*.
4.  [ ] **Scraper:** Build a `GoldRateScraper` class using Jsoup.
5.  [ ] **UI Build:** Recreate the Stitch designs using pure Compose code.