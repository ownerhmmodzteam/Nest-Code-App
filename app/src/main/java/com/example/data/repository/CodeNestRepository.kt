package com.example.data.repository

import com.example.data.local.dao.CodeNestDao
import com.example.data.cloud.CodeNestCloudApi
import org.json.JSONObject
import com.example.data.local.entity.ProjectSubmissionEntity
import com.example.data.local.entity.QuizScoreEntity
import com.example.data.local.entity.SavedSnippetEntity
import com.example.data.local.entity.UserProgressEntity
import com.example.data.local.entity.XpLedgerEventEntity
import com.example.util.XpLevelCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class RewardResult(
    val success: Boolean,
    val isDuplicate: Boolean,
    val xpEarned: Long,
    val totalXp: Long,
    val oldLevel: Int,
    val newLevel: Int,
    val leveledUp: Boolean,
    val milestoneTitle: String? = null
)

class CodeNestRepository(private val dao: CodeNestDao) {

    private val cloud = CodeNestCloudApi()


    val userProgress: Flow<UserProgressEntity> = dao.getUserProgress().map { entity ->
        entity ?: UserProgressEntity(
            id = 1,
            uid = "user_local_1",
            username = "Code Explorer",
            userTier = "Beginner",
            xp = 0L,
            level = 1,
            streak = 0,
            lastActiveDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
            learningGoal = "Full Stack Web & Mobile",
            dailyMinutesTarget = 30,
            isOnboardingCompleted = false,
            currentLevelId = 0,
            currentLessonId = "fund-01",
            completedLessonsString = "",
            unlockedAchievementsString = "first_step",
            isAdmin = false
        )
    }

    val quizScores: Flow<List<QuizScoreEntity>> = dao.getAllQuizScores()
    val projectSubmissions: Flow<List<ProjectSubmissionEntity>> = dao.getAllProjectSubmissions()
    val savedSnippets: Flow<List<SavedSnippetEntity>> = dao.getAllSnippets()
    val xpEvents: Flow<List<XpLedgerEventEntity>> = dao.getAllXpEvents()

    suspend fun signInWithGoogle(
        uid: String,
        displayName: String,
        email: String,
        photoUrl: String = ""
    ) {
        val current = dao.getUserProgressOnce() ?: UserProgressEntity()
        val assignedName = if (current.hasChangedName) current.username else displayName.ifBlank { email.substringBefore("@").ifBlank { "Developer" } }
        dao.insertOrUpdateProgress(current.copy(uid = uid, username = assignedName, email = email, photoUrl = photoUrl))
        try {
            val remote = cloud.get("bootstrap")
            applyRemoteState(remote)
        } catch (_: Exception) {
        }
    }

    suspend fun syncCurrentGoogleAccount() {
        if (FirebaseAuth.getInstance().currentUser == null) return
        try {
            applyRemoteState(cloud.get("bootstrap"))
        } catch (_: Exception) {
        }
    }

    suspend fun enterGuest() {
        dao.insertOrUpdateProgress(
            UserProgressEntity(
                uid = "guest_local",
                username = "Akun Tamu",
                userTier = "Beginner",
                xp = 0L,
                level = 1,
                streak = 0,
                lastActiveDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
                learningGoal = "Full Stack Web & Mobile",
                dailyMinutesTarget = 30,
                isOnboardingCompleted = false,
                currentLevelId = 0,
                currentLessonId = "fund-01",
                completedLessonsString = "",
                unlockedAchievementsString = "first_step",
                isAdmin = false
            )
        )
    }

    private suspend fun applyRemoteState(remote: JSONObject) {
        val u = remote.optJSONObject("user") ?: return
        val current = dao.getUserProgressOnce() ?: UserProgressEntity()
        val completed = u.optString("completedLessonsString", current.completedLessonsString)
        val achievements = u.optString("unlockedAchievementsString", current.unlockedAchievementsString)
        dao.insertOrUpdateProgress(current.copy(
            uid = u.optString("uid", current.uid),
            username = u.optString("username", current.username),
            userTier = u.optString("userTier", current.userTier),
            xp = u.optLong("xp", current.xp),
            level = u.optInt("level", current.level),
            streak = u.optInt("streak", current.streak),
            lastActiveDate = u.optString("lastActiveDate", current.lastActiveDate),
            learningGoal = u.optString("learningGoal", current.learningGoal),
            dailyMinutesTarget = u.optInt("dailyMinutesTarget", current.dailyMinutesTarget),
            isOnboardingCompleted = u.optBoolean("isOnboardingCompleted", current.isOnboardingCompleted),
            currentLevelId = u.optInt("currentLevelId", current.currentLevelId),
            currentLessonId = u.optString("currentLessonId", current.currentLessonId),
            completedLessonsString = completed,
            unlockedAchievementsString = achievements,
            email = u.optString("email", current.email),
            photoUrl = u.optString("photoUrl", current.photoUrl),
            hasChangedName = u.optBoolean("hasChangedName", current.hasChangedName),
            nameChangeCount = u.optInt("nameChangeCount", current.nameChangeCount)
        ))
    }

    suspend fun changeUsername(newName: String): Result<String> {
        val current = dao.getUserProgressOnce() ?: UserProgressEntity()
        if (current.hasChangedName || current.nameChangeCount >= 1) {
            return Result.failure(IllegalStateException("Nama akun hanya dapat diubah 1 kali per akun."))
        }
        val trimmed = newName.trim()
        if (trimmed.length < 3) {
            return Result.failure(IllegalArgumentException("Nama minimal terdiri dari 3 karakter."))
        }
        if (trimmed.length > 25) {
            return Result.failure(IllegalArgumentException("Nama maksimal 25 karakter."))
        }
        val updated = current.copy(
            username = trimmed,
            hasChangedName = true,
            nameChangeCount = 1
        )
        dao.insertOrUpdateProgress(updated)
        if (current.uid != "guest_local" && !current.uid.startsWith("user_local")) {
            try { cloud.post("profile", JSONObject().put("username", trimmed)) } catch (e: Exception) { return Result.failure(e) }
        }
        return Result.success(trimmed)
    }

    suspend fun completeOnboarding(
        username: String,
        tier: String,
        goal: String,
        dailyMinutes: Int,
        email: String = ""
    ) {
        val current = dao.getUserProgressOnce() ?: UserProgressEntity()
        val finalUsername = if (current.email.isNotBlank() && current.username.isNotBlank()) {
            current.username
        } else {
            username.ifBlank { "Programmer" }
        }
        val updated = current.copy(
            username = finalUsername,
            email = if (email.isNotBlank()) email else current.email,
            userTier = tier,
            learningGoal = goal,
            dailyMinutesTarget = dailyMinutes,
            isOnboardingCompleted = true,
            currentLevelId = if (tier == "Advanced") 6 else if (tier == "Intermediate") 3 else 0,
            currentLessonId = if (tier == "Advanced") "react-01" else if (tier == "Intermediate") "js-01" else "fund-01",
            unlockedAchievementsString = "${current.unlockedAchievementsString},first_step"
        )
        dao.insertOrUpdateProgress(updated)
        if (current.uid != "guest_local" && current.uid.isNotBlank()) {
            try {
                cloud.post("profile", JSONObject().apply {
                    put("username", finalUsername)
                    put("userTier", tier)
                    put("learningGoal", goal)
                    put("dailyMinutesTarget", dailyMinutes)
                    put("isOnboardingCompleted", true)
                    put("currentLevelId", updated.currentLevelId)
                    put("currentLessonId", updated.currentLessonId)
                })
            } catch (_: Exception) {
            }
        }
    }

    /**
     * Server-controlled lesson completion with idempotency and Level 1-9999 calculation.
     * XP reward is strictly 25 XP; client cannot alter this value.
     */
    suspend fun completeLesson(lessonId: String, nextLessonId: String?): RewardResult {
        val current = dao.getUserProgressOnce() ?: UserProgressEntity()
        if (current.uid == "guest_local" || current.uid.startsWith("user_local")) {
            return completeLessonLocal(lessonId, nextLessonId)
        }
        return try {
            val result = cloud.post("lesson-completion", JSONObject().put("lessonId", lessonId))
            val updated = current.copy(
                xp = result.optLong("totalXp", current.xp),
                level = result.optInt("newLevel", current.level),
                streak = result.optInt("streak", current.streak),
                lastActiveDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
                completedLessonsString = (current.completedLessonsString.split(",").filter { it.isNotBlank() } + lessonId).distinct().joinToString(","),
                currentLessonId = nextLessonId ?: current.currentLessonId
            )
            dao.insertOrUpdateProgress(updated)
            RewardResult(true, result.optBoolean("duplicate", false), result.optLong("xpEarned", 0), result.optLong("totalXp", updated.xp), result.optInt("oldLevel", current.level), result.optInt("newLevel", updated.level), result.optBoolean("leveledUp", false), result.optString("milestoneBadge").ifBlank { null })
        } catch (_: Exception) {
            throw IllegalStateException("Gagal menyimpan progres ke server. Periksa koneksi internet.")
        }
    }

    private suspend fun completeLessonLocal(lessonId: String, nextLessonId: String?): RewardResult {
        val current = dao.getUserProgressOnce() ?: UserProgressEntity()
        val eventId = "${current.uid}_lesson_${lessonId}"
        if (dao.hasXpEvent(eventId)) return RewardResult(true, true, 0L, current.xp, current.level, current.level, false)
        val xpReward = 25L
        val newXp = current.xp + xpReward
        val newLevel = XpLevelCalculator.calculateLevel(newXp)
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val newStreak = if (current.lastActiveDate == todayStr) current.streak else (current.streak + 1).coerceAtLeast(1)
        val completed = (current.completedLessonsString.split(",").filter { it.isNotBlank() } + lessonId).distinct()
        dao.insertXpEvent(XpLedgerEventEntity(eventId, "lesson_completion", lessonId, xpReward))
        dao.insertOrUpdateProgress(current.copy(xp = newXp, level = newLevel, streak = newStreak, lastActiveDate = todayStr, completedLessonsString = completed.joinToString(","), currentLessonId = nextLessonId ?: current.currentLessonId))
        return RewardResult(true, false, xpReward, newXp, current.level, newLevel, newLevel > current.level, if (newLevel > current.level) XpLevelCalculator.getMilestoneTitle(newLevel) else null)
    }

    /**
     * Server-controlled quiz evaluation & idempotent reward (15 - 50 XP).
     */
    suspend fun saveQuizScore(quizId: String, levelId: Int, scorePercent: Int): RewardResult {
        val current = dao.getUserProgressOnce() ?: UserProgressEntity()
        if (current.uid != "guest_local" && !current.uid.startsWith("user_local")) {
            return try {
                val result = cloud.post("quiz-completion", JSONObject().apply { put("quizId", quizId); put("levelId", levelId); put("scorePercent", scorePercent) })
                dao.insertQuizScore(QuizScoreEntity(quizId, levelId, scorePercent, 100))
                dao.insertOrUpdateProgress(current.copy(xp = result.optLong("totalXp", current.xp), level = result.optInt("newLevel", current.level)))
                RewardResult(true, result.optBoolean("duplicate", false), result.optLong("xpEarned", 0), result.optLong("totalXp", current.xp), result.optInt("oldLevel", current.level), result.optInt("newLevel", current.level), result.optBoolean("leveledUp", false), null)
            } catch (_: Exception) {
                throw IllegalStateException("Gagal menyimpan hasil quiz ke server.")
            }
        }
        dao.insertQuizScore(QuizScoreEntity(quizId, levelId, scorePercent, 100))
        val eventId = "${current.uid}_quiz_${quizId}"
        if (dao.hasXpEvent(eventId)) return RewardResult(true, true, 0, current.xp, current.level, current.level, false)
        val xpReward = when { scorePercent >= 100 -> 50L; scorePercent >= 70 -> 30L; else -> 15L }
        val newXp = current.xp + xpReward
        val newLevel = XpLevelCalculator.calculateLevel(newXp)
        dao.insertXpEvent(XpLedgerEventEntity(eventId, "quiz_completion", quizId, xpReward))
        dao.insertOrUpdateProgress(current.copy(xp = newXp, level = newLevel))
        return RewardResult(true, false, xpReward, newXp, current.level, newLevel, newLevel > current.level, null)
    }

    /**
     * Server-controlled project submission & reward (100 - 1000 XP).
     */
    suspend fun saveProjectSubmission(
        projectId: String, title: String, levelId: Int, codeSubmitted: String, passed: Boolean, feedback: String, difficulty: String
    ): RewardResult {
        val current = dao.getUserProgressOnce() ?: UserProgressEntity()
        if (current.uid != "guest_local" && !current.uid.startsWith("user_local")) {
            return try {
                val result = cloud.post("project-completion", JSONObject().apply {
                    put("projectId", projectId); put("title", title); put("levelId", levelId); put("codeSubmitted", codeSubmitted); put("passed", passed); put("feedback", feedback); put("difficulty", difficulty)
                })
                dao.insertProjectSubmission(ProjectSubmissionEntity(projectId, title, levelId, codeSubmitted, passed, feedback))
                dao.insertOrUpdateProgress(current.copy(xp = result.optLong("totalXp", current.xp), level = result.optInt("newLevel", current.level)))
                RewardResult(passed, result.optBoolean("duplicate", false), result.optLong("xpEarned", 0), result.optLong("totalXp", current.xp), result.optInt("oldLevel", current.level), result.optInt("newLevel", current.level), result.optBoolean("leveledUp", false), null)
            } catch (_: Exception) {
                throw IllegalStateException("Gagal menyimpan project ke server.")
            }
        }
        dao.insertProjectSubmission(ProjectSubmissionEntity(projectId, title, levelId, codeSubmitted, passed, feedback))
        if (!passed) return RewardResult(false, false, 0, current.xp, current.level, current.level, false)
        val eventId = "${current.uid}_project_${projectId}"
        if (dao.hasXpEvent(eventId)) return RewardResult(true, true, 0, current.xp, current.level, current.level, false)
        val xpReward = when (difficulty) { "Professional" -> 1000L; "Advanced" -> 500L; "Intermediate" -> 250L; else -> 100L }
        val newXp = current.xp + xpReward
        val newLevel = XpLevelCalculator.calculateLevel(newXp)
        dao.insertXpEvent(XpLedgerEventEntity(eventId, "project_completion", projectId, xpReward))
        dao.insertOrUpdateProgress(current.copy(xp = newXp, level = newLevel))
        return RewardResult(true, false, xpReward, newXp, current.level, newLevel, newLevel > current.level, null)
    }

    suspend fun saveSnippet(title: String, language: String, code: String): Long {
        return dao.insertSnippet(
            SavedSnippetEntity(
                title = title,
                language = language,
                code = code
            )
        )
    }

    suspend fun deleteSnippet(id: Int) {
        dao.deleteSnippet(id)
    }

    suspend fun resetAllProgress() {
        val current = dao.getUserProgressOnce()
        if (current != null && current.uid != "guest_local" && !current.uid.startsWith("user_local")) {
            try { cloud.post("reset-progress", JSONObject()) } catch (_: Exception) { throw IllegalStateException("Gagal mereset progress cloud.") }
        }
        dao.clearUserProgress()
        dao.clearQuizScores()
        dao.clearProjectSubmissions()
        dao.clearXpEvents()
        dao.insertOrUpdateProgress(
            UserProgressEntity(
                id = 1,
                uid = "user_local_1",
                username = "Code Explorer",
                userTier = "Beginner",
                xp = 0L,
                level = 1,
                streak = 0,
                isOnboardingCompleted = false,
                currentLevelId = 0,
                currentLessonId = "fund-01",
                completedLessonsString = "",
                unlockedAchievementsString = ""
            )
        )
    }
}
