package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.CodeSandboxEngine
import com.example.model.Lesson
import com.example.ui.components.HighlightedCodeViewer
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.CodeEditorBg
import com.example.viewmodel.CodeNestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonDetailScreen(
    lesson: Lesson,
    viewModel: CodeNestViewModel,
    onBackClick: () -> Unit,
    onOpenPlayground: () -> Unit,
    onStartQuiz: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    var practiceInput by remember { mutableStateOf(lesson.starterCode) }
    var consoleOutput by remember { mutableStateOf("") }
    var isCheckingChallenge by remember { mutableStateOf(false) }
    var challengePassed by remember { mutableStateOf<Boolean?>(null) }
    var isCompleted by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = lesson.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text(
                            text = "${lesson.durationMinutes} Menit",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(20.dp)
        ) {
            // 1. CONCEPT EXPLANATION
            Text(
                text = "1. Penjelasan Konsep",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = lesson.conceptExplanation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. CODE EXAMPLE
            Text(
                text = "2. Contoh Kode & Sintaks",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            HighlightedCodeViewer(
                code = lesson.codeSnippet,
                language = lesson.codeLanguage
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 3. LINE-BY-LINE EXPLANATION
            Text(
                text = "3. Penjelasan Baris demi Baris",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            lesson.lineByLine.forEach { lineExp ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = lineExp.lineCode,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = lineExp.explanation,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. INTERACTIVE PRACTICE CHALLENGE
            Text(
                text = "4. Tantangan Praktik Langsung",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = lesson.practiceChallenge,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Interactive Editor Field
            OutlinedTextField(
                value = practiceInput,
                onValueChange = {
                    practiceInput = it
                    challengePassed = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp)
                    .testTag("practice_code_input"),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp
                ),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CodeEditorBg,
                    unfocusedContainerColor = CodeEditorBg,
                    focusedTextColor = Color(0xFFE6EDF3),
                    unfocusedTextColor = Color(0xFFE6EDF3)
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Run & Check Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        val res = CodeSandboxEngine.executeCode(practiceInput, lesson.codeLanguage)
                        consoleOutput = res.output
                        val passed = practiceInput.contains(lesson.expectedKeywordsOrOutput, ignoreCase = true) ||
                                res.output.contains(lesson.expectedKeywordsOrOutput, ignoreCase = true)
                        challengePassed = passed
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Jalankan & Verifikasi")
                }

                OutlinedButton(
                    onClick = {
                        practiceInput = lesson.starterCode
                        consoleOutput = ""
                        challengePassed = null
                    },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset")
                }
            }

            // Challenge Result / Console Output
            if (consoleOutput.isNotBlank() || challengePassed != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (challengePassed == true) AccentGreen.copy(alpha = 0.15f)
                    else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (challengePassed == true) "✅ Tantangan Berhasil diselesaikan!"
                            else if (challengePassed == false) "⚠️ Belum tepat. Periksa kembali instruksi di atas."
                            else "Output Console:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (challengePassed == true) AccentGreen else MaterialTheme.colorScheme.onSurface
                        )
                        if (consoleOutput.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = consoleOutput,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5. SUMMARY
            Text(
                text = "5. Ringkasan Materi",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = lesson.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // COMPLETE LESSON ACTION BUTTON
            Button(
                onClick = {
                    viewModel.completeCurrentLesson()
                    isCompleted = true
                    onBackClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("complete_lesson_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Selesaikan Lesson (+25 XP)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
