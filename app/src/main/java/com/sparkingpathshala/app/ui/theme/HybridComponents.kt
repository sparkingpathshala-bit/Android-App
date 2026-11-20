package com.sparkingpathshala.app.ui.theme

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.animation.core.animateFloat
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * PREMIUM HYBRID DESIGN SYSTEM (GLOW, GLASS & NEUMORPHIC MULTI-COLOR STYLE)
 */

@Composable
fun getThemeColors(): Pair<Color, Color> {
    val primaryColor = MaterialTheme.colorScheme.primary
    val bgColor = MaterialTheme.colorScheme.background
    return Pair(primaryColor, bgColor)
}

/**
 * AnimatedGradientBackground (Premium Cosmic Canvas)
 */
@Composable
fun AnimatedGradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val isDark = LocalThemeIsDark.current
    val gradient = if (isDark) CosmicGradientDark else CosmicGradientLight
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradient),
        content = content
    )
}

/**
 * 1. GlassCard (Premium Bento Translucent Card with Glowing Border)
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    borderWidth: Dp = 1.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardBg = MaterialTheme.colorScheme.surface
    val borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
    val shadowColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)

    Column(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = shadowColor,
                spotColor = shadowColor
            )
            .background(cardBg, RoundedCornerShape(cornerRadius))
            .border(borderWidth, borderColor, RoundedCornerShape(cornerRadius))
            .padding(16.dp),
        content = content
    )
}

/**
 * 1b. EnhancedShimmerSkeleton (Animated Loading Placeholder)
 */
@Composable
fun ShimmerSkeleton(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp)
) {
    val transition = androidx.compose.animation.core.rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(
                durationMillis = 1200,
                easing = androidx.compose.animation.core.FastOutSlowInEasing
            ),
            repeatMode = androidx.compose.animation.core.RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val isDark = LocalThemeIsDark.current
    val shimmerColors = if (isDark) {
        listOf(
            Color(0xFF1E293B),
            Color(0xFF334155),
            Color(0xFF1E293B)
        )
    } else {
        listOf(
            Color(0xFFE2E8F0),
            Color(0xFFF1F5F9),
            Color(0xFFE2E8F0)
        )
    }

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 300f, translateAnim - 300f),
        end = Offset(translateAnim, translateAnim)
    )

    Box(
        modifier = modifier
            .background(brush = brush, shape = shape)
    )
}

/**
 * 2. NeumorphicSurface (Micro-Beveled Surface)
 */
@Composable
fun NeumorphicSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    isPressed: Boolean = false,
    elevation: Dp = 4.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = LocalThemeIsDark.current
    val surfaceColor = if (isDark) Color(0xFF151E2E) else Color.White
    val shadowColor = if (isDark) Color.Black.copy(alpha = 0.6f) else Color(0xFF64748B).copy(alpha = 0.15f)
    
    Box(
        modifier = modifier
            .shadow(
                elevation = if (isPressed) 1.dp else elevation,
                shape = shape,
                ambientColor = shadowColor,
                spotColor = shadowColor
            )
            .background(surfaceColor, shape)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), shape),
        contentAlignment = Alignment.Center,
        content = content
    )
}

/**
 * RealisticButton:
 * A hyper-realistic tactile button featuring:
 * - Specular top highlight bevel border (capturing overhead light)
 * - Micro-spring physical depression on tap (subtle 3.5% scale and drop)
 * - Directional ambient & spot shadows that dynamically collapse on press
 * - Rich vertical lighting gradient
 */
@Composable
fun RealisticButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerGradient: Brush = Brush.verticalGradient(
        colors = listOf(Color(0xFFEA580C), Color(0xFFC2410C), Color(0xFF9A3412))
    ),
    shape: Shape = RoundedCornerShape(14.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.965f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "realistic_btn_scale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 6.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "realistic_btn_elevation"
    )

    val shadowColor = Color(0xFFC2410C).copy(alpha = if (enabled) 0.35f else 0.08f)
    val groundShadow = Color.Black.copy(alpha = if (enabled) 0.20f else 0.04f)

    val activeGradient = if (enabled) containerGradient else Brush.verticalGradient(
        listOf(Color(0xFF94A3B8), Color(0xFF64748B))
    )

    val topBevelBorder = if (enabled) {
        BorderStroke(
            1.2.dp,
            Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.45f),
                    Color.White.copy(alpha = 0.08f)
                )
            )
        )
    } else {
        BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    }

    Surface(
        onClick = onClick,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = shadowColor,
                spotColor = groundShadow
            ),
        enabled = enabled,
        shape = shape,
        color = Color.Transparent,
        border = topBevelBorder,
        interactionSource = interactionSource
    ) {
        Box(
            modifier = Modifier
                .background(activeGradient)
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                content = content
            )
        }
    }
}

@Composable
fun RealisticButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color,
    shape: Shape = RoundedCornerShape(14.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
    content: @Composable RowScope.() -> Unit
) {
    val lightTint = containerColor.copy(alpha = 0.95f)
    val darkTint = Color(
        red = (containerColor.red * 0.72f).coerceIn(0f, 1f),
        green = (containerColor.green * 0.72f).coerceIn(0f, 1f),
        blue = (containerColor.blue * 0.72f).coerceIn(0f, 1f),
        alpha = containerColor.alpha
    )
    val gradient = Brush.verticalGradient(listOf(lightTint, containerColor, darkTint))
    RealisticButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        containerGradient = gradient,
        shape = shape,
        contentPadding = contentPadding,
        content = content
    )
}

/**
 * RealisticOutlinedButton:
 * A realistic tactile outlined button with:
 * - Specular dual-tone border bevel
 * - Micro-spring physical depression on tap
 * - Directional ambient & spot shadows that collapse on press
 * - Translucent tinted glass background
 */
@Composable
fun RealisticOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    strokeColor: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
    shape: Shape = RoundedCornerShape(14.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.965f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "realistic_outlined_btn_scale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isPressed) 1.dp else 3.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "realistic_outlined_btn_elevation"
    )

    val bevelBorder = if (enabled) {
        BorderStroke(
            1.5.dp,
            Brush.verticalGradient(
                colors = listOf(
                    strokeColor.copy(alpha = 0.9f),
                    strokeColor.copy(alpha = 0.45f)
                )
            )
        )
    } else {
        BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f))
    }

    Surface(
        onClick = onClick,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = strokeColor.copy(alpha = if (enabled) 0.15f else 0.03f),
                spotColor = Color.Black.copy(alpha = if (enabled) 0.10f else 0.02f)
            ),
        enabled = enabled,
        shape = shape,
        color = Color.Transparent,
        border = bevelBorder,
        interactionSource = interactionSource
    ) {
        Box(
            modifier = Modifier
                .background(
                    if (isPressed) backgroundColor.copy(alpha = (backgroundColor.alpha * 1.8f).coerceAtMost(0.4f))
                    else backgroundColor
                )
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                content = content
            )
        }
    }
}

@Composable
fun RealisticButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = PrimaryBlue,
    gradientColors: List<Color>? = null,
    textColor: Color = Color.White
) {
    val gradient = if (gradientColors != null) {
        Brush.verticalGradient(gradientColors)
    } else {
        val lightTint = containerColor.copy(alpha = 0.95f)
        val darkTint = Color(
            red = (containerColor.red * 0.72f).coerceIn(0f, 1f),
            green = (containerColor.green * 0.72f).coerceIn(0f, 1f),
            blue = (containerColor.blue * 0.72f).coerceIn(0f, 1f),
            alpha = containerColor.alpha
        )
        Brush.verticalGradient(listOf(lightTint, containerColor, darkTint))
    }
    RealisticButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        containerGradient = gradient,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun RealisticOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = borderColor
) {
    RealisticOutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        strokeColor = borderColor,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

/**
 * RealisticTactileIconButton:
 * A hyper-tactile circular or squircle icon button designed for TopBars, Navigation, and Action Triggers.
 * Features:
 * - Dual-tone specular highlight rim border
 * - Directional ambient & spot shadows
 * - Micro-spring depression on press (scale down to 0.92f)
 * - Minimum 44dp accessible touch target
 */
@Composable
fun RealisticTactileIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: Dp = 44.dp,
    iconSize: Dp = 22.dp,
    tint: Color = Color.White,
    backgroundGradient: Brush = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.28f),
            Color.White.copy(alpha = 0.12f)
        )
    ),
    badgeCount: Int = 0
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "tactile_icon_btn_scale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isPressed) 1.dp else 4.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "tactile_icon_btn_elevation"
    )

    val specularBorder = BorderStroke(
        1.2.dp,
        Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.60f),
                Color.White.copy(alpha = 0.15f)
            )
        )
    )

    Surface(
        onClick = onClick,
        modifier = modifier
            .size(size)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = elevation,
                shape = CircleShape,
                ambientColor = Color.Black.copy(alpha = 0.25f),
                spotColor = Color.Black.copy(alpha = 0.35f)
            ),
        enabled = enabled,
        shape = CircleShape,
        color = Color.Transparent,
        border = specularBorder,
        interactionSource = interactionSource
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient),
            contentAlignment = Alignment.Center
        ) {
            if (badgeCount > 0) {
                BadgedBox(
                    badge = {
                        Badge(
                            containerColor = ModernRose,
                            contentColor = Color.White
                        ) {
                            Text(
                                text = if (badgeCount > 99) "99+" else "$badgeCount",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = contentDescription,
                        tint = tint,
                        modifier = Modifier.size(iconSize)
                    )
                }
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = tint,
                    modifier = Modifier.size(iconSize)
                )
            }
        }
    }
}

/**
 * 3. NeumorphicButton / EnhancedButton (Neon Gradient Button with Tactile Animation)
 */
@Composable
fun NeumorphicButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(14.dp),
    content: @Composable RowScope.() -> Unit
) {
    RealisticButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        containerGradient = BrandGradient,
        content = content
    )
}

/**
 * 4. NeumorphicTextField (Modern Clean High-Contrast Input Field)
 */
@Composable
fun NeumorphicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    placeholderText: String = "",
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    val isDark = LocalThemeIsDark.current
    val surfaceColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC)
    val borderColor = if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1)
    val hintColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val textStyleColor = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val shape = RoundedCornerShape(14.dp)
    
    Column(modifier = modifier) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
        
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                color = textStyleColor,
                fontSize = 15.sp,
                fontFamily = FontFamily.SansSerif
            ),
            placeholder = {
                if (placeholderText.isNotEmpty()) {
                    Text(
                        text = placeholderText,
                        color = hintColor,
                        fontSize = 14.sp
                    )
                }
            },
            leadingIcon = leadingIcon?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = true,
            shape = shape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = surfaceColor,
                unfocusedContainerColor = surfaceColor,
                disabledContainerColor = surfaceColor,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = borderColor,
                focusedTextColor = textStyleColor,
                unfocusedTextColor = textStyleColor,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

/**
 * 5. ClayBadge (Vibrant Amber/Coral Capsule)
 */
@Composable
fun ClayBadge(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = modifier
            .background(GoldGlowGradient, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

/**
 * 6. ClayFAB (Purple Gradient Floating Action Button)
 */
@Composable
fun ClayFAB(
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .shadow(8.dp, CircleShape, ambientColor = RoyalPurple, spotColor = RoyalPurple)
            .background(BrandGradient, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "FAB",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

/**
 * 7. ClayAvatar (Vibrant Monogram Profile Avatar)
 */
@Composable
fun ClayAvatar(
    initials: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .background(BrandGradient, CircleShape)
            .border(2.dp, Color.White, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontSize = (size.value * 0.38).sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * 8. SkeuomorphicReceipt (Receipt Panel)
 */
@Composable
fun SkeuomorphicReceipt(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val isDark = LocalThemeIsDark.current
    val bg = if (isDark) Color(0xFF151E2E) else Color(0xFFF8FAFC)
    val border = if (isDark) Color(0xFF243044) else Color(0xFFE2E8F0)

    Column(
        modifier = modifier
            .background(bg, RoundedCornerShape(12.dp))
            .border(1.5.dp, border, RoundedCornerShape(12.dp))
            .padding(16.dp),
        content = content
    )
}

/**
 * 9. BrutalistHeader (Decent Typography Header)
 */
@Composable
fun BrutalistHeader(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onBackground
) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.ExtraBold,
        fontFamily = FontFamily.SansSerif,
        color = color,
        modifier = modifier.padding(vertical = 4.dp)
    )
}

/**
 * 10. BrutalistDivider (Sleek Colored Separator)
 */
@Composable
fun BrutalistDivider(
    modifier: Modifier = Modifier,
    color: Color = RoyalPurple
) {
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(color.copy(alpha = 0.3f))
    )
}

/**
 * 11. SkeuomorphicTab (Premium Capsule Tab Selection)
 */
@Composable
fun SkeuomorphicTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tabBg = if (selected) BrandGradient else Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
    val textColor = if (selected) Color.White else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
    val borderStroke = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(tabBg)
            .then(if (borderStroke != null) Modifier.border(borderStroke, RoundedCornerShape(20.dp)) else Modifier)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * 12. Asynchronous Memory-Optimized Base64 Image Decoder
 * Uses BitmapMemoryCache with automatic eviction on backgrounding and RGB_565 config.
 */
@Composable
fun AsyncBase64Image(
    base64String: String?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop
) {
    var bitmap by remember(base64String) { mutableStateOf<Bitmap?>(null) }
    
    DisposableEffect(base64String) {
        onDispose {
            bitmap = null
        }
    }
    
    LaunchedEffect(base64String) {
        if (!base64String.isNullOrEmpty()) {
            val cleanBase64 = if (base64String.contains(",")) {
                base64String.substringAfter(",")
            } else {
                base64String
            }

            // Check bounded memory cache first to eliminate duplicate decoding
            val cached = com.sparkingpathshala.app.core.BitmapMemoryCache.get(cleanBase64)
            if (cached != null && !cached.isRecycled) {
                bitmap = cached
                return@LaunchedEffect
            }

            withContext(Dispatchers.IO) {
                try {
                    val decodedBytes = android.util.Base64.decode(cleanBase64, android.util.Base64.DEFAULT)
                    
                    val options = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }
                    BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size, options)
                    
                    // Downsample target: 384px is sharp for thumbnails/avatars while saving >85% RAM
                    val reqDim = 384
                    var inSampleSize = 1
                    if (options.outHeight > reqDim || options.outWidth > reqDim) {
                        val halfHeight = options.outHeight / 2
                        val halfWidth = options.outWidth / 2
                        while ((halfHeight / inSampleSize) >= reqDim && (halfWidth / inSampleSize) >= reqDim) {
                            inSampleSize *= 2
                        }
                    }
                    
                    options.inJustDecodeBounds = false
                    options.inSampleSize = inSampleSize.coerceAtLeast(1)
                    // RGB_565 cuts memory footprint by 50% (2 bytes per pixel vs 4 bytes in ARGB_8888)
                    options.inPreferredConfig = Bitmap.Config.RGB_565
                    
                    val decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size, options)
                    if (decodedBitmap != null) {
                        com.sparkingpathshala.app.core.BitmapMemoryCache.put(cleanBase64, decodedBitmap)
                        bitmap = decodedBitmap
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            bitmap = null
        }
    }
    
    val currentBitmap = bitmap
    if (currentBitmap != null && !currentBitmap.isRecycled) {
        androidx.compose.foundation.Image(
            bitmap = currentBitmap.asImageBitmap(),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
    } else {
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No Photo",
                color = Color.Gray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * SparkingLegalModal:
 * An in-app dialog presenting Sparking Pathshala's official Privacy Policy, Terms of Use,
 * and Student Data Consent guidelines with direct links to sparkingpathshala.in.
 */
@Composable
fun SparkingLegalModal(
    initialTab: Int = 0,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(initialTab) }
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Sparking Pathshala Legal Terms",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = PrimaryBlue
                )
                Text(
                    text = "Official policies hosted on sparkingpathshala.in",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
            ) {
                PrimaryTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    contentColor = PrimaryBlue
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Privacy", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Terms", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Data Consent", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 4.dp)
                ) {
                    when (selectedTab) {
                        0 -> {
                            Text(
                                text = "Privacy Policy (sparkingpathshala.in/privacy-policy)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = PrimaryBlue
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "1. Student Data Protection:\n" +
                                        "Sparking Pathshala is committed to protecting student and educator privacy. We only collect the minimal academic and account data required for coaching delivery, including student name, class standard, contact details, quiz submissions, homework, and fee receipts.\n\n" +
                                        "2. Zero Data Selling & Advertising:\n" +
                                        "We never sell, rent, or trade student personal records or academic scores to advertisers or third-party marketing brokers.\n\n" +
                                        "3. Local Data Security & Encryption:\n" +
                                        "All sensitive credentials, authentication tokens, and downloaded notes are stored with AES-256 GCM encryption. Communications to sparkingpathshala.in utilize TLS 1.3 encryption.\n\n" +
                                        "4. 7-Day Auto-Logout & Session Security:\n" +
                                        "Student login sessions remain active for up to 7 days of inactivity. Tokens automatically expire after 7 days to protect account privacy on shared mobile devices.\n\n" +
                                        "5. Account Deletion & Right to Forget:\n" +
                                        "Students or guardians have the right to request permanent account deletion at any time via the Profile screen or by contacting sajjanmallik@sparkingpathshala.in.\n\n" +
                                        "6. Grievance Redressal Officer (DPDP Act 2023):\n" +
                                        "Grievance Officer: Sajjan Kumar Mallik\n" +
                                        "Email: sajjanmallik@sparkingpathshala.in\n" +
                                        "Resolution: Acknowledgment within 24 hours, statutory resolution within 15 days.",
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        1 -> {
                            Text(
                                text = "Terms of Use (sparkingpathshala.in/terms)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = PrimaryBlue
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "1. Educational License:\n" +
                                        "All study materials, practice question banks, homework assignments, and video tutorials available through Sparking Pathshala are the intellectual property of Sparking Pathshala and its faculty. They are provided solely for personal student learning.\n\n" +
                                        "2. Prohibited Conduct:\n" +
                                        "Unauthorized redistribution, bulk scraping, re-uploading to commercial websites, sharing account credentials, or attempting to compromise the platform infrastructure is strictly prohibited and subject to immediate suspension.\n\n" +
                                        "3. Code of Conduct:\n" +
                                        "Students engaging in doubt ticket discussions and quiz forums must uphold academic integrity, mutual respect, and courteous communication.",
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        2 -> {
                            Text(
                                text = "Data Consent & Parental Agreement",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = PrimaryBlue
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "1. Educational Processing Consent:\n" +
                                        "By creating an account, you consent to the collection and processing of student information (including performance telemetry, quiz results, attendance records, and faculty notifications) to enhance individual learning progression.\n\n" +
                                        "2. Minors and Guardian Authorization:\n" +
                                        "For students under 18 years of age, registration implies consent and authorization from a parent or legal guardian. Parents have full authority to review records, inspect attendance, and manage student enrollment.\n\n" +
                                        "3. Continuous Compliance:\n" +
                                        "Sparking Pathshala operates in compliance with India's Digital Personal Data Protection (DPDP) Act and student child privacy standards.",
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val url = when (selectedTab) {
                        0 -> "https://sparkingpathshala.in/privacy-policy"
                        1 -> "https://sparkingpathshala.in/terms"
                        else -> "https://sparkingpathshala.in"
                    }
                    try {
                        uriHandler.openUri(url)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Open on sparkingpathshala.in", fontSize = 12.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color.Gray)
            }
        }
    )
}
