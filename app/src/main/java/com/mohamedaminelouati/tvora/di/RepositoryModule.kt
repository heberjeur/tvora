package com.mohamedaminelouati.tvora.di

import com.mohamedaminelouati.tvora.data.repository.EpgRepositoryImpl
import com.mohamedaminelouati.tvora.data.repository.ReminderRepositoryImpl
import com.mohamedaminelouati.tvora.data.translation.TranslationManagerImpl
import com.mohamedaminelouati.tvora.domain.repository.EpgRepository
import com.mohamedaminelouati.tvora.domain.repository.ReminderRepository
import com.mohamedaminelouati.tvora.domain.translation.TranslationManager
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
    abstract fun bindEpgRepository(impl: EpgRepositoryImpl): EpgRepository

    @Binds
    @Singleton
    abstract fun bindReminderRepository(impl: ReminderRepositoryImpl): ReminderRepository

    @Binds
    @Singleton
    abstract fun bindTranslationManager(impl: TranslationManagerImpl): TranslationManager
}
