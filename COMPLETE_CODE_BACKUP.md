# ملف كود البناء الكامل لتطبيق أكرم (Akrm - Autism Kids Assistant) 🌟

هذا الملف يحتوي على كامل الأكواد والملفات البرمجية الأساسية لتطبيق **أكرم** المصمم خصيصاً لمساعدة اليافعين المصابين بالتوحد والأهل في روتينهم اليومي والتواصل البصري المنظم. يمكنك استخدام هذا الملف للاحتفاظ بنسخة احتياطية كاملة أو إعادة تصدير وبناء التطبيق على أي بيئة تطوير أندرويد (مثل **Android Studio**).

---

## 📂 خريطة هيكل المشروع (Project Structure File Tree)

لتنظيم الملفات على جهازك أو في Android Studio، تتبع هيكلية المجلدات التالية:
```text
AkrmAppProject/
│
├── build.gradle.kts (Project-level build file)
├── settings.gradle.kts (Gradle project settings)
│
└── app/
    ├── build.gradle.kts (App-level module configuration)
    └── src/
        └── main/
            ├── AndroidManifest.xml (Manifest configuration and permissions)
            └── java/
                └── com/
                    └── example/
                        ├── MainActivity.kt (App Entrypoint and permission handlers)
                        ├── data/
                        │   └── ModelsAndDb.kt (Room database setup: entities, DAOs & seed data)
                        ├── util/
                        │   └── VoiceManager.kt (Text-To-Speech, Recording & calming synth audio)
                        ├── viewmodel/
                        │   └── AkrmViewModel.kt (Kotlin StateFlow and sound trigger business logic)
                        └── ui/
                            ├── IconMapper.kt (Safe string-to-icon system)
                            ├── AkrmApp.kt (Visual Schedule layout, AAC grid & settings interface)
                            └── theme/
                                ├── Color.kt (Calm sensory color palette specifications)
                                ├── Theme.kt (Jetpack Compose customized theme adapter)
                                └── Type.kt (Typography fonts configurations)
```

---

## 🛠️ تفاصيل جميع الأكواد المصدرية (Complete Source Codes)

### 1️⃣ ملف الإعدادات العام للمشروع: `settings.gradle.kts`
```kotlin
pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "My Application"
include(":app")
```

---

### 2️⃣ ملف بناء موديول التطبيق والتباعيات: `app/build.gradle.kts`
```kotlin
plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.roborazzi)
  alias(libs.plugins.secrets)
}

android {
  namespace = "com.example"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    applicationId = "com.aistudio.akrm.uxvsqp"
    minSdk = 24
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  signingConfigs {
    create("release") {
      val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/my-upload-key.jks"
      storeFile = file(keystorePath)
      storePassword = System.getenv("STORE_PASSWORD")
      keyAlias = "upload"
      keyPassword = System.getenv("KEY_PASSWORD")
    }
    create("debugConfig") {
      storeFile = file("${rootDir}/debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
    }
    debug {
      signingConfig = signingConfigs.getByName("debugConfig")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions { unitTests { isIncludeAndroidResources = true } }
}

secrets {
  propertiesFileName = ".env"
  defaultPropertiesFileName = ".env.example"
}

dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(platform(libs.firebase.bom))
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  implementation(libs.converter.moshi)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.logging.interceptor)
  implementation(libs.moshi.kotlin)
  implementation(libs.okhttp)
  implementation(libs.retrofit)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  testImplementation(libs.roborazzi)
  testImplementation(libs.roborazzi.compose)
  testImplementation(libs.roborazzi.junit.rule)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  "ksp"(libs.androidx.room.compiler)
  "ksp"(libs.moshi.kotlin.codegen)
}
```

---

### 3️⃣ ملف المانيفست العام والصلاحيات: `app/src/main/AndroidManifest.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.RECORD_AUDIO" />

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.MyApplication">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:label="@string/app_name"
            android:theme="@style/Theme.MyApplication">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

---

### 4️⃣ ملف نقطة انطلاق التطبيق والصلاحيات: `app/src/main/java/com/example/MainActivity.kt`
```kotlin
package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Surface
import androidx.core.content.ContextCompat
import com.example.data.AppDatabase
import com.example.ui.AkrmApp
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AkrmViewModel

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: AkrmViewModel

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Handle microphone recording permission update appropriately if needed
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SQLite Room Database with applicationContext securely
        val database = AppDatabase.getInstance(applicationContext)
        viewModel = AkrmViewModel(applicationContext, database)

        // Request Audio permissions at startup for custom family voice records
        checkAndRequestAudioPermission()

        setContent {
            MyApplicationTheme {
                Surface {
                    AkrmApp(viewModel = viewModel)
                }
            }
        }
    }

    private fun checkAndRequestAudioPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up playing synthetic calming 432Hz sine wave or TTS safely on close
        viewModel.onCleared()
    }
}
```

---

### 5️⃣ قاعدة بيانات Room وهياكل النماذج البصرية: `app/src/main/java/com/example/data/ModelsAndDb.kt`
```kotlin
package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "tasks_table")
data class TaskItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val pronunciation: String, // Correct phonetic Arabic dialect phrasing readable by engine
    val iconName: String,     // mapped from IconMapper
    val isCompleted: Boolean = false,
    val isCustom: Boolean = false,
    val imageUri: String? = null,      // Path of family captured camera photo
    val voiceFilePath: String? = null  // Path of family tailored audio clip override
)

@Entity(tableName = "aac_cards_table")
data class AacCard(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String,  // e.g., "أنا أريد...", "أنا أشعر...", "ألعابي وأنشطتي"
    val title: String,
    val pronunciation: String,
    val iconName: String,
    val colorHex: String,
    val isCustom: Boolean = false,
    val imageUri: String? = null,
    val voiceFilePath: String? = null
)

@Entity(tableName = "app_settings_table")
data class AppSettings(
    @PrimaryKey val id: Int = 1,
    val parentPhoneNumber: String = "0500000000",
    val speechRate: Float = 0.45f, // Slow calming voice matching sensory pacing
    val hasInstructedFamilyBefore: Boolean = false
)

@Dao
interface TasksDao {
    @Query("SELECT * FROM tasks_table ORDER BY id ASC")
    fun getAllTasksFlow(): Flow<List<TaskItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskItem)

    @Update
    suspend fun updateTask(task: TaskItem)

    @Delete
    suspend fun deleteTask(task: TaskItem)

    @Query("UPDATE tasks_table SET isCompleted = 0")
    suspend fun resetAllTasks()
}

@Dao
interface AacDao {
    @Query("SELECT * FROM aac_cards_table ORDER BY id ASC")
    fun getAllAacCardsFlow(): Flow<List<AacCard>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: AacCard)

    @Update
    suspend fun updateCard(card: AacCard)

    @Delete
    suspend fun deleteCard(card: AacCard)
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM app_settings_table WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<AppSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: AppSettings)
}

@Database(entities = [TaskItem::class, AacCard::class, AppSettings::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tasksDao(): TasksDao
    abstract fun aacDao(): AacDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "akrm_support_database"
                )
                .addCallback(DatabaseInitializerCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseInitializerCallback : RoomDatabase.Callback() {
            override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                super.onCreate(db)
                // Seed database initialized safely during initial launch
                db.execSQL("INSERT INTO app_settings_table (id, parentPhoneNumber, speechRate, hasInstructedFamilyBefore) VALUES (1, '0500000000', 0.45, 0)")
                
                // Seed Tasks (Daily visual layout)
                db.execSQL("INSERT INTO tasks_table (title, pronunciation, iconName, isCompleted, isCustom) VALUES ('الاستيقاظ وغسل الوجه ☀️', 'أكرم، يا بطل، حان وقت غسل وجهك الجميل لبدء يوم ممتع', 'face', 0, 0)")
                db.execSQL("INSERT INTO tasks_table (title, pronunciation, iconName, isCompleted, isCustom) VALUES ('تنظيف أسناني 🪥', 'أكرم، حان وقت تنظيف أسنانك لتلمع مثل النجوم', 'dentistry', 0, 0)")
                db.execSQL("INSERT INTO tasks_table (title, pronunciation, iconName, isCompleted, isCustom) VALUES ('لبس ملابسي 👕', 'أكرم، فلنرتدي ملابسنا المريحة لنستعد للنشاط التالي بكل فخر', 'check', 0, 0)")
                db.execSQL("INSERT INTO tasks_table (title, pronunciation, iconName, isCompleted, isCustom) VALUES ('تناول الفطور اللذيذ 🥞', 'أكرم، وجبة الفطور المغدية جاهزة لتمنحك الطاقة والقوة', 'restaurant', 0, 0)")
                db.execSQL("INSERT INTO tasks_table (title, pronunciation, iconName, isCompleted, isCustom) VALUES ('حل الواجب الذكي 📝', 'أكرم، فلنستمتع معاً بحل الواجب القصير الذكي بدقائق معدودة', 'school', 0, 0)")

                // Seed AAC Communication cards (أنا أريد...)
                db.execSQL("INSERT INTO aac_cards_table (category, title, pronunciation, iconName, colorHex, isCustom) VALUES ('أنا أريد...', 'شرب الماء 🥛', 'أنا أريد أن أشرب كوباً من الماء البارد الهانئ من فضلك', 'restaurant', '#0284C7', 0)")
                db.execSQL("INSERT INTO aac_cards_table (category, title, pronunciation, iconName, colorHex, isCustom) VALUES ('أنا أريد...', 'وجبة خفيفة 🍎', 'أنا أريد تفاحة أو وجبة خفيفة لذيذة لتغذية بطني', 'restaurant', '#10B981', 0)")
                db.execSQL("INSERT INTO aac_cards_table (category, title, pronunciation, iconName, colorHex, isCustom) VALUES ('أنا أريد...', 'دخول الحمام 🚽', 'أنا أريد الذهاب إلى دورة المياه من فضلك الآن', 'home', '#F59E0B', 0)")
                db.execSQL("INSERT INTO aac_cards_table (category, title, pronunciation, iconName, colorHex, isCustom) VALUES ('أنا أريد...', 'تلفاز / يوتيوب 📺', 'أنا أريد مشاهدة برنامجي المفضّل قليلاً للاستماع والهدوء', 'video', '#EF4444', 0)")

                // Seed AAC Communication cards (أنا أشعر...)
                db.execSQL("INSERT INTO aac_cards_table (category, title, pronunciation, iconName, colorHex, isCustom) VALUES ('أنا أشعر...', 'سعيد بالألعاب 😃', 'أنا أشعر بالسعادة والسرور الشديد والرضا التام يا عائلتي', 'face', '#059669', 0)")
                db.execSQL("INSERT INTO aac_cards_table (category, title, pronunciation, iconName, colorHex, isCustom) VALUES ('أنا أشعر...', 'مضطرب / قلق 😟', 'أنا أشعر بالقلق أو التشتت وأحتاج لحظة احتضان هادئة وسكون', 'face', '#6366F1', 0)")
                db.execSQL("INSERT INTO aac_cards_table (category, title, pronunciation, iconName, colorHex, isCustom) VALUES ('أنا أشعر...', 'تعبان وأريد النوم 🥱', 'أنا تعبان جداً وأريد أن آخذ قسطاً من الراحة والنوم الهادئ', 'home', '#7C3AED', 0)")
                db.execSQL("INSERT INTO aac_cards_table (category, title, pronunciation, iconName, colorHex, isCustom) VALUES ('أنا أشعر...', 'حزين قليلاً 😢', 'أنا حزين وأتمنى أن تربتوا على كتفي برفق وتسمعوني', 'face', '#EC4899', 0)")

                // Seed AAC Communication cards (ألعابي وأنشطتي)
                db.execSQL("INSERT INTO aac_cards_table (category, title, pronunciation, iconName, colorHex, isCustom) VALUES ('ألعابي وأنشطتي', 'لعب بتركيب المكعبات 🧱', 'أنا أريد تركيب المكعبات الملونة وصنع بيوت جميلة', 'school', '#EC4899', 0)")
                db.execSQL("INSERT INTO aac_cards_table (category, title, pronunciation, iconName, colorHex, isCustom) VALUES ('ألعابي وأنشطتي', 'الخروج للمشي 🌳', 'أنا أريد الذهاب للمشي أو الحديقة واستنشاق الهواء النقي', 'home', '#059669', 0)")
                db.execSQL("INSERT INTO aac_cards_table (category, title, pronunciation, iconName, colorHex, isCustom) VALUES ('ألعابي وأنشطتي', 'لعب بالصلصال 🎨', 'أنا أريد اللعب بالصلصال الملون وتشكيل طيور وحيوانات جميلة', 'face', '#F59E0B', 0)")
            }
        }
    }
}
```

---

### 6️⃣ محرك الصوت، النطق، التسجيل والذبذبات: `app/src/main/java/com/example/util/VoiceManager.kt`
```kotlin
package com.example.util

import android.content.Context
import android.media.AudioFormat
importimport android.media.AudioManager
import android.media.AudioTrack
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

class VoiceManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    private val _currentSpokenText = MutableStateFlow("")
    val currentSpokenText = _currentSpokenText.asStateFlow()

    // Recording mechanisms for customized family voiceovers
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null

    // Synth elements (Real-time generation of calming 432Hz sine wave during panic)
    private var synthAudioTrack: AudioTrack? = null
    private var isSynthPlaying = false

    init {
        tts = TextToSpeech(context, this)
        // Add listener to release UI text automatically upon speech finish
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) {
                _currentSpokenText.value = ""
            }
            override fun onError(utteranceId: String?) {
                _currentSpokenText.value = ""
            }
        })
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val arabicLocale = Locale("ar")
            val result = tts?.setLanguage(arabicLocale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback to default engine language if missing
                tts?.setLanguage(Locale.getDefault())
            }
            isTtsReady = true
        }
    }

    fun speakAloud(text: String, rate: Float = 0.45f) {
        if (!isTtsReady) return
        
        // Ensure we stop any running mediaplayer voiceover first
        stopAnyMediaPlayerPlayback()

        _currentSpokenText.value = text
        tts?.setSpeechRate(rate)
        
        val params = android.os.Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "akrm_speech_uid")
        
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "akrm_speech_uid")
    }

    fun setSpeechRate(rate: Float) {
        if (isTtsReady) {
            tts?.setSpeechRate(rate)
        }
    }

    fun playRecordedVoice(path: String) {
        stopAnyMediaPlayerPlayback()
        _currentSpokenText.value = "▶️ تشغيل صوت الأهل المخصص..."
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(path)
                prepare()
                start()
                setOnCompletionListener {
                    _currentSpokenText.value = ""
                    it.release()
                    mediaPlayer = null
                }
            }
        } catch (e: Exception) {
            _currentSpokenText.value = "⚠️ تعذر تشغيل الصوت المخصص"
        }
    }

    private fun stopAnyMediaPlayerPlayback() {
        mediaPlayer?.let {
            try {
                if (it.isPlaying) {
                    it.stop()
                }
            } catch (e: Exception) {}
            it.release()
        }
        mediaPlayer = null
    }

    // --- Media Recording Mechanism ---
    fun startRecording(outputFile: File) {
        stopAnyMediaPlayerPlayback()
        _currentSpokenText.value = "🎙️ جاري تسجيل صوت الأهل..."
        try {
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(outputFile.absolutePath)
                prepare()
                start()
            }
        } catch (e: Exception) {
            _currentSpokenText.value = "❌ خطأ في بدء التسجيل"
        }
    }

    fun stopRecording(): String? {
        return try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            _currentSpokenText.value = ""
            "success"
        } catch (e: Exception) {
            mediaRecorder = null
            _currentSpokenText.value = ""
            null
        }
    }

    // --- Calming Sine Wave Synthesizer (432Hz Core) ---
    // Mathematically computed sine tone for autistic sensory modulation.
    fun startCalmingSynth() {
        if (isSynthPlaying) return
        isSynthPlaying = true
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val sampleRate = 44100
                val targetFreqHz = 432.0 // Veritas tone for anxiety pacification
                val numSamples = sampleRate // 1 second buffer pool
                val samples = FloatArray(numSamples)
                
                // Precompile sine values
                for (i in 0 until numSamples) {
                    samples[i] = kotlin.math.sin(2.0 * kotlin.math.PI * i / (sampleRate / targetFreqHz)).toFloat()
                }

                // Convert pure values to 16-bit PCM bytes
                val pcmBuffer = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    pcmBuffer[i] = (samples[i] * Short.MAX_VALUE).toInt().toShort()
                }

                val bufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )

                synthAudioTrack = AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize,
                    AudioTrack.MODE_STREAM
                )

                synthAudioTrack?.play()

                // Stream the calming tone in loops until state explicitly dismissed
                while (isSynthPlaying) {
                    synthAudioTrack?.write(pcmBuffer, 0, pcmBuffer.size)
                }
            } catch (e: Exception) {
                Log.e("VoiceManager", "Error generating calming synth: ${e.localizedMessage}")
            }
        }
    }

    fun stopCalmingSynth() {
        isSynthPlaying = false
        try {
            synthAudioTrack?.stop()
            synthAudioTrack?.release()
        } catch (e: Exception) {}
        synthAudioTrack = null
    }

    fun cleanup() {
        stopAnyMediaPlayerPlayback()
        stopCalmingSynth()
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
```

---

### 7️⃣ وسيط البيانات والمحاكيات وإرسال الطوارئ: `app/src/main/java/com/example/viewmodel/AkrmViewModel.kt`
```kotlin
package com.example.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.util.VoiceManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

class AkrmViewModel(
    private val context: Context,
    private val database: AppDatabase
) : ViewModel() {

    val voiceManager = VoiceManager(context)

    // Room DB Flow connections
    val tasks: StateFlow<List<TaskItem>> = database.tasksDao().getAllTasksFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val aacCards: StateFlow<List<AacCard>> = database.aacDao().getAllAacCardsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settings: StateFlow<AppSettings> = database.settingsDao().getSettingsFlow()
        .map { it ?: AppSettings() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    // UI Interactive States
    private val _isMeltdownActive = MutableStateFlow(false)
    val isMeltdownActive = _isMeltdownActive.asStateFlow()

    private val _timerDuration = MutableStateFlow(180) // default 3 minutes
    val timerDuration = _timerDuration.asStateFlow()

    private val _timerSecondsRemaining = MutableStateFlow(180)
    val timerSecondsRemaining = _timerSecondsRemaining.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning = _isTimerRunning.asStateFlow()

    // Temporary tracking states for mic recordings
    private val _recordingTaskItem = MutableStateFlow<TaskItem?>(null)
    val recordingTaskItem = _recordingTaskItem.asStateFlow()

    private val _recordingAacCard = MutableStateFlow<AacCard?>(null)
    val recordingAacCard = _recordingAacCard.asStateFlow()

    private val _isRecordingActive = MutableStateFlow(false)
    val isRecordingActive = _isRecordingActive.asStateFlow()

    private val _customGreetingTextToShow = MutableStateFlow("")
    val customGreetingTextToShow = _customGreetingTextToShow.asStateFlow()

    // SingleEvent flow channel to dispatch emergency SMS trigger back to Host context
    data class SmsPayload(val number: String, val message: String)
    private val _smsSentEvent = MutableSharedFlow<SmsPayload>(replay = 0)
    val smsSentEvent = _smsSentEvent.asSharedFlow()

    private var timerJob: kotlinx.coroutines.Job? = null

    // System Startup Greeting triggers with fallbacks
    fun triggerStartupGreeting() {
        viewModelScope.launch {
            val hasInstructed = settings.value.hasInstructedFamilyBefore
            if (!hasInstructed) {
                _customGreetingTextToShow.value = "أهلاً بك يا أكرم! اضغط على البطاقة لتتحدث معي 🌟"
                voiceManager.speakAloud("أهلاً بك يا بطل في تطبيقِكَ الخاص. معاً سنجعل يومنا رائعاً ومنظماً!", settings.value.speechRate)
                database.settingsDao().saveSettings(settings.value.copy(hasInstructedFamilyBefore = true))
            } else {
                _customGreetingTextToShow.value = "مرحباً مجدداً يا بطل أكرم! دعنا نبدأ جدولنا الجميل 👋"
                voiceManager.speakAloud("مرحباً مجدداً يا بطل. ممتع جداً أن نلتقي لنكمل إنجازاتنا الرائعة!", settings.value.speechRate)
            }
        }
    }

    // Force/Simulation controls for family testing portals
    fun forceResetFirstTime(simulateFirstLaunch: Boolean) {
        viewModelScope.launch {
            if (simulateFirstLaunch) {
                database.settingsDao().saveSettings(settings.value.copy(hasInstructedFamilyBefore = false))
                _customGreetingTextToShow.value = "أكرم البطل يرحب بكم لأول مرة! (مُحاكاة الترحيب الأولية)"
                voiceManager.speakAloud("أهلاً بك يا بطل في تطبيقِكَ الخاص. معاً سنجعل يومنا رائعاً ومنظماً!", settings.value.speechRate)
            } else {
                database.settingsDao().saveSettings(settings.value.copy(hasInstructedFamilyBefore = true))
                _customGreetingTextToShow.value = "مرحباً بعودتك! (مُحاكاة ترحيب العودة)"
                voiceManager.speakAloud("مرحباً مجدداً يا بطل. ممتع جداً أن نلتقي لنكمل إنجازاتنا الرائعة!", settings.value.speechRate)
            }
        }
    }

    // --- Task Actions ---
    // Single Tap speaks pronunciation aloud or plays customized voice overridden clip
    fun onTaskSingleTap(task: TaskItem) {
        if (task.voiceFilePath != null) {
            voiceManager.playRecordedVoice(task.voiceFilePath)
        } else {
            voiceManager.speakAloud(task.pronunciation, settings.value.speechRate)
        }
    }

    // Double Tap marks task complete and triggers audio verbal cheer
    fun onTaskDoubleTap(task: TaskItem) {
        viewModelScope.launch {
            val updated = task.copy(isCompleted = !task.isCompleted)
            database.tasksDao().updateTask(updated)
            if (updated.isCompleted) {
                voiceManager.speakAloud("كفو يا بطل! لقد أنجزتَ ${task.title} بنجاح باهر! أنا فخور بك جداً 🌟", settings.value.speechRate)
            }
        }
    }

    fun resetAllTasks() {
        viewModelScope.launch {
            database.tasksDao().resetAllTasks()
            voiceManager.speakAloud("تمت إعادة تصفير جدول المهام بنجاح كامل يا بطل اليوم", settings.value.speechRate)
        }
    }

    fun addNewCustomTask(title: String, pronunciation: String, iconName: String) {
        viewModelScope.launch {
            val newTask = TaskItem(
                title = title,
                pronunciation = pronunciation,
                iconName = iconName,
                isCompleted = false,
                isCustom = true
            )
            database.tasksDao().insertTask(newTask)
        }
    }

    fun deleteCustomTask(task: TaskItem) {
        viewModelScope.launch {
            database.tasksDao().deleteTask(task)
        }
    }

    fun updateTaskImage(task: TaskItem, uriString: String) {
        viewModelScope.launch {
            database.tasksDao().updateTask(task.copy(imageUri = uriString))
        }
    }

    // --- Voice Recording For Tasks ---
    fun startVoiceRecordingForTask(task: TaskItem, cacheDir: File) {
        _recordingTaskItem.value = task
        _isRecordingActive.value = true
        val targetFile = File(cacheDir, "task_voice_${task.id}.m4a")
        voiceManager.startRecording(targetFile)
    }

    fun stopVoiceRecordingForTask(task: TaskItem, savedFile: File) {
        val result = voiceManager.stopRecording()
        _isRecordingActive.value = false
        _recordingTaskItem.value = null
        if (result != null) {
            viewModelScope.launch {
                database.tasksDao().updateTask(task.copy(voiceFilePath = savedFile.absolutePath))
                Toast.makeText(context, "تم حفظ تسجيل الأهل للمهمة بنجاح!", Toast.LENGTH_SHORT).show()
                voiceManager.playRecordedVoice(savedFile.absolutePath)
            }
        }
    }

    // --- AAC Actions ---
    fun onAacCardTap(card: AacCard) {
        if (card.voiceFilePath != null) {
            voiceManager.playRecordedVoice(card.voiceFilePath)
        } else {
            voiceManager.speakAloud(card.pronunciation, settings.value.speechRate)
        }
    }

    fun addNewCustomAacCard(category: String, title: String, pronunciation: String, icon: String, colorHex: String) {
        viewModelScope.launch {
            val newCard = AacCard(
                category = category,
                title = title,
                pronunciation = pronunciation,
                iconName = icon,
                colorHex = colorHex,
                isCustom = true
            )
            database.aacDao().insertCard(newCard)
        }
    }

    fun deleteCustomAacCard(card: AacCard) {
        viewModelScope.launch {
            database.aacDao().deleteCard(card)
        }
    }

    fun updateCardImage(card: AacCard, uriString: String) {
        viewModelScope.launch {
            database.aacDao().updateCard(card.copy(imageUri = uriString))
        }
    }

    // --- Voice Recording For AAC Cards ---
    fun startVoiceRecordingForAacCard(card: AacCard, cacheDir: File) {
        _recordingAacCard.value = card
        _isRecordingActive.value = true
        val targetFile = File(cacheDir, "aac_voice_${card.id}.m4a")
        voiceManager.startRecording(targetFile)
    }

    fun stopVoiceRecordingForAacCard(card: AacCard, savedFile: File) {
        val result = voiceManager.stopRecording()
        _isRecordingActive.value = false
        _recordingAacCard.value = null
        if (result != null) {
            viewModelScope.launch {
                database.aacDao().updateCard(card.copy(voiceFilePath = savedFile.absolutePath))
                Toast.makeText(context, "تم حفظ نبرتِك المخصصة لكرت التواصل!", Toast.LENGTH_SHORT).show()
                voiceManager.playRecordedVoice(savedFile.absolutePath)
            }
        }
    }

    // --- General Personalization Settings ---
    fun updateParentPhoneNumber(num: String) {
        viewModelScope.launch {
            database.settingsDao().saveSettings(settings.value.copy(parentPhoneNumber = num))
        }
    }

    fun updateSpeechRate(rate: Float) {
        viewModelScope.launch {
            database.settingsDao().saveSettings(settings.value.copy(speechRate = rate))
            voiceManager.setSpeechRate(rate)
        }
    }

    // --- Countdown Timer Action Controllers ---
    fun setTimerDuration(seconds: Int) {
        _timerDuration.value = seconds
        _timerSecondsRemaining.value = seconds
        pauseTimer()
    }

    fun startTimer() {
        if (_isTimerRunning.value) return
        _isTimerRunning.value = true
        timerJob = viewModelScope.launch {
            while (_timerSecondsRemaining.value > 0 && _isTimerRunning.value) {
                kotlinx.coroutines.delay(1000)
                _timerSecondsRemaining.value -= 1

                // Reminder notification voice at remaining 1 minute or 15 seconds boundary triggers
                if (_timerSecondsRemaining.value == 60) {
                    voiceManager.speakAloud("باقي دقيقة واحدة يا بطل، سننهي النشاط قريباً بهدوء وننتقل للمهمة التالية", settings.value.speechRate)
                } else if (_timerSecondsRemaining.value == 15) {
                    voiceManager.speakAloud("باقي خمسة عشرة ثانية. فلنستعد لإنهاء النشاط معاً بابتسامة", settings.value.speechRate)
                }
            }
            if (_timerSecondsRemaining.value == 0) {
                _isTimerRunning.value = false
                voiceManager.speakAloud("🏆 لقد انتهى الوقت بنجاح كامل يا بطل أكرم، كفو عليك!", settings.value.speechRate)
            }
        }
    }

    fun pauseTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
    }

    fun resetTimer() {
        pauseTimer()
        _timerSecondsRemaining.value = _timerDuration.value
    }

    fun fastForwardTimerToReminder() {
        // Fast forwards the clock to 1 minute and 3 seconds remaining so family testers
        // can easily experience the automatic auditory prompt without long waiting.
        pauseTimer()
        _timerSecondsRemaining.value = 63
        startTimer()
    }

    // --- Meltdown Meltdown/Panic State Toggle Actions ---
    fun toggleMeltdownState() {
        _isMeltdownActive.value = !_isMeltdownActive.value
        if (_isMeltdownActive.value) {
            // Play soothing organic 432Hz sine wave synthetically from engine
            voiceManager.startCalmingSynth()
            triggerOutboundEmergencySms()
        } else {
            voiceManager.stopCalmingSynth()
        }
    }

    private fun triggerOutboundEmergencySms() {
        viewModelScope.launch {
            val destination = settings.value.parentPhoneNumber
            val messageText = "🚨 رسالة عاجلة: ابنكم أكرم قام بالضغط على زر الطوارئ (الحقوني) في التطبيق الآن وهو يمر بفترة توتر وقلق شديد؛ يحتاج رداً عاطفياً وطمأنينة سريعة!"
            _smsSentEvent.emit(SmsPayload(destination, messageText))
        }
    }

    public override fun onCleared() {
        super.onCleared()
        voiceManager.cleanup()
    }
}
```

---

### 8️⃣ مفسّر الأيقونات المبرمجة ديناميكياً: `app/src/main/java/com/example/ui/IconMapper.kt`
```kotlin
package com.example.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

object IconMapper {
    private val iconMap = mapOf(
        "face" to Icons.Default.Face,
        "school" to Icons.Default.School,
        "restaurant" to Icons.Default.Restaurant,
        "home" to Icons.Default.Home,
        "check" to Icons.Default.CheckCircle,
        "dentistry" to Icons.Default.Favorite, // Fallback favorite
        "favorite" to Icons.Default.Favorite,
        "video" to Icons.Default.PlayArrow,
        "star" to Icons.Default.Star,
        "add" to Icons.Default.Add,
        "delete" to Icons.Default.Delete,
        "settings" to Icons.Default.Settings,
        "warning" to Icons.Default.Warning,
        "person" to Icons.Default.Person
    )

    fun getIcon(name: String): ImageVector {
        return iconMap[name.lowercase()] ?: Icons.Default.Star
    }

    val availableIcons = iconMap.keys.toList()
}
```

---

### 9️⃣ واجهات ورسوم التطبيق الكاملة: `app/src/main/java/com/example/ui/AkrmApp.kt`
```kotlin
package com.example.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.viewmodel.AkrmViewModel
import androidx.compose.ui.draw.shadow
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

@Composable
fun PlayfulAnimatedBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "playful_bg")
    
    val anim1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "anim1"
    )
    
    val anim2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(22000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "anim2"
    )

    val anim3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "anim3"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFE0F2FE), // Airy blue sky
                    Color(0xFFECFDF5), // Emerald mist
                    Color(0xFFFFF7ED)  // Soft peach Glow
                )
            )
        )

        // Floating drifting colorful childhood balloons of hope & focus
        drawCircle(
            color = Color(0xFF22D3EE).copy(alpha = 0.14f),
            radius = width * 0.28f,
            center = androidx.compose.ui.geometry.Offset(
                x = width * 0.25f + kotlin.math.sin(anim1.toDouble()).toFloat() * 65f,
                y = height * 0.32f + kotlin.math.cos(anim1.toDouble()).toFloat() * 95f
            )
        )

        drawCircle(
            color = Color(0xFFF472B6).copy(alpha = 0.12f),
            radius = width * 0.38f,
            center = androidx.compose.ui.geometry.Offset(
                x = width * 0.75f + kotlin.math.cos(anim2.toDouble()).toFloat() * 75f,
                y = height * 0.58f + kotlin.math.sin(anim2.toDouble()).toFloat() * 110f
            )
        )

        drawCircle(
            color = Color(0xFFFDE047).copy(alpha = 0.14f),
            radius = width * 0.22f,
            center = androidx.compose.ui.geometry.Offset(
                x = width * 0.52f + kotlin.math.sin(anim3.toDouble()).toFloat() * 85f,
                y = height * 0.76f + kotlin.math.cos(anim3.toDouble()).toFloat() * 65f
            )
        )

        drawCircle(
            color = Color(0xFF4ADE80).copy(alpha = 0.10f),
            radius = width * 0.32f,
            center = androidx.compose.ui.geometry.Offset(
                x = width * 0.18f + kotlin.math.cos(anim1.toDouble() + 1.2).toFloat() * 55f,
                y = height * 0.82f + kotlin.math.sin(anim1.toDouble() + 1.2).toFloat() * 75f
            )
        )

        drawCircle(
            color = Color(0xFFC084FC).copy(alpha = 0.12f),
            radius = width * 0.24f,
            center = androidx.compose.ui.geometry.Offset(
                x = width * 0.82f + kotlin.math.sin(anim2.toDouble() + 1.8).toFloat() * 70f,
                y = height * 0.22f + kotlin.math.cos(anim2.toDouble() + 1.8).toFloat() * 60f
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AkrmApp(viewModel: AkrmViewModel) {
    val context = LocalContext.current

    // Observe State Streams
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val aacCards by viewModel.aacCards.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val spokenSubtitle by viewModel.voiceManager.currentSpokenText.collectAsStateWithLifecycle()

    val isMeltdownActive by viewModel.isMeltdownActive.collectAsStateWithLifecycle()
    val timerDuration by viewModel.timerDuration.collectAsStateWithLifecycle()
    val timerSecondsRemaining by viewModel.timerSecondsRemaining.collectAsStateWithLifecycle()
    val isTimerRunning by viewModel.isTimerRunning.collectAsStateWithLifecycle()

    val recordingTaskItem by viewModel.recordingTaskItem.collectAsStateWithLifecycle()
    val recordingAacCard by viewModel.recordingAacCard.collectAsStateWithLifecycle()
    val isRecordingActive by viewModel.isRecordingActive.collectAsStateWithLifecycle()

    val customGreetingText by viewModel.customGreetingTextToShow.collectAsStateWithLifecycle()

    // UI-only tab management
    var currentTab by remember { mutableStateOf("home") } // "home" (Tasks & AAC), "timer" (Countdown), "customise" (Family settings)

    // Trigger startup greeting once when database loads default rows
    LaunchedEffect(tasks) {
        if (tasks.isNotEmpty()) {
            viewModel.triggerStartupGreeting()
        }
    }

    // Handle SMS Outbound event
    LaunchedEffect(Unit) {
        viewModel.smsSentEvent.collect { payload ->
            try {
                val smsUri = Uri.parse("smsto:${payload.number}")
                val smsIntent = Intent(Intent.ACTION_SENDTO, smsUri).apply {
                    putExtra("sms_body", payload.message)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(smsIntent)
                Toast.makeText(context, "الرجاء تأكيد إرسال رسالة الطوارئ للأهل", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, "لم نتمكن من فتح تطبيق الرسائل: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Main Scaffold with clean background
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("main_scaffold"),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(1.dp)
                        .background(Color.White),
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Sleek avatar and typography
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0D9488)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "🌟",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column {
                                Text(
                                    text = "مرحباً بك يا بطل! 👋",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Color(0xFF0D9488)
                                    ),
                                    modifier = Modifier.testTag("app_title_text")
                                )
                                Text(
                                    text = "أتمنى لك يوماً سعيداً وممتعاً!",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF64748B),
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }

                        // Personalization action inside rounded slate-100 box
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF1F5F9))
                                .clickable { currentTab = "customise" }
                                .testTag("btn_settings_tab"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "تخصيص",
                                tint = if (currentTab == "customise") Color(0xFF008080) else Color(0xFF475569)
                            )
                        }
                    }
                }

                // Greeting Display banner
                if (customGreetingText.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Greeting Icon",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = customGreetingText,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 17.sp,
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            // Accessible Dynamic Navigation & Big Red Panique Help Button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                // Subtitles floating bar (Speaks text aloud visualizer)
                AnimatedVisibility(
                    visible = spokenSubtitle.isNotEmpty(),
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Brush.horizontalGradient(listOf(Color(0xFF00796B), Color(0xFF004D40))))
                            .shadow(4.dp, RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = spokenSubtitle,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                ),
                                textAlign = TextAlign.Right,
                                modifier = Modifier.weight(1f).testTag("subtitle_text")
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Icon(
                                imageVector = Icons.Default.Sms,
                                contentDescription = "نطق صوتي",
                                tint = Color(0xFFE0F2F1),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                // Core Sticky actions Row: Tabs + Red Panic Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .border(1.dp, Color(0xFFF1F5F9))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left action: Tabs navigation
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { currentTab = "home" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (currentTab == "home") Color(0xFFE2F2F2) else Color.Transparent,
                                contentColor = if (currentTab == "home") Color(0xFF008080) else Color(0xFF64748B)
                            ),
                            elevation = null,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .testTag("tab_home_button")
                        ) {
                            Text("الرئيسية", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        Button(
                            onClick = { currentTab = "timer" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (currentTab == "timer") Color(0xFFE2F2F2) else Color.Transparent,
                                contentColor = if (currentTab == "timer") Color(0xFF008080) else Color(0xFF64748B)
                            ),
                            elevation = null,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .testTag("tab_timer_button")
                        ) {
                            Text("المؤقت", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Right action: Red Meltdown Emergency Button "الحقوني" with white pulsing circle
                    Button(
                        onClick = { viewModel.toggleMeltdownState() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF4444)
                        ),
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier
                            .width(130.dp)
                            .height(54.dp)
                            .testTag("btn_emergency_panic")
                            .scale(if (isMeltdownActive) 0.95f else 1.0f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            // Pulsing/Live indicator dot
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "الحقوني",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 17.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Playful Animated Background Layer
            PlayfulAnimatedBackground()

            // Main views routing
            when (currentTab) {
                "home" -> HomeScreen(
                    viewModel = viewModel,
                    tasks = tasks,
                    aacCards = aacCards
                )
                "timer" -> CodeTimerScreen(
                    viewModel = viewModel,
                    timerDuration = timerDuration,
                    timerSecondsRemaining = timerSecondsRemaining,
                    isTimerRunning = isTimerRunning
                )
                "customise" -> PersonalizationScreen(
                    viewModel = viewModel,
                    tasks = tasks,
                    aacCards = aacCards,
                    settings = settings
                )
            }

            // Meltdown / Panic Overlay (Calming breathing screen)
            AnimatedVisibility(
                visible = isMeltdownActive,
                enter = fadeIn() + expandIn(),
                exit = fadeOut() + shrinkOut()
            ) {
                MeltdownCalmingOverlay(
                    onDismiss = { viewModel.toggleMeltdownState() },
                    parentPhone = settings.parentPhoneNumber
                )
            }
        }
    }
}

// ================== HOMEPAGE SCREEN ==================

@Composable
fun HomeScreen(
    viewModel: AkrmViewModel,
    tasks: List<TaskItem>,
    aacCards: List<AacCard>
) {
    var selectedAacCategory by remember { mutableStateOf("أنا أريد...") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        // --- SECTION 1: Daily Tasks Visual Schedule ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🗓️ جَدْوَلِي لِلْيَوْمِ 🗓️",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF0D9488)
                    ),
                    textAlign = TextAlign.Right
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val incompleteCount = tasks.count { !it.isCompleted }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFCCFBF1))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "$incompleteCount متبقية",
                            color = Color(0xFF0F766E),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    TextButton(
                        onClick = { viewModel.resetAllTasks() },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF008080)),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text("إعادة تصفير", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Text(
                text = "💡 كبسة واحدة لتسمع المهمة بصوتي، كبستين عشان تخلّصها يا بطل!",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp),
                textAlign = TextAlign.Right
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.LightGray.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("لا توجد مهام حالية، حمّل مخصصاً", color = Color.Gray)
                }
            } else {
                val firstIncompleteId = remember(tasks) { tasks.firstOrNull { !it.isCompleted }?.id }
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("visual_tasks_row"),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(tasks, key = { it.id }) { task ->
                        val isActive = task.id == firstIncompleteId
                        TaskItemCard(
                            task = task,
                            isActive = isActive,
                            onSingleTap = { viewModel.onTaskSingleTap(task) },
                            onDoubleTap = { viewModel.onTaskDoubleTap(task) }
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .height(1.dp)
                .background(Color(0xFFE2E8F0))
        )

        // --- SECTION 2: AAC Communication Board ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text(
                text = "💬 لَوْحَةُ التَّوَاصُلِ الْبَصَرِيِّ 💬",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF0D9488)
                ),
                modifier = Modifier.padding(horizontal = 20.dp),
                textAlign = TextAlign.Right
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Categories horizontal selector
            val categories = listOf("أنا أريد...", "أنا أشعر...", "ألعابي وأنشطتي")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = selectedAacCategory == category
                    Button(
                        onClick = { selectedAacCategory = category },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Color(0xFF008080) else Color(0xFFF1F5F9),
                            contentColor = if (isSelected) Color.White else Color(0xFF475569)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("aac_cat_${category}")
                    ) {
                        Text(
                            text = category,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Cards Grid
            val filteredCards = aacCards.filter { it.category == selectedAacCategory }

            if (filteredCards.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("لا توجد بطاقة تواصل في هذا القسم", color = Color.Gray)
                }
            } else {
                // We use a fixed-height box and simple local layout to nested-scroll safely
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val chunked = filteredCards.chunked(2)
                    chunked.forEach { rowPairs ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            rowPairs.forEach { card ->
                                Box(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    AacCardItem(
                                        card = card,
                                        onTap = { viewModel.onAacCardTap(card) }
                                    )
                                }
                            }
                            if (rowPairs.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskItemCard(
    task: TaskItem,
    isActive: Boolean = false,
    onSingleTap: () -> Unit,
    onDoubleTap: () -> Unit
) {
    val scale = remember { Animatable(1f) }

    Box(
        modifier = Modifier
            .width(160.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                if (task.isCompleted) Color(0xFFF8FAFC)
                else Color.White
            )
            .border(
                width = if (isActive || task.isCompleted) 2.dp else 1.dp,
                color = if (task.isCompleted) Color(0xFF008080).copy(alpha = 0.4f)
                        else if (isActive) Color(0xFF008080)
                        else Color(0xFFE2E8F0),
                shape = RoundedCornerShape(24.dp)
            )
            .then(
                if (task.isCompleted) Modifier.alpha(0.65f) else Modifier
            )
            .pointerInput(task.id) {
                detectTapGestures(
                    onTap = { onSingleTap() },
                    onDoubleTap = { onDoubleTap() }
                )
            }
            .scale(scale.value)
            .testTag("task_card_${task.id}")
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (task.isCustom) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0xFF008080).copy(alpha = 0.1f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("مخصص", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF008080))
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Icon(
                    imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.Circle,
                    contentDescription = if (task.isCompleted) "مكتمل" else "غير مكتمل",
                    tint = if (task.isCompleted) Color(0xFF008080) else Color(0xFFCBD5E1),
                    modifier = Modifier.size(24.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        if (task.isCompleted) Color(0xFFE2E8F0)
                        else if (isActive) Color(0xFFCCFBF1)
                        else Color(0xFFF1F5F9)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (task.imageUri != null) {
                    val bitmap = remember(task.imageUri) {
                        ImageUriDecoder.decodeUriToBitmap(task.imageUri)
                    }
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = task.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = IconMapper.getIcon(task.iconName),
                            contentDescription = task.title,
                            tint = Color(0xFF008080),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                } else {
                    Icon(
                        imageVector = IconMapper.getIcon(task.iconName),
                        contentDescription = task.title,
                        tint = Color(0xFF008080),
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF1E293B)
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun AacCardItem(
    card: AacCard,
    onTap: () -> Unit
) {
    val cardColor = remember(card.colorHex) {
        try {
            Color(android.graphics.Color.parseColor(card.colorHex))
        } catch (e: Exception) {
            Color(0xFF00796B)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable { onTap() }
            .testTag("aac_card_${card.id}"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor.copy(alpha = 0.08f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, cardColor.copy(alpha = 0.25f), RoundedCornerShape(24.dp))
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = cardColor.copy(alpha = 0.8f),
                    modifier = Modifier.size(16.dp)
                )

                if (card.isCustom) {
                    Text("مخصص", fontSize = 8.sp, color = cardColor, fontWeight = FontWeight.Bold)
                }
            }

            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(cardColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                if (card.imageUri != null) {
                    val bitmap = remember(card.imageUri) {
                        ImageUriDecoder.decodeUriToBitmap(card.imageUri)
                    }
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = card.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = IconMapper.getIcon(card.iconName),
                            contentDescription = card.title,
                            tint = cardColor,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                } else {
                    Icon(
                        imageVector = IconMapper.getIcon(card.iconName),
                        contentDescription = card.title,
                        tint = cardColor,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Text(
                text = card.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = cardColor
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ================== VISUAL COUNTDOWN TIMER SCREEN ==================

@Composable
fun CodeTimerScreen(
    viewModel: AkrmViewModel,
    timerDuration: Int,
    timerSecondsRemaining: Int,
    isTimerRunning: Boolean
) {
    val totalMinutes = timerDuration / 60
    val remainingMinutes = timerSecondsRemaining / 60
    val remainingSeconds = timerSecondsRemaining % 60

    val fractionRemaining = if (timerDuration > 0) {
        timerSecondsRemaining.toFloat() / timerDuration.toFloat()
    } else {
        0f
    }

    val primaryColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "⏱️ مُؤَقِّتُ الأَبْطَالِ التَّنَازُلِيُّ ⏱️",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )

        Text(
            text = "هذا المؤقت البصري يساعد الأبطال على تنظيم الوقت قبل انتهاء اللعب والأنشطة.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Box(
            modifier = Modifier
                .size(230.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    style = Stroke(width = 16.dp.toPx())
                )

                drawArc(
                    color = primaryColor,
                    startAngle = -90f,
                    sweepAngle = fractionRemaining * 360f,
                    useCenter = false,
                    style = Stroke(width = 18.dp.toPx())
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = String.format(Locale.US, "%02d:%02d", remainingMinutes, remainingSeconds),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 44.sp,
                        color = primaryColor
                    ),
                    modifier = Modifier.testTag("timer_countdown_text")
                )
                Text(
                    text = "الوقت المتبقي",
                    fontSize = 13.sp,
                    color = Color.Gray,
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("اختر الوقت المناسب للنشاط:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val presets = listOf(60, 180, 300, 600, 900)
                val labels = listOf("١ ق", "٣ ق", "٥ ق", "١٠ ق", "١٥ ق")

                presets.forEachIndexed { idx, s ->
                    val isSelected = timerDuration == s
                    Button(
                        onClick = { viewModel.setTimerDuration(s) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.2f),
                            contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_timer_preset_$s")
                    ) {
                        Text(labels[idx], fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    if (isTimerRunning) viewModel.pauseTimer()
                    else viewModel.startTimer()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isTimerRunning) Color(0xFFE53935) else Color(0xFF4CAF50)
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .testTag("btn_timer_toggle")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (isTimerRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = "تحكم"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isTimerRunning) "توقف مؤقت" else "ابدأ المؤقت",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Button(
                onClick = { viewModel.resetTimer() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray.copy(alpha = 0.4f),
                    contentColor = MaterialTheme.colorScheme.onBackground
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(0.7f)
                    .height(54.dp)
                    .testTag("btn_timer_reset")
            ) {
                Text("إعادة تعيين", fontWeight = FontWeight.Bold)
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .clickable { viewModel.fastForwardTimerToReminder() },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "🚀 زر الفحص السريع (تسريع الوقت)",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "اضغط للتسريع وتجربة التنبيه الصوتي التلقائي \"باقي شوية واللعب ينتهي...\"",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "تسريع",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// ================== EMERGENCY CALMING OVERLAY ==================

@Composable
fun MeltdownCalmingOverlay(
    onDismiss: () -> Unit,
    parentPhone: String
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val circleScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    var breathInstruction by remember { mutableStateOf("شهيق... اسحب هواء بهدوء") }
    LaunchedEffect(circleScale) {
        breathInstruction = if (circleScale > 1.05f) {
            "زفير... طلع قلقك برة براااحة 🧘"
        } else {
            "شهيق... اسحب نفس عميق وجميل 🌸"
        }
    }

    Dialog(onDismissRequest = { onDismiss() }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .pointerInput(Unit) {}
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = "🚨 وضع النجدة والسلامة",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0x20EF5350)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "تم تجهيز رسالة الطوارئ للأهل على الرقم:",
                            color = Color.LightGray,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = parentPhone,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "يجري الآن تشغيل ذبذبة صوتية هادئة (432Hz) لتهدئة الأعصاب وتنظيم التنفس.",
                            color = Color(0xFFA5D6A7),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .drawBehind {
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFF00BFA5).copy(alpha = 0.4f),
                                            Color(0xFF00796B).copy(alpha = 0.05f)
                                        )
                                    ),
                                    radius = (size.minDimension / 2) * circleScale
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00796B))
                        )
                    }

                    Text(
                        text = breathInstruction,
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                }

                Button(
                    onClick = { onDismiss() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("btn_dismiss_emergency")
                ) {
                    Text(
                        text = "أنا بقيت أحسن، أرجع للتطبيق",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

// ================== PERSONALIZATION/SETTINGS SCREEN ==================

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PersonalizationScreen(
    viewModel: AkrmViewModel,
    tasks: List<TaskItem>,
    aacCards: List<AacCard>,
    settings: AppSettings
) {
    val context = LocalContext.current
    val cacheDir = context.cacheDir

    val recordingTaskItem by viewModel.recordingTaskItem.collectAsStateWithLifecycle()
    val recordingAacCard by viewModel.recordingAacCard.collectAsStateWithLifecycle()
    val isRecordingActive by viewModel.isRecordingActive.collectAsStateWithLifecycle()

    var parentMobile by remember(settings.parentPhoneNumber) { mutableStateOf(settings.parentPhoneNumber) }
    var speechRateSlider by remember(settings.speechRate) { mutableStateOf(settings.speechRate) }

    var showNewTaskForm by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskPronounce by remember { mutableStateOf("") }
    var newTaskIcon by remember { mutableStateOf("school") }

    var showNewCardForm by remember { mutableStateOf(false) }
    var newCardCategory by remember { mutableStateOf("أنا أريد...") }
    var newCardTitle by remember { mutableStateOf("") }
    var newCardPronounce by remember { mutableStateOf("") }
    var newCardIcon by remember { mutableStateOf("restaurant") }
    var newCardColorHex by remember { mutableStateOf("#00796B") }

    var selectedTaskToCapture by remember { mutableStateOf<TaskItem?>(null) }
    var selectedAacCardToCapture by remember { mutableStateOf<AacCard?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val f = File(cacheDir, "family_image_${System.currentTimeMillis()}.png")
            try {
                val bos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 95, bos)
                val fos = FileOutputStream(f)
                fos.write(bos.toByteArray())
                fos.flush()
                fos.close()

                val uriStr = Uri.fromFile(f).toString()
                selectedTaskToCapture?.let { viewModel.updateTaskImage(it, uriStr) }
                selectedAacCardToCapture?.let { viewModel.updateCardImage(it, uriStr) }
                Toast.makeText(context, "تم حفظ الصورة العائلية المخصصة بنجاح!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "فشل حفظ الصورة المخصصة", Toast.LENGTH_SHORT).show()
            }
        }
        selectedTaskToCapture = null
        selectedAacCardToCapture = null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "📸 التخصيص العائلي والصوتي (بوابة الأهل)",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("📞 هاتف الأهل لرسائل الطوارئ:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                OutlinedTextField(
                    value = parentMobile,
                    onValueChange = { text -> parentMobile = text },
                    placeholder = { Text("أدخل رقم والد/والدة أكرم") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_parent_phone")
                )
                Button(
                    onClick = {
                        viewModel.updateParentPhoneNumber(parentMobile)
                        Toast.makeText(context, "تم حفظ رقم الهاتف بنجاح", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("حفظ رقم الهاتف", fontWeight = FontWeight.Bold)
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("🗣️ تجربة نطق الصوت والسرعة (Speech Rate):", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    text = "السرعة المفضلة لليافعين المصابين بالتوحد هي سرعة بطيئة (0.45) لمساعدتهم في الاستيعاب.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("سرعة: ${String.format(Locale.US, "%.2f", speechRateSlider)}", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(12.dp))
                    Slider(
                        value = speechRateSlider,
                        onValueChange = {
                            speechRateSlider = it
                            viewModel.updateSpeechRate(it)
                        },
                        valueRange = 0.2f..1.1f,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.forceResetFirstTime(true) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray.copy(alpha = 0.25f), contentColor = Color.Black),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("محاكاة: ترحيب أول مرّة", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.forceResetFirstTime(false) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray.copy(alpha = 0.25f), contentColor = Color.Black),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("محاكاة: ترحيب العودة", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🗓️ ميزات وتخصيص مهام الجدول:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Button(
                        onClick = { showNewTaskForm = !showNewTaskForm },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(if (showNewTaskForm) "إغلاق" else "إضافة مهمة جديدة", fontSize = 11.sp)
                    }
                }

                if (showNewTaskForm) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("إضافة مهمة جديدة للجدول البصري:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        OutlinedTextField(
                            value = newTaskTitle,
                            onValueChange = { text -> newTaskTitle = text },
                            label = { Text("اسم المهمة (مثال: الذهاب للمسجد)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newTaskPronounce,
                            onValueChange = { text -> newTaskPronounce = text },
                            label = { Text("جملة النطق باللغة العربية الفصحى") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("اختر الصورة/الأيقونة المعبرة:")
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            IconMapper.availableIcons.take(6).forEach { ic ->
                                val active = newTaskIcon == ic
                                IconButton(
                                    onClick = { newTaskIcon = ic },
                                    modifier = Modifier.background(if (active) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent)
                                ) {
                                    Icon(imageVector = IconMapper.getIcon(ic), contentDescription = ic)
                                }
                            }
                        }

                        Button(
                            onClick = {
                                if (newTaskTitle.isNotEmpty() && newTaskPronounce.isNotEmpty()) {
                                    viewModel.addNewCustomTask(newTaskTitle, newTaskPronounce, newTaskIcon)
                                    showNewTaskForm = false
                                    newTaskTitle = ""
                                    newTaskPronounce = ""
                                    Toast.makeText(context, "تمت إضافة المهمة للجدول", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("أضف المهمة")
                        }
                    }
                }

                Text("المهمّات ومسجّلات الصوت العائلي:", fontWeight = FontWeight.Medium, fontSize = 13.sp, color = Color.Gray)
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tasks.forEach { task ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.LightGray.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(task.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                if (task.voiceFilePath != null) {
                                    Text("🎤 صوت عائلي مسجل", fontSize = 11.sp, color = Color(0xFF00796B), fontWeight = FontWeight.Bold)
                                } else {
                                    Text("🔊 صوت الروبوت المبرمج", fontSize = 11.sp, color = Color.LightGray)
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        selectedTaskToCapture = task
                                        cameraLauncher.launch()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "صورة مخصصة من الكاميرا",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }

                                val isRecordedThis = recordingTaskItem?.id == task.id
                                Button(
                                    onClick = {
                                        if (isRecordingActive) {
                                            viewModel.stopVoiceRecordingForTask(task, File(cacheDir, "task_voice_${task.id}.m4a"))
                                        } else {
                                            viewModel.startVoiceRecordingForTask(task, cacheDir)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isRecordedThis) Color.Red else Color.LightGray.copy(alpha = 0.2f),
                                        contentColor = if (isRecordedThis) Color.White else Color.Black
                                    ),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Text(
                                        text = if (isRecordedThis) "جاري التسجيل... اضغط للحفظ" else "سجل صوتك 🎙️",
                                        fontSize = 10.sp
                                    )
                                }

                                if (task.isCustom) {
                                    IconButton(
                                        onClick = { viewModel.deleteCustomTask(task) }
                                    ) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف", tint = Color.Red)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("💬 ميزات وبطاقات التواصل البديل:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Button(
                        onClick = { showNewCardForm = !showNewCardForm },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(if (showNewCardForm) "إغلاق" else "إضافة بطاقة جديدة", fontSize = 11.sp)
                    }
                }

                if (showNewCardForm) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("إضافة بطاقة تواصل جديدة للوحة:", fontWeight = FontWeight.Bold, fontSize = 12.sp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val categories = listOf("أنا أريد...", "أنا أشعر...", "ألعابي وأنشطتي")
                            categories.forEach { cat ->
                                val active = newCardCategory == cat
                                Button(
                                    onClick = { newCardCategory = cat },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (active) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.2f),
                                        contentColor = if (active) Color.White else Color.Black
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(cat, fontSize = 9.sp)
                                }
                            }
                        }

                        OutlinedTextField(
                            value = newCardTitle,
                            onValueChange = { text -> newCardTitle = text },
                            label = { Text("اسم البطاقة (أكل تفاحة، داير زول)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = newCardPronounce,
                            onValueChange = { text -> newCardPronounce = text },
                            label = { Text("جملة النطق باللغة العربية الفصحى") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                if (newCardTitle.isNotEmpty() && newCardPronounce.isNotEmpty()) {
                                    viewModel.addNewCustomAacCard(
                                        category = newCardCategory,
                                        title = newCardTitle,
                                        pronunciation = newCardPronounce,
                                        icon = "favorite",
                                        colorHex = "#00796B"
                                    )
                                    showNewCardForm = false
                                    newCardTitle = ""
                                    newCardPronounce = ""
                                    Toast.makeText(context, "تمت إضافة الكرت للوحة", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("أضف كرت التواصل")
                        }
                    }
                }

                Text("تخصيص تسجيل كروت التواصل المعروضة:", fontWeight = FontWeight.Medium, fontSize = 13.sp, color = Color.Gray)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    aacCards.take(8).forEach { card ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.LightGray.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(card.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                if (card.voiceFilePath != null) {
                                    Text("🎤 صوت عائلي مسجل", fontSize = 11.sp, color = Color(0xFF00796B), fontWeight = FontWeight.Bold)
                                } else {
                                    Text("🔊 صوت مدمج تلقائي", fontSize = 11.sp, color = Color.LightGray)
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        selectedAacCardToCapture = card
                                        cameraLauncher.launch()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "صورة عائلية مخصصة",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }

                                val isRecordedThis = recordingAacCard?.id == card.id
                                Button(
                                    onClick = {
                                        if (isRecordingActive) {
                                            viewModel.stopVoiceRecordingForAacCard(card, File(cacheDir, "aac_voice_${card.id}.m4a"))
                                        } else {
                                            viewModel.startVoiceRecordingForAacCard(card, cacheDir)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isRecordedThis) Color.Red else Color.LightGray.copy(alpha = 0.2f),
                                        contentColor = if (isRecordedThis) Color.White else Color.Black
                                    ),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Text(
                                        text = if (isRecordedThis) "جاري التسجيل... احفظ" else "سجل 🎙️",
                                        fontSize = 10.sp
                                    )
                                }

                                if (card.isCustom) {
                                    IconButton(
                                        onClick = { viewModel.deleteCustomAacCard(card) }
                                    ) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف", tint = Color.Red)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color(0xFFF43F5E), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFF1F2),
                contentColor = Color(0xFF9F1239)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "⚠️ تنبيه وحل مشكلة ظهور خطأ (Ererr / 404) للأهل والأصدقاء",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFFBE123C)
                        )
                    )
                }

                Text(
                    text = "رابط التجربة السريعة (ais-pre) مخصص للمطور لمعاينة العمل مؤقتاً، وينتهي تلقائياً (تظهر صفحة 404 الخطأ المرفقة) عند خمول الجلسة أو إغلاق المتصفح. لتجربة ثابتة ومستقرة للأهل للأبد دون أي انقطاع، تفضل بتطبيق الحل الأمثل بالأسفل:",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                    color = Color(0xFF1E293B)
                )

                Card(
                    modifier = Modifier.fillMaxWidth()
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "✅ الحل الأسهل والأبدي: تثبيت التطبيق مباشرة بصيغة (APK)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF15803D)
                        )
                        
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("1️⃣ من شريط أدوات منصة Google AI Studio بالمتصفح (وليس داخل الجوال)، اضغط على القائمة العلوية ثم زر التصدير (Export/Build) أو خيارات البناء.", fontSize = 11.5.sp, color = Color(0xFF334155))
                            Text("2️⃣ اختر خيار توليد حزمة أندرويد المستقلة (Generate APK) ليقوم النظام ببناء التطبيق بشكل آمن تماماً.", fontSize = 11.5.sp, color = Color(0xFF334155))
                            Text("3️⃣ سيظهر لك رمز كود QR أو رابط تحميل مباشر؛ امسحه بكاميرا هاتفك أو اضغط على تحميل لتنزيل ملف التطبيق (ملف APK).", fontSize = 11.5.sp, color = Color(0xFF334155))
                            Text("4️⃣ أسهل طريقة لمشاركته مع العائلة: أرسل ملف الـ APK الذي قمت بتنزيله كـ (مستند/ملف Document) مباشرة إليهم في محادثة الواتساب (WhatsApp)! بمجرد أن يضغطوا عليه في هاتفهم، سيتمكنون من تثبيته وسيعمل معهم بكل موثوقية وبلا حاجة لإنترنت نهائياً 🌐✨", fontSize = 11.5.sp, color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0xFFE2E8F0))
                        )
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("💡", fontSize = 14.sp)
                            Text(
                                text = "تثبيت ملف الـ APK يوفر حفظاً دائماً لإعدادات الطفل ونطقه وسجل نشاطه اليومي بأعلى أداء للأجهزة.",
                                fontSize = 10.5.sp,
                                color = Color(0xFF475569)
                            )
                        }
                    }
                }

                val previewUrl = "https://ais-pre-erekjsi2rsgoqbgfe5mi3x-296498838657.europe-west2.run.app"
                Card(
                    modifier = Modifier.fillMaxWidth()
                        .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "🔗 رابط التجربة السريعة (المؤقت):",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF475569)
                        )
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "🔗",
                                fontSize = 16.sp
                            )
                            Text(
                                text = previewUrl,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A),
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    val clipboardManager = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                    val clipData = android.content.ClipData.newPlainText("Akrm App Link", previewUrl)
                                    clipboardManager.setPrimaryClip(clipData)
                                    Toast.makeText(context, "📋 تم نسخ الرابط المؤقت بنجاح!", Toast.LENGTH_SHORT).show()
                                },
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                                modifier = Modifier.weight(1f).height(40.dp)
                            ) {
                                Text("نسخ الرابط المؤقت 📋", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            Button(
                                onClick = {
                                    val sendIntent = android.content.Intent().apply {
                                        action = android.content.Intent.ACTION_SEND
                                        putExtra(
                                            android.content.Intent.EXTRA_TEXT,
                                            "أهلاً بك! يمكنك الآن تجربة تطبيق أكرم (Akrm) للأقارب بخصوص طيف التوحد مباشرة بمتصفح هاتفك عبر الرابط التالي:\n\n$previewUrl\n\n(يرجى ملاحظة أن الرابط مؤقت؛ للحصول على نسخة تثبيت دائمة للأبد، اطلب مني إرسال ملف APK) 🌟"
                                        )
                                        type = "text/plain"
                                    }
                                    val shareIntent = android.content.Intent.createChooser(sendIntent, "مشاركة تطبيق أكرم عبر:")
                                    context.startActivity(shareIntent)
                                },
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                                modifier = Modifier.weight(1f).height(40.dp)
                            ) {
                                Text("مشاركة الرابط 📤", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

object ImageUriDecoder {
    fun decodeUriToBitmap(uriString: String): Bitmap? {
        return try {
            val uri = Uri.parse(uriString)
            val file = File(uri.path ?: return null)
            if (file.exists()) {
                android.graphics.BitmapFactory.decodeFile(file.absolutePath)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
```

---

### 🔟 تهيئة الألوان والأنماط الحسية للتطبيق: `app/src/main/java/com/example/ui/theme/Color.kt` & `Theme.kt`
#### Color.kt
```kotlin
package com.example.ui.theme

import androidx.compose.ui.graphics.Color

val TealPrimary = Color(0xFF008080)
val TealSecondary = Color(0xFF0D9488)
val TealTertiary = Color(0xFF042F2E)
val SkyBlue = Color(0xFF0369A1)
val SkyBlueLight = Color(0xFFE0F2FE)
val MintContainer = Color(0xFFCCFBF1)
val CalmBackground = Color(0xFFF5F7F8)
val CalmSurface = Color(0xFFFFFFFF)
val TextDark = Color(0xFF1E293B)

val TealPrimaryDark = Color(0xFF4DB6AC)
val TealSecondaryDark = Color(0xFF80CBC4)
val DarkBackground = Color(0xFF121A1A)
val DarkSurface = Color(0xFF1E2828)

val EmergencyRed = Color(0xFFD32F2F)
val EmergencyRedContainer = Color(0xFFFFEBEE)
val WarningYellow = Color(0xFFFFA000)
```

#### Theme.kt
```kotlin
package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = TealPrimaryDark,
    secondary = TealSecondaryDark,
    tertiary = SkyBlue,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = TextDark,
    onSecondary = TextDark,
    onBackground = CalmSurface,
    onSurface = CalmSurface
)

private val LightColorScheme = lightColorScheme(
    primary = TealPrimary,
    secondary = TealSecondary,
    tertiary = SkyBlue,
    background = CalmBackground,
    surface = CalmSurface,
    onPrimary = CalmSurface,
    onSecondary = CalmSurface,
    onBackground = TextDark,
    onSurface = TextDark
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
```

#### Type.kt
```kotlin
package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    )
)
```

---

## 🚀 كيفية تشغيل وبناء ملفات الأكواد وإصدار حزمة APK للتحميل والتشغيل الدائم؟

1. **تحميل المشروع كملف ZIP**:
   يمكنك تنزيل المشروع بالكامل كملف مضغوط بالنقر على زر الترس الإعدادي في **Google AI Studio** واختيار **Export as ZIP**.
   
2. **الاستيراد والتحديث في Android Studio**:
   - قم بفك الضغط عن الملف المرفوع.
   - افتح برنامج **Android Studio** واضغط على `Open an existing project`.
   - اختر المجلد الرئيسي للمشروع ليتولى نظام البناء Gradle مزامنة الملحقات تلقائياً.

3. **تصدير ملف تثبيت مباشر (APK) دائم وبدون لابتوب**:
   - من داخل منصة **Google AI Studio** على المتصفح (في اللابتوب)، اضغط على القائمة العلوية ثم خيارات تصدير وبناء التطبيقات (**Export/Build APK**).
   - النظام سيبني التطبيق آمن تماماً ويعطيك رابط تحميل مباشر أو كود QR.
   - امسح الكود بكاميرا الجوال وحمّل ملف الـ APK.
   
4. **طريقة المشاركة مع عائلتك للأبد**:
   - أرسل ملف الـ PDF/APK الذي قمت بتحميله مباشرة كمرفق **مستند (Document)** لأي محادثة عائلية في **WhatsApp/Telegram**.
   - يمكن لكل فرد من أفراد العائلة النقر عليه مباشرة لتثبيته في جواله وسيعمل معهم فورياً وللأبد دون الحاجة لمتصفح أو إنترنت.
