package com.xiaomai.financeapp.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.xiaomai.financeapp.data.entity.Transaction
import com.xiaomai.financeapp.repository.SettingRepository
import com.xiaomai.financeapp.repository.TransactionRepository
import com.xiaomai.financeapp.ui.screen.AddTransactionScreen
import com.xiaomai.financeapp.ui.screen.HomeScreen
import com.xiaomai.financeapp.ui.screen.SettingsScreen
import com.xiaomai.financeapp.ui.screen.StatisticsScreen
import com.xiaomai.financeapp.viewmodel.TransactionViewModel

/**
 * 项目: financeApp
 * 包名: com.xiaomai.finaceapp.ui
 * 作者: bobowg
 * 日期: 2025/8/16 时间: 14:45
 * 备注：
 **/

@Composable
fun FinanceApp(
    transactionRepository: TransactionRepository,
    settingRepository: SettingRepository
) {
    val navController = rememberNavController()
    val viewModel: TransactionViewModel = viewModel(
        factory = TransactionViewModel.Factory(transactionRepository, settingRepository)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val items = listOf(
                    BottomNavItem("home", "首页", Icons.Default.Home),
                    BottomNavItem("add", "记账", Icons.Default.Add),
                    BottomNavItem("statistics", "统计", Icons.Default.CheckCircle),
                    BottomNavItem("settings", "设置", Icons.Default.Settings)
                )
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
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
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToEdit = { transactionId ->
                        navController.navigate("edit/$transactionId")
                    }
                )
            }

            composable("add") {
                AddTransactionScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(
                "edit/{transactionId}",
                arguments = listOf(navArgument("transactionId") { type = NavType.LongType })
            ) { backStackEntry ->
                val transactionId = backStackEntry.arguments?.getLong("transactionId") ?: 0L
                var transactionToEdit by remember { mutableStateOf<Transaction?>(null) }
                
                LaunchedEffect(transactionId) {
                    transactionToEdit = viewModel.getTransactionById(transactionId)
                }
                
                transactionToEdit?.let { transaction ->
                    AddTransactionScreen(
                        viewModel = viewModel,
                        transactionToEdit = transaction,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }

            composable("statistics") {
                StatisticsScreen(
                    viewModel = viewModel
                )
            }

            composable("settings") {
                SettingsScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)