package com.xiaomai.financeapp.repository

import com.xiaomai.financeapp.data.dao.SettingDao
import com.xiaomai.financeapp.data.entity.Setting
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class SettingRepository(private val settingDao: SettingDao) {

    fun getAutoBackupEnabled(): Flow<Boolean?> {
        return settingDao.getAutoBackupEnabled()
    }

    suspend fun updateAutoBackupEnabled(enabled: Boolean) {
        settingDao.updateAutoBackupEnabled(enabled)
    }

    suspend fun getSetting(): Setting? {
        return settingDao.getSetting().first()
    }

    suspend fun insertSetting(setting: Setting): Long {
        return settingDao.insertSetting(setting)
    }

    suspend fun updateSetting(setting: Setting) {
        settingDao.updateSetting(setting)
    }
}

