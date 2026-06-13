package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "task_items")
data class TaskItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val ttsPhrase: String,
    val isCompleted: Boolean = false,
    val iconName: String,
    val orderIndex: Int,
    val isCustom: Boolean = false,
    val voiceFilePath: String? = null,
    val imageUri: String? = null
)

@Entity(tableName = "aac_cards")
data class AacCard(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String, // "ANA_DAIR" (Basic Needs), "ANA_HASSI" (Feelings), "RAGHBAT" (Wishes/Activities)
    val title: String,
    val ttsPhrase: String,
    val iconName: String,
    val colorHex: String,
    val isCustom: Boolean = false,
    val voiceFilePath: String? = null,
    val imageUri: String? = null
)

@Entity(tableName = "app_settings")
data class AppSettings(
    @PrimaryKey val id: Int = 1,
    val isFirstTime: Boolean = true,
    val parentPhoneNumber: String = "0912345678", // Default Sudanese mobile format placeholder
    val speechRate: Float = 0.45f
)

@Dao
interface TaskDao {
    @Query("SELECT * FROM task_items ORDER BY orderIndex ASC")
    fun getAllTasks(): Flow<List<TaskItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskItem): Long

    @Update
    suspend fun updateTask(task: TaskItem)

    @Delete
    suspend fun deleteTask(task: TaskItem)

    @Query("SELECT COUNT(*) FROM task_items")
    suspend fun getTaskCount(): Int
}

@Dao
interface AacCardDao {
    @Query("SELECT * FROM aac_cards")
    fun getAllCards(): Flow<List<AacCard>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: AacCard): Long

    @Update
    suspend fun updateCard(card: AacCard)

    @Delete
    suspend fun deleteCard(card: AacCard)

    @Query("SELECT COUNT(*) FROM aac_cards")
    suspend fun getCardCount(): Int
}

@Dao
interface AppSettingsDao {
    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<AppSettings?>

    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsDirect(): AppSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: AppSettings)
}

@Database(entities = [TaskItem::class, AacCard::class, AppSettings::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun aacCardDao(): AacCardDao
    abstract fun appSettingsDao(): AppSettingsDao
}
