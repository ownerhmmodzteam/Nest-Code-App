package com.example.model

data class RoadmapLevel(
    val id: Int,
    val levelNumber: Int,
    val title: String,
    val subtitle: String,
    val description: String,
    val icon: String,
    val colorHex: Long,
    val estimatedHours: Int,
    val modules: List<CourseModule>,
    val project: ProjectDefinition? = null
)

data class CourseModule(
    val id: String,
    val levelId: Int,
    val title: String,
    val description: String,
    val lessons: List<Lesson>,
    val quiz: QuizDefinition? = null
)

data class LineExplanation(
    val lineCode: String,
    val explanation: String
)

data class Lesson(
    val id: String,
    val levelId: Int,
    val moduleId: String,
    val title: String,
    val durationMinutes: Int,
    val conceptExplanation: String,
    val codeSnippet: String,
    val codeLanguage: String,
    val lineByLine: List<LineExplanation>,
    val practiceChallenge: String,
    val starterCode: String,
    val expectedKeywordsOrOutput: String,
    val summary: String,
    val nextLessonId: String? = null
)

data class QuizQuestion(
    val id: String,
    val question: String,
    val codeSnippet: String? = null,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val conceptDoc: String
)

data class QuizDefinition(
    val id: String,
    val title: String,
    val levelId: Int,
    val xpReward: Int,
    val questions: List<QuizQuestion>
)

data class ProjectDefinition(
    val id: String,
    val title: String,
    val levelId: Int,
    val difficulty: String, // "Beginner", "Intermediate", "Advanced", "Professional"
    val brief: String,
    val requirements: List<String>,
    val hints: List<String>,
    val starterCode: String,
    val language: String,
    val validationRules: List<String>
)

data class AchievementItem(
    val id: String,
    val title: String,
    val description: String,
    val xpReward: Int,
    val iconEmoji: String
)

data class DailyChallenge(
    val id: String,
    val title: String,
    val prompt: String,
    val starterCode: String,
    val language: String,
    val expectedOutput: String,
    val xpReward: Int
)
