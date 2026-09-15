package com.example.ui.calendar

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppThemeMode
import com.example.ui.clay.ClayButton
import com.example.ui.clay.ClayColors

private enum class SettingsSubScreen {
    MAIN,
    APPEARANCE,
    NOTIFICATIONS,
    PRIVACY,
    BACKUP_RESTORE
}

@Composable
fun SettingsScreen(
    currentThemeMode: AppThemeMode,
    activeAccent: String,
    eventsCount: Int,
    onThemeModeChange: (AppThemeMode) -> Unit,
    onAccentColorChange: (String) -> Unit,
    onExportData: () -> Unit,
    onImportData: () -> Unit,
    onClearAllData: () -> Unit,
    onOpenOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeSubScreen by remember { mutableStateOf(SettingsSubScreen.MAIN) }
    var defaultView by remember { mutableStateOf("Month") }
    var startWeekOn by remember { mutableStateOf("Monday") }
    var showClearConfirm by remember { mutableStateOf(false) }

    BackHandler(enabled = showClearConfirm || activeSubScreen != SettingsSubScreen.MAIN) {
        if (showClearConfirm) {
            showClearConfirm = false
        } else if (activeSubScreen != SettingsSubScreen.MAIN) {
            activeSubScreen = SettingsSubScreen.MAIN
        }
    }

    when (activeSubScreen) {
        SettingsSubScreen.MAIN -> {
            SettingsMainContent(
                currentThemeMode = currentThemeMode,
                defaultView = defaultView,
                startWeekOn = startWeekOn,
                onNavigateTo = { activeSubScreen = it },
                onToggleDefaultView = {
                    defaultView = if (defaultView == "Month") "Week" else if (defaultView == "Week") "Day" else "Month"
                },
                onToggleStartWeek = {
                    startWeekOn = if (startWeekOn == "Monday") "Sunday" else "Monday"
                },
                onOpenOnboarding = onOpenOnboarding,
                onPromptClearData = { showClearConfirm = true },
                modifier = modifier
            )
        }

        SettingsSubScreen.APPEARANCE -> {
            AppearanceSubScreen(
                currentThemeMode = currentThemeMode,
                activeAccent = activeAccent,
                onThemeModeChange = onThemeModeChange,
                onAccentColorChange = onAccentColorChange,
                onBack = { activeSubScreen = SettingsSubScreen.MAIN },
                modifier = modifier
            )
        }

        SettingsSubScreen.NOTIFICATIONS -> {
            NotificationsSubScreen(
                onBack = { activeSubScreen = SettingsSubScreen.MAIN },
                modifier = modifier
            )
        }

        SettingsSubScreen.PRIVACY -> {
            PrivacySubScreen(
                onBack = { activeSubScreen = SettingsSubScreen.MAIN },
                modifier = modifier
            )
        }

        SettingsSubScreen.BACKUP_RESTORE -> {
            BackupRestoreSubScreen(
                onExport = onExportData,
                onImport = onImportData,
                onBack = { activeSubScreen = SettingsSubScreen.MAIN },
                modifier = modifier
            )
        }
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Clear all events?", color = ClayColors.TextPrimary) },
            text = { Text("This will permanently remove all $eventsCount events stored on your device.", color = ClayColors.TextSecondary) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearAllData()
                        showClearConfirm = false
                    }
                ) {
                    Text("Delete All", color = ClayColors.DangerRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Cancel", color = ClayColors.TextSecondary)
                }
            },
            containerColor = ClayColors.SurfaceMarshmallow
        )
    }
}

@Composable
private fun SettingsMainContent(
    currentThemeMode: AppThemeMode,
    defaultView: String,
    startWeekOn: String,
    onNavigateTo: (SettingsSubScreen) -> Unit,
    onToggleDefaultView: () -> Unit,
    onToggleStartWeek: () -> Unit,
    onOpenOnboarding: () -> Unit,
    onPromptClearData: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ClayColors.Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Settings",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = ClayColors.TextPrimary
        )

        // Preferences Group (Screen 16)
        SettingsGroupCard {
            SettingsRow(
                icon = Icons.Default.Palette,
                title = "Appearance",
                value = when (currentThemeMode) {
                    AppThemeMode.LIGHT -> "Light"
                    AppThemeMode.DARK -> "Dark"
                    AppThemeMode.SYSTEM -> "System"
                },
                onClick = { onNavigateTo(SettingsSubScreen.APPEARANCE) }
            )
            SettingsDivider()
            SettingsRow(
                icon = Icons.Default.ViewAgenda,
                title = "Default view",
                value = defaultView,
                onClick = onToggleDefaultView
            )
            SettingsDivider()
            SettingsRow(
                icon = Icons.Default.ViewAgenda,
                title = "Start week on",
                value = startWeekOn,
                onClick = onToggleStartWeek
            )
        }

        // Privacy & Data Group
        SettingsGroupCard {
            SettingsRow(
                icon = Icons.Default.Notifications,
                title = "Notifications",
                onClick = { onNavigateTo(SettingsSubScreen.NOTIFICATIONS) }
            )
            SettingsDivider()
            SettingsRow(
                icon = Icons.Default.Security,
                title = "Privacy",
                subtitle = "Keep your data local",
                onClick = { onNavigateTo(SettingsSubScreen.PRIVACY) }
            )
            SettingsDivider()
            SettingsRow(
                icon = Icons.Default.Storage,
                title = "Backup & Restore",
                subtitle = "Export or import your data",
                onClick = { onNavigateTo(SettingsSubScreen.BACKUP_RESTORE) }
            )
        }

        // About & Tour Group
        SettingsGroupCard {
            SettingsRow(
                icon = Icons.Default.Visibility,
                title = "Welcome Tour",
                value = "View guide",
                onClick = onOpenOnboarding
            )
            SettingsDivider()
            SettingsRow(
                icon = Icons.Default.Storage,
                title = "Clear Database",
                value = "Reset",
                onClick = onPromptClearData
            )
            SettingsDivider()
            SettingsRow(
                icon = Icons.Default.Security,
                title = "About",
                value = "ClayCal v${com.example.BuildConfig.VERSION_NAME}",
                onClick = {}
            )
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun AppearanceSubScreen(
    currentThemeMode: AppThemeMode,
    activeAccent: String,
    onThemeModeChange: (AppThemeMode) -> Unit,
    onAccentColorChange: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ClayColors.Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = ClayColors.TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Appearance",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = ClayColors.TextPrimary
            )
        }

        // Theme Options (Screen 20)
        SettingsGroupCard {
            ThemeOptionRow(
                title = "Light",
                isSelected = currentThemeMode == AppThemeMode.LIGHT,
                onClick = { onThemeModeChange(AppThemeMode.LIGHT) }
            )
            SettingsDivider()
            ThemeOptionRow(
                title = "Dark",
                isSelected = currentThemeMode == AppThemeMode.DARK,
                onClick = { onThemeModeChange(AppThemeMode.DARK) }
            )
            SettingsDivider()
            ThemeOptionRow(
                title = "System",
                isSelected = currentThemeMode == AppThemeMode.SYSTEM,
                onClick = { onThemeModeChange(AppThemeMode.SYSTEM) }
            )
        }

        Text(
            text = "Accent color",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = ClayColors.TextSecondary,
            modifier = Modifier.padding(start = 4.dp, top = 8.dp)
        )

        val accentOptions = listOf(
            "Sage" to ClayColors.ClaySage,
            "Terracotta" to ClayColors.ClayTerracotta,
            "Peach" to ClayColors.ClayPeach,
            "Blue" to ClayColors.ClaySoftBlue,
            "Lavender" to ClayColors.ClayLavender,
            "Mint" to ClayColors.ClayMint
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            accentOptions.forEach { (name, color) ->
                val isSelected = activeAccent.equals(name, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .shadow(
                            elevation = if (isSelected) 4.dp else 1.dp,
                            shape = CircleShape
                        )
                        .background(color, CircleShape)
                        .border(
                            width = if (isSelected) 3.dp else 1.dp,
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                        .clickable { onAccentColorChange(name) },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationsSubScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var eventReminders by remember { mutableStateOf(true) }
    var allDayReminders by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ClayColors.Background)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = ClayColors.TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Notifications",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = ClayColors.TextPrimary
            )
        }

        // Screen 19 layout
        SettingsGroupCard {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Event reminders",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = ClayColors.TextPrimary
                )
                Switch(
                    checked = eventReminders,
                    onCheckedChange = { eventReminders = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = ClayColors.PrimaryAccent
                    )
                )
            }
            SettingsDivider()
            SettingsRow(
                icon = Icons.Default.Notifications,
                title = "Default reminder time",
                value = "10 min before",
                onClick = {}
            )
            SettingsDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "All day reminders",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = ClayColors.TextPrimary
                )
                Switch(
                    checked = allDayReminders,
                    onCheckedChange = { allDayReminders = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = ClayColors.PrimaryAccent
                    )
                )
            }
        }
    }
}

@Composable
private fun PrivacySubScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ClayColors.Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = ClayColors.TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Privacy",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = ClayColors.TextPrimary
            )
        }

        // Screen 18 Layout: 4 items
        SettingsGroupCard {
            PrivacyItem(
                icon = Icons.Default.Lock,
                title = "No Accounts",
                subtitle = "Use ClayCal without signing up or sharing personal information."
            )
            SettingsDivider()
            PrivacyItem(
                icon = Icons.Default.Storage,
                title = "Local Storage Only",
                subtitle = "Your events are stored on your device, not in the cloud."
            )
            SettingsDivider()
            PrivacyItem(
                icon = Icons.Default.VisibilityOff,
                title = "No Tracking",
                subtitle = "We don't collect analytics or track your behavior."
            )
            SettingsDivider()
            PrivacyItem(
                icon = Icons.Default.Security,
                title = "Open Source",
                subtitle = "Transparency you can trust."
            )
        }
    }
}

@Composable
private fun BackupRestoreSubScreen(
    onExport: () -> Unit,
    onImport: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ClayColors.Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = ClayColors.TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Backup & Restore",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = ClayColors.TextPrimary
            )
        }

        // Screen 17 Layout
        SettingsGroupCard {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = null,
                        tint = ClayColors.TextPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Export Backup",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClayColors.TextPrimary
                        )
                        Text(
                            text = "Save a copy of your data to your device.",
                            fontSize = 13.sp,
                            color = ClayColors.TextSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                ClayButton(
                    onClick = onExport,
                    containerColor = ClayColors.PrimaryAccent,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Export Backup",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        SettingsGroupCard {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CloudDownload,
                        contentDescription = null,
                        tint = ClayColors.TextPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Import Backup",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClayColors.TextPrimary
                        )
                        Text(
                            text = "Restore your data from a file.",
                            fontSize = 13.sp,
                            color = ClayColors.TextSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                ClayButton(
                    onClick = onImport,
                    containerColor = ClayColors.PrimaryAccent,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Import Backup",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Text(
            text = "Backups are stored locally on your device. You're always in control.",
            fontSize = 13.sp,
            color = ClayColors.TextTertiary,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

@Composable
private fun SettingsGroupCard(
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = ClayColors.ShadowAmbient,
                spotColor = ClayColors.ShadowSpot
            )
            .background(
                color = ClayColors.SurfaceMarshmallow,
                shape = RoundedCornerShape(22.dp)
            )
            .border(
                width = 1.2.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.9f),
                        ClayColors.ShadowBevel.copy(alpha = 0.35f)
                    )
                ),
                shape = RoundedCornerShape(22.dp)
            )
            .clip(RoundedCornerShape(22.dp))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            content()
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    value: String = "",
    subtitle: String = "",
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ClayColors.TextSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = ClayColors.TextPrimary
                )
                if (subtitle.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = ClayColors.TextTertiary,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (value.isNotEmpty()) {
                Text(
                    text = value,
                    fontSize = 13.sp,
                    color = ClayColors.TextTertiary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = ClayColors.TextTertiary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun ThemeOptionRow(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = ClayColors.TextPrimary
        )

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = ClayColors.PrimaryAccent,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun PrivacyItem(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 15.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ClayColors.PrimaryAccent,
            modifier = Modifier
                .size(22.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ClayColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = ClayColors.TextSecondary,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(ClayColors.ShadowBevel.copy(alpha = 0.35f))
    )
}
