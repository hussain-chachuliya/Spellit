package com.spellit.data.repository

import com.spellit.data.local.dao.SessionDao
import com.spellit.data.local.dao.SettingsDao
import com.spellit.data.local.dao.WordDao
import com.spellit.data.local.entity.SessionEntity
import com.spellit.data.local.entity.SettingsEntity
import com.spellit.data.local.entity.WordEntity
import com.spellit.domain.model.AppSettings
import com.spellit.domain.model.GameMode
import com.spellit.domain.model.GameSession
import com.spellit.domain.model.GameConfig
import com.spellit.domain.model.Word
import com.spellit.domain.model.WordResult
import com.spellit.domain.repository.SessionRepository
import com.spellit.domain.repository.SettingsRepository
import com.spellit.domain.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordRepositoryImpl @Inject constructor(private val dao: WordDao) : WordRepository {

    override fun observeWords(): Flow<List<Word>> =
        dao.observeWords().map { list -> list.map { it.toDomain() } }

    override suspend fun getWords(): List<Word> = dao.getWords().map { it.toDomain() }

    override suspend fun getWord(id: Long): Word? = dao.getWord(id)?.toDomain()

    override suspend fun addWord(spelling: String, audioFileName: String?): Long =
        dao.insert(WordEntity(spelling = spelling.trim(), audioFileName = audioFileName))

    override suspend fun deleteWord(id: Long) {
        dao.getWord(id)?.let { dao.delete(it) }
    }

    override suspend fun wordCount(): Int = dao.count()

    override suspend fun longWordCount(): Int = dao.countLongWords(GameConfig.EASY_MIN_LENGTH)

    override suspend fun getRandomWords(count: Int, easyModeOnly: Boolean): List<Word> {
        val pool = if (easyModeOnly) dao.getLongWords(GameConfig.EASY_MIN_LENGTH) else dao.getAll()
        return pool.shuffled().take(count).map { it.toDomain() }
    }
}

@Singleton
class SessionRepositoryImpl @Inject constructor(private val dao: SessionDao) : SessionRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override fun observeSessions(): Flow<List<GameSession>> =
        dao.observeSessions().map { list -> list.map { it.toDomain() } }

    override suspend fun getSessions(): List<GameSession> = dao.getSessions().map { it.toDomain() }

    override suspend fun addSession(session: GameSession): Long =
        dao.insert(session.toEntity())

    override suspend fun deleteSession(id: Long) {
        dao.getSessions().firstOrNull { it.id == id }?.let { dao.delete(it) }
    }

    override suspend fun deleteAllSessions() = dao.deleteAll()

    override suspend fun getPlayerNames(): List<String> = dao.getPlayerNames()
}

@Singleton
class SettingsRepositoryImpl @Inject constructor(private val dao: SettingsDao) : SettingsRepository {

    override fun observeSettings(): Flow<AppSettings> =
        dao.observeSetting().map { it?.toDomain() ?: AppSettings() }

    override suspend fun getSettings(): AppSettings =
        dao.getSetting()?.toDomain() ?: AppSettings().also { dao.insert(it.toEntity()) }

    override suspend fun saveSettings(settings: AppSettings) = dao.insert(settings.toEntity())
}

private fun WordEntity.toDomain() = Word(id = id, spelling = spelling, audioFileName = audioFileName)

@kotlinx.serialization.Serializable
private data class StoredResult(
    val wordId: Long,
    val spelling: String,
    val correct: Boolean
)

@kotlinx.serialization.Serializable
private data class StoredResults(
    val results: List<StoredResult>
)

private fun SessionEntity.toDomain(): GameSession {
    val gameMode = runCatching { GameMode.valueOf(gameMode) }.getOrDefault(GameMode.MEDIUM)
    val stored = runCatching { Json.decodeFromString(StoredResults.serializer(), resultsJson) }
        .getOrDefault(StoredResults(emptyList()))
    val results = stored.results.map {
        WordResult(wordId = it.wordId, spelling = it.spelling, correct = it.correct)
    }
    return GameSession(
        id = id,
        playerName = playerName,
        gameMode = gameMode,
        timestamp = timestamp,
        results = results,
        score = score
    )
}

private fun GameSession.toEntity(): SessionEntity {
    val stored = StoredResults(
        results = results.map { StoredResult(it.wordId, it.spelling, it.correct) }
    )
    return SessionEntity(
        id = id,
        playerName = playerName,
        gameMode = gameMode.name,
        timestamp = timestamp,
        resultsJson = Json.encodeToString(StoredResults.serializer(), stored),
        score = score
    )
}

private fun SettingsEntity.toDomain() = AppSettings(
    id = id,
    adminPin = adminPin,
    easyTimerSeconds = easyTimerSeconds,
    mediumTimerSeconds = mediumTimerSeconds,
    hardTimerSeconds = hardTimerSeconds,
    easyTimerVisible = easyTimerVisible,
    mediumTimerVisible = mediumTimerVisible,
    hardTimerVisible = hardTimerVisible
)

private fun AppSettings.toEntity() = SettingsEntity(
    id = 0,
    adminPin = adminPin,
    easyTimerSeconds = easyTimerSeconds,
    mediumTimerSeconds = mediumTimerSeconds,
    hardTimerSeconds = hardTimerSeconds,
    easyTimerVisible = easyTimerVisible,
    mediumTimerVisible = mediumTimerVisible,
    hardTimerVisible = hardTimerVisible
)