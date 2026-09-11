package com.spellit.di

import com.spellit.data.repository.SessionRepositoryImpl
import com.spellit.data.repository.SettingsRepositoryImpl
import com.spellit.data.repository.WordRepositoryImpl
import com.spellit.domain.repository.SessionRepository
import com.spellit.domain.repository.SettingsRepository
import com.spellit.domain.repository.WordRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWordRepository(impl: WordRepositoryImpl): WordRepository

    @Binds
    @Singleton
    abstract fun bindSessionRepository(impl: SessionRepositoryImpl): SessionRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}