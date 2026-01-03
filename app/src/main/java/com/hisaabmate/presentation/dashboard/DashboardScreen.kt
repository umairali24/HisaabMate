package com.hisaabmate.presentation.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.hisaabmate.presentation.theme.*
import com.hisaabmate.data.local.entity.AccountEntity
import com.hisaabmate.data.local.entity.TransactionEntity


import com.hisaabmate.presentation.navigation.Screen

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Switch based on theme style
    when (uiState.currentTheme) {
        "PLAYFUL" -> PlayfulDashboardContent(navController = navController, uiState = uiState)
        else -> MinimalistDashboardContent(navController = navController, uiState = uiState)
    }
}

@Composable
fun PlayfulDashboardContent(
    navController: NavController,
    uiState: DashboardUiState
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            // Floating Bottom Bar "Island"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp, start = 16.dp, end = 16.dp)
                    .height(80.dp), // Height to accommodate content
                contentAlignment = Alignment.BottomCenter
            ) {
                 Surface(
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp,
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                         // Items: Dashboard, Transactions, Budget, Settings
                         // With a gap in the middle for FAB if needed, or just evenly spaced.
                         // HTML shows: Dashboard, Transactions, [Space], Budget, Settings
                         // And FAB is absolute positioned above.
                         
                         NavigationBarItem(
                            selected = true,
                            onClick = { },
                            icon = { Icon(Icons.Default.GridView, "Dashboard") },
                            label = { Text("Dashboard", style = MaterialTheme.typography.labelSmall) },
                            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent, selectedIconColor = PlayfulPrimary, selectedTextColor = PlayfulPrimary)
                         )
                         NavigationBarItem(
                            selected = false,
                            onClick = { },
                            icon = { Icon(Icons.Default.ReceiptLong, "Transactions") },
                            label = { Text("Transactions", style = MaterialTheme.typography.labelSmall) },
                            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                         )
                         
                         Spacer(modifier = Modifier.width(48.dp)) // Space for FAB
                         
                         NavigationBarItem(
                            selected = false,
                            onClick = { 
                                navController.navigate("budget") {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(Icons.Default.PieChart, "Budget") },
                            label = { Text("Budget", style = MaterialTheme.typography.labelSmall) },
                            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                         )
                         NavigationBarItem(
                            selected = false,
                            onClick = {
                        navController.navigate(Screen.Settings.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                            icon = { Icon(Icons.Default.Settings, "Settings") },
                            label = { Text("Settings", style = MaterialTheme.typography.labelSmall) },
                            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                         )
                    }
                }
                
                // Floating FAB
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.AddTransaction.route) },
                    containerColor = PlayfulPrimary,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier
                        .offset(y = (-32).dp) // Move up half its height roughly to sit on rim
                        .size(64.dp)
                        .border(4.dp, MaterialTheme.colorScheme.background, CircleShape),
                    elevation = FloatingActionButtonDefaults.elevation(8.dp)
                ) {
                    Icon(Icons.Default.Add, "Add", modifier = Modifier.size(32.dp))
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 100.dp)
        ) {
            // Header: Salam, Ali
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Salam,",
                            style = MaterialTheme.typography.bodyMedium,
                            color = PlayfulSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${uiState.userName}! \uD83D\uDC4B", // 👋
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, Color.LightGray.copy(alpha=0.3f)),
                        shadowElevation = 2.dp,
                        modifier = Modifier.size(40.dp).clickable { }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Visibility, "View", tint = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }

            // Hero Card (Safe Zone)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().height(200.dp).padding(bottom = 24.dp),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = PlayfulPrimary),
                    elevation = CardDefaults.cardElevation(10.dp) // Glow effect simulated by elevation
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Decorative Circles (approximate)
                        Box(Modifier.offset(x = (-20).dp, y = (120).dp).size(150.dp).background(Color.White.copy(alpha=0.1f), CircleShape))
                        Box(Modifier.align(Alignment.TopEnd).offset(x = 20.dp, y = (-20).dp).size(150.dp).background(Color.White.copy(alpha=0.1f), CircleShape))

                        Row(
                            modifier = Modifier.fillMaxSize().padding(24.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Aaj ke liye Bachat", color = Color.White.copy(alpha = 0.9f), style = MaterialTheme.typography.bodyMedium)
                                Text("Rs. ${String.format("%,.0f", uiState.totalBalance)}", color = Color.White, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.TrendingUp, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Safe Zone", color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            // Radial Progress Placeholder
                            Box(contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(
                                    progress = { 0.75f },
                                    modifier = Modifier.size(80.dp),
                                    color = Color.White,
                                    trackColor = Color.White.copy(alpha = 0.2f),
                                    strokeWidth = 8.dp,
                                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                                )
                                Text("75%", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Income/Expense Grid
            item {
                Row(Modifier.fillMaxWidth().padding(bottom = 24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Expense
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Box(Modifier.size(40.dp).background(Color(0xFFFEE2E2), CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Payments, null, tint = Color.Red)
                            }
                            Spacer(Modifier.height(12.dp))
                            Text("KHARCHA", style = MaterialTheme.typography.labelSmall, color = PlayfulSecondary, fontWeight = FontWeight.Bold)
                            Text("Rs. ${String.format("%,.0f", uiState.totalExpense)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                    // Income
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Box(Modifier.size(40.dp).background(Color(0xFFDCFCE7), CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Savings, null, tint = Color.Green) // Savings icon
                            }
                            Spacer(Modifier.height(12.dp))
                            Text("BACHAT", style = MaterialTheme.typography.labelSmall, color = PlayfulSecondary, fontWeight = FontWeight.Bold)
                            Text("Rs. ${String.format("%,.0f", uiState.totalIncome - uiState.totalExpense)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }

            // Accounts Section
            item {
                 Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Mere Accounts", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Text(
                        text = "Manage", 
                        color = PlayfulPrimary, 
                        fontWeight = FontWeight.Bold, 
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.clickable { navController.navigate(Screen.AddAccount.route) }
                    )
                }
            }
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp) // Slight padding to avoid clipping shadow
                ) {
                     items(uiState.accounts.size) { i ->
                        val acc = uiState.accounts[i]
                        Card(
                            modifier = Modifier.width(160.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Box(Modifier.size(40.dp).background(Color(0xFFFEF3C7), CircleShape), contentAlignment = Alignment.Center) {
                                    Text("💵", fontSize = 20.sp)
                                }
                                Spacer(Modifier.height(12.dp))
                                Text(acc.name, style = MaterialTheme.typography.labelSmall, color = PlayfulSecondary, fontWeight = FontWeight.Bold)
                                Text("Rs. ${String.format("%,.0f", acc.current_balance)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                    if(uiState.accounts.isEmpty()) {
                        item { Text("No accounts yet.") }
                    }
                }
            }

            // Recent Transactions
            item {
                 Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Abhi ka Hisaab", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Text("Sab Dekhein", color = PlayfulPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                }
            }
            items(uiState.recentTransactions.size) { i ->
                val tx = uiState.recentTransactions[i]
                val isDebit = tx.type == "DEBIT"
                val color = if (isDebit) Color.Red else Color.Green
                val sign = if (isDebit) "-" else "+"
                
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(0.dp), // Flat with border maybe? HTML has shadow-soft
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha=0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.size(48.dp).background(color.copy(alpha=0.1f), CircleShape), contentAlignment = Alignment.Center) {
                            Text(if (isDebit) "\uD83D\uDE95" else "\uD83D\uDCB5", fontSize=20.sp) // Taxi or Money bag placeholder
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) {
                            Text(tx.category, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text(SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(tx.date)), style = MaterialTheme.typography.labelSmall, color = PlayfulSecondary)
                        }
                        Text("$sign Rs. ${String.format("%,.0f", tx.amount)}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = if (isDebit) MaterialTheme.colorScheme.onSurface else Color.Green) // Using onSurface for debit as per visual clean look, or Red
                    }
                }
            }
        }
    }
}

@Composable
fun MinimalistDashboardContent(
    navController: NavController,
    uiState: DashboardUiState
) {

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddTransaction.route) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        },
        bottomBar = {

            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = currentRoute == Screen.Dashboard.route,
                    onClick = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Analytics, contentDescription = "Reports") },
                    label = { Text("Reports") },
                    selected = false, // Placeholder
                    onClick = { /* TODO: Implement Reports Route */ }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Budget") },
                    label = { Text("Budget") },
                    selected = false, 
                    onClick = { 
                        navController.navigate("budget") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = currentRoute == Screen.Settings.route,
                    onClick = {
                        navController.navigate(Screen.Settings.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item { TopBar(userName = uiState.userName) }
            item { 
                HeroStatsCard(
                    totalBalance = uiState.totalBalance,
                    income = uiState.totalIncome,
                    expense = uiState.totalExpense,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) 
            }
            item {
                AccountsSection(
                    accounts = uiState.accounts,
                    onSeeAllClick = { navController.navigate(Screen.AddAccount.route) }
                )
            }
            item {
                TransactionSection(transactions = uiState.recentTransactions)
            }
        }
    }
}

@Composable

fun TopBar(userName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "KHUSHAMDEED",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Salaam, $userName",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        IconButton(
            onClick = { },
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.05f))
        ) {
            Icon(Icons.Default.Notifications, contentDescription = "Notifications")
        }
    }
}

@Composable
fun HeroStatsCard(
    totalBalance: Double,
    income: Double,
    expense: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceDarkAccent
        )
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Kharch karne ke liye (Safe-to-Spend)",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
                Icon(Icons.Default.Visibility, contentDescription = "View", tint = Color.Gray, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "PKR ${String.format("%,.0f", totalBalance)}",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatBadge(icon = Icons.Default.ArrowDownward, text = "In: ${String.format("%,.0f", income/1000)}k", color = GreenIncome)
                StatBadge(icon = Icons.Default.ArrowUpward, text = "Out: ${String.format("%,.0f", expense/1000)}k", color = RedExpense)
            }
        }
    }
}

@Composable
fun StatBadge(icon: ImageVector, text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
        }
        Text(text = text, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}

@Composable
fun AccountsSection(accounts: List<AccountEntity>, onSeeAllClick: () -> Unit) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Accounts (Khatay)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                text = "SEE ALL", 
                style = MaterialTheme.typography.labelSmall, 
                color = MaterialTheme.colorScheme.primary, 
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onSeeAllClick() }
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(accounts.size) { index ->
                val account = accounts[index]
                val icon = when(account.account_type) {
                    com.hisaabmate.data.local.AccountType.BANK -> Icons.Default.AccountBalance
                    com.hisaabmate.data.local.AccountType.WALLET -> Icons.Default.AccountBalanceWallet
                    else -> Icons.Default.Smartphone
                }
                
                AccountCard(
                    name = account.name, 
                    balance = "Rs. ${String.format("%,.0f", account.current_balance)}", 
                    icon = icon, 
                    color = Primary
                )
            }
            if (accounts.isEmpty()) {
                 item { Text("No accounts found", modifier = Modifier.padding(start = 16.dp)) }
            }
        }
    }
}

@Composable
fun AccountCard(name: String, balance: String, icon: ImageVector, color: Color) {
    Card(
        modifier = Modifier.width(150.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .height(110.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Column {
                Text(text = name, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text(text = balance, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun TransactionSection(transactions: List<TransactionEntity>) {
    Column(modifier = Modifier.padding(top = 8.dp)) {
        Text(
            text = "Haaliya Hisaab",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            transactions.forEachIndexed { index, tx ->
                val isIncome = tx.type == "CREDIT"
                val icon = if (isIncome) Icons.Default.Payments else Icons.Default.ShoppingCart
                TransactionItem(
                    title = tx.category,
                    time = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(tx.date)),
                    amount = "${if (isIncome) "+" else "-"} Rs. ${String.format("%,.0f", tx.amount)}",
                    isIncome = isIncome,
                    icon = icon
                )
                if (index < transactions.size - 1) {
                    Divider(color = MaterialTheme.colorScheme.background, thickness = 1.dp)
                }
            }
             if (transactions.isEmpty()) {
                Text("No recent transactions", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun TransactionItem(title: String, time: String, amount: String, isIncome: Boolean = false, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isIncome) GreenIncome.copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = if (isIncome) GreenIncome else Color.Gray)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(text = time, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Text(
            text = amount, 
            style = MaterialTheme.typography.bodyMedium, 
            fontWeight = FontWeight.Bold,
            color = if (isIncome) GreenIncome else MaterialTheme.colorScheme.onSurface
        )
    }
}
