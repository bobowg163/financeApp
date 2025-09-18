package com.xiaomai.financeapp.ui.screen

import android.text.format.DateFormat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xiaomai.financeapp.util.BackupFile
import com.xiaomai.financeapp.util.BackupManager
import com.xiaomai.financeapp.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch
import java.util.Date

/**
 * 项目: financeApp
 * 包名: com.xiaomai.financeapp.ui.screen
 * 作者: bobowg
 * 日期: 2025/8/19 时间: 09:03
 * 备注：设置界面包含数据的统计与设置
 **/

// Helper functions for formatting
private fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        else -> "${bytes / (1024 * 1024)} MB"
    }
}

private fun formatDateTime(date: Date): String {
    return DateFormat.format("yyyy-MM-dd HH:mm", date).toString()
}

@Composable
fun SettingsScreen(viewModel: TransactionViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var backupManager by remember { mutableStateOf<BackupManager?>(null) }
    var backupMessage by remember { mutableStateOf("") }
    var showMessageDialog by remember { mutableStateOf(false) }
    var autoBackupFiles by remember { mutableStateOf<List<BackupFile>>(emptyList()) }
    var selectedBackupFile by remember { mutableStateOf<BackupFile?>(null) }
    var showImportConfirmDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var autoBackupEnabled by remember { mutableStateOf(false) }
    var autoBackupInterval by remember { mutableIntStateOf(7) } // days

    // 导出文件选择器
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json"),
        onResult = { uri ->
            uri?.let {
                backupManager?.let { manager ->
                    scope.launch {
                        val result = manager.exportToJson(it)
                        backupMessage = if (result.isSuccess) {
                            result.getOrNull() ?: "导出成功"
                        } else {
                            result.exceptionOrNull()?.message ?: "导出失败"
                        }
                        showMessageDialog = true
                    }
                }
            }
        }
    )

    // 导入文件选择器
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                backupManager?.let { manager ->
                    scope.launch {
                        val result = manager.importFromJson(it)
                        backupMessage = if (result.isSuccess) {
                            result.getOrNull() ?: "导入成功"
                        } else {
                            result.exceptionOrNull()?.message ?: "导入失败"
                        }
                        showMessageDialog = true
                    }
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        backupManager = BackupManager(context, viewModel.repository)
        backupManager?.let { manager ->
            autoBackupFiles = manager.getAutoBackupFiles()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "设置",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyColumn {
            item {
                // 数据备份卡片
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "数据备份",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "导出所有数据到JSON文件，以便在其他设备上恢复",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val fileName =
                                        backupManager?.generateBackupFileName() ?: "backup.json"
                                    exportLauncher.launch(fileName)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("导出")
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Button(
                                onClick = {
                                    // 直接创建自动备份
                                    backupManager?.let { manager ->
                                        scope.launch {
                                            val result = manager.createAutoBackup()
                                            backupMessage = if (result.isSuccess) {
                                                // 刷新备份文件列表
                                                autoBackupFiles = manager.getAutoBackupFiles()
                                                result.getOrNull() ?: "自动备份创建成功"
                                            } else {
                                                result.exceptionOrNull()?.message
                                                    ?: "自动备份创建失败"
                                            }
                                            showMessageDialog = true
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("自动备份")
                            }
                        }
                    }
                }
            }

            item {

                // 数据恢复卡片
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "数据恢复",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "从JSON备份文件恢复数据",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Button(
                            onClick = {
                                importLauncher.launch("application/json")
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("导入数据")
                        }
                    }
                }
            }

            item {

                // 定期备份设置卡片
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "定期自动备份",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "启用定期自动备份功能，定期为您的数据创建备份",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "定期备份",
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text("启用定期自动备份")
                            }
                            Switch(
                                checked = autoBackupEnabled,
                                onCheckedChange = { enabled ->
                                    autoBackupEnabled = enabled
                                    scope.launch {
                                        backupManager?.let { manager ->
                                            if (enabled) {
                                                val result = manager.createAutoBackup()
                                                backupMessage = if (result.isSuccess) {
                                                    autoBackupFiles = manager.getAutoBackupFiles()
                                                    result.getOrNull()
                                                        ?: "定期备份已启用，首次备份创建成功"
                                                } else {
                                                    result.exceptionOrNull()?.message
                                                        ?: "定期备份启用失败"
                                                }
                                            } else {
                                                backupMessage = "定期备份已禁用"
                                            }
                                            showMessageDialog = true
                                        }
                                    }
                                }
                            )
                        }

                        if (autoBackupEnabled) {
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("备份间隔：${autoBackupInterval} 天")
                                Spacer(modifier = Modifier.width(8.dp))
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "系统将每隔 $autoBackupInterval 天自动创建一次备份文件",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {

                // 自动备份文件列表
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "自动备份文件",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        if (autoBackupFiles.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "无备份文件",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "暂无自动备份文件",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "点击上方\"自动备份\"按钮创建第一个备份",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = "共 ${autoBackupFiles.size} 个备份文件",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            LazyColumn(
                                modifier = Modifier.heightIn(max = 300.dp)
                            ) {
                                items(autoBackupFiles) { file ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surface
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clickable {
                                                        selectedBackupFile = file
                                                        showImportConfirmDialog = true
                                                    }
                                            ) {
                                                Text(
                                                    text = file.name.replace("auto_backup_", "")
                                                        .replace(".json", ""),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Medium
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.DateRange,
                                                        contentDescription = "备份时间",
                                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        modifier = Modifier
                                                            .size(14.dp)
                                                            .padding(end = 4.dp)
                                                    )
                                                    Text(
                                                        text = formatDateTime(file.lastModified),
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.DateRange,
                                                        contentDescription = "文件大小",
                                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        modifier = Modifier
                                                            .size(14.dp)
                                                            .padding(end = 4.dp)
                                                    )
                                                    Text(
                                                        text = formatFileSize(file.size),
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "点击导入此备份文件",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                            IconButton(
                                                onClick = {
                                                    selectedBackupFile = file
                                                    showDeleteConfirmDialog = true
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "删除备份文件",
                                                    tint = MaterialTheme.colorScheme.error
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
    }

    // 删除确认对话框
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteConfirmDialog = false
                selectedBackupFile = null
            },
            title = { Text("确认删除") },
            text = {
                Text("确定要删除备份文件 \"${selectedBackupFile?.name}\" 吗？\n\n此操作不可撤销，请谨慎操作。")
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedBackupFile?.let { file ->
                            backupManager?.let { manager ->
                                scope.launch {
                                    val result = manager.deleteBackupFile(file)
                                    backupMessage = if (result.isSuccess) {
                                        // 刷新备份文件列表
                                        autoBackupFiles = manager.getAutoBackupFiles()
                                        result.getOrNull() ?: "删除成功"
                                    } else {
                                        result.exceptionOrNull()?.message ?: "删除失败"
                                    }
                                    showMessageDialog = true
                                    showDeleteConfirmDialog = false
                                    selectedBackupFile = null
                                }
                            }
                        }
                    }
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        selectedBackupFile = null
                    }
                ) {
                    Text("取消")
                }
            }
        )
    }

    // 导入确认对话框
    if (showImportConfirmDialog) {
        AlertDialog(
            onDismissRequest = {
                showImportConfirmDialog = false
                selectedBackupFile = null
            },
            title = { Text("确认导入") },
            text = {
                Text("确定要导入备份文件 \"${selectedBackupFile?.name}\" 吗？\n\n注意：导入将会覆盖当前所有数据，请谨慎操作。")
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedBackupFile?.let { file ->
                            backupManager?.let { manager ->
                                scope.launch {
                                    val result = manager.importFromAutoBackup(file)
                                    backupMessage = if (result.isSuccess) {
                                        result.getOrNull() ?: "导入成功"
                                    } else {
                                        result.exceptionOrNull()?.message ?: "导入失败"
                                    }
                                    showMessageDialog = true
                                    showImportConfirmDialog = false
                                    selectedBackupFile = null
                                }
                            }
                        }
                    }
                ) {
                    Text("导入")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showImportConfirmDialog = false
                        selectedBackupFile = null
                    }
                ) {
                    Text("取消")
                }
            }
        )
    }

    // 消息对话框
    if (showMessageDialog) {
        AlertDialog(
            onDismissRequest = { showMessageDialog = false },
            title = { Text("提示") },
            text = { Text(backupMessage) },
            confirmButton = {
                Button(onClick = { showMessageDialog = false }) {
                    Text("确定")
                }
            }
        )
    }
}