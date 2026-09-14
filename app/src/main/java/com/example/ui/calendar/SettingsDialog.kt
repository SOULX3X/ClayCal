package com.example.ui.calendar

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.AppThemeMode
import com.example.ui.clay.ClayButton
import com.example.ui.clay.ClayCard
import com.example.ui.clay.ClayColors
import com.example.ui.clay.ClayIconButton
import com.example.ui.clay.ClayPill
import com.example.ui.clay.ClayTextField

@Composable
fun SettingsDialog(
    currentThemeMode: AppThemeMode,
    activeAccent: String,
    eventsCount: Int,
    onThemeModeChange: (AppThemeMode) -> Unit,
    onAccentChange: (String) -> Unit,
    onExportJson: () -> String,
    onImportJson: (json: String, replace: Boolean) -> Result<Int>,
    onDeleteAllData: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var isImportExpanded by remember { mutableStateOf(false) }
    var importJsonText by remember { mutableStateOf("") }
    var importReplaceExisting by remember { mutableStateOf(false) }
    var importErrorMessage by remember { mutableStateOf<String?>(null) }

    // File picker for JSON import
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val content = inputStream?.bufferedReader()?.use { reader -> reader.readText() }
                if (!content.isNullOrBlank()) {
                    importJsonText = content
                    isImportExpanded = true
                    importErrorMessage = null
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error reading file: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 560.dp)
                .padding(vertical = 12.dp)
        ) {
            ClayCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.94f)
                    .testTag("settings_dialog_card"),
                shape = RoundedCornerShape(28.dp),
                surfaceColor = ClayColors.SurfaceMarshmallow,
                elevation = 12.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    // Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .shadow(
                                        elevation = 4.dp,
                                        shape = RoundedCornerShape(12.dp),
                                        ambientColor = ClayColors.PrimaryAccent.copy(alpha = 0.35f),
                                        spotColor = ClayColors.PrimaryAccent.copy(alpha = 0.4f)
                                    )
                                    .background(ClayColors.PrimaryAccent, shape = RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "⚙️", fontSize = 18.sp)
                            }

                            Column {
                                Text(
                                    text = "Settings",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ClayColors.TextPrimary
                                )
                                Text(
                                    text = "Themes, Data & Backup",
                                    fontSize = 12.sp,
                                    color = ClayColors.TextTertiary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        ClayIconButton(
                            onClick = onDismiss,
                            icon = Icons.Default.Close,
                            contentDescription = "Close settings",
                            containerColor = ClayColors.SurfaceSoftClay,
                            iconTint = ClayColors.TextPrimary,
                            size = 36.dp,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // 1. Theme Mode Section
                    SectionHeader(title = "Appearance & Theme Mode", icon = "🎨")

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeModeCard(
                            title = "Light",
                            isSelected = currentThemeMode == AppThemeMode.LIGHT,
                            icon = Icons.Default.LightMode,
                            onClick = { onThemeModeChange(AppThemeMode.LIGHT) },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeModeCard(
                            title = "Dark",
                            isSelected = currentThemeMode == AppThemeMode.DARK,
                            icon = Icons.Default.DarkMode,
                            onClick = { onThemeModeChange(AppThemeMode.DARK) },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeModeCard(
                            title = "System",
                            isSelected = currentThemeMode == AppThemeMode.SYSTEM,
                            icon = Icons.Default.SettingsBrightness,
                            onClick = { onThemeModeChange(AppThemeMode.SYSTEM) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Theme Accent Colors
                    Text(
                        text = "Accent Palette",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClayColors.TextSecondary
                    )

                    val palettes = listOf(
                        "Terracotta" to ClayColors.ClayTerracotta,
                        "Lavender" to ClayColors.ClayLavender,
                        "Mint" to ClayColors.ClayMint,
                        "Peach" to ClayColors.ClayPeach,
                        "Indigo" to ClayColors.ClayIndigo
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        palettes.forEach { (name, color) ->
                            val isSelected = activeAccent.equals(name, ignoreCase = true)
                            AccentColorBubble(
                                name = name,
                                color = color,
                                isSelected = isSelected,
                                onClick = { onAccentChange(name) }
                            )
                        }
                    }

                    // Live Theme Preview Box
                    ClayCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        surfaceColor = ClayColors.SurfaceSoftClay,
                        elevation = 3.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .background(ClayColors.PrimaryAccent, shape = CircleShape)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Tactile Preview: $activeAccent Accent",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ClayColors.TextPrimary
                                )
                                Text(
                                    text = if (ClayColors.isDark) "Active Dark Mode Slate" else "Active Warm Porcelain Light Mode",
                                    fontSize = 11.sp,
                                    color = ClayColors.TextTertiary
                                )
                            }
                            ClayPill(
                                text = "Preview",
                                isSelected = true,
                                selectedColor = ClayColors.PrimaryAccent,
                                onClick = {}
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // 2. Data Management (Export & Import)
                    SectionHeader(title = "Calendar Data & Backup", icon = "💾")

                    // Export Card
                    ClayCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        surfaceColor = ClayColors.SurfaceSoftClay,
                        elevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Export Calendar",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ClayColors.TextPrimary
                                    )
                                    Text(
                                        text = "$eventsCount total events stored",
                                        fontSize = 12.sp,
                                        color = ClayColors.TextTertiary
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ClayButton(
                                    onClick = {
                                        val json = onExportJson()
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, json)
                                            type = "text/plain"
                                        }
                                        val shareIntent = Intent.createChooser(sendIntent, "Export Claycal Backup")
                                        context.startActivity(shareIntent)
                                    },
                                    containerColor = ClayColors.PrimaryAccent,
                                    shape = RoundedCornerShape(14.dp),
                                    elevation = 4.dp,
                                    modifier = Modifier.weight(1f).height(42.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Share JSON", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }

                                ClayButton(
                                    onClick = {
                                        val json = onExportJson()
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Claycal Backup", json)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "JSON copied to clipboard!", Toast.LENGTH_SHORT).show()
                                    },
                                    containerColor = ClayColors.SurfaceMarshmallow,
                                    contentColor = ClayColors.TextPrimary,
                                    shape = RoundedCornerShape(14.dp),
                                    elevation = 4.dp,
                                    modifier = Modifier.weight(1f).height(42.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = null,
                                        tint = ClayColors.TextPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Copy JSON", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Import Card
                    ClayCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        surfaceColor = ClayColors.SurfaceSoftClay,
                        elevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Import Calendar",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ClayColors.TextPrimary
                                    )
                                    Text(
                                        text = "Restore from JSON backup or file",
                                        fontSize = 12.sp,
                                        color = ClayColors.TextTertiary
                                    )
                                }

                                ClayButton(
                                    onClick = { isImportExpanded = !isImportExpanded },
                                    containerColor = if (isImportExpanded) ClayColors.PrimaryAccent else ClayColors.SurfaceMarshmallow,
                                    contentColor = if (isImportExpanded) Color.White else ClayColors.TextPrimary,
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = 3.dp,
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isImportExpanded) Icons.Default.Close else Icons.Default.Upload,
                                        contentDescription = null,
                                        tint = if (isImportExpanded) Color.White else ClayColors.TextPrimary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isImportExpanded) "Cancel" else "Import",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            AnimatedVisibility(
                                visible = isImportExpanded,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    ClayButton(
                                        onClick = { filePickerLauncher.launch("*/*") },
                                        containerColor = ClayColors.SurfaceMarshmallow,
                                        contentColor = ClayColors.TextPrimary,
                                        shape = RoundedCornerShape(12.dp),
                                        elevation = 2.dp,
                                        modifier = Modifier.fillMaxWidth().height(38.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Download,
                                            contentDescription = null,
                                            tint = ClayColors.PrimaryAccent,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Pick .json File From Storage", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }

                                    ClayTextField(
                                        value = importJsonText,
                                        onValueChange = {
                                            importJsonText = it
                                            importErrorMessage = null
                                        },
                                        placeholder = "Or paste JSON backup string here...",
                                        singleLine = false,
                                        maxLines = 6,
                                        minHeight = 80.dp,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    importErrorMessage?.let { err ->
                                        Text(
                                            text = "⚠️ $err",
                                            fontSize = 12.sp,
                                            color = ClayColors.DangerRed,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    // Replace or Merge Choice
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = !importReplaceExisting,
                                            onClick = { importReplaceExisting = false },
                                            colors = RadioButtonDefaults.colors(selectedColor = ClayColors.PrimaryAccent)
                                        )
                                        Text(
                                            text = "Merge (Keep existing)",
                                            fontSize = 12.sp,
                                            color = ClayColors.TextPrimary,
                                            modifier = Modifier.clickable { importReplaceExisting = false }
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        RadioButton(
                                            selected = importReplaceExisting,
                                            onClick = { importReplaceExisting = true },
                                            colors = RadioButtonDefaults.colors(selectedColor = ClayColors.DangerRed)
                                        )
                                        Text(
                                            text = "Replace all",
                                            fontSize = 12.sp,
                                            color = ClayColors.TextPrimary,
                                            modifier = Modifier.clickable { importReplaceExisting = true }
                                        )
                                    }

                                    ClayButton(
                                        onClick = {
                                            if (importJsonText.isBlank()) {
                                                importErrorMessage = "Please paste JSON or choose a file first."
                                                return@ClayButton
                                            }
                                            val res = onImportJson(importJsonText, importReplaceExisting)
                                            res.onSuccess { count ->
                                                Toast.makeText(context, "Imported $count events successfully!", Toast.LENGTH_SHORT).show()
                                                importJsonText = ""
                                                isImportExpanded = false
                                                importErrorMessage = null
                                            }.onFailure { ex ->
                                                importErrorMessage = ex.localizedMessage ?: "Invalid JSON format."
                                            }
                                        },
                                        containerColor = ClayColors.PrimaryAccent,
                                        shape = RoundedCornerShape(14.dp),
                                        elevation = 4.dp,
                                        modifier = Modifier.fillMaxWidth().height(42.dp)
                                    ) {
                                        Text("Confirm & Load Events", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // 3. Danger Zone (At the very bottom as requested)
                    ClayCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.5.dp,
                                color = ClayColors.DangerBorder,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .testTag("danger_zone_card"),
                        shape = RoundedCornerShape(20.dp),
                        surfaceColor = ClayColors.DangerBg,
                        elevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WarningAmber,
                                    contentDescription = null,
                                    tint = ClayColors.DangerRed,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Danger Zone",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ClayColors.DangerRed
                                )
                            }

                            Text(
                                text = "Permanently removes all events, schedules, and custom items stored in Claycal. This action cannot be undone.",
                                fontSize = 12.sp,
                                color = ClayColors.TextSecondary,
                                lineHeight = 16.sp
                            )

                            ClayButton(
                                onClick = { showDeleteConfirmDialog = true },
                                containerColor = ClayColors.DangerRed,
                                contentColor = Color.White,
                                shape = RoundedCornerShape(14.dp),
                                elevation = 6.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("delete_all_data_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteForever,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Remove All Calendar Data",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Confirmation Alert Dialog for Danger Zone
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "⚠️", fontSize = 20.sp)
                    Text(text = "Delete Everything?", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text(
                    text = "Are you sure you want to erase all calendar events? All $eventsCount saved items will be deleted permanently.",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                ClayButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDeleteAllData()
                        Toast.makeText(context, "All calendar data erased", Toast.LENGTH_SHORT).show()
                    },
                    containerColor = ClayColors.DangerRed,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    elevation = 4.dp,
                    modifier = Modifier.height(38.dp)
                ) {
                    Text("Yes, Delete All", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                ClayButton(
                    onClick = { showDeleteConfirmDialog = false },
                    containerColor = ClayColors.SurfaceSoftClay,
                    contentColor = ClayColors.TextPrimary,
                    shape = RoundedCornerShape(12.dp),
                    elevation = 2.dp,
                    modifier = Modifier.height(38.dp)
                ) {
                    Text("Cancel", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = ClayColors.SurfaceMarshmallow,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun SectionHeader(title: String, icon: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(text = icon, fontSize = 16.sp)
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = ClayColors.TextPrimary
        )
    }
}

@Composable
private fun ThemeModeCard(
    title: String,
    isSelected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isSelected) ClayColors.PrimaryAccent else ClayColors.SurfaceSoftClay
    val textColor = if (isSelected) Color.White else ClayColors.TextPrimary
    val iconColor = if (isSelected) Color.White else ClayColors.TextSecondary

    ClayCard(
        modifier = modifier
            .height(68.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        surfaceColor = containerColor,
        elevation = if (isSelected) 6.dp else 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = textColor
            )
        }
    }
}

@Composable
private fun AccentColorBubble(
    name: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .shadow(
                    elevation = if (isSelected) 6.dp else 2.dp,
                    shape = CircleShape,
                    ambientColor = color.copy(alpha = 0.4f),
                    spotColor = color.copy(alpha = 0.5f)
                )
                .background(color, shape = CircleShape)
                .border(
                    width = if (isSelected) 2.5.dp else 1.dp,
                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Text(
            text = name,
            fontSize = 11.sp,
            color = if (isSelected) ClayColors.TextPrimary else ClayColors.TextTertiary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
