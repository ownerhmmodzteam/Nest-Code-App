package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.viewmodel.CodeNestViewModel
import kotlinx.coroutines.delay

import androidx.activity.compose.BackHandler

@Composable
fun OnboardingScreen(
    viewModel: CodeNestViewModel,
    onComplete: () -> Unit,
    onBackToLogin: (() -> Unit)? = null
) {
    val progress by viewModel.userProgress.collectAsState()
    var step by remember { mutableIntStateOf(0) }
    var selectedTier by remember { mutableStateOf("Beginner") }
    var selectedGoal by remember { mutableStateOf("Full Stack Web & Mobile") }
    var selectedMinutes by remember { mutableIntStateOf(30) }
    var usernameInput by remember { mutableStateOf(progress.username.ifBlank { "" }) }
    var isGeneratingPath by remember { mutableStateOf(false) }

    // Pre-populate with Google account name if available
    LaunchedEffect(progress.username) {
        if (usernameInput.isBlank() && progress.username.isNotBlank()) {
            usernameInput = progress.username
        }
    }

    // System Back Press logic to navigate to previous step
    BackHandler {
        if (step > 0) {
            step--
        } else if (onBackToLogin != null) {
            onBackToLogin()
        }
    }

    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Progress Indicator
            LinearProgressIndicator(
                progress = { (step + 1) / 5f },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 24.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            // Step Content
            AnimatedContent(
                targetState = step,
                label = "onboarding_step_anim"
            ) { currentStep ->
                when (currentStep) {
                    0 -> WelcomeStep()
                    1 -> LevelSelectionStep(
                        selectedTier = selectedTier,
                        onSelectTier = { selectedTier = it }
                    )
                    2 -> GoalSelectionStep(
                        selectedGoal = selectedGoal,
                        onSelectGoal = { selectedGoal = it }
                    )
                    3 -> CommitmentStep(
                        selectedMinutes = selectedMinutes,
                        onSelectMinutes = { selectedMinutes = it }
                    )
                    4 -> AccountAndGeneratingStep(
                        username = usernameInput,
                        email = progress.email,
                        onUsernameChange = { usernameInput = it },
                        isGenerating = isGeneratingPath
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if ((step > 0 || onBackToLogin != null) && !isGeneratingPath) {
                    OutlinedButton(
                        onClick = {
                            if (step > 0) step-- else onBackToLogin?.invoke()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Sebelumnya")
                    }
                }

                Button(
                    onClick = {
                        if (step < 4) {
                            step++
                        } else {
                            isGeneratingPath = true
                        }
                    },
                    modifier = Modifier
                        .weight(if (step > 0 || onBackToLogin != null) 1.5f else 1f)
                        .height(52.dp)
                        .testTag("onboarding_next_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    enabled = !isGeneratingPath
                ) {
                    if (isGeneratingPath) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Menyusun Roadmap...")
                    } else {
                        Text(
                            text = if (step == 4) "Mulai Belajar Sekarang" else "Lanjutkan",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }

    // Effect to simulate generating personalized path
    LaunchedEffect(isGeneratingPath) {
        if (isGeneratingPath) {
            delay(1200)
            viewModel.completeOnboarding(
                username = usernameInput.ifBlank { "Programmer" },
                tier = selectedTier,
                goal = selectedGoal,
                dailyMinutes = selectedMinutes
            )
            onComplete()
        }
    }
}

@Composable
private fun WelcomeStep() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.codenest_hero_1790266568930),
                contentDescription = "CodeNest Hero",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Selamat Datang di CodeNest",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Platform belajar pemrograman dari nol hingga profesional dengan roadmap terstruktur, playground interaktif, dan proyek nyata.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}

@Composable
private fun LevelSelectionStep(
    selectedTier: String,
    onSelectTier: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Pilih Tingkat Pengalamanmu",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Kami akan menyesuaikan titik awal kurikulum belajarmu.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
        )

        val tiers = listOf(
            Triple("Beginner", "Belum Pernah Coding", "Mulai dari Level 0: Logika komputer, dasar internet, dan struktur HTML."),
            Triple("Intermediate", "Paham Dasar Syntax", "Mulai dari Level 3: JavaScript modern, async/await, API, dan React."),
            Triple("Advanced", "Software Developer", "Fokus ke Clean Architecture, Sistem C++, Cybersecurity defensif, dan DevOps.")
        )

        tiers.forEach { (tier, subtitle, desc) ->
            val isSelected = selectedTier == tier
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable { onSelectTier(tier) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = tier,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "($subtitle)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GoalSelectionStep(
    selectedGoal: String,
    onSelectGoal: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Apa Tujuan Utama Belajarmu?",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Roadmap akan memprioritaskan teknologi yang paling relevan.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
        )

        val goals = listOf(
            Pair("Full Stack Web & Mobile", "React, Node.js, Express, dan Android Kotlin"),
            Pair("Mobile Application Engineer", "Native Android, Jetpack Compose, dan Flutter"),
            Pair("Cybersecurity & Defensive Tech", "OWASP Top 10, Kriptografi, Secure Coding"),
            Pair("Systems & Game Engineering", "C/C++, Algoritma Kompleksitas, dan Big-O"),
            Pair("AI, Python & Automation", "Pythonic OOP, Data Scripting, dan REST APIs")
        )

        goals.forEach { (goal, desc) ->
            val isSelected = selectedGoal == goal
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable { onSelectGoal(goal) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = goal,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CommitmentStep(
    selectedMinutes: Int,
    onSelectMinutes: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Target Komitmen Harian",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Belajar rutin sedikit setiap hari lebih efektif daripada belajar semalaman penuh.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
        )

        val times = listOf(
            Triple(15, "Santai (15 Menit/Hari)", "Cocok untuk pelajar yang sibuk. Menyelesaikan 1 lesson per hari."),
            Triple(30, "Reguler (30 Menit/Hari)", "Rekomendasi! 1 lesson + 1 tantangan coding + 1 quiz harian."),
            Triple(60, "Intensif (60 Menit/Hari)", "Cocok untuk percepatan karir programmer dalam 3 bulan.")
        )

        times.forEach { (mins, title, desc) ->
            val isSelected = selectedMinutes == mins
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable { onSelectMinutes(mins) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AccountAndGeneratingStep(
    username: String,
    email: String,
    onUsernameChange: (String) -> Unit,
    isGenerating: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.RocketLaunch,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(56.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Profil Pengguna",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Nama ini akan ditampilkan pada profil, leaderboard portofolio, dan sertifikat belajar.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
        )

        if (email.isNotBlank()) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "G",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Terhubung dengan Akun Google",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = email,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Nama akun disetel otomatis dari Google. Anda memiliki 1x kesempatan pergantian nama.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text("Nama Akun") },
            placeholder = { Text("Contoh: Alex, Sarah, Budi") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("onboarding_username_input"),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Info: Setiap akun memiliki batas 1 kali ganti nama untuk menjaga integritas kredensial.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
