package com.xiaomai.financeapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.xiaomai.financeapp.data.entity.Setting
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingDao {
    @Query("SELECT * FROM settings WHERE id = 1")
    fun getSetting(): Flow<Setting?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(setting: Setting): Long

    @Update
    suspend fun updateSetting(setting: Setting)

    @Query("UPDATE settings SET autoBackup = :enabled WHERE id = 1")
    suspend fun updateAutoBackupEnabled(enabled: Boolean)

    @Query("SELECT autoBackup FROM settings WHERE id = 1")
    fun getAutoBackupEnabled(): Flow<Boolean?>
}



