package com.spellit.domain.usecase
import javax.inject.Inject
import com.spellit.domain.model.GameSession
import com.spellit.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow

class ObserveSessionsUseCase @Inject constructor(private val repository: SessionRepository) {
    operator fun invoke(): Flow<List<GameSession>> = repository.observeSessions()
}

class GetSessionsUseCase @Inject constructor(private val repository: SessionRepository) {
    suspend operator fun invoke(): List<GameSession> = repository.getSessions()
}

class AddSessionUseCase @Inject constructor(private val repository: SessionRepository) {
    suspend operator fun invoke(session: GameSession): Long = repository.addSession(session)
}

class DeleteSessionUseCase @Inject constructor(private val repository: SessionRepository) {
    suspend operator fun invoke(id: Long) = repository.deleteSession(id)
}

class DeleteAllSessionsUseCase @Inject constructor(private val repository: SessionRepository) {
    suspend operator fun invoke() = repository.deleteAllSessions()
}

class GetPlayerNamesUseCase @Inject constructor(private val repository: SessionRepository) {
    suspend operator fun invoke(): List<String> = repository.getPlayerNames()
}