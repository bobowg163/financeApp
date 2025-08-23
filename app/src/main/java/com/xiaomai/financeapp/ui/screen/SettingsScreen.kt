package com.xiaomai.financeapp.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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

/**
 * 项目: financeApp
 * 包名: com.xiaomai.financeapp.ui.screen
 * 作者: bobowg
 * 日期: 2025/8/19 时间: 09:03
 * 备注：设置界面包含数据的统计与设置
 **/

@Composable
fun SettingsScreen(viewModel: TransactionViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var backupManager by remember { mutableStateOf<BackupManager?>(null) }
    var backupMessage by remember { mutableStateOf("") }
    var showMessageDialog by remember { mutableStateOf(false) }
    var autoBackupFiles by remember { mutableStateOf<List<BackupFile>>(emptyList()) }
    
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
                            val fileName = backupManager?.generateBackupFileName() ?: "backup.json"
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
                                        result.getOrNull() ?: "自动备份创建成功"
                                    } else {
                                        result.exceptionOrNull()?.message ?: "自动备份创建失败"
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
                
                Text(
                    text = "以下是应用自动创建的备份文件",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                LazyColumn {
                    items(autoBackupFiles) { file ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = file.name,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "大小: ${file.size} bytes",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
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