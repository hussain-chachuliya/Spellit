package com.spellit.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.spellit.data.local.entity.SessionEntity
import com.spellit.data.local.entity.SettingsEntity
import com.spellit.data.local.entity.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words ORDER BY spelling COLLATE NOCASE ASC")
    fun observeWords(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words ORDER BY spelling COLLATE NOCASE ASC")
    suspend fun getWords(): List<WordEntity>

    @Query("SELECT * FROM words WHERE id = :id")
    suspend fun getWord(id: Long): WordEntity?

    @Insert
    suspend fun insert(word: WordEntity): Long

    @Delete
    suspend fun delete(word: WordEntity)

    @Query("SELECT COUNT(*) FROM words")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM words WHERE length(spelling) >= :minLength")
    suspend fun countLongWords(minLength: Int): Int

    @Query("SELECT * FROM words WHERE length(spelling) >= :minLength")
    suspend fun getLongWords(minLength: Int): List<WordEntity>

    @Query("SELECT * FROM words")
    suspend fun getAll(): List<WordEntity>
}

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions ORDER BY timestamp DESC")
    fun observeSessions(): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions ORDER BY timestamp DESC")
    suspend fun getSessions(): List<SessionEntity>

    @Insert
    suspend fun insert(session: SessionEntity): Long

    @Delete
    suspend fun delete(session: SessionEntity)

    @Query("DELETE FROM sessions")
    suspend fun deleteAll()

    @Query("SELECT DISTINCT playerName FROM sessions ORDER BY playerName COLLATE NOCASE ASC")
    suspend fun getPlayerNames(): List<String>
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE id = 0")
    suspend fun getSetting(): SettingsEntity?

    @Query("SELECT * FROM settings WHERE id = 0")
    fun observeSetting(): Flow<SettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(settings: SettingsEntity)
}