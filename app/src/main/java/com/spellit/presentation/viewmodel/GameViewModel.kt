package com.spellit.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spellit.audio.AudioManager
import com.spellit.domain.model.AppSettings
import com.spellit.domain.model.GameMode
import com.spellit.domain.model.GameSession
import com.spellit.domain.model.ScoreCalculator
import com.spellit.domain.model.Word
import com.spellit.domain.model.WordResult
import com.spellit.domain.usecase.AddSessionUseCase
import com.spellit.domain.usecase.GetRandomWordsUseCase
import com.spellit.domain.usecase.ObserveSettingsUseCase
import com.spellit.presentation.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class GamePhase { LOADING, PLAYING, FINISHED }

sealed class WordFeedback {
    data class Correct(val word: String) : WordFeedback()
    data class Wrong(val word: String) : WordFeedback()
}

data class FinishSummary(
    val score: Int,
    val correctCount: Int,
    val totalWords: Int,
    val stars: Int
)

data class GameUiState(
    val phase: GamePhase = GamePhase.LOADING,
    val mode: GameMode = GameMode.MEDIUM,
    val playerName: String = "",
    val words: List<Word> = emptyList(),
    val currentIndex: Int = 0,
    val currentWord: Word? = null,
    val normalMode: Boolean = true,
    val timerSeconds: Int = 0,
    val timerVisible: Boolean = false,
    val timeRemainingMillis: Long = 0L,
    val totalTimeMillis: Long = 0L,
    val score: Int = 0,
    val correctCount: Int = 0,
    val results: List<WordResult> = emptyList(),
    val feedback: WordFeedback? = null,
    val summary: FinishSummary? = null
)

@HiltViewModel
class GameViewModel @Inject constructor(
    private val getRandomWords: GetRandomWordsUseCase,
    private val observeSettings: ObserveSettingsUseCase,
    private val audioManager: AudioManager,
    private val addSession: AddSessionUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val mode: GameMode = runCatching {
        GameMode.valueOf(savedStateHandle[Routes.modeArg()] ?: GameMode.MEDIUM.name)
    }.getOrDefault(GameMode.MEDIUM)

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var settings: AppSettings = AppSettings()
    private var timerJob: Job? = null
    private var wordFinalized = false
    private var baseTimerMillis = 0L
    private var answerTimerStartNanos: Long = 0L

    init {
        viewModelScope.launch {
            observeSettings().collect { settings = it }
        }
        _uiState.value = _uiState.value.copy(mode = mode)
    }

    fun startGame(playerName: String) {
        if (_uiState.value.phase == GamePhase.PLAYING) return
        viewModelScope.launch {
            val words = getRandomWords(settings.wordsPerSession, mode == GameMode.EASY)
            _uiState.value = _uiState.value.copy(
                phase = GamePhase.PLAYING,
                mode = mode,
                playerName = playerName,
                words = words,
                currentIndex = 0,
                score = 0,
                correctCount = 0,
                results = emptyList(),
                feedback = null,
                summary = null,
                normalMode = true
            )
            loadWord(0)
        }
    }

    private suspend fun loadWord(index: Int) {
        val state = _uiState.value
        val word = state.words.getOrNull(index) ?: return finish()
        wordFinalized = false
        val (timerSeconds, timerVisible) = timerConfig(state.mode)
        baseTimerMillis = timerSeconds * 1000L
        _uiState.value = state.copy(
            currentIndex = index,
            currentWord = word,
            feedback = null,
            timerSeconds = timerSeconds,
            timerVisible = timerVisible && timerSeconds > 0,
            timeRemainingMillis = baseTimerMillis,
            totalTimeMillis = baseTimerMillis,
            normalMode = true
        )
        startTimerIfNeeded()
    }

    private fun timerConfig(mode: GameMode): Pair<Int, Boolean> = when (mode) {
        GameMode.EASY -> settings.easyTimerSeconds to settings.easyTimerVisible
        GameMode.MEDIUM -> settings.mediumTimerSeconds to settings.mediumTimerVisible
        GameMode.HARD -> settings.hardTimerSeconds to settings.hardTimerVisible
    }

    private fun startTimerIfNeeded() {
        timerJob?.cancel()
        if (baseTimerMillis <= 0L) return
        answerTimerStartNanos = System.nanoTime()
        timerJob = viewModelScope.launch {
            var remaining = _uiState.value.timeRemainingMillis
            while (remaining > 0) {
                delay(100L)
                remaining -= 100L
                if (!wordFinalized) {
                    _uiState.value = _uiState.value.copy(
                        timeRemainingMillis = remaining.coerceAtLeast(0L),
                        normalMode = true
                    )
                }
                if (remaining <= 0L && !wordFinalized) {
                    submitAnswer(false, timedOut = true)
                }
            }
        }
    }

    /** Plays the pronunciation audio for the current word. */
    fun playCurrentAudio() {
        val word = _uiState.value.currentWord ?: return
        audioManager.playWord(word.audioFileName.orEmpty())
    }

    /**
     * Records the child's answer. Correct answers (with hints, or typed for
     * hard mode) may be submitted directly; wrong answers reveal the correct
     * spelling before moving on.
     */
    fun submitAnswer(correct: Boolean, timedOut: Boolean = false) {
        val state = _uiState.value
        val word = state.currentWord ?: return
        if (state.phase != GamePhase.PLAYING) return
        if (wordFinalized) return
        wordFinalized = true
        timerJob?.cancel()

        val remaining = if (baseTimerMillis > 0L) {
            (baseTimerMillis - (System.nanoTime() - answerTimerStartNanos) / 1_000_000L).coerceAtLeast(0L)
        } else null

        val result = WordResult(
            wordId = word.id,
            spelling = word.spelling,
            correct = correct,
            remainingTimeMillis = remaining
        )
        val newResults = state.results + result
        val newScore = state.score + ScoreCalculator.pointsForWord(correct, remaining, if (baseTimerMillis > 0) baseTimerMillis else null)
        val newCorrectCount = state.correctCount + if (correct) 1 else 0

        _uiState.value = state.copy(
            results = newResults,
            score = newScore,
            correctCount = newCorrectCount,
            normalMode = false
        )

        if (correct) {
            _uiState.value = _uiState.value.copy(
                feedback = WordFeedback.Correct(word.spelling)
            )
        } else {
            _uiState.value = _uiState.value.copy(
                feedback = WordFeedback.Wrong(word.spelling)
            )
        }
    }

    /** Advances to the next word after the feedback pause. */
    fun onFeedbackDismissed() {
        if (_uiState.value.feedback == null) return
        val state = _uiState.value
        if (state.currentIndex + 1 < state.words.size) {
            viewModelScope.launch { loadWord(state.currentIndex + 1) }
        } else {
            finish()
        }
    }

    private fun finish() {
        timerJob?.cancel()
        val state = _uiState.value
        val summary = FinishSummary(
            score = state.score,
            correctCount = state.correctCount,
            totalWords = state.words.size,
            stars = ScoreCalculator.starRating(state.correctCount, state.words.size)
        )
        _uiState.value = state.copy(phase = GamePhase.FINISHED, summary = summary, feedback = null)

        if (state.playerName.isNotBlank() && state.results.isNotEmpty()) {
            val session = GameSession(
                playerName = state.playerName,
                gameMode = state.mode,
                timestamp = System.currentTimeMillis(),
                results = state.results,
                score = state.score
            )
            viewModelScope.launch { runCatching { addSession(session) } }
        }
    }

    fun playAbsolute(path: String) {
        audioManager.playAbsolutePath(path)
    }

    override fun onCleared() {
        timerJob?.cancel()
        audioManager.release()
        super.onCleared()
    }
}