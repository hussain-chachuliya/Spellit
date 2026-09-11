package com.spellit.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spellit.audio.AudioManager
import com.spellit.domain.model.Word
import com.spellit.domain.usecase.AddWordUseCase
import com.spellit.domain.usecase.DeleteWordUseCase
import com.spellit.domain.usecase.GetLongWordCountUseCase
import com.spellit.domain.usecase.ObserveWordsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WordManageUiState(
    val words: List<Word> = emptyList(),
    val longWordCount: Int = 0,
    val loading: Boolean = true
) {
    val totalCount: Int get() = words.size
}

@HiltViewModel
class WordManageViewModel @Inject constructor(
    observeWords: ObserveWordsUseCase,
    private val getLongWordCount: GetLongWordCountUseCase,
    private val addWord: AddWordUseCase,
    private val deleteWord: DeleteWordUseCase,
    private val audioManager: AudioManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(WordManageUiState())
    val uiState: StateFlow<WordManageUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeWords().collect { words ->
                val long = getLongWordCount()
                _uiState.value = WordManageUiState(words = words, longWordCount = long, loading = false)
            }
        }
    }

    fun addNewWord(spelling: String, audioFileName: String?) {
        val clean = spelling.trim()
        if (clean.isEmpty()) return
        viewModelScope.launch { addWord(clean, audioFileName) }
    }

    fun deleteWordAndAudio(word: Word) {
        viewModelScope.launch {
            audioManager.deleteAudioFile(word.audioFileName)
            deleteWord(word.id)
        }
    }

    fun previewAudio(word: Word) {
        audioManager.playWord(word.audioFileName.orEmpty())
    }

    fun createRecordingPath(): String = audioManager.createRecordingPath()

    suspend fun startRecording(path: String) = audioManager.startRecording(path)

    suspend fun stopRecording(): String? = audioManager.stopRecording()

    suspend fun cancelRecording() = audioManager.cancelRecording()

    fun playAbsolute(path: String) {
        audioManager.playAbsolutePath(path)
    }
}