package com.example.ui.calendar

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.clay.ClayButton
import com.example.ui.clay.ClayColors

@Composable
fun OnboardingFlow(
    onFinish: () -> Unit,
    onRestoreBackup: () -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(0) } // 0..5 (Screens 1..6)

    BackHandler {
        if (step > 0) {
            step--
        } else {
            onFinish()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ClayColors.Background)
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        AnimatedContent(
            targetState = step,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "onboarding_step"
        ) { currentStep ->
            when (currentStep) {
                0 -> WelcomeScreen(onNext = { step = 1 })
                1 -> InfoScreen(
                    icon = Icons.Default.Security,
                    iconBg = ClayColors.ClayPeach,
                    title = "Private by design",
                    subtitle = "Your events stay on your device.\nNo accounts, no tracking.",
                    dotIndex = 1,
                    onNext = { step = 2 }
                )
                2 -> InfoScreen(
                    icon = Icons.Default.Spa,
                    iconBg = ClayColors.ClaySage,
                    title = "Focus on what matters",
                    subtitle = "A calm, clutter-free calendar\nfor a clearer mind.",
                    dotIndex = 2,
                    onNext = { step = 3 }
                )
                3 -> InfoScreen(
                    icon = Icons.Default.WbSunny,
                    iconBg = ClayColors.ClayAmber,
                    title = "A brighter tomorrow",
                    subtitle = "Plan your days, your way.",
                    dotIndex = 3,
                    buttonLabel = "Let's Go",
                    onNext = { step = 4 }
                )
                4 -> InitialSetupScreen(
                    onContinue = { step = 5 }
                )
                5 -> NoAccountScreen(
                    onStart = onFinish,
                    onRestore = {
                        onFinish()
                        onRestoreBackup()
                    }
                )
            }
        }
    }
}

@Composable
private fun WelcomeScreen(onNext: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Clay Art Graphic (Screen 1)
        Box(
            modifier = Modifier
                .size(180.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(48.dp),
                    ambientColor = ClayColors.ClaySage.copy(alpha = 0.25f),
                    spotColor = ClayColors.ClaySage.copy(alpha = 0.35f)
                )
                .background(
                    color = ClayColors.SurfaceMarshmallow,
                    shape = RoundedCornerShape(48.dp)
                )
                .border(
                    width = 2.dp,
                    brush = Brush.verticalGradient(
                        listOf(Color.White, ClayColors.ClaySage.copy(alpha = 0.3f))
                    ),
                    shape = RoundedCornerShape(48.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(ClayColors.ClaySage.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = ClayColors.ClaySage,
                    modifier = Modifier.size(54.dp)
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "ClayCal",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = ClayColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your Time. Your data.\nAlways yours.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = ClayColors.TextSecondary,
                lineHeight = 22.sp
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DotsIndicator(selectedIndex = 0, total = 5)
            Spacer(modifier = Modifier.height(24.dp))
            ClayButton(
                onClick = onNext,
                containerColor = ClayColors.PrimaryAccent,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("onboarding_get_started")
            ) {
                Text("Get Started →", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun InfoScreen(
    icon: ImageVector,
    iconBg: Color,
    title: String,
    subtitle: String,
    dotIndex: Int,
    buttonLabel: String = "Next →",
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .size(180.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(48.dp),
                    ambientColor = iconBg.copy(alpha = 0.25f),
                    spotColor = iconBg.copy(alpha = 0.35f)
                )
                .background(
                    color = ClayColors.SurfaceMarshmallow,
                    shape = RoundedCornerShape(48.dp)
                )
                .border(
                    width = 2.dp,
                    brush = Brush.verticalGradient(
                        listOf(Color.White, iconBg.copy(alpha = 0.3f))
                    ),
                    shape = RoundedCornerShape(48.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(iconBg.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconBg,
                    modifier = Modifier.size(52.dp)
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = ClayColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = subtitle,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                color = ClayColors.TextSecondary,
                lineHeight = 22.sp
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DotsIndicator(selectedIndex = dotIndex, total = 5)
            Spacer(modifier = Modifier.height(24.dp))
            ClayButton(
                onClick = onNext,
                containerColor = ClayColors.PrimaryAccent,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(buttonLabel, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun InitialSetupScreen(onContinue: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.padding(top = 20.dp)) {
            Text(
                text = "Let's set things up",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = ClayColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Choose your preferences.\nYou can change these anytime.",
                fontSize = 14.sp,
                color = ClayColors.TextSecondary,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Screen 5 Preferences card
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
                            listOf(Color.White, ClayColors.ShadowBevel.copy(alpha = 0.35f))
                        ),
                        shape = RoundedCornerShape(22.dp)
                    )
                    .padding(vertical = 6.dp)
            ) {
                Column {
                    SetupRow(icon = Icons.Default.ViewAgenda, label = "Start week on", value = "Monday")
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(ClayColors.ShadowBevel.copy(alpha = 0.3f)))
                    SetupRow(icon = Icons.Default.CalendarMonth, label = "Default view", value = "Month")
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(ClayColors.ShadowBevel.copy(alpha = 0.3f)))
                    SetupRow(icon = Icons.Default.Notifications, label = "Reminder time", value = "10 minutes")
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(ClayColors.ShadowBevel.copy(alpha = 0.3f)))
                    SetupRow(icon = Icons.Default.Palette, label = "Theme", value = "System")
                }
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            ClayButton(
                onClick = onContinue,
                containerColor = ClayColors.PrimaryAccent,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun NoAccountScreen(
    onStart: () -> Unit,
    onRestore: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .size(180.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(48.dp),
                    ambientColor = ClayColors.ClaySage.copy(alpha = 0.25f),
                    spotColor = ClayColors.ClaySage.copy(alpha = 0.35f)
                )
                .background(
                    color = ClayColors.SurfaceMarshmallow,
                    shape = RoundedCornerShape(48.dp)
                )
                .border(
                    width = 2.dp,
                    brush = Brush.verticalGradient(
                        listOf(Color.White, ClayColors.ClaySage.copy(alpha = 0.3f))
                    ),
                    shape = RoundedCornerShape(48.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(ClayColors.ClaySage.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = ClayColors.ClaySage,
                    modifier = Modifier.size(52.dp)
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "No account needed",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = ClayColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "ClayCal works offline.\nYour data stays on your device.",
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                color = ClayColors.TextSecondary,
                lineHeight = 22.sp
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ClayButton(
                onClick = onStart,
                containerColor = ClayColors.PrimaryAccent,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("onboarding_start_using")
            ) {
                Text("Start Using ClayCal", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            TextButton(onClick = onRestore) {
                Text(
                    text = "I already have a backup",
                    fontSize = 14.sp,
                    color = ClayColors.TextSecondary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SetupRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ClayColors.TextSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = ClayColors.TextPrimary
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                fontSize = 14.sp,
                color = ClayColors.TextSecondary
            )
            Spacer(modifier = Modifier.width(4.dp))
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
private fun DotsIndicator(selectedIndex: Int, total: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until total) {
            Box(
                modifier = Modifier
                    .size(if (i == selectedIndex) 8.dp else 6.dp)
                    .background(
                        color = if (i == selectedIndex) ClayColors.PrimaryAccent else ClayColors.ShadowBevel,
                        shape = CircleShape
                    )
            )
        }
    }
}
