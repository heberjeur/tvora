package com.mohamedaminelouati.tvora.ui.detail.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mohamedaminelouati.tvora.R
import com.mohamedaminelouati.tvora.domain.model.Program
import com.mohamedaminelouati.tvora.domain.model.Reminder
import com.mohamedaminelouati.tvora.ui.theme.AccentRed
import com.mohamedaminelouati.tvora.ui.theme.DarkSurface
import com.mohamedaminelouati.tvora.ui.theme.DarkSurfaceVariant
import com.mohamedaminelouati.tvora.ui.theme.TextPrimary
import com.mohamedaminelouati.tvora.ui.theme.TextSecondary
import java.util.Calendar

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReminderScheduleSection(
    program: Program,
    reminder: Reminder?,
    defaultLeadTime: Int,
    defaultRecurrence: String,
    onToggleReminder: (Int?, String?) -> Unit,
    onCustomDateTimeReminder: (Long, String) -> Unit,
    createCalendarIntent: (Program) -> Intent,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showLeadTimeOptions by remember { mutableStateOf(false) }
    var selectedLeadTime by remember(defaultLeadTime) { mutableIntStateOf(defaultLeadTime) }
    var selectedRecurrence by remember(defaultRecurrence) { mutableStateOf(defaultRecurrence) }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    if (reminder != null) {
                        onToggleReminder(null, null)
                    } else {
                        showLeadTimeOptions = !showLeadTimeOptions
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (reminder != null) DarkSurfaceVariant else AccentRed
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = if (reminder != null) Icons.Default.AlarmOn else Icons.Default.Alarm,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                val reminderButtonText = if (reminder != null) {
                    val recurrenceText = when (reminder.recurrence) {
                        "daily" -> " (${stringResource(R.string.reminder_recurrence_daily)})"
                        "weekly" -> " (${stringResource(R.string.reminder_recurrence_weekly)})"
                        "monthly" -> " (${stringResource(R.string.reminder_recurrence_monthly)})"
                        else -> ""
                    }
                    stringResource(R.string.reminder_active) + recurrenceText
                } else {
                    stringResource(R.string.reminder_set)
                }
                Text(text = reminderButtonText)
            }

            OutlinedButton(
                onClick = {
                    val intent = createCalendarIntent(program)
                    try {
                        context.startActivity(intent)
                    } catch (_: ActivityNotFoundException) {
                        val fallbackIntent = Intent(Intent.ACTION_EDIT).apply {
                            setDataAndType(CalendarContract.Events.CONTENT_URI, "vnd.android.cursor.item/event")
                            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, program.startTimestamp)
                            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, program.stopTimestamp)
                            putExtra(CalendarContract.Events.TITLE, program.title)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        try {
                            context.startActivity(fallbackIntent)
                        } catch (_: ActivityNotFoundException) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.no_calendar_app_installed),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = TextPrimary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.action_external_calendar),
                    color = TextPrimary
                )
            }
        }

        if (showLeadTimeOptions && reminder == null) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = stringResource(R.string.settings_reminder_lead_time),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(0, 30, 60).forEach { mins ->
                            val isChosen = selectedLeadTime == mins
                            SuggestionChip(
                                onClick = { selectedLeadTime = mins },
                                label = {
                                    Text(
                                        text = if (mins == 0) {
                                            stringResource(R.string.reminder_exact_time)
                                        } else {
                                            stringResource(R.string.minutes_short, mins)
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isChosen) AccentRed else TextPrimary
                                    )
                                },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (isChosen) DarkSurfaceVariant else Color.Transparent
                                ),
                                border = null
                            )
                        }

                        SuggestionChip(
                            onClick = {
                                val cal = Calendar.getInstance().apply {
                                    timeInMillis = program.startTimestamp
                                }
                                DatePickerDialog(
                                    context,
                                    { _, year, month, day ->
                                        TimePickerDialog(
                                            context,
                                            { _, hour, minute ->
                                                val picked = Calendar.getInstance().apply {
                                                    set(year, month, day, hour, minute, 0)
                                                }
                                                onCustomDateTimeReminder(picked.timeInMillis, selectedRecurrence)
                                                showLeadTimeOptions = false
                                            },
                                            cal.get(Calendar.HOUR_OF_DAY),
                                            cal.get(Calendar.MINUTE),
                                            true
                                        ).show()
                                    },
                                    cal.get(Calendar.YEAR),
                                    cal.get(Calendar.MONTH),
                                    cal.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            },
                            label = {
                                Text(
                                    text = stringResource(R.string.action_pick_datetime),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AccentRed
                                )
                            },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = DarkSurfaceVariant
                            ),
                            border = null
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.settings_reminder_recurrence),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "once" to stringResource(R.string.reminder_recurrence_once),
                            "daily" to stringResource(R.string.reminder_recurrence_daily),
                            "weekly" to stringResource(R.string.reminder_recurrence_weekly),
                            "monthly" to stringResource(R.string.reminder_recurrence_monthly)
                        ).forEach { (recurrenceKey, label) ->
                            val isChosen = selectedRecurrence == recurrenceKey
                            SuggestionChip(
                                onClick = { selectedRecurrence = recurrenceKey },
                                label = {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isChosen) AccentRed else TextPrimary
                                    )
                                },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (isChosen) DarkSurfaceVariant else Color.Transparent
                                ),
                                border = null
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            showLeadTimeOptions = false
                            onToggleReminder(selectedLeadTime, selectedRecurrence)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = stringResource(R.string.action_confirm), color = Color.White)
                    }
                }
            }
        }
    }
}
