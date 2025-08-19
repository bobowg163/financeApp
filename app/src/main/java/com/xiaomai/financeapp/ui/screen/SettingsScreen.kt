package com.xiaomai.financeapp.ui.screen

import android.app.backup.BackupManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.xiaomai.financeapp.util.BackupFile
import com.xiaomai.financeapp.viewmodel.TransactionViewModel

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
    var showBackupDialog by remember { mutableStateOf(false) }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var backupMessage by remember { mutableStateOf("") }
    var autoBackupFiles by remember { mutableStateOf<List<BackupFile>>(emptyList()) }


}