package com.spellit.domain.usecase
import javax.inject.Inject
import com.spellit.domain.model.AppSettings
import com.spellit.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class ObserveSettingsUseCase @Inject constructor(private val repository: SettingsRepository) {
    operator fun invoke(): Flow<AppSettings> = repository.observeSettings()
}

class GetSettingsUseCase @Inject constructor(private val repository: SettingsRepository) {
    suspend operator fun invoke(): AppSettings = repository.getSettings()
}

class SaveSettingsUseCase @Inject constructor(private val repository: SettingsRepository) {
    suspend operator fun invoke(settings: AppSettings) = repository.saveSettings(settings)
}

class VerifyPinUseCase @Inject constructor(private val repository: SettingsRepository) {
    suspend operator fun invoke(pin: String): Boolean {
        val settings = repository.getSettings()
        return pin.length == 4 && pin.all { it.isDigit() } && pin == settings.adminPin
    }
}