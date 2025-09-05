package com.xiaomai.financeapp.util

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.xiaomai.financeapp.data.entity.Category
import com.xiaomai.financeapp.data.entity.Transaction
import com.xiaomai.financeapp.repository.TransactionRepository
import kotlinx.coroutines.flow.first
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 项目: financeApp
 * 包名: com.xiaomai.financeapp.util
 * 作者: bobowg
 * 日期: 2025/8/19 时间: 09:32
 * 备注：
 **/

class BackupManager(private val context: Context, private val repository: TransactionRepository) {
    private val gson = Gson()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())

    suspend fun exportToJson(outputUri: Uri): Result<String> {
        return try {
            val transactions = repository.getAllTransactions().first()
            val categories = repository.getAllCategories().first()

            val backupData = BackupData(
                transactions = transactions,
                categories = categories,
                exportDate = Date(),
                version = "1.0"
            )
            val jsonData = gson.toJson(backupData)
            context.contentResolver.openOutputStream(outputUri)?.use { outputStream ->
                outputStream.write(jsonData.toByteArray())
            }
            Result.success("导出成功！")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun importFromJson(inputUri: Uri): Result<String> {
        return try {
            val jsonString = context.contentResolver.openInputStream(inputUri)?.use { inputStream ->
                inputStream.readBytes().toString(Charsets.UTF_8)
            }?: throw IOException("无法读取文件")
            val backupData = gson.fromJson(jsonString, BackupData::class.java)
            //清空数据
            repository.deleteAllTransactions()
            repository.deleteNonDefaultCategories()
            //导入分类
            backupData.categories.forEach {category ->
                if (!category.isDefault){
                    repository.insertCategory(category.copy(id = 0))
                }
            }

            //导入交易记录
            backupData.transactions.forEach {transaction ->
                repository.insertTransaction(transaction.copy(id = 0))
            }

            Result.success("数据导入成功，共导入 ${backupData.transactions.size} 条记录!")
        }catch (e: Exception){
            Result.failure(e)
        }

    }


    fun generateBackupFileName():String{
        return "财务记账_备份_${dateFormat.format(Date())}.json"
    }

    suspend fun createAutoBackup():Result<String>{
        return try {
            val transactions = repository.getAllTransactions().first()
            val categories = repository.getAllCategories().first()

            val backupData = BackupData(
                transactions = transactions,
                categories = categories,
                exportDate = Date(),
                version = "1.0"
            )

            val jsonString = gson.toJson(backupData)
            val fileName = "auto_backup_${dateFormat.format(Date())}.json"

            // 保存到应用私有目录
            val file = java.io.File(context.filesDir, fileName)
            file.writeText(jsonString)

            // 清理旧的自动备份文件（保留最近5个）
            cleanOldBackups()

            Result.success("自动备份创建成功: $fileName")
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    /**
     * 获取所有自动备份文件列表
     **/
    fun getAutoBackupFiles(): List<BackupFile> {
        return try {
            val backupFiles = context.filesDir.listFiles { file ->
                file.name.startsWith("auto_backup_") && file.name.endsWith(".json")
            }?.sortedByDescending { it.lastModified() } ?: emptyList()

            backupFiles.map { file ->
                BackupFile(
                    name = file.name,
                    path = file.absolutePath,
                    size = file.length(),
                    lastModified = Date(file.lastModified())
                )
            }
        } catch (e: Exception) {
            emptyList()

        }
    }

    /**
     * 清理旧的自动备份文件（保留最近5个）
     **/
    private fun cleanOldBackups() {
        try {
            val backupFiles = context.filesDir.listFiles { file ->
                file.name.startsWith("auto_backup_") && file.name.endsWith(".json")
            }?.sortedByDescending { it.lastModified() }

            backupFiles?.drop(5)?.forEach { file ->
                file.delete()
            }
        } catch (e: Exception) {
            // 忽略清理错误
        }
    }
}


data class BackupData(
    val transactions: List<Transaction>,
    val categories: List<Category>,
    val exportDate: Date,
    val version: String
)

data class BackupFile(
    val name: String,
    val path: String,
    val size: Long,
    val lastModified: Date
)