package com.example.circlechord.ui.settings

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.circlechord.data.model.AppTheme
import com.example.circlechord.ui.CircleChordViewModel
import com.example.circlechord.util.HapticHelper
import java.math.RoundingMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: CircleChordViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val view = LocalView.current

    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var showResetSettingsDialog by remember { mutableStateOf(false) }

    Scaffold(modifier = modifier) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Customize calculator precision, display, and behavior",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            // APPEARANCE
            SectionHeader(title = "APPEARANCE")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Theme",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    AppTheme.entries.forEach { theme ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    HapticHelper.performLightHaptic(view, settings.hapticFeedback)
                                    viewModel.updateTheme(theme)
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = settings.theme == theme,
                                onClick = {
                                    HapticHelper.performLightHaptic(view, settings.hapticFeedback)
                                    viewModel.updateTheme(theme)
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = theme.displayName,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))

                    SwitchRow(
                        title = "Dynamic Color",
                        subtitle = "Use Material You theme colors on supported devices",
                        checked = settings.dynamicColor,
                        onCheckedChange = {
                            HapticHelper.performLightHaptic(view, settings.hapticFeedback)
                            viewModel.updateDynamicColor(it)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // BEHAVIOR
            SectionHeader(title = "BEHAVIOR")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SwitchRow(
                        title = "Haptic Feedback",
                        subtitle = "Vibrate on button taps and calculations",
                        checked = settings.hapticFeedback,
                        onCheckedChange = { viewModel.updateHapticFeedback(it) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))
                    SwitchRow(
                        title = "Animations",
                        subtitle = "Smooth transitions and diagram animations",
                        checked = settings.animations,
                        onCheckedChange = {
                            HapticHelper.performLightHaptic(view, settings.hapticFeedback)
                            viewModel.updateAnimations(it)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // NUMBER DISPLAY
            SectionHeader(title = "NUMBER DISPLAY")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Decimal places dropdown
                    val decimalOptions = listOf(0, 1, 2, 3, 4, 6, 8, 10, 12)
                    var decimalExpanded by remember { mutableStateOf(false) }

                    Text(
                        text = "Decimal Places",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    ExposedDropdownMenuBox(
                        expanded = decimalExpanded,
                        onExpandedChange = { decimalExpanded = !decimalExpanded }
                    ) {
                        OutlinedTextField(
                            value = "${settings.decimalPlaces} decimal places",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = decimalExpanded) },
                            modifier = Modifier
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = decimalExpanded,
                            onDismissRequest = { decimalExpanded = false }
                        ) {
                            decimalOptions.forEach { places ->
                                DropdownMenuItem(
                                    text = { Text("$places decimal places") },
                                    onClick = {
                                        HapticHelper.performLightHaptic(view, settings.hapticFeedback)
                                        viewModel.updateDecimalPlaces(places)
                                        decimalExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(14.dp))

                    // Rounding mode dropdown
                    val roundingModes = listOf(
                        RoundingMode.HALF_UP to "Half Up",
                        RoundingMode.HALF_DOWN to "Half Down",
                        RoundingMode.HALF_EVEN to "Half Even",
                        RoundingMode.UP to "Up",
                        RoundingMode.DOWN to "Down",
                        RoundingMode.CEILING to "Ceiling",
                        RoundingMode.FLOOR to "Floor"
                    )
                    var roundingExpanded by remember { mutableStateOf(false) }

                    Text(
                        text = "Rounding Mode",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    ExposedDropdownMenuBox(
                        expanded = roundingExpanded,
                        onExpandedChange = { roundingExpanded = !roundingExpanded }
                    ) {
                        val currentName = roundingModes.firstOrNull { it.first == settings.roundingMode }?.second ?: "Half Up"
                        OutlinedTextField(
                            value = currentName,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roundingExpanded) },
                            modifier = Modifier
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = roundingExpanded,
                            onDismissRequest = { roundingExpanded = false }
                        ) {
                            roundingModes.forEach { (mode, label) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        HapticHelper.performLightHaptic(view, settings.hapticFeedback)
                                        viewModel.updateRoundingMode(mode)
                                        roundingExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // DISPLAY
            SectionHeader(title = "DISPLAY")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SwitchRow(
                        title = "Show Formula",
                        subtitle = "Display chord formula and variable explanations",
                        checked = settings.showFormula,
                        onCheckedChange = {
                            HapticHelper.performLightHaptic(view, settings.hapticFeedback)
                            viewModel.updateShowFormula(it)
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))
                    SwitchRow(
                        title = "Show Calculation Steps",
                        subtitle = "Step-by-step mathematical substitution",
                        checked = settings.showCalculationSteps,
                        onCheckedChange = {
                            HapticHelper.performLightHaptic(view, settings.hapticFeedback)
                            viewModel.updateShowCalculationSteps(it)
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))
                    SwitchRow(
                        title = "Show Circle Diagram",
                        subtitle = "Vector geometric visualizer",
                        checked = settings.showDiagram,
                        onCheckedChange = {
                            HapticHelper.performLightHaptic(view, settings.hapticFeedback)
                            viewModel.updateShowDiagram(it)
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))
                    SwitchRow(
                        title = "Show More Circle Details",
                        subtitle = "Expand diameter, arc length, sector area, and sagitta by default",
                        checked = settings.showMoreDetails,
                        onCheckedChange = {
                            HapticHelper.performLightHaptic(view, settings.hapticFeedback)
                            viewModel.updateShowMoreDetails(it)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // HISTORY & RESET
            SectionHeader(title = "HISTORY & DATA")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SwitchRow(
                        title = "Confirm Delete",
                        subtitle = "Prompt before deleting saved calculations",
                        checked = settings.confirmDelete,
                        onCheckedChange = {
                            HapticHelper.performLightHaptic(view, settings.hapticFeedback)
                            viewModel.updateConfirmDelete(it)
                        }
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                HapticHelper.performLightHaptic(view, settings.hapticFeedback)
                                showClearHistoryDialog = true
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Clear History")
                        }

                        OutlinedButton(
                            onClick = {
                                HapticHelper.performLightHaptic(view, settings.hapticFeedback)
                                showResetSettingsDialog = true
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Reset Settings")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Clear History Dialog
        if (showClearHistoryDialog) {
            AlertDialog(
                onDismissRequest = { showClearHistoryDialog = false },
                title = { Text("Clear History") },
                text = { Text("Clear all saved chord calculations?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.clearHistory()
                            showClearHistoryDialog = false
                            Toast.makeText(context, "History cleared", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("Clear", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearHistoryDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Reset Settings Dialog
        if (showResetSettingsDialog) {
            AlertDialog(
                onDismissRequest = { showResetSettingsDialog = false },
                title = { Text("Reset Settings") },
                text = { Text("Reset all settings to default values? Your saved history will be preserved.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.resetSettings()
                            showResetSettingsDialog = false
                            Toast.makeText(context, "Settings reset to defaults", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("Reset")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetSettingsDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun SwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
