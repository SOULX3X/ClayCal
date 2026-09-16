package com.example.ui.clay

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max

/**
 * Applies the true Claymorphism visual traits:
 * 1. Big outer drop shadow in the element's OWN hue (not grey) so it looks moulded, not floating.
 * 2. Two inset shadows:
 *    - Light top-left inset shadow (white soft shine)
 *    - Darker bottom-right inset shadow (20% darker shade of the SAME hue)
 * 3. Smooth inflated pillowy cushion curve.
 * 4. Zero thin borders or sharp wireframes.
 */
fun Modifier.clayMoulded(
    color: Color,
    shape: Shape = RoundedCornerShape(32.dp),
    elevation: Dp = 10.dp,
    darkerFactor: Float = 0.20f,
    isPressed: Boolean = false,
    isInsetRecessed: Boolean = false
): Modifier {
    val darkerColor = if (color == Color.White || color == ClayColors.SurfaceMarshmallow) {
        ClayColors.DefaultShadowTone
    } else {
        color.darker(darkerFactor)
    }
    val lighterColor = Color.White

    return this
        .shadow(
            elevation = if (isPressed) maxOf(1.dp, elevation / 3) else elevation,
            shape = shape,
            clip = false,
            ambientColor = darkerColor.copy(alpha = if (isInsetRecessed) 0.15f else 0.28f),
            spotColor = darkerColor.copy(alpha = if (isInsetRecessed) 0.20f else 0.38f)
        )
        .background(color, shape = shape)
        .clip(shape)
        .drawWithContent {
            // Draw inflated puffy clay lighting before content
            val w = size.width
            val h = size.height
            val maxDimension = max(w, h)

            if (!isInsetRecessed) {
                // Base puffy gradient from slight light at top-left to subtle body
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            color.lighter(0.12f),
                            color,
                            color.darker(0.08f)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(w, h)
                    )
                )

                // 1. Inset Shadow: Light top-left shine (inflates the soft clay surface)
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            lighterColor.copy(alpha = if (isPressed) 0.45f else 0.72f),
                            lighterColor.copy(alpha = if (isPressed) 0.15f else 0.25f),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.10f, h * 0.10f),
                        radius = maxDimension * 0.85f
                    )
                )

                // Directional top-left soft rim sheen
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            lighterColor.copy(alpha = if (isPressed) 0.30f else 0.55f),
                            Color.Transparent
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(w * 0.35f, h * 0.35f)
                    )
                )

                // 2. Inset Shadow: 20% darker shade at bottom-right (gives moulded clay depth)
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            darkerColor.copy(alpha = if (isPressed) 0.55f else 0.42f),
                            darkerColor.copy(alpha = if (isPressed) 0.25f else 0.18f),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.92f, h * 0.92f),
                        radius = maxDimension * 0.80f
                    )
                )

                // Directional bottom-right soft shadow
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            darkerColor.copy(alpha = if (isPressed) 0.50f else 0.38f)
                        ),
                        start = Offset(w * 0.65f, h * 0.65f),
                        end = Offset(w, h)
                    )
                )
            } else {
                // Inset / Recessed clay (like a carved tray or text input)
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            color.darker(0.06f),
                            color,
                            color.lighter(0.08f)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(w, h)
                    )
                )

                // Top-left cast shadow (carved inside)
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            darkerColor.copy(alpha = 0.42f),
                            Color.Transparent
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(w * 0.30f, h * 0.30f)
                    )
                )

                // Bottom-right rim reflection
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            lighterColor.copy(alpha = 0.60f)
                        ),
                        start = Offset(w * 0.70f, h * 0.70f),
                        end = Offset(w, h)
                    )
                )
            }

            // Draw children content over the clay surface
            drawContent()
        }
}

@Composable
fun ClayCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(32.dp),
    surfaceColor: Color = ClayColors.SurfaceMarshmallow,
    elevation: Dp = 10.dp,
    borderWidth: Dp = 0.dp, // Maintained for parameter compatibility; wire borders omitted by design
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.clayMoulded(
            color = surfaceColor,
            shape = shape,
            elevation = elevation
        ),
        content = content
    )
}

@Composable
fun ClayButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = ClayColors.ClayTerracotta,
    contentColor: Color = Color.White,
    shape: Shape = RoundedCornerShape(28.dp),
    elevation: Dp = 10.dp,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "button_scale"
    )

    val currentElevation by animateDpAsState(
        targetValue = if (isPressed && enabled) 2.dp else elevation,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "button_elevation"
    )

    Row(
        modifier = modifier
            .defaultMinSize(minHeight = 52.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clayMoulded(
                color = if (enabled) containerColor else ClayColors.SurfaceDimmed,
                shape = shape,
                elevation = if (enabled) currentElevation else 1.dp,
                isPressed = isPressed
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = 22.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
fun ClayIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    containerColor: Color = ClayColors.SurfaceMarshmallow,
    iconTint: Color = ClayColors.TextPrimary,
    size: Dp = 48.dp,
    shape: Shape = CircleShape,
    elevation: Dp = 8.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "icon_btn_scale"
    )

    val currentElevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else elevation,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "icon_btn_elevation"
    )

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clayMoulded(
                color = containerColor,
                shape = shape,
                elevation = currentElevation,
                isPressed = isPressed
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
fun ClayPill(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconEmoji: String? = null,
    selectedColor: Color = ClayColors.ClayTerracotta,
    unselectedColor: Color = ClayColors.SurfaceMarshmallow,
    badgeCount: Int? = null
) {
    // Fully rounded pill shape
    val shape = CircleShape
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "pill_scale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else if (isSelected) 8.dp else 4.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "pill_elev"
    )

    val animatedBg by animateColorAsState(
        targetValue = if (isSelected) selectedColor else unselectedColor,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "pill_bg"
    )

    val animatedTextColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else ClayColors.TextPrimary,
        label = "pill_text"
    )

    Row(
        modifier = modifier
            .defaultMinSize(minHeight = 44.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clayMoulded(
                color = animatedBg,
                shape = shape,
                elevation = elevation,
                isPressed = isPressed
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (iconEmoji != null) {
            Text(text = iconEmoji, fontSize = 15.sp)
        }
        Text(
            text = text,
            color = animatedTextColor,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
        if (badgeCount != null && badgeCount > 0) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(
                        color = if (isSelected) Color.White.copy(alpha = 0.35f) else ClayColors.SurfaceDimmed,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$badgeCount",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else ClayColors.TextSecondary
                )
            }
        }
    }
}

@Composable
fun ClayTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    minHeight: Dp = 54.dp
) {
    // Very large radius on input fields
    val shape = RoundedCornerShape(26.dp)

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = minHeight)
            .clayMoulded(
                color = ClayColors.SurfaceSoftClay,
                shape = shape,
                elevation = 3.dp,
                isInsetRecessed = true
            )
            .padding(horizontal = 18.dp, vertical = 14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (leadingIcon != null) {
                leadingIcon()
            }

            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = ClayColors.TextTertiary,
                        fontSize = 14.sp
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        color = ClayColors.TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    singleLine = singleLine,
                    maxLines = maxLines,
                    cursorBrush = SolidColor(ClayColors.PrimaryAccent)
                )
            }

            if (trailingIcon != null) {
                trailingIcon()
            }
        }
    }
}

@Composable
fun ClayBadge(
    text: String,
    backgroundColor: Color,
    contentColor: Color = Color.White,
    modifier: Modifier = Modifier,
    iconEmoji: String? = null
) {
    val shape = CircleShape
    Row(
        modifier = modifier
            .clayMoulded(
                color = backgroundColor,
                shape = shape,
                elevation = 4.dp
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        if (iconEmoji != null) {
            Text(text = iconEmoji, fontSize = 12.sp)
        }
        Text(
            text = text,
            color = contentColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * A cute 3D-looking moulded soft clay sphere/illustration.
 * Matches the surfaces with soft dual lighting and contact drop shadow.
 */
@Composable
fun Clay3DBlob(
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    val darker = color.darker(0.25f)
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val radius = this.size.minDimension / 2f
            val center = Offset(this.size.width / 2f, this.size.height / 2f)

            // Contact soft shadow
            drawCircle(
                color = darker.copy(alpha = 0.35f),
                radius = radius * 0.95f,
                center = center + Offset(0f, radius * 0.15f)
            )

            // Base sphere body
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        color.lighter(0.35f),
                        color,
                        darker
                    ),
                    center = center - Offset(radius * 0.35f, radius * 0.35f),
                    radius = radius * 1.3f
                ),
                radius = radius * 0.90f,
                center = center
            )

            // Top-left soft specular clay shine
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.85f),
                        Color.White.copy(alpha = 0.20f),
                        Color.Transparent
                    ),
                    center = center - Offset(radius * 0.40f, radius * 0.40f),
                    radius = radius * 0.50f
                ),
                radius = radius * 0.45f,
                center = center - Offset(radius * 0.40f, radius * 0.40f)
            )
        }
    }
}
