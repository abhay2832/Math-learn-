package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "timetable_items")
data class TimetableItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val subject: String,
    val targetHours: Double,
    val timeString: String, // e.g. "09:30 AM"
    val dayOfWeek: String, // e.g. "Monday", "Everyday"
    val completed: Boolean = false,
    val reminderEnabled: Boolean = true
)

@Entity(tableName = "quiz_results")
data class QuizResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String, // e.g., "Tables", "Squares", "Roots", "Cubes", "Addition", "Mixed"
    val difficulty: String, // "Easy", "Medium", "Hard"
    val score: Int,
    val totalQuestions: Int,
    val percentage: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val timeTakenSeconds: Int,
    val isMockTest: Boolean = false,
    val weakTopics: String = "", // comma-separated list of categories
    val strongTopics: String = "" // comma-separated list of categories
)

@Entity(tableName = "favorite_items")
data class FavoriteItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "TABLE", "SQUARE", "CUBE", "ROOT", "PYP"
    val itemKey: String, // e.g. "table_25", "square_125", "pyp_Agniveer_2023_5"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey val id: String, // e.g., "streak_3", "perfect_quiz", "pyp_master"
    val title: String,
    val description: String,
    val unlocked: Boolean = false,
    val unlockedAt: Long? = null
)

@Dao
interface MathLearnDao {
    // Timetable
    @Query("SELECT * FROM timetable_items ORDER BY id DESC")
    fun getTimetableFlow(): Flow<List<TimetableItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimetableItem(item: TimetableItem)

    @Query("UPDATE timetable_items SET completed = :completed WHERE id = :id")
    suspend fun updateTimetableStatus(id: Int, completed: Boolean)

    @Query("DELETE FROM timetable_items WHERE id = :id")
    suspend fun deleteTimetableItem(id: Int)

    // Quiz Results
    @Query("SELECT * FROM quiz_results ORDER BY timestamp DESC")
    fun getQuizResultsFlow(): Flow<List<QuizResult>>

    @Query("SELECT * FROM quiz_results WHERE isMockTest = 1 ORDER BY timestamp DESC")
    fun getMockTestResultsFlow(): Flow<List<QuizResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizResult(result: QuizResult)

    @Query("DELETE FROM quiz_results")
    suspend fun clearQuizResults()

    // Favorites
    @Query("SELECT * FROM favorite_items ORDER BY timestamp DESC")
    fun getFavoritesFlow(): Flow<List<FavoriteItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(item: FavoriteItem)

    @Query("DELETE FROM favorite_items WHERE type = :type AND itemKey = :itemKey")
    suspend fun deleteFavorite(type: String, itemKey: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_items WHERE type = :type AND itemKey = :itemKey)")
    fun isFavorite(type: String, itemKey: String): Flow<Boolean>

    // Achievements
    @Query("SELECT * FROM achievements")
    fun getAchievementsFlow(): Flow<List<Achievement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<Achievement>)

    @Query("UPDATE achievements SET unlocked = :unlocked, unlockedAt = :unlockedAt WHERE id = :id")
    suspend fun updateAchievementStatus(id: String, unlocked: Boolean, unlockedAt: Long?)
}

@Database(
    entities = [
        TimetableItem::class,
        QuizResult::class,
        FavoriteItem::class,
        Achievement::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MathLearnDatabase : RoomDatabase() {
    abstract fun mathLearnDao(): MathLearnDao

    companion object {
        @Volatile
        private var INSTANCE: MathLearnDatabase? = null

        fun getDatabase(context: Context): MathLearnDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MathLearnDatabase::class.java,
                    "math_learn_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
