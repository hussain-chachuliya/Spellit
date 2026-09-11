package com.spellit.di

import android.content.Context
import androidx.room.Room
import com.spellit.data.local.dao.SessionDao
import com.spellit.data.local.dao.SettingsDao
import com.spellit.data.local.dao.WordDao
import com.spellit.data.local.database.SpellItDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SpellItDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            SpellItDatabase::class.java,
            "spellit.db"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideWordDao(db: SpellItDatabase): WordDao = db.wordDao()

    @Provides
    fun provideSessionDao(db: SpellItDatabase): SessionDao = db.sessionDao()

    @Provides
    fun provideSettingsDao(db: SpellItDatabase): SettingsDao = db.settingsDao()
}