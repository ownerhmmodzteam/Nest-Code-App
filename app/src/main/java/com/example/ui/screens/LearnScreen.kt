package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.catalog.CurriculumCatalog
import com.example.model.RoadmapLevel
import com.example.ui.components.CodeNestTopBar
import com.example.ui.components.IosGlassCard
import com.example.ui.theme.*
import com.example.viewmodel.CodeNestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnScreen(
    viewModel: CodeNestViewModel,
    onNavigateToLesson: (String) -> Unit,
    onNavigateToQuiz: (String) -> Unit
) {
    val progress by viewModel.userProgress.collectAsState()
    val allLevels = remember { CurriculumCatalog.getAllLevels() }
    val completedList = progress.completedLessonsString.split(",").filter { it.isNotBlank() }

    var selectedLevelForSheet by remember { mutableStateOf<RoadmapLevel?>(null) }

    Scaffold(
        topBar = {
            CodeNestTopBar(
                title = "Roadmap Belajar",
                streak = progress.streak,
                xp = progress.xp
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Roadmap banner summary (iOS Glass Card)
            IosGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 6.dp),
                cornerRadius = 20.dp,
                highlightGlow = IosNeonCyan
            ) {
                Column {
                    Text(
                        text = "17 Tahapan Kurikulum (Level 0 - 16)",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.2).sp
                        ),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Jalur komprehensif mulai dari dasar komputasi hingga DevOps & Keamanan Cyber",
                        style = MaterialTheme.typography.bodySmall,
                        color = IosTextSecondary
                    )
                }
            }

            // Interactive Connected Roadmap List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp)
            ) {
                itemsIndexed(allLevels) { index, level ->
                    val isFirst = index == 0
                    val isLast = index == allLevels.size - 1

                    // Determine level status
                    val levelLessonIds = level.modules.flatMap { it.lessons.map { l -> l.id } }
                    val completedCount = levelLessonIds.count { completedList.contains(it) }
                    val isCompleted = levelLessonIds.isNotEmpty() && completedCount == levelLessonIds.size
                    val isCurrent = level.id == progress.currentLevelId || (completedCount > 0 && !isCompleted)
                    val isLocked = index > 0 && !isCompleted && !isCurrent && level.id > progress.currentLevelId + 1

                    RoadmapNodeItem(
                        level = level,
                        isFirst = isFirst,
                        isLast = isLast,
                        isCompleted = isCompleted,
                        isCurrent = isCurrent,
                        isLocked = isLocked,
                        completedCount = completedCount,
                        totalLessons = levelLessonIds.size,
                        onClick = { selectedLevelForSheet = level }
                    )
                }
            }
        }

        // Level Details Bottom Sheet
        if (selectedLevelForSheet != null) {
            val level = selectedLevelForSheet!!
            ModalBottomSheet(
                onDismissRequest = { selectedLevelForSheet = null },
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                        .padding(bottom = 32.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(level.colorHex).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "LEVEL ${level.levelNumber}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(level.colorHex),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        IconButton(onClick = { selectedLevelForSheet = null }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = level.title,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = level.subtitle,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = level.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Modul & Pelajaran:",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    level.modules.forEach { module ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = module.title,
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            module.lessons.forEach { lesson ->
                                val isDone = completedList.contains(lesson.id)
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            selectedLevelForSheet = null
                                            onNavigateToLesson(lesson.id)
                                        },
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (isDone) Icons.Default.Check else Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            tint = if (isDone) AccentGreen else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = lesson.title,
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "${lesson.durationMinutes} menit • Praktik Interactive",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            // Quiz button if available
                            if (module.quiz != null) {
                                Button(
                                    onClick = {
                                        selectedLevelForSheet = null
                                        onNavigateToQuiz(module.quiz.id)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondary
                                    )
                                ) {
                                    Text("Ikuti Kuis Modul (+${module.quiz.xpReward} XP)")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RoadmapNodeItem(
    level: RoadmapLevel,
    isFirst: Boolean,
    isLast: Boolean,
    isCompleted: Boolean,
    isCurrent: Boolean,
    isLocked: Boolean,
    completedCount: Int,
    totalLessons: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("roadmap_level_${level.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Vertical Timeline Line & Circle Indicator
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(48.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(20.dp)
                    .background(
                        if (isFirst) Color.Transparent
                        else if (isCompleted) AccentGreen
                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
            )

            // Circle Node
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCompleted -> AccentGreen
                            isCurrent -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                    .border(
                        width = if (isCurrent) 3.dp else 1.dp,
                        color = if (isCurrent) Color(level.colorHex) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selesai",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                } else if (isLocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Terkunci",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Text(
                        text = "${level.levelNumber}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isCurrent) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(20.dp)
                    .background(
                        if (isLast) Color.Transparent
                        else if (isCompleted) AccentGreen
                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Level Content Card (iOS 28 Liquid Glass)
        IosGlassCard(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 6.dp),
            cornerRadius = 20.dp,
            highlightGlow = if (isCurrent) Color(level.colorHex) else null
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LEVEL ${level.levelNumber}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = Color(level.colorHex)
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isCompleted) IosNeonEmerald.copy(alpha = 0.18f)
                    else if (isCurrent) IosNeonCyan.copy(alpha = 0.18f)
                    else Color.White.copy(alpha = 0.06f)
                ) {
                    Text(
                        text = if (isCompleted) "Selesai" else if (isCurrent) "Aktif" else "$completedCount/$totalLessons",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isCompleted) IosNeonEmerald else if (isCurrent) IosNeonCyan else IosTextSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = level.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )

            Text(
                text = level.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = IosTextSecondary,
                maxLines = 1
            )
        }
    }
}
