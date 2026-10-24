package com.mohamedaminelouati.tvora.ui.settings.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mohamedaminelouati.tvora.R
import com.mohamedaminelouati.tvora.util.ColorUtils

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomColorDialog(
    initialColorHex: String,
    onDismiss: () -> Unit,
    onColorSelected: (String) -> Unit
) {
    val initialColor = remember(initialColorHex) {
        ColorUtils.parseHexColor(initialColorHex)
    }
    var red by remember { mutableStateOf((initialColor.red * 255).toInt().coerceIn(0, 255)) }
    var green by remember { mutableStateOf((initialColor.green * 255).toInt().coerceIn(0, 255)) }
    var blue by remember { mutableStateOf((initialColor.blue * 255).toInt().coerceIn(0, 255)) }
    var hexText by remember { mutableStateOf(ColorUtils.toHex(initialColor)) }

    val currentColor = remember(red, green, blue) {
        Color(android.graphics.Color.rgb(red, green, blue))
    }
    val variantColor = remember(currentColor) {
        ColorUtils.computeVariant(currentColor)
    }

    val samplePalette = remember {
        listOf(
            "#E50914", "#E91E63", "#9C27B0", "#673AB7",
            "#3F51B5", "#2196F3", "#00BCD4", "#009688",
            "#4CAF50", "#8BC34A", "#FF9800", "#FF5722"
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.settings_custom_color_title),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.preview_label),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(currentColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Accent",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(variantColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Variant",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    samplePalette.forEach { sampleHex ->
                        val sampleColor = remember(sampleHex) { ColorUtils.parseHexColor(sampleHex) }
                        val isPicked = sampleHex.equals(hexText, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(sampleColor)
                                .then(
                                    if (isPicked) {
                                        Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                    } else {
                                        Modifier.border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f), CircleShape)
                                    }
                                )
                                .clickable {
                                    red = (sampleColor.red * 255).toInt().coerceIn(0, 255)
                                    green = (sampleColor.green * 255).toInt().coerceIn(0, 255)
                                    blue = (sampleColor.blue * 255).toInt().coerceIn(0, 255)
                                    hexText = sampleHex
                                }
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    ColorSliderRow(
                        label = "R",
                        value = red,
                        activeColor = Color(0xFFE53935),
                        onValueChange = {
                            red = it
                            hexText = ColorUtils.toHex(Color(android.graphics.Color.rgb(red, green, blue)))
                        }
                    )
                    ColorSliderRow(
                        label = "G",
                        value = green,
                        activeColor = Color(0xFF43A047),
                        onValueChange = {
                            green = it
                            hexText = ColorUtils.toHex(Color(android.graphics.Color.rgb(red, green, blue)))
                        }
                    )
                    ColorSliderRow(
                        label = "B",
                        value = blue,
                        activeColor = Color(0xFF1E88E5),
                        onValueChange = {
                            blue = it
                            hexText = ColorUtils.toHex(Color(android.graphics.Color.rgb(red, green, blue)))
                        }
                    )
                }

                OutlinedTextField(
                    value = hexText,
                    onValueChange = { input ->
                        hexText = input
                        val parsed = ColorUtils.parseHexColor(input, fallback = Color.Unspecified)
                        if (parsed != Color.Unspecified) {
                            red = (parsed.red * 255).toInt().coerceIn(0, 255)
                            green = (parsed.green * 255).toInt().coerceIn(0, 255)
                            blue = (parsed.blue * 255).toInt().coerceIn(0, 255)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = stringResource(R.string.hex_color_hint)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = currentColor,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onColorSelected(ColorUtils.toHex(currentColor)) },
                colors = ButtonDefaults.buttonColors(containerColor = currentColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = stringResource(R.string.action_confirm), color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.action_cancel))
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
private fun ColorSliderRow(
    label: String,
    value: Int,
    activeColor: Color,
    onValueChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = activeColor,
            modifier = Modifier.width(16.dp)
        )
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt().coerceIn(0, 255)) },
            valueRange = 0f..255f,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
                thumbColor = activeColor,
                activeTrackColor = activeColor,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
        Text(
            text = value.toString().padStart(3, ' '),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(30.dp)
        )
    }
}
