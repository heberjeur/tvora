package com.mohamedaminelouati.tvora.data.mapper

import com.mohamedaminelouati.tvora.data.local.entity.ReminderEntity
import com.mohamedaminelouati.tvora.domain.model.Reminder

fun ReminderEntity.toDomain(): Reminder {
    return Reminder(
        id = id,
        programId = programId,
        channelId = channelId,
        title = title,
        startTimestamp = startTimestamp,
        triggerTimestamp = triggerTimestamp,
        leadTimeMinutes = leadTimeMinutes,
        isCalendarSyncEnabled = isCalendarSyncEnabled,
        calendarEventId = calendarEventId,
        isNotificationEnabled = isNotificationEnabled,
        soundUri = soundUri,
        recurrence = recurrence
    )
}

fun Reminder.toEntity(): ReminderEntity {
    return ReminderEntity(
        id = id,
        programId = programId,
        channelId = channelId,
        title = title,
        startTimestamp = startTimestamp,
        triggerTimestamp = triggerTimestamp,
        leadTimeMinutes = leadTimeMinutes,
        isCalendarSyncEnabled = isCalendarSyncEnabled,
        calendarEventId = calendarEventId,
        isNotificationEnabled = isNotificationEnabled,
        soundUri = soundUri,
        recurrence = recurrence
    )
}
