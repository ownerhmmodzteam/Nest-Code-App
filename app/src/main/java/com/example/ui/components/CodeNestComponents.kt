package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

enum class NavigationTab(val title: String, val iconSelected: ImageVector, val iconUnselected: ImageVector) {
    HOME("Beranda", Icons.Filled.Home, Icons.Outlined.Home),
    LEARN("Belajar", Icons.Filled.School, Icons.Outlined.School),
    PRACTICE("Praktik", Icons.Filled.Terminal, Icons.Outlined.Terminal),
    PROJECTS("Proyek", Icons.Filled.Code, Icons.Outlined.Code),
    PROFILE("Profil", Icons.Filled.Person, Icons.Outlined.Person)
}

/**
 * iOS 28 Dynamic Island & Floating Frosted Glass Header
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeNestTopBar(
    title: String,
    streak: Int = 0,
    xp: Long = 0L,
    showBack: Boolean = false,
    onBackClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(26.dp),
        color = IosDarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.07f)),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Back button or App Brand Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (showBack) {
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(
                        targetValue = if (isPressed) 0.88f else 1f,
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        label = "back_scale"
                    )

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .scale(scale)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = onBackClick
                            )
                            .testTag("top_bar_back_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.3).sp
                    ),
                    color = Color.White
                )
            }

            // Right: Dynamic Island Pills (Streak & XP) + Extra Actions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (streak > 0) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = IosNeonAmber.copy(alpha = 0.16f),
                        border = androidx.compose.foundation.BorderStroke(
                            0.8.dp,
                            IosNeonAmber.copy(alpha = 0.35f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🔥", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "$streak d",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = IosNeonAmber
                            )
                        }
                    }
                }

                if (xp > 0) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = IosNeonCyan.copy(alpha = 0.16f),
                        border = androidx.compose.foundation.BorderStroke(
                            0.8.dp,
                            IosNeonCyan.copy(alpha = 0.35f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "⚡", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "$xp XP",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = IosNeonCyan
                            )
                        }
                    }
                }

                actions()
            }
        }
    }
}

/**
 * iOS 28 Floating Liquid Glass Dock (Bottom Navigation Bar)
 * Features tactile spring scaling, frosted glass backdrop, and glowing capsule indicator.
 */
@Composable
fun CodeNestBottomNavBar(
    selectedTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(34.dp),
                    spotColor = IosNeonCyan.copy(alpha = 0.25f),
                    ambientColor = Color.Black
                ),
            shape = RoundedCornerShape(34.dp),
            color = IosDarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavigationTab.entries.forEach { tab ->
                    val isSelected = tab == selectedTab
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()

                    // iOS tactile bounce physics
                    val scale by animateFloatAsState(
                        targetValue = when {
                            isPressed -> 0.88f
                            isSelected -> 1.05f
                            else -> 1f
                        },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        ),
                        label = "tab_scale_${tab.name}"
                    )

                    val activeBgColor by animateColorAsState(
                        targetValue = if (isSelected) IosNeonCyan.copy(alpha = 0.18f) else Color.Transparent,
                        animationSpec = spring(stiffness = Spring.StiffnessMedium),
                        label = "tab_bg_${tab.name}"
                    )

                    val iconTint by animateColorAsState(
                        targetValue = if (isSelected) IosNeonCyan else IosTextSecondary,
                        animationSpec = spring(stiffness = Spring.StiffnessMedium),
                        label = "tab_tint_${tab.name}"
                    )

                    Box(
                        modifier = Modifier
                            .scale(scale)
                            .clip(RoundedCornerShape(22.dp))
                            .background(activeBgColor)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = { onTabSelected(tab) }
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("nav_tab_${tab.name.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (isSelected) tab.iconSelected else tab.iconUnselected,
                                contentDescription = tab.title,
                                tint = iconTint,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                                ),
                                color = iconTint
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * iOS 28 Glassmorphic Card Container with Spring Tactile Press Effect
 */
@Composable
fun IosGlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    onClick: (() -> Unit)? = null,
    highlightGlow: Color? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (onClick != null && isPressed) 0.975f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "ios_card_scale"
    )

    Surface(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = if (isPressed) 4.dp else 10.dp,
                shape = RoundedCornerShape(cornerRadius),
                spotColor = highlightGlow?.copy(alpha = 0.25f) ?: Color.Black.copy(alpha = 0.4f)
            ),
        shape = RoundedCornerShape(cornerRadius),
        color = IosDarkCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        val clickableModifier = if (onClick != null) {
            Modifier.clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
        } else {
            Modifier
        }

        Column(
            modifier = Modifier
                .then(clickableModifier)
                .background(IosDarkCard)
                .padding(18.dp)
        ) {
            content()
        }
    }
}

/**
 * macOS / iOS Style Syntax Highlighted Code Viewer
 */
@Composable
fun HighlightedCodeViewer(
    code: String,
    language: String,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val lines = code.trim().lines()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(
                1.dp,
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.25f),
                        Color.White.copy(alpha = 0.04f)
                    )
                ),
                RoundedCornerShape(18.dp)
            ),
        color = CodeEditorBg
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header tag with Mac/iOS terminal window dots
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(modifier = Modifier.size(11.dp).clip(CircleShape).background(Color(0xFFFF5F56)))
                    Box(modifier = Modifier.size(11.dp).clip(CircleShape).background(Color(0xFFFFBD2E)))
                    Box(modifier = Modifier.size(11.dp).clip(CircleShape).background(Color(0xFF27C93F)))
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.08f)
                ) {
                    Text(
                        text = language.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = IosNeonCyan,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            HorizontalDivider(color = Color(0xFF1E2638), thickness = 1.dp)

            Spacer(modifier = Modifier.height(10.dp))

            // Lines and code content
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
            ) {
                // Line numbers
                Column(modifier = Modifier.padding(end = 14.dp)) {
                    lines.indices.forEach { index ->
                        Text(
                            text = "${index + 1}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = CodeGutterText
                        )
                    }
                }

                // Code text
                Column {
                    lines.forEach { line ->
                        Text(
                            text = line,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = when {
                                line.trim().startsWith("//") || line.trim().startsWith("#") -> CodeComment
                                line.contains("function") || line.contains("def") || line.contains("fun") -> CodeFunction
                                line.contains("const") || line.contains("let") || line.contains("val") || line.contains("var") -> CodeKeyword
                                line.contains("\"") || line.contains("'") -> CodeString
                                else -> Color(0xFFE6EDF3)
                            }
                        )
                    }
                }
            }
        }
    }
}
