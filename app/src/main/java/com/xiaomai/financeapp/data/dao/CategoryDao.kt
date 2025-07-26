package com.xiaomai.financeapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.xiaomai.financeapp.data.entity.Category
import com.xiaomai.financeapp.data.entity.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE type = :type ORDER BY name ASC")
    fun getCategoriesByType(type: TransactionType): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id:Long): Category?

    @Query("SELECT * FROM categories WHERE name = :name AND type= :type")
    suspend fun getCategoryByNameAndType(name:String,type: TransactionType): Category?

    @Insert
    suspend fun insertCategory(category: Category): Long

    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("DELETE FROM categories WHERE isDefault = 0")
    suspend fun deleteNonDefaultCategories()
}