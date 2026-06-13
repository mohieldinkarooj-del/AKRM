package com.example.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.*
import com.example.util.VoiceManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File

class AkrmViewModel(application: Application) : AndroidViewModel(application) {

    // Initialize Room database
    private val database: AppDatabase by lazy {
        Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            "akrm_database"
        ).fallbackToDestructiveMigration().build()
    }

    private val taskDao = database.taskDao()
    private val aacCardDao = database.aacCardDao()
    private val appSettingsDao = database.appSettingsDao()

    // Instantiate VoiceManager
    val voiceManager = VoiceManager(application)

    // Data Flow Streams
    val tasks: StateFlow<List<TaskItem>> = taskDao.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val aacCards: StateFlow<List<AacCard>> = aacCardDao.getAllCards()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settings: StateFlow<AppSettings> = appSettingsDao.getSettingsFlow()
        .filterNotNull()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AppSettings() // default initial
        )

    // UI State / Reactive variables
    private val _isMeltdownActive = MutableStateFlow(false)
    val isMeltdownActive: StateFlow<Boolean> = _isMeltdownActive

    private val _smsSentEvent = MutableSharedFlow<SmsPayload>()
    val smsSentEvent: SharedFlow<SmsPayload> = _smsSentEvent

    // Interactive Timer State
    private val _timerDuration = MutableStateFlow(300) // Default 5 minutes (300 seconds)
    val timerDuration: StateFlow<Int> = _timerDuration

    private val _timerSecondsRemaining = MutableStateFlow(0)
    val timerSecondsRemaining: StateFlow<Int> = _timerSecondsRemaining

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning

    private var countdownJob: Job? = null
    private var reminderSpokenDuringCurrentSession = false

    // Custom voice recording states
    private val _recordingTaskItem = MutableStateFlow<TaskItem?>(null)
    val recordingTaskItem: StateFlow<TaskItem?> = _recordingTaskItem

    private val _recordingAacCard = MutableStateFlow<AacCard?>(null)
    val recordingAacCard: StateFlow<AacCard?> = _recordingAacCard

    private val _isRecordingActive = MutableStateFlow(false)
    val isRecordingActive: StateFlow<Boolean> = _isRecordingActive

    // General app state tracking
    private val _isGreetingShown = MutableStateFlow(false)
    val isGreetingShown: StateFlow<Boolean> = _isGreetingShown

    private val _customGreetingTextToShow = MutableStateFlow("")
    val customGreetingTextToShow: StateFlow<String> = _customGreetingTextToShow

    init {
        viewModelScope.launch {
            seedDatabaseIfRequired()
            initializeSettingsIfRequired()
            // Observe settings to sync rate with VoiceManager
            settings.collect { set ->
                voiceManager.setSpeechRate(set.speechRate)
            }
        }
    }

    private suspend fun initializeSettingsIfRequired() {
        val existingSettings = appSettingsDao.getSettingsDirect()
        if (existingSettings == null) {
            appSettingsDao.insertSettings(AppSettings())
        }
    }

    private suspend fun seedDatabaseIfRequired() {
        withContext(Dispatchers.IO) {
            // Seed Task items if empty
            if (taskDao.getTaskCount() == 0) {
                val defaultTasks = listOf(
                    TaskItem(
                        title = "غسل الوجه والأسنان 🧼",
                        ttsPhrase = "غسل الوجه الجميل وتنظيف الأسنان بالفرشاة والمعجون لنكون دائماً رائعين ونظيفين!",
                        iconName = "brush",
                        orderIndex = 0
                    ),
                    TaskItem(
                        title = "الدروس والواجبات 📚",
                        ttsPhrase = "يا بطل، حان وقت مراجعة دروسنا الرائعة وحل واجبنا لنتعلم أشياء جديدة وننجح بتفوق!",
                        iconName = "school",
                        orderIndex = 1
                    ),
                    TaskItem(
                        title = "اللعب والراحة 🎮",
                        ttsPhrase = "الآن هو وقت اللعب الممتع والراحة السعيدة، لنشعر بالنشاط والسرور!",
                        iconName = "sports_esports",
                        orderIndex = 2
                    ),
                    TaskItem(
                        title = "زيارة بيت جدي 🏡",
                        ttsPhrase = "هيا بنا في نزهة لطيفة لزيارة بيت جدي الحبيب وقضاء وقت رائع معه!",
                        iconName = "home",
                        orderIndex = 3
                    ),
                    TaskItem(
                        title = "الاستحمام المنعش 🚿",
                        ttsPhrase = "حان وقت الاستحمام بالماء اللطيف لنشعر بالانتعاش والنشاط والراحة!",
                        iconName = "bathtub",
                        orderIndex = 4
                    ),
                    TaskItem(
                        title = "نوم هادئ وسعيد 😴",
                        ttsPhrase = "ليلة سعيدة ونوم هادئ وعافية، لنحلم بأحلام جميلة ورائعة، تصبح على خير!",
                        iconName = "bedtime",
                        orderIndex = 5
                    )
                )
                defaultTasks.forEach { taskDao.insertTask(it) }
            }

            // Seed AAC Cards if empty
            if (aacCardDao.getCardCount() == 0) {
                val defaultCards = listOf(
                    // Category: Basic Needs - "أنا أريد..."
                    AacCard(
                        category = "أنا أريد...",
                        title = "أنا جائع 🍎",
                        ttsPhrase = "أنا جائع يا أمي، أريد أن آكل وجبة لذيذة الآن لو سمحتِ",
                        iconName = "restaurant",
                        colorHex = "#FF9800" // Vibrant Orange
                    ),
                    AacCard(
                        category = "أنا أريد...",
                        title = "أنا عطشان 💧",
                        ttsPhrase = "أنا عطشان، هل يمكنني الحصول على كوب ماء منعش من فضلك؟",
                        iconName = "water_drop",
                        colorHex = "#2196F3" // Blue
                    ),
                    AacCard(
                        category = "أنا أريد...",
                        title = "دورة المياه 🚾",
                        ttsPhrase = "أريد الذهاب إلى دورة المياه الآن لو سمحتم",
                        iconName = "wc",
                        colorHex = "#9C27B0" // Purple
                    ),
                    AacCard(
                        category = "أنا أريد...",
                        title = "النوم والراحة 🛌",
                        ttsPhrase = "أشعر بالنعاس والتعب، وأريد الاستلقاء على سريري المريح للنوم قليلاً",
                        iconName = "airline_seat_flat",
                        colorHex = "#795548" // Brown
                    ),

                    // Category: Feelings - "أنا أشعر..."
                    AacCard(
                        category = "أنا أشعر...",
                        title = "سعيد وجذل 🎉",
                        ttsPhrase = "أنا سعيد ومبتهج وفرحان للغاية اليوم، الحمد لله!",
                        iconName = "sentiment_very_satisfied",
                        colorHex = "#4CAF50" // Soft Green
                    ),
                    AacCard(
                        category = "أنا أشعر...",
                        title = "حزين أو متعب 🥺",
                        ttsPhrase = "أنا أشعر بالحزن والضيق قليلاً، وأحتاج لبعض الرعاية والاهتمام",
                        iconName = "sentiment_very_dissatisfied",
                        colorHex = "#E91E63" // Pinkish Red
                    ),
                    AacCard(
                        category = "أنا أشعر...",
                        title = "قلق ومتوتر 😟",
                        ttsPhrase = "أنا أشعر بالتوتر والقلق، أرجوكم ساعدوني لأشعر بالطمأنينة والهدوء",
                        iconName = "warning",
                        colorHex = "#FFEB3B" // Amber-Yellow
                    ),
                    AacCard(
                        category = "أنا أشعر...",
                        title = "صوت مرتفع 🔊",
                        ttsPhrase = "الصوت هنا مرتفع ومزعج جداً لأذني، أرجوكم قوموا بخفضه أو إغلاقه",
                        iconName = "volume_off",
                        colorHex = "#F44336" // Red
                    ),

                    // Category: Wishes/Activities - "ألعابي وأنشطتي"
                    AacCard(
                        category = "ألعابي وأنشطتي",
                        title = "أريد الهاتف 📱",
                        ttsPhrase = "هل يمكنني اللعب بلعبتي المفضلة على الهاتف قليلاً لو سمحتم؟",
                        iconName = "smartphone",
                        colorHex = "#00BCD4" // Cyan
                    ),
                    AacCard(
                        category = "ألعابي وأنشطتي",
                        title = "نخرج لنلعب 🎡",
                        ttsPhrase = "أريد الخروج للتمشي في الحديقة وتنشق بعض الهواء الجميل واللعب!",
                        iconName = "directions_walk",
                        colorHex = "#4CAF50" // Green
                    ),
                    AacCard(
                        category = "ألعابي وأنشطتي",
                        title = "البقاء لوحدي 🧘",
                        ttsPhrase = "أريد الجلوس بمفردي لبعض الوقت في هدوء تام وصمت لأرتاح",
                        iconName = "self_improvement",
                        colorHex = "#607D8B" // Blue Grey
                    ),
                    AacCard(
                        category = "ألعابي وأنشطتي",
                        title = "أحبكم جداً ❤️",
                        ttsPhrase = "أنا أحبكم كثيراً، وأريد الحصول على حضن دافئ ولطيف الآن!",
                        iconName = "favorite",
                        colorHex = "#FF1744" // Crimson Heart
                    )
                )
                defaultCards.forEach { aacCardDao.insertCard(it) }
            }
        }
    }

    // --- Startup Greeting Logic ---

    fun triggerStartupGreeting() {
        if (_isGreetingShown.value) return // Only play once per app session to avoid annoyance

        viewModelScope.launch {
            // Wait slightly for voice engine to register
            delay(1000)
            val currentSett = appSettingsDao.getSettingsDirect() ?: AppSettings()
            
            if (currentSett.isFirstTime) {
                val greeting = "السلام عليكم ورحمة الله وبركاته يا بطل! أنا صديقك أكرم، كيف حالك اليوم؟"
                _customGreetingTextToShow.value = "الترحيب الأول: " + greeting
                voiceManager.speak(greeting)
                
                // Set first time to false for returning greets
                appSettingsDao.insertSettings(currentSett.copy(isFirstTime = false))
            } else {
                val greeting = "مرحباً بصديقي البطل أكرم! سررت بلقائك مجدداً اليوم، هل أنت جاهز للعب والإنجاز؟"
                _customGreetingTextToShow.value = "عودة مجدَّدة: " + greeting
                voiceManager.speak(greeting)
            }
            _isGreetingShown.value = true
        }
    }

    fun forceResetFirstTime(isFirst: Boolean) {
        viewModelScope.launch {
            val currentSett = appSettingsDao.getSettingsDirect() ?: AppSettings()
            appSettingsDao.insertSettings(currentSett.copy(isFirstTime = isFirst))
            
            // Replay appropriate greeting based on state
            if (isFirst) {
                val greeting = "السلام عليكم ورحمة الله وبركاته يا بطل! أنا صديقك أكرم، كيف حالك اليوم؟"
                _customGreetingTextToShow.value = "تمت إعادة التهيئة: " + greeting
                voiceManager.speak(greeting)
            } else {
                val greeting = "مرحباً بصديقي البطل أكرم! سررت بلقائك مجدداً اليوم، هل أنت جاهز للعب والإنجاز؟"
                _customGreetingTextToShow.value = "تم تفعيل العودة: " + greeting
                voiceManager.speak(greeting)
            }
            _isGreetingShown.value = true
        }
    }

    // --- Task actions with single tap (say name) & double tap (complete) ---

    fun onTaskSingleTap(task: TaskItem) {
        voiceManager.playVoiceOrSpeak(task.voiceFilePath, task.ttsPhrase)
    }

    fun onTaskDoubleTap(task: TaskItem) {
        viewModelScope.launch {
            val updated = task.copy(isCompleted = !task.isCompleted)
            taskDao.updateTask(updated)

            if (updated.isCompleted) {
                // play interactive reward voice
                delay(400)
                val rewardText = "بطل عفارم عليك يا أكرم! أكملت المهمة بنجاح"
                voiceManager.speak(rewardText)
                Log.d("AkrmViewModel", "Task completed reward spoken: $rewardText")
            }
        }
    }

    fun resetAllTasks() {
        viewModelScope.launch {
            val currentTasks = tasks.value
            currentTasks.forEach {
                taskDao.updateTask(it.copy(isCompleted = false))
            }
            voiceManager.speak("تمت إعادة تعيين جميع المهام اليومية يا بطل")
        }
    }

    // --- AAC communication card tap events ---

    fun onAacCardTap(card: AacCard) {
        voiceManager.playVoiceOrSpeak(card.voiceFilePath, card.ttsPhrase)
    }

    // --- Visual Countdown Timer Logic ---

    fun setTimerDuration(seconds: Int) {
        _timerDuration.value = seconds
        if (!_isTimerRunning.value) {
            _timerSecondsRemaining.value = seconds
        }
    }

    fun startTimer() {
        if (_isTimerRunning.value) return
        
        _isTimerRunning.value = true
        if (_timerSecondsRemaining.value <= 0) {
            _timerSecondsRemaining.value = _timerDuration.value
        }
        reminderSpokenDuringCurrentSession = false

        countdownJob = viewModelScope.launch {
            while (_timerSecondsRemaining.value > 0 && _isTimerRunning.value) {
                delay(1000)
                _timerSecondsRemaining.value -= 1

                val remaining = _timerSecondsRemaining.value
                val total = _timerDuration.value

                // 3 minutes remaining triggering rule (180 seconds)
                // If total duration is less than 3 minutes, we trigger when 20 seconds remain as a test fallback
                val shouldTriggerReminder = if (total > 180) {
                    remaining == 180
                } else {
                    remaining == (total / 4).coerceAtLeast(10) // trigger when a quarter time remains
                }

                if (shouldTriggerReminder && !reminderSpokenDuringCurrentSession) {
                    reminderSpokenDuringCurrentSession = true
                    voiceManager.speak("يا بطل، باقي شوية واللعب ينتهي عشان نقرأ الحصص")
                }
            }

            if (_timerSecondsRemaining.value <= 0 && _isTimerRunning.value) {
                _isTimerRunning.value = false
                voiceManager.speak("انتهى الوقف يا بطل، عفارم عليك! جاد وكت المهمة التانية")
            }
        }
    }

    fun pauseTimer() {
        _isTimerRunning.value = false
        countdownJob?.cancel()
    }

    fun resetTimer() {
        pauseTimer()
        _timerSecondsRemaining.value = _timerDuration.value
        reminderSpokenDuringCurrentSession = false
    }

    fun fastForwardTimerToReminder() {
        // Fast-forward to exactly 3 minutes or a quarter time to trigger the reminder instantly
        val total = _timerDuration.value
        val targetSeconds = if (total > 180) 181 else ((total / 4).coerceAtLeast(10) + 1)
        
        if (_isTimerRunning.value) {
            _timerSecondsRemaining.value = targetSeconds
        } else {
            _timerSecondsRemaining.value = targetSeconds
            startTimer()
        }
    }

    // --- Emergency / Meltdown "الحقوني" Logic ---

    fun toggleMeltdownState() {
        val nextState = !_isMeltdownActive.value
        _isMeltdownActive.value = nextState

        if (nextState) {
            // 1. Play serene breathing melody (synthesizer)
            voiceManager.startMeltdownCalmingSynth()

            // 2. Schedule SMS event
            viewModelScope.launch {
                val currentSett = settings.value
                val messageText = "ابنكم أكرم متضايق شديد هسّع ومحتاج زول جنبو، تعالوا ليهو سريع"
                _smsSentEvent.emit(SmsPayload(number = currentSett.parentPhoneNumber, message = messageText))
            }
        } else {
            // Turn off soothing synth sound on stop
            voiceManager.stopMeltdownCalmingSynth()
            voiceManager.stopAllPlayback()
        }
    }

    // --- Family customization configuration ---

    fun updateParentPhoneNumber(newNumber: String) {
        viewModelScope.launch {
            val currentSett = settings.value
            appSettingsDao.insertSettings(currentSett.copy(parentPhoneNumber = newNumber))
        }
    }

    fun updateSpeechRate(newRate: Float) {
        viewModelScope.launch {
            val currentSett = settings.value
            appSettingsDao.insertSettings(currentSett.copy(speechRate = newRate))
        }
    }

    // Add Custom Task
    fun addNewCustomTask(title: String, pronunciation: String, icon: String) {
        viewModelScope.launch {
            val currentCount = tasks.value.size
            val newTask = TaskItem(
                title = title,
                ttsPhrase = pronunciation,
                iconName = icon,
                orderIndex = currentCount,
                isCustom = true
            )
            taskDao.insertTask(newTask)
        }
    }

    fun deleteCustomTask(task: TaskItem) {
        viewModelScope.launch {
            taskDao.deleteTask(task)
            
            // Delete custom voice file if exists
            task.voiceFilePath?.let { path ->
                val f = File(path)
                if (f.exists()) f.delete()
            }
        }
    }

    // Add Custom AAC Card
    fun addNewCustomAacCard(category: String, title: String, pronunciation: String, icon: String, colorHex: String) {
        viewModelScope.launch {
            val newCard = AacCard(
                category = category,
                title = title,
                ttsPhrase = pronunciation,
                iconName = icon,
                colorHex = colorHex,
                isCustom = true
            )
            aacCardDao.insertCard(newCard)
        }
    }

    fun deleteCustomAacCard(card: AacCard) {
        viewModelScope.launch {
            aacCardDao.deleteCard(card)
            
            // Delete custom voice file if exists
            card.voiceFilePath?.let { path ->
                val f = File(path)
                if (f.exists()) f.delete()
            }
        }
    }

    // --- Custom recording implementation callbacks ---

    fun startVoiceRecordingForTask(task: TaskItem, cacheDir: File) {
        val recordFile = File(cacheDir, "task_voice_${task.id}_${System.currentTimeMillis()}.m4a")
        _recordingTaskItem.value = task
        _recordingAacCard.value = null
        val ok = voiceManager.startRecording(recordFile)
        if (ok) {
            _isRecordingActive.value = true
        }
    }

    fun stopVoiceRecordingForTask(task: TaskItem, recordFile: File) {
        val resultText = voiceManager.stopRecording()
        _isRecordingActive.value = false
        _recordingTaskItem.value = null

        if (resultText != null && recordFile.exists() && recordFile.length() > 0) {
            // Update database reference
            viewModelScope.launch {
                // Delete previous file if any
                task.voiceFilePath?.let { prevPath ->
                    val prevFile = File(prevPath)
                    if (prevFile.exists()) prevFile.delete()
                }

                val updatedTask = task.copy(voiceFilePath = recordFile.absolutePath)
                taskDao.updateTask(updatedTask)
                voiceManager.speak("تم حفظ التسجيل الخاص بالمهمة بنجاح")
            }
        }
    }

    fun startVoiceRecordingForAacCard(card: AacCard, cacheDir: File) {
        val recordFile = File(cacheDir, "aac_voice_${card.id}_${System.currentTimeMillis()}.m4a")
        _recordingAacCard.value = card
        _recordingTaskItem.value = null
        val ok = voiceManager.startRecording(recordFile)
        if (ok) {
            _isRecordingActive.value = true
        }
    }

    fun stopVoiceRecordingForAacCard(card: AacCard, recordFile: File) {
        val resultText = voiceManager.stopRecording()
        _isRecordingActive.value = false
        _recordingAacCard.value = null

        if (resultText != null && recordFile.exists() && recordFile.length() > 0) {
            viewModelScope.launch {
                // Delete previous
                card.voiceFilePath?.let { prevPath ->
                    val prevFile = File(prevPath)
                    if (prevFile.exists()) prevFile.delete()
                }

                val updatedCard = card.copy(voiceFilePath = recordFile.absolutePath)
                aacCardDao.updateCard(updatedCard)
                voiceManager.speak("تم حفظ التسجيل الخاص ببطاقة التواصل")
            }
        }
    }

    fun updateTaskImage(task: TaskItem, uriString: String) {
        viewModelScope.launch {
            taskDao.updateTask(task.copy(imageUri = uriString))
        }
    }

    fun updateCardImage(card: AacCard, uriString: String) {
        viewModelScope.launch {
            aacCardDao.updateCard(card.copy(imageUri = uriString))
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.release()
    }
}

data class SmsPayload(
    val number: String,
    val message: String
)
