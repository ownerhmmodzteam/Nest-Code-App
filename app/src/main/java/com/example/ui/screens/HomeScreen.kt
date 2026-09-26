package com.example.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.catalog.CurriculumCatalog
import com.example.data.catalog.CurriculumLevelsAdvanced
import com.example.ui.components.CodeNestTopBar
import com.example.ui.components.IosGlassCard
import com.example.ui.theme.*
import com.example.util.XpLevelCalculator
import com.example.viewmodel.CodeNestViewModel

@Composable
fun HomeScreen(
    viewModel: CodeNestViewModel,
    onNavigateToLesson: (String) -> Unit,
    onNavigateToLearn: () -> Unit,
    onNavigateToPlayground: () -> Unit,
    onNavigateToVisualizer: () -> Unit,
    onNavigateToSearch: () -> Unit
) {
    val progress by viewModel.userProgress.collectAsState()
    val scrollState = rememberScrollState()

    val currentLesson = CurriculumCatalog.findLessonById(progress.currentLessonId)
        ?: CurriculumCatalog.getAllLevels().first().modules.first().lessons.first()
    val currentLevel = CurriculumCatalog.findLevelById(currentLesson.levelId)
        ?: CurriculumCatalog.getAllLevels().first()

    val completedList = progress.completedLessonsString.split(",").filter { it.isNotBlank() }
    val totalLessonsCount = CurriculumCatalog.getAllLevels().sumOf { level ->
        level.modules.sumOf { it.lessons.size }
    }

    val levelProgress = XpLevelCalculator.getLevelProgress(progress.xp)

    Scaffold(
        topBar = {
            CodeNestTopBar(
                title = "CodeNest",
                streak = progress.streak,
                xp = progress.xp,
                actions = {
                    val searchInteraction = remember { MutableInteractionSource() }
                    val isSearchPressed by searchInteraction.collectIsPressedAsState()
                    val searchScale by animateFloatAsState(
                        targetValue = if (isSearchPressed) 0.88f else 1f,
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        label = "search_btn_scale"
                    )

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .scale(searchScale)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                            .clickable(
                                interactionSource = searchInteraction,
                                indication = null,
                                onClick = onNavigateToSearch
                            )
                            .testTag("home_search_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Cari Kursus & Materi",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 8.dp)
        ) {
            // iOS 28 Greeting & Profile Pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Halo, ${progress.username} 👋",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Target ${progress.dailyMinutesTarget}m hari ini • ${progress.userTier}",
                        style = MaterialTheme.typography.bodySmall,
                        color = IosTextSecondary
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = IosNeonCyan.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, IosNeonCyan.copy(alpha = 0.4f)),
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "⚡", fontSize = 20.sp)
                    }
                }
            }

            // iOS 28 LEVEL PROGRESS CAPSULE CARD
            IosGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                cornerRadius = 24.dp,
                highlightGlow = IosNeonCyan
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = IosNeonCyan.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, IosNeonCyan.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "LEVEL ${levelProgress.currentLevel}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = IosNeonCyan,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = XpLevelCalculator.getMilestoneTitle(levelProgress.currentLevel),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = IosNeonViolet
                        )
                    }

                    Text(
                        text = if (levelProgress.isMaxLevel) "MAX LEVEL" else "${levelProgress.currentXp} XP",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = if (levelProgress.isMaxLevel) IosNeonAmber else Color.White
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Smooth Glowing Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(levelProgress.progressFraction.coerceIn(0.02f, 1f))
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(IosNeonCyan, IosNeonViolet)
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (levelProgress.isMaxLevel) "Level Maksimum (Capped 9999)"
                        else "${levelProgress.remainingXpToNextLevel} XP menuju Level ${levelProgress.currentLevel + 1}",
                        style = MaterialTheme.typography.labelSmall,
                        color = IosTextSecondary
                    )
                    Text(
                        text = "${(levelProgress.progressFraction * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }

            // HERO: CONTINUE LEARNING CARD (iOS 28 Liquid Card with Specular Rim)
            IosGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("continue_learning_card"),
                cornerRadius = 26.dp,
                highlightGlow = Color(currentLevel.colorHex),
                onClick = { onNavigateToLesson(currentLesson.id) }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(currentLevel.colorHex).copy(alpha = 0.22f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(currentLevel.colorHex).copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "LANJUTKAN BELAJAR",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            ),
                            color = Color(currentLevel.colorHex),
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = "Level ${currentLevel.levelNumber}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = IosTextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = currentLesson.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.3).sp
                    ),
                    color = Color.White
                )

                Text(
                    text = "${currentLevel.title} • ${currentLesson.durationMinutes} menit pembelajaran",
                    style = MaterialTheme.typography.bodySmall,
                    color = IosTextSecondary,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = IosNeonCyan,
                        shadowElevation = 6.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Mulai Sekarang",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF001B24)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color(0xFF001B24),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = IosNeonCyan.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "+25 XP",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = IosNeonCyan,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // STATS ROW (3 iOS Liquid Glass Cards)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCardItem(
                    title = "Total XP",
                    value = "${progress.xp}",
                    icon = "⚡",
                    color = IosNeonCyan,
                    modifier = Modifier.weight(1f)
                )
                StatCardItem(
                    title = "Streak",
                    value = "${progress.streak} Hari",
                    icon = "🔥",
                    color = IosNeonAmber,
                    modifier = Modifier.weight(1f)
                )
                StatCardItem(
                    title = "Selesai",
                    value = "${completedList.size} Modul",
                    icon = "✅",
                    color = IosNeonEmerald,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // DAILY CHALLENGE SECTION
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tantangan Hari Ini",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.2).sp
                    ),
                    color = Color.White
                )
                TextButton(onClick = onNavigateToPlayground) {
                    Text(
                        text = "Playground →",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = IosNeonCyan
                    )
                }
            }

            val daily = CurriculumLevelsAdvanced.dailyChallenges.first()
            IosGlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 22.dp,
                highlightGlow = IosNeonAmber,
                onClick = onNavigateToPlayground
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(46.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = IosNeonAmber.copy(alpha = 0.16f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, IosNeonAmber.copy(alpha = 0.35f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "🎯", fontSize = 22.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = daily.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = daily.prompt,
                            style = MaterialTheme.typography.bodySmall,
                            color = IosTextSecondary,
                            maxLines = 2
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = IosNeonAmber.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, IosNeonAmber.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "+${daily.xpReward} XP",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = IosNeonAmber,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ALGORITHM VISUALIZER SHORTCUT
            IosGlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 22.dp,
                highlightGlow = IosNeonViolet,
                onClick = onNavigateToVisualizer
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(46.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = IosNeonViolet.copy(alpha = 0.16f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, IosNeonViolet.copy(alpha = 0.35f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "📊", fontSize = 22.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Visualisasi Algoritma",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "Step-by-step Bubble Sort & Binary Search",
                            style = MaterialTheme.typography.bodySmall,
                            color = IosTextSecondary
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = IosNeonViolet,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // ACHIEVEMENTS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pencapaian & Medali",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.2).sp
                    ),
                    color = Color.White
                )
                Text(
                    text = "${CurriculumLevelsAdvanced.achievementsList.size} Tersedia",
                    style = MaterialTheme.typography.labelSmall,
                    color = IosNeonCyan
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            val unlockedSet = progress.unlockedAchievementsString.split(",").toSet()

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(CurriculumLevelsAdvanced.achievementsList) { achievement ->
                    val isUnlocked = unlockedSet.contains(achievement.id)
                    IosGlassCard(
                        modifier = Modifier.width(150.dp),
                        cornerRadius = 20.dp,
                        highlightGlow = if (isUnlocked) IosNeonEmerald else null
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = achievement.iconEmoji,
                                fontSize = 32.sp,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            Text(
                                text = achievement.title,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                ),
                                color = if (isUnlocked) Color.White else IosTextSecondary,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isUnlocked) IosNeonEmerald.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.06f)
                            ) {
                                Text(
                                    text = if (isUnlocked) "Terbuka" else "Terkunci",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isUnlocked) IosNeonEmerald else IosTextTertiary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@Composable
fun StatCardItem(
    title: String,
    value: String,
    icon: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    IosGlassCard(
        modifier = modifier,
        cornerRadius = 20.dp,
        highlightGlow = color
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = icon, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    letterSpacing = (-0.3).sp
                ),
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = IosTextSecondary
            )
        }
    }
}
