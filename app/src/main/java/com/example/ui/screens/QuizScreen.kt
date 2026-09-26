package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import com.example.ui.components.HighlightedCodeViewer
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentRed
import com.example.viewmodel.CodeNestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: CodeNestViewModel,
    onBackClick: () -> Unit
) {
    val quizState by viewModel.quizState.collectAsState()
    val quiz = quizState.quiz

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(quiz?.title ?: "Kuis Pemrograman", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (quiz == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Kuis tidak ditemukan.")
            }
        } else if (quizState.isCompleted) {
            // QUIZ COMPLETION SUMMARY
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "🎉", fontSize = 56.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Kuis Selesai!",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                val scorePercent = (quizState.correctCount * 100) / quiz.questions.size
                Text(
                    text = "Skor Kamu: $scorePercent% (${quizState.correctCount} dari ${quiz.questions.size} Benar)",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "+${quiz.xpReward} XP Didapatkan!",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onBackClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Kembali ke Pembelajaran")
                }
            }
        } else {
            val currentQ = quiz.questions.getOrNull(quizState.currentQuestionIndex) ?: return@Scaffold
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .padding(20.dp)
            ) {
                // Progress Indicator
                LinearProgressIndicator(
                    progress = { (quizState.currentQuestionIndex + 1).toFloat() / quiz.questions.size },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Pertanyaan ${quizState.currentQuestionIndex + 1} dari ${quiz.questions.size}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Question Text
                Text(
                    text = currentQ.question,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground,
                    lineHeight = 24.sp
                )

                // Optional code snippet
                if (!currentQ.codeSnippet.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    HighlightedCodeViewer(
                        code = currentQ.codeSnippet,
                        language = "code"
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Options List
                currentQ.options.forEachIndexed { index, option ->
                    val isSelected = quizState.selectedOptionIndex == index
                    val isChecked = quizState.isAnswerChecked
                    val isCorrect = index == currentQ.correctIndex

                    val cardBg = when {
                        isChecked && isCorrect -> AccentGreen.copy(alpha = 0.2f)
                        isChecked && isSelected && !isCorrect -> AccentRed.copy(alpha = 0.2f)
                        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        else -> MaterialTheme.colorScheme.surface
                    }

                    val borderColor = when {
                        isChecked && isCorrect -> AccentGreen
                        isChecked && isSelected && !isCorrect -> AccentRed
                        isSelected -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(width = 1.5.dp, color = borderColor, shape = RoundedCornerShape(14.dp))
                            .clickable(enabled = !isChecked) { viewModel.selectQuizOption(index) }
                            .testTag("quiz_option_$index"),
                        colors = CardDefaults.cardColors(containerColor = cardBg)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isChecked && isCorrect -> AccentGreen
                                            isChecked && isSelected && !isCorrect -> AccentRed
                                            isSelected -> MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.colorScheme.surfaceVariant
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isChecked && isCorrect) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                } else if (isChecked && isSelected && !isCorrect) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                } else {
                                    Text(
                                        text = "${('A'.code + index).toChar()}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Detailed Explanation Card (When checked)
                if (quizState.isAnswerChecked) {
                    Spacer(modifier = Modifier.height(16.dp))
                    val wasCorrect = quizState.selectedOptionIndex == currentQ.correctIndex

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (wasCorrect) AccentGreen.copy(alpha = 0.12f) else AccentRed.copy(alpha = 0.12f)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (wasCorrect) "🎉 Jawabanmu Tepat!" else "💡 Konsep yang Benar:",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (wasCorrect) AccentGreen else AccentRed
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = currentQ.explanation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 22.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Referensi: ${currentQ.conceptDoc}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Bottom Action Button
                if (!quizState.isAnswerChecked) {
                    Button(
                        onClick = { viewModel.checkQuizAnswer() },
                        enabled = quizState.selectedOptionIndex != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("quiz_check_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Periksa Jawaban", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = { viewModel.nextQuizQuestion() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("quiz_next_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            if (quizState.currentQuestionIndex < quiz.questions.size - 1) "Pertanyaan Selanjutnya"
                            else "Lihat Hasil Akhir",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
