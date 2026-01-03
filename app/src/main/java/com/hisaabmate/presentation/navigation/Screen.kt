package com.hisaabmate.presentation.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object AddTransaction : Screen("add_transaction")
    object Settings : Screen("settings")
    object Onboarding : Screen("onboarding")
    object AddAccount : Screen("add_account")
    object ManageAccounts : Screen("manage_accounts")
    object Budget : Screen("budget")
}
