package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
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
import com.example.data.catalog.CurriculumCatalog
import com.example.model.ProjectDefinition
import com.example.ui.components.CodeNestTopBar
import com.example.ui.components.IosGlassCard
import com.example.ui.theme.*
import com.example.viewmodel.CodeNestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(
    viewModel: CodeNestViewModel
) {
    val progress by viewModel.userProgress.collectAsState()
    val submissions by viewModel.projectSubmissions.collectAsState()

    var activeProjectForModal by remember { mutableStateOf<ProjectDefinition?>(null) }
    var selectedDifficultyFilter by remember { mutableStateOf("All") }

    val allProjects = remember {
        val list = mutableListOf<ProjectDefinition>()
        CurriculumCatalog.getAllLevels().forEach { level ->
            if (level.project != null) {
                list.add(level.project)
            }
        }
        // Additional showcase projects for advanced levels
        list.add(
            ProjectDefinition(
                id = "proj-intermediate-todo",
                title = "Interactive Task Management Engine",
                levelId = 3,
                difficulty = "Intermediate",
                brief = "Bangun modul Todo List berbasis JavaScript modern dengan filter status, event listener, dan LocalStorage persisten.",
                requirements = listOf(
                    "Menggunakan fungsi add, delete, toggle",
                    "Menyimpan state ke array objek",
                    "Menangani event click tanpa reload halaman"
                ),
                hints = listOf("Gunakan array method filter() dan map()", "Cegah default form submit event"),
                starterCode = """
                    const tasks = [];
                    function addTask(title) {
                      tasks.push({ id: Date.now(), title, completed: false });
                    }
                    addTask("Selesaikan Project CodeNest");
                    console.log(tasks);
                """.trimIndent(),
                language = "javascript",
                validationRules = listOf("function addTask", "tasks.push")
            )
        )
        list.add(
            ProjectDefinition(
                id = "proj-advanced-api",
                title = "Defensive Authentication Gateway",
                levelId = 15,
                difficulty = "Advanced",
                brief = "Buat middleware keamanan REST API untuk mencegah serangan Brute Force dan verifikasi integritas JWT token.",
                requirements = listOf(
                    "Menerapkan rate limiting middleware",
                    "Memverifikasi Authorization Bearer header",
                    "Sanitasi payload input dari karakter injeksi"
                ),
                hints = listOf("Gunakan Express middleware (req, res, next)", "Kembalikan HTTP 401 jika token invalid"),
                starterCode = """
                    function authMiddleware(req, res, next) {
                      const authHeader = req.headers['authorization'];
                      if (!authHeader || !authHeader.startsWith('Bearer ')) {
                        return res.status(401).json({ error: 'Unauthorized' });
                      }
                      next();
                    }
                """.trimIndent(),
                language = "javascript",
                validationRules = listOf("authMiddleware", "status(401)")
            )
        )
        list
    }

    val filteredProjects = if (selectedDifficultyFilter == "All") allProjects
    else allProjects.filter { it.difficulty.equals(selectedDifficultyFilter, ignoreCase = true) }

    Scaffold(
        topBar = {
            CodeNestTopBar(
                title = "Portofolio & Project",
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
            // Difficulty Filters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Beginner", "Intermediate", "Advanced").forEach { diff ->
                    FilterChip(
                        selected = selectedDifficultyFilter == diff,
                        onClick = { selectedDifficultyFilter = diff },
                        label = { Text(diff, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                    )
                }
            }

            // Projects List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 48.dp)
            ) {
                items(filteredProjects) { project ->
                    val submission = submissions.find { it.projectId == project.id }
                    val isCompleted = submission?.passed == true

                    IosGlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .testTag("project_item_${project.id}"),
                        cornerRadius = 22.dp,
                        highlightGlow = if (isCompleted) IosNeonEmerald else IosNeonCyan,
                        onClick = { activeProjectForModal = project }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = when (project.difficulty) {
                                    "Beginner" -> IosNeonEmerald.copy(alpha = 0.16f)
                                    "Intermediate" -> IosNeonCyan.copy(alpha = 0.16f)
                                    else -> IosNeonViolet.copy(alpha = 0.16f)
                                }
                            ) {
                                Text(
                                    text = project.difficulty.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                    color = when (project.difficulty) {
                                        "Beginner" -> IosNeonEmerald
                                        "Intermediate" -> IosNeonCyan
                                        else -> IosNeonViolet
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            if (isCompleted) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = IosNeonEmerald, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Selesai", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = IosNeonEmerald)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = project.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )

                        Text(
                            text = project.brief,
                            style = MaterialTheme.typography.bodySmall,
                            color = IosTextSecondary,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${project.requirements.size} Persyaratan • ${project.language.uppercase()}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = IosNeonCyan
                            )

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isCompleted) Color.White.copy(alpha = 0.12f) else IosNeonCyan
                            ) {
                                Text(
                                    text = if (isCompleted) "Review" else "Buka Project",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (isCompleted) Color.White else Color(0xFF001B24),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Project Submission Modal
        if (activeProjectForModal != null) {
            val proj = activeProjectForModal!!
            ProjectWorkModal(
                project = proj,
                onDismiss = { activeProjectForModal = null },
                onSubmit = { code ->
                    viewModel.submitProject(proj, code)
                    activeProjectForModal = null
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProjectWorkModal(
    project: ProjectDefinition,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var codeInput by remember { mutableStateOf(project.starterCode) }
    var currentTab by remember { mutableIntStateOf(0) } // 0: Brief & Requirements, 1: Code Submission

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = project.title,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(10.dp))

            TabRow(selectedTabIndex = currentTab) {
                Tab(selected = currentTab == 0, onClick = { currentTab = 0 }, text = { Text("Deskripsi") })
                Tab(selected = currentTab == 1, onClick = { currentTab = 1 }, text = { Text("Editor Submission") })
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (currentTab == 0) {
                Text(
                    text = "Deskripsi Project:",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(text = project.brief, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Persyaratan Wajib:",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                project.requirements.forEach { req ->
                    Text(text = "• $req", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 2.dp))
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Petunjuk & Hints:",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                project.hints.forEach { hint ->
                    Text(text = "💡 $hint", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 2.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { currentTab = 1 },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Lanjut Tulis Kode Solusi")
                }
            } else {
                Text(
                    text = "Tulis Kode Solusi (${project.language.uppercase()}):",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = codeInput,
                    onValueChange = { codeInput = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .testTag("project_code_input"),
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

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { onSubmit(codeInput) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("project_submit_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Kirim & Uji Validasi Project (+150 XP)", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
