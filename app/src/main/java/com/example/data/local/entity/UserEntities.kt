package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val id: Int = 1,
    val uid: String = "user_local_1",
    val username: String = "Code Explorer",
    val userTier: String = "Beginner",
    val xp: Long = 0L,
    val level: Int = 1,
    val streak: Int = 0,
    val lastActiveDate: String = "2026-09-25",
    val learningGoal: String = "Full Stack Web & Mobile",
    val dailyMinutesTarget: Int = 30,
    val isOnboardingCompleted: Boolean = false,
    val currentLevelId: Int = 0,
    val currentLessonId: String = "fund-01",
    val completedLessonsString: String = "",
    val unlockedAchievementsString: String = "first_step",
    val isAdmin: Boolean = false,
    val email: String = "",
    val photoUrl: String = "",
    val hasChangedName: Boolean = false,
    val nameChangeCount: Int = 0
)

@Entity(tableName = "quiz_scores")
data class QuizScoreEntity(
    @PrimaryKey val quizId: String,
    val levelId: Int,
    val score: Int,
    val maxScore: Int,
    val completedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "project_submissions")
data class ProjectSubmissionEntity(
    @PrimaryKey val projectId: String,
    val title: String,
    val levelId: Int,
    val codeSubmitted: String,
    val passed: Boolean,
    val feedback: String,
    val submittedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_snippets")
data class SavedSnippetEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val language: String,
    val code: String,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "xp_ledger_events")
data class XpLedgerEventEntity(
    @PrimaryKey val eventId: String, // Idempotency key format: {uid}_{resourceType}_{resourceId}
    val type: String,
    val resourceId: String,
    val amount: Long,
    val timestamp: Long = System.currentTimeMillis()
)
