package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import com.example.ui.theme.AccentGreen
import com.example.viewmodel.CodeNestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlgorithmVisualizerScreen(
    viewModel: CodeNestViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.visualizerState.collectAsState()
    val scrollState = rememberScrollState()

    val currentStep = state.steps.getOrNull(state.currentStepIndex)
    val arrayData = currentStep?.arrayState ?: listOf(64, 34, 25, 12, 22, 11, 90)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Visualisasi Algoritma", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Algorithm Switcher Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilterChip(
                    selected = state.algorithmName == "Bubble Sort",
                    onClick = { viewModel.initAlgorithm("Bubble Sort") },
                    label = { Text("Bubble Sort O(N²)") }
                )
                FilterChip(
                    selected = state.algorithmName == "Binary Search",
                    onClick = { viewModel.initAlgorithm("Binary Search") },
                    label = { Text("Binary Search O(log N)") }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Step Counter
            Text(
                text = "Langkah ${state.currentStepIndex + 1} dari ${state.steps.size}",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ANIMATED BAR GRAPH CANVAS
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val maxValue = (arrayData.maxOrNull() ?: 100).coerceAtLeast(1)
                        arrayData.forEachIndexed { index, value ->
                            val isComparing = currentStep?.activeIndices?.contains(index) == true
                            val isSorted = currentStep?.sortedIndices?.contains(index) == true

                            val barColor by animateColorAsState(
                                targetValue = when {
                                    isSorted -> AccentGreen
                                    isComparing -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                                },
                                label = "bar_color_$index"
                            )

                            val heightDp by animateDpAsState(
                                targetValue = ((value.toFloat() / maxValue) * 140).dp.coerceAtLeast(16.dp),
                                label = "bar_height_$index"
                            )

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "$value",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(28.dp)
                                        .height(heightDp)
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(barColor)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "[$index]",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // STEP DESCRIPTION CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "💡", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Penjelasan Langkah:",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = currentStep?.description ?: "Memuat langkah...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // PLAYER CONTROLS (Step Back, Play/Pause, Step Forward)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.stepBack() },
                    enabled = state.currentStepIndex > 0,
                    modifier = Modifier.size(52.dp)
                ) {
                    Icon(imageVector = Icons.Default.SkipPrevious, contentDescription = "Langkah Mundur", modifier = Modifier.size(28.dp))
                }

                Spacer(modifier = Modifier.width(16.dp))

                FloatingActionButton(
                    onClick = { viewModel.toggleVisualizerPlay() },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("visualizer_play_pause")
                ) {
                    Icon(
                        imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (state.isPlaying) "Jeda" else "Putar Otomatis",
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(
                    onClick = { viewModel.stepForward() },
                    enabled = state.currentStepIndex < state.steps.size - 1,
                    modifier = Modifier.size(52.dp)
                ) {
                    Icon(imageVector = Icons.Default.SkipNext, contentDescription = "Langkah Maju", modifier = Modifier.size(28.dp))
                }

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(
                    onClick = { viewModel.initAlgorithm(state.algorithmName) },
                    modifier = Modifier.size(52.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Ulangi dari Awal", modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}
