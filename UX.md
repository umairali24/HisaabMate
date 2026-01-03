# 🧙‍♂️ UX Wizards & User Flows

## 1. Onboarding Wizard (The "First Hello")
* **Step 1: Language & Theme** (Same as before)
* **Step 2: The "Pot"** (Initial Balance Setup)
* **Step 3: "Automate My Spending" (The Critical Permission)**
    * *Context:* Android does not allow apps to read notifications by default.
    * *UI:* "Enable Auto-Tracking? We need to see your JazzCash/Bank alerts to save you time."
    * *Action:* User taps "Enable".
    * *System Jump:* App opens Android System Settings -> **Notification Access**.
    * *User Task:* User toggles "HisaabMate" -> ON.
    * *Return:* User presses Back button -> App detects permission granted -> "Success! We are listening."

## 2. "Teach-the-App" Wizard (Notification Edition)
* **Trigger:** A new notification arrives from a Bank App, but the format is unrecognized.
* **Notification:** HisaabMate posts its own notification: "New Transaction Detected: Tap to Categorize".
* **Action:**
    1.  User taps the HisaabMate notification.
    2.  App opens a Dialog showing the text captured from the Bank app.
    3.  User highlights Amount/Merchant.
    4.  App saves this pattern for the Notification Parser.

## 3. Zakat Calculator (Scraper Edition)
* **Step 1: Fetching Rates...**
    * *UI:* "Updating Gold Rates..." (Spinner).
    * *Action:* Jsoup scrapes the target website in the background.
    * *Success:* Displays "Today's Rate: 240,000 PKR/Tola".
    * *Failure:* "Could not fetch rates. Enter manually?"
* **Step 2-3:** (Same asset calculation logic).