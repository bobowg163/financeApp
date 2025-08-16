package com.xiaomai.financeapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.xiaomai.financeapp.repository.TransactionRepository
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
    modifier: Modifier = Modifier,
    repository: TransactionRepository
) {
    val navController = rememberNavController()
    val viewModel: TransactionViewModel = viewModel(

    )
}

