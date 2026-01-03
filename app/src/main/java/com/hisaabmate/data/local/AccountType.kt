package com.hisaabmate.data.local

enum class AccountType {
    BANK,
    CREDIT_CARD,
    @Deprecated("Merged into BANK. Use BANK for new Digital Wallet accounts.")
    WALLET
}
