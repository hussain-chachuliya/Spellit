package com.spellit.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val spelling: String,
    val audioFileName: String?
)

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val playerName: String,
    val gameMode: String,
    val timestamp: Long,
    val resultsJson: String,
    val score: Int
)

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: Int = 0,
    val adminPin: String = "1234",
    val wordsPerSession: Int = 10,
    val easyTimerSeconds: Int = 0,
    val mediumTimerSeconds: Int = 0,
    val hardTimerSeconds: Int = 0,
    val easyTimerVisible: Boolean = true,
    val mediumTimerVisible: Boolean = true,
    val hardTimerVisible: Boolean = true
)