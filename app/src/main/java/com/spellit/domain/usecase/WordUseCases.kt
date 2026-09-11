package com.spellit.domain.usecase
import javax.inject.Inject
import com.spellit.domain.model.Word
import com.spellit.domain.repository.WordRepository
import kotlinx.coroutines.flow.Flow

class ObserveWordsUseCase @Inject constructor(private val repository: WordRepository) {
    operator fun invoke(): Flow<List<Word>> = repository.observeWords()
}

class GetWordsUseCase @Inject constructor(private val repository: WordRepository) {
    suspend operator fun invoke(): List<Word> = repository.getWords()
}

class AddWordUseCase @Inject constructor(private val repository: WordRepository) {
    suspend operator fun invoke(spelling: String, audioFileName: String?): Long =
        repository.addWord(spelling, audioFileName)
}

class DeleteWordUseCase @Inject constructor(private val repository: WordRepository) {
    suspend operator fun invoke(id: Long) = repository.deleteWord(id)
}

class GetWordCountUseCase @Inject constructor(private val repository: WordRepository) {
    suspend operator fun invoke(): Int = repository.wordCount()
}

class GetLongWordCountUseCase @Inject constructor(private val repository: WordRepository) {
    suspend operator fun invoke(): Int = repository.longWordCount()
}

class GetRandomWordsUseCase @Inject constructor(private val repository: WordRepository) {
    suspend operator fun invoke(count: Int, easyModeOnly: Boolean): List<Word> =
        repository.getRandomWords(count, easyModeOnly)
}