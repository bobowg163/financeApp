package com.xiaomai.financeapp.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaomai.financeapp.data.entity.Transaction
import com.xiaomai.financeapp.data.entity.TransactionType
import com.xiaomai.financeapp.ui.theme.ExpenseRed
import com.xiaomai.financeapp.ui.theme.FinanceAppTheme
import com.xiaomai.financeapp.ui.theme.IncomeGreen
import com.xiaomai.financeapp.viewmodel.TransactionViewModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * 项目: financeApp
 * 包名: com.xiaomai.financeapp.ui.screen
 * 作者: bobowg
 * 日期: 2025/8/16 时间: 17:13
 * 备注：主页菜单，包括统计、最近交易
 **/

@Composable
fun HomeScreen(
    viewModel: TransactionViewModel
) {
    val transactions by viewModel.allTransactions.collectAsState(initial = emptyList())
    val uiState by viewModel.uiState.collectAsState()
    val decimalFormat = DecimalFormat("#,##0.00")

    LaunchedEffect(Unit) {
        viewModel.loadCurrentMonthStatistics()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 顶部统计卡片
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "本月概览",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatisticItem(
                        label = "总收入",
                        amount = uiState.totalIncome,
                        color = IncomeGreen
                    )
                    StatisticItem(
                        label = "总支出",
                        amount = uiState.totalExpense,
                        color = ExpenseRed
                    )
                    StatisticItem(
                        label = "余额",
                        amount = uiState.balance,
                        color = if (uiState.balance >= 0) IncomeGreen else ExpenseRed
                    )

                }

            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        // 交易列表
        Text(
            text = "最近交易",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(transactions) { transaction ->
                TransactionItem(
                    transaction = transaction,
                    onEdit = { /* TODO: 编辑功能 */ },
                    onDelete = { viewModel.deleteTransaction(transaction) }
                )
            }
        }

    }

}

@Composable
fun TransactionItem(transaction: Transaction, onEdit: () -> Unit, onDelete: () -> Unit) {
    val dateFormat = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
    val decimalFormat = DecimalFormat("#,##0.00")
    Card (
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ){
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = transaction.category,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (transaction.type == TransactionType.INCOME) "收入" else "支出",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (transaction.type == TransactionType.INCOME) IncomeGreen else ExpenseRed
                    )
                }

                if (transaction.note.isNotEmpty()) {
                    Text(
                        text = transaction.note,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = dateFormat.format(transaction.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${if (transaction.type == TransactionType.INCOME) "+" else "-"}¥${decimalFormat.format(transaction.amount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (transaction.type == TransactionType.INCOME) IncomeGreen else ExpenseRed
                )

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "编辑",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "删除",
                            tint = ExpenseRed
                        )
                    }
                }
            }

        }
    }

}

@Composable
fun StatisticItem(
    label: String,
    amount: Double,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val decimalFormat = DecimalFormat("#,##0.00")
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "¥${decimalFormat.format(amount)}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    FinanceAppTheme {
        StatisticItem(
            "商店",
            2.01,
            Color.Red
        )
    }
}