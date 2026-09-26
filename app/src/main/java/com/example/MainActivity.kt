package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.catalog.CurriculumCatalog
import com.example.data.cloud.GuestSession
import com.google.firebase.auth.FirebaseAuth
import com.example.ui.components.CodeNestBottomNavBar
import com.example.ui.components.NavigationTab
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.CodeNestViewModel
import com.example.viewmodel.CodeNestViewModelFactory

sealed class ScreenDestination {
    data object MainTabs : ScreenDestination()
    data class LessonDetail(val lessonId: String) : ScreenDestination()
    data class Quiz(val quizId: String) : ScreenDestination()
    data object AlgorithmVisualizer : ScreenDestination()
    data object Search : ScreenDestination()
    data object GoogleLogin : ScreenDestination()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CodeNestTheme {
                val viewModel: CodeNestViewModel = viewModel(
                    factory = CodeNestViewModelFactory(application)
                )

                val progress by viewModel.userProgress.collectAsState()
                val guestSession = remember { GuestSession(applicationContext) }
                var signedIn by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser != null) }
                var guest by remember { mutableStateOf(guestSession.isGuest()) }

                LaunchedEffect(signedIn, guest) {
                    if (signedIn && !guest) viewModel.syncCurrentGoogleAccount()
                }

                when {
                    !signedIn && !guest -> {
                        GoogleLoginScreen(
                            viewModel = viewModel,
                            onLoginSuccess = {
                                guestSession.setGuest(false)
                                guest = false
                                signedIn = true
                            },
                            onContinueAsGuest = {
                                viewModel.enterGuest()
                                guestSession.setGuest(true)
                                guest = true
                            }
                        )
                    }
                    !progress.isOnboardingCompleted -> {
                        OnboardingScreen(
                            viewModel = viewModel,
                            onComplete = { }
                        )
                    }
                    else -> {
                        CodeNestApp(
                            viewModel = viewModel,
                            onOpenGoogleLogin = {
                                guestSession.setGuest(false)
                                guest = false
                                signedIn = FirebaseAuth.getInstance().currentUser != null
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CodeNestApp(viewModel: CodeNestViewModel, onOpenGoogleLogin: () -> Unit = {}) {
    var selectedTab by remember { mutableStateOf(NavigationTab.HOME) }
    var currentDestination by remember { mutableStateOf<ScreenDestination>(ScreenDestination.MainTabs) }

    // System Back Press handling
    BackHandler(enabled = currentDestination != ScreenDestination.MainTabs) {
        currentDestination = ScreenDestination.MainTabs
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        IosDarkBackground,
                        Color(0xFF0A0E1A),
                        IosDarkBackground
                    )
                )
            )
    ) {
        when (val dest = currentDestination) {
            is ScreenDestination.LessonDetail -> {
                val lesson = CurriculumCatalog.findLessonById(dest.lessonId)
                if (lesson != null) {
                    LessonDetailScreen(
                        lesson = lesson,
                        viewModel = viewModel,
                        onBackClick = { currentDestination = ScreenDestination.MainTabs },
                        onOpenPlayground = {
                            selectedTab = NavigationTab.PRACTICE
                            currentDestination = ScreenDestination.MainTabs
                        },
                        onStartQuiz = { quizId ->
                            viewModel.startQuiz(quizId)
                            currentDestination = ScreenDestination.Quiz(quizId)
                        }
                    )
                } else {
                    currentDestination = ScreenDestination.MainTabs
                }
            }

            is ScreenDestination.Quiz -> {
                QuizScreen(
                    viewModel = viewModel,
                    onBackClick = { currentDestination = ScreenDestination.MainTabs }
                )
            }

            is ScreenDestination.AlgorithmVisualizer -> {
                AlgorithmVisualizerScreen(
                    viewModel = viewModel,
                    onBackClick = { currentDestination = ScreenDestination.MainTabs }
                )
            }

            is ScreenDestination.GoogleLogin -> {
                GoogleLoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = { currentDestination = ScreenDestination.MainTabs; onOpenGoogleLogin() },
                    onContinueAsGuest = { currentDestination = ScreenDestination.MainTabs }
                )
            }

            is ScreenDestination.Search -> {
                SearchScreen(
                    viewModel = viewModel,
                    onBackClick = { currentDestination = ScreenDestination.MainTabs },
                    onSelectLesson = { lessonId ->
                        viewModel.selectLesson(lessonId)
                        currentDestination = ScreenDestination.LessonDetail(lessonId)
                    }
                )
            }

            is ScreenDestination.MainTabs -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Screen Tab Content with iOS 28 Fluid Spring Transitions
                    AnimatedContent(
                        targetState = selectedTab,
                        transitionSpec = {
                            (fadeIn(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioNoBouncy,
                                    stiffness = Spring.StiffnessMediumLow
                                )
                            ) + scaleIn(
                                initialScale = 0.97f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )).togetherWith(
                                fadeOut(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioNoBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    )
                                ) + scaleOut(
                                    targetScale = 0.98f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioNoBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    )
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 80.dp), // Space for floating dock
                        label = "ios_tab_transition"
                    ) { targetTab ->
                        when (targetTab) {
                            NavigationTab.HOME -> HomeScreen(
                                viewModel = viewModel,
                                onNavigateToLesson = { lessonId ->
                                    viewModel.selectLesson(lessonId)
                                    currentDestination = ScreenDestination.LessonDetail(lessonId)
                                },
                                onNavigateToLearn = { selectedTab = NavigationTab.LEARN },
                                onNavigateToPlayground = { selectedTab = NavigationTab.PRACTICE },
                                onNavigateToVisualizer = { currentDestination = ScreenDestination.AlgorithmVisualizer },
                                onNavigateToSearch = { currentDestination = ScreenDestination.Search }
                            )

                            NavigationTab.LEARN -> LearnScreen(
                                viewModel = viewModel,
                                onNavigateToLesson = { lessonId ->
                                    viewModel.selectLesson(lessonId)
                                    currentDestination = ScreenDestination.LessonDetail(lessonId)
                                },
                                onNavigateToQuiz = { quizId ->
                                    viewModel.startQuiz(quizId)
                                    currentDestination = ScreenDestination.Quiz(quizId)
                                }
                            )

                            NavigationTab.PRACTICE -> PlaygroundScreen(
                                viewModel = viewModel
                            )

                            NavigationTab.PROJECTS -> ProjectsScreen(
                                viewModel = viewModel
                            )

                            NavigationTab.PROFILE -> ProfileScreen(
                                viewModel = viewModel,
                                onSignInGoogle = { currentDestination = ScreenDestination.GoogleLogin }
                            )
                        }
                    }

                    // Floating Liquid Glass Dock aligned to bottom
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                    ) {
                        CodeNestBottomNavBar(
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it }
                        )
                    }
                }
            }
        }
    }
}
