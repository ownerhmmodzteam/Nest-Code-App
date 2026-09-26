package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.CodeNestDao
import com.example.data.local.entity.ProjectSubmissionEntity
import com.example.data.local.entity.QuizScoreEntity
import com.example.data.local.entity.SavedSnippetEntity
import com.example.data.local.entity.UserProgressEntity
import com.example.data.local.entity.XpLedgerEventEntity

@Database(
    entities = [
        UserProgressEntity::class,
        QuizScoreEntity::class,
        ProjectSubmissionEntity::class,
        SavedSnippetEntity::class,
        XpLedgerEventEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun codeNestDao(): CodeNestDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "codenest_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
