package com.xiaomai.financeapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.xiaomai.financeapp.data.converter.Converters
import com.xiaomai.financeapp.data.dao.CategoryDao
import com.xiaomai.financeapp.data.dao.SettingDao
import com.xiaomai.financeapp.data.dao.TransactionDao
import com.xiaomai.financeapp.data.entity.Category
import com.xiaomai.financeapp.data.entity.Setting
import com.xiaomai.financeapp.data.entity.Transaction
import com.xiaomai.financeapp.data.entity.TransactionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [Transaction::class, Category::class, Setting::class],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun settingDao(): SettingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "finance_database"
                ).addCallback(DatabaseCallback(scope)).build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(private val scope: CoroutineScope) :
            RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let {database ->
                    scope.launch {
                        populateDatabase(database.categoryDao(), database.settingDao())
                    }
                }
            }
        }

        suspend fun populateDatabase(categoryDao: CategoryDao, settingDao: SettingDao){
            // 预设收入分类
            val incomeCategories = listOf(
                Category(name = "工资", type = TransactionType.INCOME, color = "#4CAF50", icon = "💰", isDefault = true),
                Category(name = "奖金", type = TransactionType.INCOME, color = "#8BC34A", icon = "🎁", isDefault = true),
                Category(name = "投资收益", type = TransactionType.INCOME, color = "#CDDC39", icon = "📈", isDefault = true),
                Category(name = "兼职", type = TransactionType.INCOME, color = "#FFC107", icon = "💼", isDefault = true),
                Category(name = "其他收入", type = TransactionType.INCOME, color = "#FF9800", icon = "💡", isDefault = true)
            )

            // 预设支出分类
            val expenseCategories = listOf(
                Category(name = "餐饮", type = TransactionType.EXPENSE, color = "#F44336", icon = "🍽️", isDefault = true),
                Category(name = "交通", type = TransactionType.EXPENSE, color = "#E91E63", icon = "🚗", isDefault = true),
                Category(name = "购物", type = TransactionType.EXPENSE, color = "#9C27B0", icon = "🛒", isDefault = true),
                Category(name = "娱乐", type = TransactionType.EXPENSE, color = "#673AB7", icon = "🎮", isDefault = true),
                Category(name = "医疗", type = TransactionType.EXPENSE, color = "#3F51B5", icon = "🏥", isDefault = true),
                Category(name = "教育", type = TransactionType.EXPENSE, color = "#2196F3", icon = "📚", isDefault = true),
                Category(name = "住房", type = TransactionType.EXPENSE, color = "#03A9F4", icon = "🏠", isDefault = true),
                Category(name = "水电费", type = TransactionType.EXPENSE, color = "#00BCD4", icon = "💡", isDefault = true),
                Category(name = "通讯", type = TransactionType.EXPENSE, color = "#009688", icon = "📱", isDefault = true),
                Category(name = "其他支出", type = TransactionType.EXPENSE, color = "#795548", icon = "📝", isDefault = true)
            )
            incomeCategories.forEach { categoryDao.insertCategory(it) }
            expenseCategories.forEach { categoryDao.insertCategory(it) }

            // 创建默认设置
            val defaultSetting = Setting(autoBackup = false)
            settingDao.insertSetting(defaultSetting)
        }
    }
}