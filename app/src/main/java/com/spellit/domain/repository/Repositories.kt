package com.spellit.domain.repository

import com.spellit.domain.model.AppSettings
import com.spellit.domain.model.GameSession
import com.spellit.domain.model.Word
import kotlinx.coroutines.flow.Flow

interface WordRepository {
    fun observeWords(): Flow<List<Word>>
    suspend fun getWords(): List<Word>
    suspend fun getWord(id: Long): Word?
    suspend fun addWord(spelling: String, audioFileName: String?): Long
    suspend fun deleteWord(id: Long)
    suspend fun wordCount(): Int
    suspend fun longWordCount(): Int
    suspend fun getRandomWords(count: Int, easyModeOnly: Boolean): List<Word>
}

interface SessionRepository {
    fun observeSessions(): Flow<List<GameSession>>
    suspend fun getSessions(): List<GameSession>
    suspend fun addSession(session: GameSession): Long
    suspend fun deleteSession(id: Long)
    suspend fun deleteAllSessions()
    suspend fun getPlayerNames(): List<String>
}

interface SettingsRepository {
    fun observeSettings(): Flow<AppSettings>
    suspend fun getSettings(): AppSettings
    suspend fun saveSettings(settings: AppSettings)
}