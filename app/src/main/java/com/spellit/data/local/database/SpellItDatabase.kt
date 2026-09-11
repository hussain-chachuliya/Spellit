package com.spellit.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.spellit.data.local.dao.SessionDao
import com.spellit.data.local.dao.SettingsDao
import com.spellit.data.local.dao.WordDao
import com.spellit.data.local.entity.SessionEntity
import com.spellit.data.local.entity.SettingsEntity
import com.spellit.data.local.entity.WordEntity

@Database(
    entities = [WordEntity::class, SessionEntity::class, SettingsEntity::class],
    version = 2,
    exportSchema = false
)
abstract class SpellItDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao
    abstract fun sessionDao(): SessionDao
    abstract fun settingsDao(): SettingsDao
}