package com.example.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.components.CodeNestTopBar
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.CodeEditorBg
import com.example.ui.theme.CodeGutterBg
import com.example.ui.theme.CodeGutterText
import com.example.viewmodel.CodeNestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaygroundScreen(
    viewModel: CodeNestViewModel
) {
    val state by viewModel.playgroundState.collectAsState()
    val userProgress by viewModel.userProgress.collectAsState()
    val snippets by viewModel.savedSnippets.collectAsState()

    var showSaveDialog by remember { mutableStateOf(false) }
    var snippetTitle by remember { mutableStateOf("") }
    var showSnippetsSheet by remember { mutableStateOf(false) }

    val languages = listOf("javascript", "html", "python", "kotlin", "sql", "cpp")

    Scaffold(
        topBar = {
            CodeNestTopBar(
                title = "Code Playground",
                streak = userProgress.streak,
                xp = userProgress.xp,
                actions = {
                    IconButton(
                        onClick = { showSnippetsSheet = true },
                        modifier = Modifier.testTag("playground_saved_snippets")
                    ) {
                        Icon(imageVector = Icons.Default.Bookmark, contentDescription = "Tersimpan")
                    }
                    IconButton(
                        onClick = { showSaveDialog = true },
                        modifier = Modifier.testTag("playground_save_snippet")
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = "Simpan Kode")
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
        ) {
            // Language selector chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                languages.forEach { lang ->
                    val isSelected = state.language.equals(lang, ignoreCase = true)
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setPlaygroundLanguage(lang) },
                        label = { Text(lang.uppercase(), fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            // Tab navigation: Editor | Output Console | Web Preview
            TabRow(
                selectedTabIndex = state.activeTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = state.activeTab == 0,
                    onClick = { viewModel.setPlaygroundTab(0) },
                    text = { Text("Code Editor") },
                    icon = { Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = state.activeTab == 1,
                    onClick = { viewModel.setPlaygroundTab(1) },
                    text = { Text("Console Output") },
                    icon = { Icon(Icons.Default.Terminal, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = state.activeTab == 2,
                    onClick = { viewModel.setPlaygroundTab(2) },
                    text = { Text("Web Preview") },
                    icon = { Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }

            // Body Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (state.activeTab) {
                    0 -> CodeEditorView(
                        code = state.code,
                        onCodeChange = { viewModel.updatePlaygroundCode(it) }
                    )
                    1 -> ConsoleOutputView(
                        output = state.output,
                        isRunning = state.isRunning
                    )
                    2 -> SandboxedWebPreviewView(
                        html = state.previewHtml
                    )
                }
            }

            // Bottom action buttons: Run, Reset, Tab Jump
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { viewModel.resetPlaygroundCode() },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset")
                    }

                    Button(
                        onClick = { viewModel.runPlaygroundCode() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("playground_run_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        enabled = !state.isRunning
                    ) {
                        if (state.isRunning) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Jalankan Kode", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Save Snippet Dialog
        if (showSaveDialog) {
            AlertDialog(
                onDismissRequest = { showSaveDialog = false },
                title = { Text("Simpan Cuplikan Kode") },
                text = {
                    OutlinedTextField(
                        value = snippetTitle,
                        onValueChange = { snippetTitle = it },
                        label = { Text("Judul Snippet") },
                        placeholder = { Text("Contoh: Solusi Algoritma Saya") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.saveCurrentSnippet(snippetTitle)
                            showSaveDialog = false
                            snippetTitle = ""
                        }
                    ) {
                        Text("Simpan")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSaveDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }

        // Saved Snippets Bottom Sheet
        if (showSnippetsSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSnippetsSheet = false }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .padding(bottom = 32.dp)
                ) {
                    Text(
                        text = "Snippet Tersimpan (${snippets.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (snippets.isEmpty()) {
                        Text(
                            text = "Belum ada snippet tersimpan. Tulis kode lalu klik ikon simpan di atas!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        snippets.forEach { snippet ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = snippet.title,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "${snippet.language.uppercase()} • Tersimpan",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    Row {
                                        IconButton(
                                            onClick = {
                                                viewModel.setPlaygroundLanguage(snippet.language)
                                                viewModel.updatePlaygroundCode(snippet.code)
                                                showSnippetsSheet = false
                                            }
                                        ) {
                                            Icon(imageVector = Icons.Default.FileOpen, contentDescription = "Muat")
                                        }
                                        IconButton(
                                            onClick = { viewModel.deleteSnippet(snippet.id) }
                                        ) {
                                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Hapus")
                                        }
                                    }
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
private fun CodeEditorView(
    code: String,
    onCodeChange: (String) -> Unit
) {
    val lines = code.lines()

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(CodeEditorBg)
    ) {
        // Line Numbers Gutter
        Column(
            modifier = Modifier
                .width(42.dp)
                .fillMaxHeight()
                .background(CodeGutterBg)
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.End
        ) {
            lines.indices.forEach { index ->
                Text(
                    text = "${index + 1}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    color = CodeGutterText,
                    lineHeight = 20.sp
                )
            }
        }

        // Editable Text Area
        OutlinedTextField(
            value = code,
            onValueChange = onCodeChange,
            modifier = Modifier
                .fillMaxSize()
                .testTag("code_playground_editor"),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                lineHeight = 20.sp
            ),
            shape = RoundedCornerShape(0.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CodeEditorBg,
                unfocusedContainerColor = CodeEditorBg,
                focusedTextColor = Color(0xFFE6EDF3),
                unfocusedTextColor = Color(0xFFE6EDF3),
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            )
        )
    }
}

@Composable
private fun ConsoleOutputView(
    output: String,
    isRunning: Boolean
) {
    Surface(
        color = Color(0xFF090D16),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "🖥️", fontSize = 16.sp)
                Text(
                    text = "Standard Output (Stdout / Stderr)",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = AccentGreen
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isRunning) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mengeksekusi kode di sandbox runtime...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else if (output.isBlank()) {
                Text(
                    text = "Console kosong. Tekan 'Jalankan Kode' untuk melihat hasil output eksekusi.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF6E7681)
                )
            } else {
                Text(
                    text = output,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    color = Color(0xFF58A6FF),
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun SandboxedWebPreviewView(
    html: String
) {
    if (html.isBlank()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CodeEditorBg),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Pilih bahasa 'HTML' lalu klik 'Jalankan Kode' untuk melihat web preview.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .testTag("playground_webview"),
            factory = { context ->
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    // Sandboxed configuration: Javascript enabled for visual DOM elements, no file scheme access
                    settings.javaScriptEnabled = true
                    settings.allowFileAccess = false
                    settings.allowContentAccess = false
                    settings.domStorageEnabled = true
                    loadDataWithBaseURL("https://sandbox.codenest.local", html, "text/html", "UTF-8", null)
                }
            },
            update = { webView ->
                webView.loadDataWithBaseURL("https://sandbox.codenest.local", html, "text/html", "UTF-8", null)
            }
        )
    }
}
