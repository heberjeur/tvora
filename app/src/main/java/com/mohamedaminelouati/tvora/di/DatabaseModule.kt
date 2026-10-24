package com.mohamedaminelouati.tvora.di

import android.content.Context
import androidx.room.Room
import com.mohamedaminelouati.tvora.data.local.db.ChannelDao
import com.mohamedaminelouati.tvora.data.local.db.ProgramDao
import com.mohamedaminelouati.tvora.data.local.db.ReminderDao
import com.mohamedaminelouati.tvora.data.local.db.SyncMetadataDao
import com.mohamedaminelouati.tvora.data.local.db.TvoraDatabase
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
    fun provideTvoraDatabase(@ApplicationContext context: Context): TvoraDatabase {
        return Room.databaseBuilder(
            context,
            TvoraDatabase::class.java,
            "tvora.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideChannelDao(database: TvoraDatabase): ChannelDao = database.channelDao()

    @Provides
    fun provideProgramDao(database: TvoraDatabase): ProgramDao = database.programDao()

    @Provides
    fun provideReminderDao(database: TvoraDatabase): ReminderDao = database.reminderDao()

    @Provides
    fun provideSyncMetadataDao(database: TvoraDatabase): SyncMetadataDao = database.syncMetadataDao()
}
