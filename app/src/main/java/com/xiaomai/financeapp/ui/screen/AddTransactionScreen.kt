package com.xiaomai.financeapp.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.xiaomai.financeapp.data.entity.Category
import com.xiaomai.financeapp.data.entity.Transaction
import com.xiaomai.financeapp.data.entity.TransactionType
import com.xiaomai.financeapp.ui.theme.ExpenseRed
import com.xiaomai.financeapp.ui.theme.IncomeGreen
import com.xiaomai.financeapp.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import androidx.core.graphics.toColorInt
import com.xiaomai.financeapp.R

/**
 * 项目: financeApp
 * 包名: com.xiaomai.financeapp.ui.screen
 * 作者: bobowg
 * 日期: 2025/8/16 时间: 20:42
 * 备注：
 **/

@Composable
fun AddTransactionScreen(
    viewModel: TransactionViewModel,
    transactionToEdit: Transaction? = null,
    onNavigateBack: () -> Unit = {}
) {
    var amount by remember { mutableStateOf(transactionToEdit?.amount?.toString() ?: "") }
    var selectedType by remember { mutableStateOf(transactionToEdit?.type ?: TransactionType.EXPENSE) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var note by remember { mutableStateOf(transactionToEdit?.note ?: "") }
    var selectedDate by remember { mutableStateOf(transactionToEdit?.date ?: Date()) }

    val categories by viewModel.getCategoriesByType(selectedType)
        .collectAsState(initial = emptyList())
    val dateDialogState = rememberMaterialDialogState()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    LaunchedEffect(categories, transactionToEdit) {
        if (transactionToEdit != null && categories.isNotEmpty()) {
            selectedCategory = categories.find { it.name == transactionToEdit.category }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (transactionToEdit == null) stringResource(R.string.add_transaction) else stringResource(R.string.edit_transaction),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // 交易类型选择
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.transaction_type),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TransactionTypeButton(
                        text = stringResource(R.string.expense),
                        selected = selectedType == TransactionType.EXPENSE,
                        color = ExpenseRed,
                        onClick = { selectedType = TransactionType.EXPENSE }
                    )
                    TransactionTypeButton(
                        text = stringResource(R.string.income),
                        selected = selectedType == TransactionType.INCOME,
                        color = IncomeGreen,
                        onClick = { selectedType = TransactionType.INCOME }
                    )
                }
            }
        }

        // 分类选择
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "选择分类",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        CategoryChip(
                            category = category,
                            selected = selectedCategory?.id == category.id,
                            onClick = { selectedCategory = category }
                        )
                    }
                }
            }
        }

        // 金额输入
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text(stringResource(R.string.amount)) },
            placeholder = { Text("0.00") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Text("¥", style = MaterialTheme.typography.titleMedium) }
        )

        // 日期选择
        OutlinedTextField(
            value = dateFormat.format(selectedDate),
            onValueChange = { },
            label = { Text(stringResource(R.string.date)) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { dateDialogState.show() }) {
                    Icon(Icons.Default.DateRange, contentDescription = stringResource(R.string.select_date))
                }
            }
        )

        // 备注输入
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text(stringResource(R.string.note)) },
            placeholder = { Text(stringResource(R.string.optional)) },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )

        Spacer(modifier = Modifier.weight(1f))

        // 保存按钮
        Button(
            onClick = {
                if (amount.isNotEmpty() && selectedCategory != null) {
                    val transaction = transactionToEdit?.copy(
                        amount = amount.toDoubleOrNull() ?: 0.0,
                        type = selectedType,
                        category = selectedCategory!!.name,
                        note = note,
                        date = selectedDate
                    )
                        ?: Transaction(
                            amount = amount.toDoubleOrNull() ?: 0.0,
                            type = selectedType,
                            category = selectedCategory!!.name,
                            note = note,
                            date = selectedDate
                        )
                    
                    if (transactionToEdit != null) {
                        viewModel.updateTransaction(transaction)
                    } else {
                        viewModel.insertTransaction(transaction)
                    }
                    
                    onNavigateBack()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = amount.isNotEmpty() && selectedCategory != null
        ) {
            Text(if (transactionToEdit == null) stringResource(R.string.save) else stringResource(R.string.update))
        }
    }

    // 日期选择对话框
    MaterialDialog(
        dialogState = dateDialogState,
        buttons = {
            positiveButton(text = stringResource(R.string.sure))
            negativeButton(text = stringResource(R.string.cancel))
        }
    ) {
        datepicker(
            initialDate = LocalDate.now(),
            title = stringResource(R.string.select_date)
        ) { date ->
            selectedDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())
        }
    }
}

@Composable
fun TransactionTypeButton(
    text: String,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    FilterChip(
        onClick = onClick,
        label = { Text(text) },
        selected = selected,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = color.copy(alpha = 0.2f),
            selectedLabelColor = color
        )
    )
}

@Composable
fun CategoryChip(
    category: Category,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        onClick = onClick,
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(category.icon)
                Text(category.name)
            }
        },
        selected = selected,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(category.color.toColorInt()).copy(
                alpha = 0.2f
            ),
            selectedLabelColor = Color(category.color.toColorInt())
        )
    )
}