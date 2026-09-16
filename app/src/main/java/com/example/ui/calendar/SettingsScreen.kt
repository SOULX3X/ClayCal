package com.example.ui.calendar

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material.icons.rounded.ViewAgenda
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.AppThemeMode
import com.example.ui.clay.ClayButton
import com.example.ui.clay.ClayCard
import com.example.ui.clay.ClayColors
import com.example.ui.clay.ClayPill
import com.example.ui.clay.clayBounceClickable
import com.example.ui.clay.clayMoulded
import com.example.util.ApkInstaller

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
    notificationsEnabled: Boolean = true,
    defaultReminderMinutes: Int = 15,
    onThemeModeChange: (AppThemeMode) -> Unit,
    onAccentColorChange: (String) -> Unit,
    onNotificationsEnabledChange: (Boolean) -> Unit = {},
    onDefaultReminderMinutesChange: (Int) -> Unit = {},
    onSendTestNotification: () -> Unit = {},
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
    var showUpdateDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val apkPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            if (ApkInstaller.canInstallApk(context)) {
                ApkInstaller.installApkFromUri(context, uri)
            } else {
                Toast.makeText(
                    context,
                    "Enable 'Allow from this source' for ClayCal to install updates directly",
                    Toast.LENGTH_LONG
                ).show()
                ApkInstaller.openInstallPermissionSettings(context)
            }
        }
    }

    BackHandler(enabled = showClearConfirm || showUpdateDialog || activeSubScreen != SettingsSubScreen.MAIN) {
        if (showClearConfirm) {
            showClearConfirm = false
        } else if (showUpdateDialog) {
            showUpdateDialog = false
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
                onPromptUpdate = { showUpdateDialog = true },
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
                notificationsEnabled = notificationsEnabled,
                defaultReminderMinutes = defaultReminderMinutes,
                onNotificationsEnabledChange = onNotificationsEnabledChange,
                onDefaultReminderMinutesChange = onDefaultReminderMinutesChange,
                onSendTestNotification = onSendTestNotification,
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

    if (showUpdateDialog) {
        AlertDialog(
            onDismissRequest = { showUpdateDialog = false },
            containerColor = ClayColors.SurfaceMarshmallow,
            icon = {
                Icon(
                    imageVector = Icons.Rounded.SystemUpdate,
                    contentDescription = null,
                    tint = ClayColors.PrimaryAccent,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Direct APK Update",
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp,
                    color = ClayColors.TextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Install an update directly using a downloaded ClayCal .apk file. Your events, settings, and themes will be preserved without resetting.",
                        fontSize = 14.sp,
                        color = ClayColors.TextSecondary
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ClayColors.SurfaceSoftClay, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "Installed Version",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ClayColors.TextSecondary
                            )
                            Text(
                                text = "ClayCal v${com.example.BuildConfig.VERSION_NAME} (Build ${com.example.BuildConfig.VERSION_CODE})",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = ClayColors.PrimaryAccent
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showUpdateDialog = false
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !ApkInstaller.canInstallApk(context)) {
                            Toast.makeText(
                                context,
                                "Please enable 'Allow from this source' for ClayCal, then pick the APK file.",
                                Toast.LENGTH_LONG
                            ).show()
                            ApkInstaller.openInstallPermissionSettings(context)
                        } else {
                            apkPickerLauncher.launch("*/*")
                        }
                    }
                ) {
                    Text("Select APK File", color = ClayColors.PrimaryAccent, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showUpdateDialog = false }) {
                    Text("Cancel", color = ClayColors.TextSecondary)
                }
            }
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
    onPromptUpdate: () -> Unit,
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
                icon = Icons.Rounded.Palette,
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
                icon = Icons.Rounded.ViewAgenda,
                title = "Default view",
                value = defaultView,
                onClick = onToggleDefaultView
            )
            SettingsDivider()
            SettingsRow(
                icon = Icons.Rounded.ViewAgenda,
                title = "Start week on",
                value = startWeekOn,
                onClick = onToggleStartWeek
            )
        }

        // Privacy & Data Group
        SettingsGroupCard {
            SettingsRow(
                icon = Icons.Rounded.Notifications,
                title = "Notifications",
                onClick = { onNavigateTo(SettingsSubScreen.NOTIFICATIONS) }
            )
            SettingsDivider()
            SettingsRow(
                icon = Icons.Rounded.Security,
                title = "Privacy",
                subtitle = "Keep your data local",
                onClick = { onNavigateTo(SettingsSubScreen.PRIVACY) }
            )
            SettingsDivider()
            SettingsRow(
                icon = Icons.Rounded.Storage,
                title = "Backup & Restore",
                subtitle = "Export or import your data",
                onClick = { onNavigateTo(SettingsSubScreen.BACKUP_RESTORE) }
            )
        }

        // About & Tour Group
        SettingsGroupCard {
            SettingsRow(
                icon = Icons.Rounded.Visibility,
                title = "Welcome Tour",
                value = "View guide",
                onClick = onOpenOnboarding
            )
            SettingsDivider()
            SettingsRow(
                icon = Icons.Rounded.Storage,
                title = "Clear Database",
                value = "Reset",
                onClick = onPromptClearData
            )
            SettingsDivider()
            SettingsRow(
                icon = Icons.Rounded.SystemUpdate,
                title = "Direct APK Update",
                subtitle = "Install update from downloaded .apk",
                onClick = onPromptUpdate
            )
            SettingsDivider()
            SettingsRow(
                icon = Icons.Rounded.Security,
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
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
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

        // Bright & Vivid Themes Section
        Text(
            text = "Bright & Vivid Colors",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = ClayColors.TextPrimary,
            modifier = Modifier.padding(start = 4.dp, top = 6.dp)
        )

        val brightOptions = listOf(
            Triple("Sunshine", ClayColors.BrightSunshine, "Warm glow"),
            Triple("Coral", ClayColors.BrightCoral, "Electric coral"),
            Triple("Rose", ClayColors.BrightRose, "Candy pink"),
            Triple("Violet", ClayColors.BrightViolet, "Ultraviolet"),
            Triple("Aqua", ClayColors.BrightAqua, "Electric cyan"),
            Triple("Emerald", ClayColors.BrightEmerald, "Spring green"),
            Triple("Tangerine", ClayColors.BrightTangerine, "Sunny orange"),
            Triple("Berry", ClayColors.BrightBerry, "Vivid magenta")
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            brightOptions.forEach { (name, color, _) ->
                val isSelected = activeAccent.equals(name, ignoreCase = true)
                ClayThemeColorBubble(
                    name = name,
                    color = color,
                    isSelected = isSelected,
                    onClick = { onAccentColorChange(name) }
                )
            }
        }

        // Soft Pastel Themes Section
        Text(
            text = "Soft Pastel Colors",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = ClayColors.TextPrimary,
            modifier = Modifier.padding(start = 4.dp, top = 8.dp)
        )

        val pastelOptions = listOf(
            Triple("Sage", ClayColors.PastelSage, "Pistachio"),
            Triple("Terracotta", ClayColors.PastelTerracotta, "Warm earth"),
            Triple("Peach", ClayColors.PastelPeach, "Soft apricot"),
            Triple("Lavender", ClayColors.PastelLavender, "Dreamy lilac"),
            Triple("Blue", ClayColors.PastelSky, "Pastel sky"),
            Triple("Mint", ClayColors.PastelMint, "Fresh mint")
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            pastelOptions.forEach { (name, color, _) ->
                val isSelected = activeAccent.equals(name, ignoreCase = true)
                ClayThemeColorBubble(
                    name = name,
                    color = color,
                    isSelected = isSelected,
                    onClick = { onAccentColorChange(name) }
                )
            }
        }

        // Live Claymorphic Palette Preview
        val previewShape = RoundedCornerShape(32.dp)
        ClayCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            shape = previewShape,
            surfaceColor = ClayColors.SurfaceSoftClay,
            elevation = 5.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clayMoulded(ClayColors.PrimaryAccent, shape = CircleShape, elevation = 2.dp)
                    )
                    Text(
                        text = "Active Theme: $activeAccent Clay",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClayColors.TextPrimary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sample Clay Pill
                    val pillShape = RoundedCornerShape(24.dp)
                    Box(
                        modifier = Modifier
                            .clayMoulded(
                                color = ClayColors.PrimaryAccent.copy(alpha = 0.22f),
                                shape = pillShape,
                                elevation = 2.dp
                            )
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Sample Event",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClayColors.PrimaryAccent
                        )
                    }

                    // Sample Action Button
                    ClayButton(
                        onClick = { },
                        containerColor = ClayColors.PrimaryAccent,
                        shape = RoundedCornerShape(24.dp),
                        elevation = 4.dp,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Pastel Clay Style",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ClayThemeColorBubble(
    name: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bubbleShape = CircleShape
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .clayBounceClickable(shape = bubbleShape, pressedScale = 0.88f) {
                onClick()
            }
            .padding(vertical = 4.dp)
            .testTag("theme_color_$name")
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clayMoulded(
                    color = color,
                    shape = bubbleShape,
                    elevation = if (isSelected) 6.dp else 2.dp
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Text(
            text = name,
            fontSize = 11.sp,
            color = if (isSelected) ClayColors.TextPrimary else ClayColors.TextTertiary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun NotificationsSubScreen(
    notificationsEnabled: Boolean,
    defaultReminderMinutes: Int,
    onNotificationsEnabledChange: (Boolean) -> Unit,
    onDefaultReminderMinutesChange: (Int) -> Unit,
    onSendTestNotification: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var hasPostPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPostPermission = granted
        if (granted) {
            Toast.makeText(context, "Notification permission granted!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ClayColors.Background)
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = ClayColors.TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Notifications & Reminders",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = ClayColors.TextPrimary
            )
        }

        // System Permission status banner (if Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ClayCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                surfaceColor = if (hasPostPermission) ClayColors.SurfaceMarshmallow else ClayColors.SurfaceSoftClay,
                elevation = 3.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "System Permission",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClayColors.TextPrimary
                        )
                        Text(
                            text = if (hasPostPermission) "Notifications are allowed on this device" else "Android permission needed to show alerts",
                            fontSize = 12.sp,
                            color = if (hasPostPermission) ClayColors.ClayMint else ClayColors.ClayTerracotta
                        )
                    }

                    if (!hasPostPermission) {
                        ClayButton(
                            onClick = {
                                permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                            },
                            containerColor = ClayColors.ClayTerracotta,
                            contentColor = Color.White,
                            elevation = 3.dp,
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Allow", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        ClayPill(
                            text = "Allowed ✓",
                            isSelected = true,
                            onClick = {},
                            selectedColor = ClayColors.ClayMint
                        )
                    }
                }
            }
        }

        // Master toggle & settings card
        SettingsGroupCard {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Event Reminders",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClayColors.TextPrimary
                    )
                    Text(
                        text = "Schedule alerts ahead of planned events",
                        fontSize = 12.sp,
                        color = ClayColors.TextSecondary
                    )
                }
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { onNotificationsEnabledChange(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = ClayColors.PrimaryAccent
                    )
                )
            }

            SettingsDivider()

            // Default reminder timing selection
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Default Reminder Time",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = ClayColors.TextPrimary
                )

                val reminderPresets = listOf(
                    Pair("At start", 0),
                    Pair("5m before", 5),
                    Pair("15m before", 15),
                    Pair("30m before", 30),
                    Pair("1h before", 60),
                    Pair("1d before", 1440)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    reminderPresets.forEach { (label, mins) ->
                        val isSelected = defaultReminderMinutes == mins
                        ClayPill(
                            text = label,
                            isSelected = isSelected,
                            onClick = { onDefaultReminderMinutesChange(mins) },
                            selectedColor = ClayColors.PrimaryAccent,
                            unselectedColor = ClayColors.SurfaceSoftClay
                        )
                    }
                }
            }
        }

        // Instant Notification Tester Card
        ClayCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            surfaceColor = ClayColors.SurfaceMarshmallow,
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.NotificationsActive,
                        contentDescription = null,
                        tint = ClayColors.PrimaryAccent,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "Test Reminder Notification",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClayColors.TextPrimary
                        )
                        Text(
                            text = "Send an immediate clay notification to verify alert sound, priority banner, and quick actions",
                            fontSize = 12.sp,
                            color = ClayColors.TextSecondary
                        )
                    }
                }

                ClayButton(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasPostPermission) {
                            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            onSendTestNotification()
                        }
                    },
                    containerColor = ClayColors.PrimaryAccent,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    elevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Send Test Alert Now", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Reliability Note
        ClayCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            surfaceColor = ClayColors.SurfaceSoftClay,
            elevation = 1.dp
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "🔔 High-Precision Alarms",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ClayColors.TextPrimary
                )
                Text(
                    text = "Event reminders use Android AlarmManager to trigger precisely even if ClayCal is closed. Tapping the notification opens the event details, with 'Mark Done' and 'Snooze 10m' available right in the notification shade.",
                    fontSize = 12.sp,
                    color = ClayColors.TextSecondary,
                    lineHeight = 16.sp
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
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
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
                icon = Icons.Rounded.Lock,
                title = "No Accounts",
                subtitle = "Use ClayCal without signing up or sharing personal information."
            )
            SettingsDivider()
            PrivacyItem(
                icon = Icons.Rounded.Storage,
                title = "Local Storage Only",
                subtitle = "Your events are stored on your device, not in the cloud."
            )
            SettingsDivider()
            PrivacyItem(
                icon = Icons.Rounded.VisibilityOff,
                title = "No Tracking",
                subtitle = "We don't collect analytics or track your behavior."
            )
            SettingsDivider()
            PrivacyItem(
                icon = Icons.Rounded.Security,
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
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
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
                        imageVector = Icons.Rounded.CloudUpload,
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
                        imageVector = Icons.Rounded.CloudDownload,
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
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    ClayCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        surfaceColor = ClayColors.SurfaceMarshmallow,
        elevation = 6.dp
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
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
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
                imageVector = Icons.Rounded.Check,
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
