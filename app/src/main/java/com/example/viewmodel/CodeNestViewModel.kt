package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.catalog.CurriculumCatalog
import com.example.data.catalog.CurriculumLevelsAdvanced
import com.example.data.local.AppDatabase
import com.example.data.local.entity.ProjectSubmissionEntity
import com.example.data.local.entity.QuizScoreEntity
import com.example.data.local.entity.SavedSnippetEntity
import com.example.data.local.entity.UserProgressEntity
import com.example.data.repository.CodeNestRepository
import com.example.engine.AlgorithmVisualizer
import com.example.engine.CodeSandboxEngine
import com.example.engine.VisualizerStep
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CodePlaygroundUiState(
    val language: String = "javascript",
    val code: String = "// Selamat datang di CodeNest Sandbox\nconsole.log(\"Halo Dunia dari CodeNest!\");",
    val output: String = "",
    val isRunning: Boolean = false,
    val previewHtml: String = "",
    val activeTab: Int = 0 // 0: Editor, 1: Console, 2: Web Preview
)

data class QuizUiState(
    val quiz: QuizDefinition? = null,
    val currentQuestionIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val isAnswerChecked: Boolean = false,
    val correctCount: Int = 0,
    val isCompleted: Boolean = false
)

data class VisualizerUiState(
    val algorithmName: String = "Bubble Sort",
    val currentStepIndex: Int = 0,
    val steps: List<VisualizerStep> = emptyList(),
    val isPlaying: Boolean = false
)

class CodeNestViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CodeNestRepository

    init {
        val database = AppDatabase.getInstance(application)
        repository = CodeNestRepository(database.codeNestDao())
    }

    val userProgress: StateFlow<UserProgressEntity> = repository.userProgress
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProgressEntity()
        )

    val quizScores: StateFlow<List<QuizScoreEntity>> = repository.quizScores
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val projectSubmissions: StateFlow<List<ProjectSubmissionEntity>> = repository.projectSubmissions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val savedSnippets: StateFlow<List<SavedSnippetEntity>> = repository.savedSnippets
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Active Lesson state
    private val _currentLesson = MutableStateFlow<Lesson?>(null)
    val currentLesson: StateFlow<Lesson?> = _currentLesson.asStateFlow()

    // Playground state
    private val _playgroundState = MutableStateFlow(CodePlaygroundUiState())
    val playgroundState: StateFlow<CodePlaygroundUiState> = _playgroundState.asStateFlow()

    // Quiz state
    private val _quizState = MutableStateFlow(QuizUiState())
    val quizState: StateFlow<QuizUiState> = _quizState.asStateFlow()

    // Visualizer state
    private val _visualizerState = MutableStateFlow(VisualizerUiState())
    val visualizerState: StateFlow<VisualizerUiState> = _visualizerState.asStateFlow()
    private var visualizerJob: Job? = null

    // Search query state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchFilter = MutableStateFlow("All")
    val searchFilter: StateFlow<String> = _searchFilter.asStateFlow()

    val searchResults: StateFlow<List<Lesson>> = combine(_searchQuery, _searchFilter) { query, filter ->
        val allLessons = CurriculumCatalog.getAllLevels().flatMap { level ->
            level.modules.flatMap { it.lessons }
        }
        if (query.isBlank()) {
            if (filter == "All") allLessons.take(8)
            else allLessons.filter { it.codeLanguage.equals(filter, ignoreCase = true) }
        } else {
            allLessons.filter { lesson ->
                val matchesQuery = lesson.title.contains(query, ignoreCase = true) ||
                        lesson.conceptExplanation.contains(query, ignoreCase = true) ||
                        lesson.codeLanguage.contains(query, ignoreCase = true)
                val matchesFilter = (filter == "All" || lesson.codeLanguage.equals(filter, ignoreCase = true))
                matchesQuery && matchesFilter
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Initialize visualizer with default Bubble Sort
        initAlgorithm("Bubble Sort")
    }

    fun completeOnboarding(username: String, tier: String, goal: String, dailyMinutes: Int) {
        viewModelScope.launch {
            repository.completeOnboarding(username, tier, goal, dailyMinutes)
        }
    }

    fun signInWithGoogle(uid: String, displayName: String, email: String, photoUrl: String = "", onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.signInWithGoogle(uid, displayName, email, photoUrl)
            onComplete()
        }
    }

    fun syncCurrentGoogleAccount() {
        viewModelScope.launch { repository.syncCurrentGoogleAccount() }
    }

    fun changeUsername(newName: String, onComplete: (Result<String>) -> Unit) {
        viewModelScope.launch {
            val result = repository.changeUsername(newName)
            onComplete(result)
        }
    }

    fun selectLesson(lessonId: String) {
        val lesson = CurriculumCatalog.findLessonById(lessonId)
        _currentLesson.value = lesson
        if (lesson != null) {
            _playgroundState.update {
                it.copy(
                    language = lesson.codeLanguage,
                    code = lesson.starterCode.ifBlank { lesson.codeSnippet },
                    output = "",
                    previewHtml = if (lesson.codeLanguage == "html") {
                        CodeSandboxEngine.buildSandboxedHtml(lesson.codeSnippet)
                    } else ""
                )
            }
        }
    }

    fun completeCurrentLesson() {
        val lesson = _currentLesson.value ?: return
        viewModelScope.launch {
            repository.completeLesson(lesson.id, lesson.nextLessonId)
            if (lesson.nextLessonId != null) {
                selectLesson(lesson.nextLessonId)
            }
        }
    }

    // Playground operations
    fun updatePlaygroundCode(newCode: String) {
        _playgroundState.update { it.copy(code = newCode) }
    }

    fun setPlaygroundLanguage(newLanguage: String) {
        val starter = when (newLanguage.lowercase()) {
            "html" -> """
                <div style="text-align: center; padding: 20px;">
                  <h1 style="color: #38bdf8;">CodeNest Sandbox</h1>
                  <p>Coba ubah HTML dan klik jalankan!</p>
                  <button onclick="alert('Halo dari Web Sandbox!')">Klik Saya</button>
                </div>
            """.trimIndent()
            "python" -> "def greet(name):\n    return f'Halo, {name}!'\n\nprint(greet('CodeNest Developer'))"
            "sql" -> "SELECT username, xp FROM users WHERE xp > 100 ORDER BY xp DESC;"
            "kotlin" -> "fun main() {\n    val greeting = \"Selamat belajar Jetpack Compose & Kotlin!\"\n    println(greeting)\n}"
            "cpp" -> "#include <iostream>\n\nint main() {\n    std::cout << \"Halo dari C++ Compiler!\" << std::endl;\n    return 0;\n}"
            else -> "console.log(\"CodeNest Web Engine Aktif!\");"
        }
        _playgroundState.update {
            it.copy(
                language = newLanguage,
                code = starter,
                output = "",
                previewHtml = if (newLanguage.equals("html", ignoreCase = true)) {
                    CodeSandboxEngine.buildSandboxedHtml(starter)
                } else ""
            )
        }
    }

    fun setPlaygroundTab(index: Int) {
        _playgroundState.update { it.copy(activeTab = index) }
    }

    fun runPlaygroundCode() {
        val state = _playgroundState.value
        _playgroundState.update { it.copy(isRunning = true) }

        viewModelScope.launch {
            delay(200) // Brief natural feel
            val result = CodeSandboxEngine.executeCode(state.code, state.language)
            val sandboxed = if (state.language.equals("html", true) || state.language.equals("javascript", true)) {
                if (state.language.equals("html", true)) {
                    CodeSandboxEngine.buildSandboxedHtml(state.code)
                } else {
                    CodeSandboxEngine.buildSandboxedHtml(
                        htmlContent = "<h3>Output JavaScript:</h3><div id='log-display'></div>",
                        jsContent = state.code
                    )
                }
            } else ""

            _playgroundState.update {
                it.copy(
                    isRunning = false,
                    output = result.output,
                    previewHtml = sandboxed,
                    activeTab = if (state.language.equals("html", true)) 2 else 1
                )
            }
        }
    }

    fun resetPlaygroundCode() {
        val lang = _playgroundState.value.language
        setPlaygroundLanguage(lang)
    }

    fun saveCurrentSnippet(title: String) {
        val state = _playgroundState.value
        viewModelScope.launch {
            repository.saveSnippet(
                title = title.ifBlank { "Snippet ${state.language.uppercase()}" },
                language = state.language,
                code = state.code
            )
        }
    }

    fun deleteSnippet(id: Int) {
        viewModelScope.launch {
            repository.deleteSnippet(id)
        }
    }

    // Quiz operations
    fun startQuiz(quizId: String) {
        val quiz = CurriculumCatalog.findQuizById(quizId)
        if (quiz != null) {
            _quizState.value = QuizUiState(
                quiz = quiz,
                currentQuestionIndex = 0,
                selectedOptionIndex = null,
                isAnswerChecked = false,
                correctCount = 0,
                isCompleted = false
            )
        }
    }

    fun selectQuizOption(index: Int) {
        if (!_quizState.value.isAnswerChecked) {
            _quizState.update { it.copy(selectedOptionIndex = index) }
        }
    }

    fun checkQuizAnswer() {
        val state = _quizState.value
        val quiz = state.quiz ?: return
        val currentQ = quiz.questions.getOrNull(state.currentQuestionIndex) ?: return
        val isCorrect = state.selectedOptionIndex == currentQ.correctIndex

        _quizState.update {
            it.copy(
                isAnswerChecked = true,
                correctCount = if (isCorrect) it.correctCount + 1 else it.correctCount
            )
        }
    }

    fun nextQuizQuestion() {
        val state = _quizState.value
        val quiz = state.quiz ?: return
        val nextIndex = state.currentQuestionIndex + 1

        if (nextIndex < quiz.questions.size) {
            _quizState.update {
                it.copy(
                    currentQuestionIndex = nextIndex,
                    selectedOptionIndex = null,
                    isAnswerChecked = false
                )
            }
        } else {
            // Quiz completed
            _quizState.update { it.copy(isCompleted = true) }
            val scorePercent = (state.correctCount * 100) / quiz.questions.size
            viewModelScope.launch {
                repository.saveQuizScore(
                    quizId = quiz.id,
                    levelId = quiz.levelId,
                    scorePercent = scorePercent
                )
            }
        }
    }

    // Project submission
    fun submitProject(project: ProjectDefinition, submittedCode: String) {
        val hasPassed = project.validationRules.all { rule -> submittedCode.contains(rule, ignoreCase = true) }
        val feedback = if (hasPassed) {
            "Luar biasa! Seluruh persyaratan dan skema semantic telah terpenuhi dengan baik."
        } else {
            "Masih ada tag atau persyaratan yang terlewat. Periksa kembali struktur kode dan hints."
        }
        viewModelScope.launch {
            repository.saveProjectSubmission(
                projectId = project.id,
                title = project.title,
                levelId = project.levelId,
                codeSubmitted = submittedCode,
                passed = hasPassed,
                feedback = feedback,
                difficulty = project.difficulty
            )
        }
    }

    // Algorithm Visualizer
    fun initAlgorithm(algorithmName: String) {
        visualizerJob?.cancel()
        val steps = when (algorithmName) {
            "Binary Search" -> AlgorithmVisualizer.generateBinarySearchSteps(
                sortedList = listOf(4, 9, 15, 23, 38, 42, 56, 71, 88, 95),
                target = 42
            )
            else -> AlgorithmVisualizer.generateBubbleSortSteps(listOf(64, 34, 25, 12, 22, 11, 90))
        }
        _visualizerState.value = VisualizerUiState(
            algorithmName = algorithmName,
            currentStepIndex = 0,
            steps = steps,
            isPlaying = false
        )
    }

    fun stepForward() {
        val state = _visualizerState.value
        if (state.currentStepIndex < state.steps.size - 1) {
            _visualizerState.update { it.copy(currentStepIndex = it.currentStepIndex + 1) }
        }
    }

    fun stepBack() {
        val state = _visualizerState.value
        if (state.currentStepIndex > 0) {
            _visualizerState.update { it.copy(currentStepIndex = it.currentStepIndex - 1) }
        }
    }

    fun toggleVisualizerPlay() {
        val isCurrentlyPlaying = _visualizerState.value.isPlaying
        if (isCurrentlyPlaying) {
            visualizerJob?.cancel()
            _visualizerState.update { it.copy(isPlaying = false) }
        } else {
            _visualizerState.update { it.copy(isPlaying = true) }
            visualizerJob = viewModelScope.launch {
                while (_visualizerState.value.currentStepIndex < _visualizerState.value.steps.size - 1) {
                    delay(700)
                    _visualizerState.update { it.copy(currentStepIndex = it.currentStepIndex + 1) }
                }
                _visualizerState.update { it.copy(isPlaying = false) }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSearchFilter(filter: String) {
        _searchFilter.value = filter
    }

    fun resetAllProgress() {
        viewModelScope.launch {
            repository.resetAllProgress()
        }
    }
}

class CodeNestViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CodeNestViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CodeNestViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
