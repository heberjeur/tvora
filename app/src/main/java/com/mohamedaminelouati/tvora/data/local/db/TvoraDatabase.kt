package com.mohamedaminelouati.tvora.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mohamedaminelouati.tvora.data.local.entity.ChannelEntity
import com.mohamedaminelouati.tvora.data.local.entity.KeywordAlertEntity
import com.mohamedaminelouati.tvora.data.local.entity.ProgramEntity
import com.mohamedaminelouati.tvora.data.local.entity.ReminderEntity
import com.mohamedaminelouati.tvora.data.local.entity.SyncMetadataEntity

@Database(
    entities = [
        ChannelEntity::class,
        ProgramEntity::class,
        ReminderEntity::class,
        SyncMetadataEntity::class,
        KeywordAlertEntity::class
    ],
    version = 16,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TvoraDatabase : RoomDatabase() {
    abstract fun channelDao(): ChannelDao
    abstract fun programDao(): ProgramDao
    abstract fun reminderDao(): ReminderDao
    abstract fun syncMetadataDao(): SyncMetadataDao
    abstract fun keywordAlertDao(): KeywordAlertDao
}
