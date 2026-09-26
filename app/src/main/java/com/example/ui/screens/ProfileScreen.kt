package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.catalog.CurriculumLevelsAdvanced
import com.example.ui.components.CodeNestTopBar
import com.example.ui.components.IosGlassCard
import com.example.ui.theme.*
import com.example.util.XpLevelCalculator
import com.example.viewmodel.CodeNestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: CodeNestViewModel,
    onSignInGoogle: (() -> Unit)? = null
) {
    val progress by viewModel.userProgress.collectAsState()
    val scrollState = rememberScrollState()

    var showResetDialog by remember { mutableStateOf(false) }

    val unlockedSet = progress.unlockedAchievementsString.split(",").toSet()
    val completedList = progress.completedLessonsString.split(",").filter { it.isNotBlank() }

    val currentLevel = XpLevelCalculator.calculateLevel(progress.xp)
    val milestoneTitle = XpLevelCalculator.getMilestoneTitle(currentLevel)
    val levelRank = if (currentLevel >= 9999) "LEVEL 9999 (MAX LEVEL) • $milestoneTitle"
    else "Level $currentLevel: $milestoneTitle"

    Scaffold(
        topBar = {
            CodeNestTopBar(
                title = "Profil Saya",
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
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // iOS 28 Hero Profile Card (Squircle + Liquid Gradient Glow)
            IosGlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 28.dp,
                highlightGlow = IosNeonCyan
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(86.dp)
                            .clip(CircleShape)
                            .background(IosNeonCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.codenest_badge_1790266583533),
                            contentDescription = "Badge Profile",
                            modifier = Modifier.size(66.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = progress.username,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = IosNeonCyan.copy(alpha = 0.16f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, IosNeonCyan.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = levelRank,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = IosNeonCyan,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Tujuan Belajar: ${progress.learningGoal}",
                        style = MaterialTheme.typography.bodySmall,
                        color = IosTextSecondary
                    )
                }
            }

            if (progress.uid == "guest_local" || progress.uid.startsWith("user_local")) {
                Spacer(modifier = Modifier.height(14.dp))
                IosGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 22.dp,
                    highlightGlow = IosNeonAmber
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Akun Tamu",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Progress tamu hanya tersimpan di perangkat ini. Masuk dengan Google untuk menyimpan progress di cloud dan membawanya ke perangkat lain.",
                            style = MaterialTheme.typography.bodySmall,
                            color = IosTextSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { onSignInGoogle?.invoke() },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = onSignInGoogle != null
                        ) {
                            Text("Masuk dengan Google")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // STATS ROW (3 Cards)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCardItem(title = "Total XP", value = "${progress.xp}", icon = "⚡", color = IosNeonCyan, modifier = Modifier.weight(1f))
                StatCardItem(title = "Streak", value = "${progress.streak} Hari", icon = "🔥", color = IosNeonAmber, modifier = Modifier.weight(1f))
                StatCardItem(title = "Selesai", value = "${completedList.size}", icon = "📚", color = IosNeonEmerald, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ACHIEVEMENTS / MEDALS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daftar Medali & Prestasi",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.2).sp
                    ),
                    color = Color.White
                )
                Text(
                    text = "${unlockedSet.size}/${CurriculumLevelsAdvanced.achievementsList.size} Terbuka",
                    style = MaterialTheme.typography.labelSmall,
                    color = IosNeonCyan
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            CurriculumLevelsAdvanced.achievementsList.forEach { ach ->
                val isUnlocked = unlockedSet.contains(ach.id)
                IosGlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    cornerRadius = 20.dp,
                    highlightGlow = if (isUnlocked) IosNeonEmerald else null
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isUnlocked) IosNeonEmerald.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = ach.iconEmoji, fontSize = 24.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = ach.title,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isUnlocked) Color.White else IosTextSecondary
                            )
                            Text(
                                text = ach.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = IosTextSecondary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isUnlocked) IosNeonEmerald.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.05f)
                        ) {
                            Text(
                                text = "+${ach.xpReward} XP",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isUnlocked) IosNeonEmerald else IosTextTertiary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // iOS STYLE SYSTEM SETTINGS CARD
            IosGlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 22.dp
            ) {
                Text(
                    text = "PENGATURAN AKUN",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    ),
                    color = IosTextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { showResetDialog = true }
                        .padding(vertical = 10.dp, horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = IosNeonCoral.copy(alpha = 0.15f),
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.RestartAlt,
                                    contentDescription = null,
                                    tint = IosNeonCoral,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Reset Semua Progress Belajar",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = IosNeonCoral
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = IosTextTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }

        // Reset Confirmation Dialog (iOS Alert Style)
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = {
                    Text(
                        "Reset Progress Belajar?",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                text = {
                    Text(
                        "Semua XP, level, riwayat kuis, dan data submission code playground akan dikembalikan ke status awal.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = IosTextSecondary
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.resetAllProgress()
                            showResetDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IosNeonCoral),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Reset Sekarang", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("Batal", color = Color.White)
                    }
                },
                containerColor = IosDarkSurfaceVariant,
                shape = RoundedCornerShape(26.dp)
            )
        }
    }
}
