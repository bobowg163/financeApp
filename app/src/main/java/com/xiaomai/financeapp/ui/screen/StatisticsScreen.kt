package com.xiaomai.financeapp.ui.screen

import android.annotation.SuppressLint
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.toColorInt
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.xiaomai.financeapp.R
import com.xiaomai.financeapp.data.dao.CategoryTotal
import com.xiaomai.financeapp.data.entity.ChartType
import com.xiaomai.financeapp.data.entity.TransactionType
import com.xiaomai.financeapp.ui.theme.ExpenseRed
import com.xiaomai.financeapp.ui.theme.FinanceAppTheme
import com.xiaomai.financeapp.ui.theme.IncomeGreen
import com.xiaomai.financeapp.viewmodel.TransactionViewModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * 项目: financeApp
 * 包名: com.xiaomai.financeapp.ui.screen
 * 作者: bobowg
 * 日期: 2025/8/17 时间: 20:16
 * 备注：图表选择与分析
 **/

@Composable
fun StatisticsScreen(viewModel: TransactionViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedPeriod by remember { mutableStateOf("本月") }
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var selectedChartType by remember { mutableStateOf(ChartType.PIE_CHART) }
    var customStartDate by remember { mutableStateOf(Date()) }
    var customEndDate by remember { mutableStateOf(Date()) }

    val startDateDialogState = rememberMaterialDialogState()
    val endDateDialogState = rememberMaterialDialogState()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    LaunchedEffect(selectedPeriod) {
        when (selectedPeriod) {
            "本周" -> {
                val calendar = Calendar.getInstance()
                val today = calendar.get(Calendar.DAY_OF_WEEK)
                val mondayOffset = if (today == Calendar.SUNDAY) -6 else Calendar.MONDAY - today
                
                calendar.add(Calendar.DAY_OF_MONTH, mondayOffset)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startDate = calendar.time
                
                calendar.add(Calendar.DAY_OF_MONTH, 6)
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                calendar.set(Calendar.MILLISECOND, 999)
                val endDate = calendar.time
                
                viewModel.setSelectedDateRange(startDate, endDate)
            }
            
            "本月" -> {
                viewModel.loadCurrentMonthStatistics()
            }

            "上月" -> {
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.MONTH, -1)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startDate = calendar.time

                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                calendar.set(Calendar.MILLISECOND, 999)
                val endDate = calendar.time

                viewModel.setSelectedDateRange(startDate, endDate)
            }

            "今年" -> {
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.MONTH, Calendar.JANUARY)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startDate = calendar.time

                calendar.set(Calendar.MONTH, Calendar.DECEMBER)
                calendar.set(Calendar.DAY_OF_MONTH, 31)
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                calendar.set(Calendar.MILLISECOND, 999)
                val endDate = calendar.time

                viewModel.setSelectedDateRange(startDate, endDate)
            }

            "自定义" -> {
                // 设置默认的日期范围（最近30天）
                val calendar = Calendar.getInstance()
                val endDate = calendar.time

                calendar.add(Calendar.DAY_OF_MONTH, -30)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startDate = calendar.time

                customStartDate = startDate
                customEndDate = endDate
                viewModel.setSelectedDateRange(startDate, endDate)
            }
        }

    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "统计分析",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        // 时间周期选择

        item {
            androidx.compose.material3.Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "时间周期",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val periods = listOf("本周", "本月", "上月", "今年", "自定义")
                        periods.forEach { period ->
                            FilterChip(
                                onClick = { selectedPeriod = period },
                                label = { Text(period) },
                                selected = selectedPeriod == period
                            )
                        }
                    }

                    if (selectedPeriod == "自定义") {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = dateFormat.format(customStartDate),
                                onValueChange = { },
                                label = { Text("开始日期") },
                                modifier = Modifier.weight(1f),
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = { startDateDialogState.show() }) {
                                        Icon(
                                            Icons.Default.DateRange,
                                            contentDescription = "选择开始日期"
                                        )
                                    }
                                }
                            )

                            OutlinedTextField(
                                value = dateFormat.format(customEndDate),
                                onValueChange = {
                                },
                                label = { Text("结束日期") },
                                readOnly = true,
                                modifier = Modifier.weight(1f),
                                trailingIcon = {
                                    IconButton(onClick = { endDateDialogState.show() }) {
                                        Icon(
                                            Icons.Default.DateRange,
                                            contentDescription = "选择结束日期"
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
        // 总览统计

        item {
            androidx.compose.material3.Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = "总览统计",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatisticColumn(
                            label = "总收入",
                            amount = uiState.totalIncome,
                            color = IncomeGreen
                        )
                        StatisticColumn(
                            label = "总支出",
                            amount = uiState.totalExpense,
                            color = ExpenseRed
                        )
                        StatisticColumn(
                            label = "净收入",
                            amount = uiState.balance,
                            color = if (uiState.balance >= 0) IncomeGreen else ExpenseRed
                        )

                    }
                }
            }
        }
        // 分类统计选择
        item {
            androidx.compose.material3.Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "分类统计",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            onClick = { selectedType = TransactionType.EXPENSE },
                            label = { Text("支出分类") },
                            selected = selectedType == TransactionType.EXPENSE
                        )
                        FilterChip(
                            onClick = { selectedType = TransactionType.INCOME },
                            label = { Text("收入分类") },
                            selected = selectedType == TransactionType.INCOME
                        )

                    }
                }
            }
        }
        
        // 图表类型选择器
        item {
            androidx.compose.material3.Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "图表类型",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            onClick = { selectedChartType = ChartType.PIE_CHART },
                            label = { Text("饼图") },
                            selected = selectedChartType == ChartType.PIE_CHART
                        )
                        FilterChip(
                            onClick = { selectedChartType = ChartType.BAR_CHART },
                            label = { Text("柱状图") },
                            selected = selectedChartType == ChartType.BAR_CHART
                        )
                        FilterChip(
                            onClick = { selectedChartType = ChartType.LINE_CHART },
                            label = { Text("折线图") },
                            selected = selectedChartType == ChartType.LINE_CHART
                        )
                    }
                }
            }
        }
        
        // 图表显示区域
        item {
            val categoryTotals = if (selectedType == TransactionType.EXPENSE) {
                uiState.expenseCategoryTotals
            } else {
                uiState.incomeCategoryTotals
            }
            
            if (categoryTotals.isNotEmpty()) {
                androidx.compose.material3.Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        val chartTitle = when (selectedChartType) {
                            ChartType.PIE_CHART -> "${if (selectedType == TransactionType.EXPENSE) "支出" else "收入"}分布"
                            ChartType.BAR_CHART -> "${if (selectedType == TransactionType.EXPENSE) "支出" else "收入"}对比"
                            ChartType.LINE_CHART -> "${if (selectedType == TransactionType.EXPENSE) "支出" else "收入"}趋势"
                        }
                        
                        Text(
                            text = chartTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        when (selectedChartType) {
                            ChartType.PIE_CHART -> {
                                PieChartView(categoryTotals)
                            }
                            ChartType.BAR_CHART -> {
                                BarChartView(categoryTotals)
                            }
                            ChartType.LINE_CHART -> {
                                LineChartView(categoryTotals)
                            }
                        }
                    }
                }
            }
        }

        // 分类详细列表
        items(
            if (selectedType == TransactionType.EXPENSE) {
                uiState.expenseCategoryTotals
            } else {
                uiState.incomeCategoryTotals
            }
        ) { categoryTotal ->
            CategoryTotalItem(
                categoryTotal = categoryTotal,
                totalAmount = if (selectedType == TransactionType.EXPENSE) uiState.totalExpense else uiState.totalIncome,
                color = if (selectedType == TransactionType.EXPENSE) ExpenseRed else IncomeGreen
            )
        }
    }
    // 日期选择对话框
    MaterialDialog(
        dialogState = startDateDialogState,
        buttons = {
            positiveButton(text = "确定")
            negativeButton(text = "取消")
        }
    ) {
        datepicker(
            initialDate = customStartDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
            title = "选择开始日期"
        ) { date ->
            val newStartDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())
            customStartDate = newStartDate
            viewModel.setSelectedDateRange(newStartDate, customEndDate)
        }
    }

    MaterialDialog(
        dialogState = endDateDialogState,
        buttons = {
            positiveButton(text = "确定")
            negativeButton(text = "取消")
        }
    ) {
        datepicker(
            initialDate = customEndDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
            title = "选择结束日期"
        ) { date ->
            val newEndDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())
            customEndDate = newEndDate
            viewModel.setSelectedDateRange(customStartDate, newEndDate)
        }
    }
}


private fun generateColors(count: Int): List<Int> {
    val colors = mutableListOf<Int>()
    val baseColors = listOf(
        "#F44336", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5",
        "#2196F3", "#03A9F4", "#00BCD4", "#009688", "#4CAF50",
        "#8BC34A", "#CDDC39", "#FFEB3B", "#FFC107", "#FF9800",
        "#FF5722", "#795548", "#9E9E9E", "#607D8B"
    )

    for (i in 0 until count) {
        val colorStr = baseColors[i % baseColors.size]
        colors.add(colorStr.toColorInt())
    }

    return colors
}

@Composable
fun StatisticColumn(label: String, amount: Double, color: Color) {
    val decimalFormat = DecimalFormat("#,##0.00")
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
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

@SuppressLint("DefaultLocale")
@Composable
fun CategoryTotalItem(
    categoryTotal: CategoryTotal,
    totalAmount: Double,
    color: Color
) {
    val decimalFormat = DecimalFormat("#,##0.00")
    val percentage = if (totalAmount > 0) (categoryTotal.total / totalAmount) * 100 else 0.0
    androidx.compose.material3.Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = categoryTotal.category,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${String.format("%.1f", percentage)}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "¥${decimalFormat.format(categoryTotal.total)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Preview
@Composable
private fun StatisticColumnPreview() {
    FinanceAppTheme {
        Column {
            StatisticColumn(
                label = stringResource(R.string.total_income),
                amount = 1000.0,
                color = IncomeGreen
            )

            CategoryTotalItem(
                categoryTotal = CategoryTotal("食品", 100.0),
                totalAmount = 1000.0,
                color = ExpenseRed
            )
        }

    }
}

@Composable
fun PieChartView(categoryTotals: List<CategoryTotal>) {
    AndroidView(
        factory = { context ->
            PieChart(context).apply {
                setUsePercentValues(true)
                description.isEnabled = false
                setExtraOffsets(5f, 10f, 5f, 5f)
                dragDecelerationFrictionCoef = 0.95f
                isDrawHoleEnabled = true
                setHoleColor(android.graphics.Color.WHITE)
                setTransparentCircleColor(android.graphics.Color.WHITE)
                setTransparentCircleAlpha(110)
                holeRadius = 58f
                transparentCircleRadius = 61f
                setDrawCenterText(true)
                rotationAngle = 0f
                isRotationEnabled = true
                isHighlightPerTapEnabled = true
                legend.isEnabled = false
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        update = { pieChart ->
            val entries = categoryTotals.map {
                PieEntry(it.total.toFloat(), "%${it.category}")

            }
            val colors = generateColors(categoryTotals.size)
            val dataSet = PieDataSet(entries, "").apply {
                setDrawIcons(false)
                sliceSpace = 3f
                iconsOffset = MPPointF(0f, 40f)
                selectionShift = 5f
                setColors(colors)
            }
            val data = PieData(dataSet).apply {
                setValueFormatter(PercentFormatter())
                setValueTextSize(11f)
                setValueTextColor(android.graphics.Color.WHITE)
            }

            pieChart.data = data
            pieChart.highlightValue(null)
            pieChart.invalidate()
        }
    )
}

@Composable
fun BarChartView(categoryTotals: List<CategoryTotal>) {
    AndroidView(
        factory = { context ->
            BarChart(context).apply {
                description.isEnabled = false
                setDrawGridBackground(false)
                setDrawBarShadow(false)
                setDrawValueAboveBar(true)
                setPinchZoom(false)
                setDrawGridBackground(false)
                
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    granularity = 1f
                    labelCount = categoryTotals.size
                }
                
                axisLeft.apply {
                    setDrawGridLines(true)
                    axisMinimum = 0f
                }
                
                axisRight.isEnabled = false
                legend.isEnabled = false
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        update = { barChart ->
            val entries = categoryTotals.mapIndexed { index, categoryTotal ->
                BarEntry(index.toFloat(), categoryTotal.total.toFloat())
            }
            
            val dataSet = BarDataSet(entries, "").apply {
                colors = generateColors(categoryTotals.size)
                setDrawValues(true)
                valueTextSize = 10f
            }
            
            val data = BarData(dataSet).apply {
                barWidth = 0.8f
            }
            
            barChart.apply {
                this.data = data
                xAxis.valueFormatter = IndexAxisValueFormatter(categoryTotals.map { it.category })
                animateY(1000)
                invalidate()
            }
        }
    )
}

@Composable
fun LineChartView(categoryTotals: List<CategoryTotal>) {
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                setDrawGridBackground(false)
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(false)
                
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    granularity = 1f
                    labelCount = categoryTotals.size
                }
                
                axisLeft.apply {
                    setDrawGridLines(true)
                    axisMinimum = 0f
                }
                
                axisRight.isEnabled = false
                legend.isEnabled = false
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        update = { lineChart ->
            val entries = categoryTotals.mapIndexed { index, categoryTotal ->
                Entry(index.toFloat(), categoryTotal.total.toFloat())
            }
            
            val dataSet = LineDataSet(entries, "").apply {
                color = ExpenseRed.hashCode()
                lineWidth = 2f
                circleRadius = 4f
                setCircleColor(ExpenseRed.hashCode())
                setDrawCircleHole(false)
                setDrawValues(true)
                valueTextSize = 10f
            }
            
            val data = LineData(dataSet)
            
            lineChart.apply {
                this.data = data
                xAxis.valueFormatter = IndexAxisValueFormatter(categoryTotals.map { it.category })
                animateX(1000)
                invalidate()
            }
        }
    )
}