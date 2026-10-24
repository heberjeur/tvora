package com.mohamedaminelouati.tvora.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mohamedaminelouati.tvora.data.local.entity.ChannelEntity
import com.mohamedaminelouati.tvora.data.local.entity.ProgramEntity
import com.mohamedaminelouati.tvora.data.local.entity.ProgramTranslationEntity
import com.mohamedaminelouati.tvora.data.local.entity.ReminderEntity
import com.mohamedaminelouati.tvora.data.local.entity.SyncMetadataEntity

@Database(
    entities = [
        ChannelEntity::class,
        ProgramEntity::class,
        ProgramTranslationEntity::class,
        ReminderEntity::class,
        SyncMetadataEntity::class
    ],
    version = 15,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TvoraDatabase : RoomDatabase() {
    abstract fun channelDao(): ChannelDao
    abstract fun programDao(): ProgramDao
    abstract fun programTranslationDao(): ProgramTranslationDao
    abstract fun reminderDao(): ReminderDao
    abstract fun syncMetadataDao(): SyncMetadataDao
}
