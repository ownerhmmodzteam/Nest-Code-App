package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.ProjectSubmissionEntity
import com.example.data.local.entity.QuizScoreEntity
import com.example.data.local.entity.SavedSnippetEntity
import com.example.data.local.entity.UserProgressEntity
import com.example.data.local.entity.XpLedgerEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CodeNestDao {

    @Query("SELECT * FROM user_progress WHERE id = 1 LIMIT 1")
    fun getUserProgress(): Flow<UserProgressEntity?>

    @Query("SELECT * FROM user_progress WHERE id = 1 LIMIT 1")
    suspend fun getUserProgressOnce(): UserProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: UserProgressEntity)

    @Query("SELECT * FROM quiz_scores ORDER BY completedAt DESC")
    fun getAllQuizScores(): Flow<List<QuizScoreEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizScore(score: QuizScoreEntity)

    @Query("SELECT * FROM project_submissions ORDER BY submittedAt DESC")
    fun getAllProjectSubmissions(): Flow<List<ProjectSubmissionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjectSubmission(submission: ProjectSubmissionEntity)

    @Query("SELECT * FROM saved_snippets ORDER BY updatedAt DESC")
    fun getAllSnippets(): Flow<List<SavedSnippetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnippet(snippet: SavedSnippetEntity): Long

    @Query("DELETE FROM saved_snippets WHERE id = :id")
    suspend fun deleteSnippet(id: Int)

    // XP Ledger Idempotency Queries
    @Query("SELECT COUNT(*) > 0 FROM xp_ledger_events WHERE eventId = :eventId")
    suspend fun hasXpEvent(eventId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertXpEvent(event: XpLedgerEventEntity)

    @Query("SELECT * FROM xp_ledger_events ORDER BY timestamp DESC")
    fun getAllXpEvents(): Flow<List<XpLedgerEventEntity>>

    @Query("DELETE FROM user_progress")
    suspend fun clearUserProgress()

    @Query("DELETE FROM quiz_scores")
    suspend fun clearQuizScores()

    @Query("DELETE FROM project_submissions")
    suspend fun clearProjectSubmissions()

    @Query("DELETE FROM xp_ledger_events")
    suspend fun clearXpEvents()
}
