package com.mohamedaminelouati.tvora.widget

import com.mohamedaminelouati.tvora.data.local.db.ProgramDao
import com.mohamedaminelouati.tvora.data.local.prefs.UserPreferences
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun programDao(): ProgramDao
    fun userPreferences(): UserPreferences
}
