package com.example.ui.clay

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.clayContainer(
    shape: Shape = RoundedCornerShape(28.dp),
    surfaceColor: Color = ClayColors.SurfaceMarshmallow,
    elevation: Dp = 8.dp,
    borderWidth: Dp = 1.5.dp,
    highlightColor: Color = Color.White.copy(alpha = 0.85f),
    bevelColor: Color = ClayColors.ShadowBevel.copy(alpha = 0.45f)
): Modifier = this
    .shadow(
        elevation = elevation,
        shape = shape,
        clip = false,
        ambientColor = ClayColors.ShadowAmbient,
        spotColor = ClayColors.ShadowSpot
    )
    .background(surfaceColor, shape = shape)
    .border(
        width = borderWidth,
        brush = Brush.linearGradient(
            colors = listOf(highlightColor, bevelColor),
            start = Offset.Zero,
            end = Offset.Infinite
        ),
        shape = shape
    )

fun Modifier.clayClickable(
    shape: Shape = RoundedCornerShape(28.dp),
    surfaceColor: Color = ClayColors.SurfaceMarshmallow,
    baseElevation: Dp = 8.dp,
    pressedElevation: Dp = 2.dp,
    highlightColor: Color = Color.White.copy(alpha = 0.85f),
    bevelColor: Color = ClayColors.ShadowBevel.copy(alpha = 0.45f),
    onClick: () -> Unit
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "clay_scale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isPressed) pressedElevation else baseElevation,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "clay_elevation"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .shadow(
            elevation = elevation,
            shape = shape,
            clip = false,
            ambientColor = ClayColors.ShadowAmbient,
            spotColor = ClayColors.ShadowSpot
        )
        .background(surfaceColor, shape = shape)
        .border(
            width = 1.5.dp,
            brush = Brush.linearGradient(
                colors = listOf(highlightColor, bevelColor),
                start = Offset.Zero,
                end = Offset.Infinite
            ),
            shape = shape
        )
        .clip(shape)
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
}

/**
 * Interactive squishy spring bounce modifier that adds playful tactile feedback
 * with soft scale dampening on touch/press.
 */
fun Modifier.clayBounceClickable(
    shape: Shape = RoundedCornerShape(24.dp),
    pressedScale: Float = 0.93f,
    onClick: () -> Unit
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressedScale else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "clay_bounce_clickable_scale"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clip(shape)
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
}
