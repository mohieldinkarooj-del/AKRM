package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.util.Locale
import kotlin.math.sin

class VoiceManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false
    private var speechRate = 0.45f

    // For visual subtitle/caption display (Accessibility)
    private val _currentSpokenText = MutableStateFlow("")
    val currentSpokenText: StateFlow<String> = _currentSpokenText

    // Custom voice recorder & player
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var isRecording = false

    // Synthesizer for peaceful calming breathing tone (432Hz meditation sine wave)
    private var audioTrack: AudioTrack? = null
    private var synthJob: Job? = null
    private val synthScope = CoroutineScope(Dispatchers.Default)

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("ar"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("VoiceManager", "Arabic language is not supported or missing voice data")
                // Fall back to default
                tts?.language = Locale.getDefault()
            }
            tts?.setPitch(1.35f) // Set to a realistic little boy pitch
            tts?.setSpeechRate(if (speechRate < 0.6f) 0.85f else speechRate)
            isTtsInitialized = true
        } else {
            Log.e("VoiceManager", "TTS Initialization failed!")
        }
    }

    fun setSpeechRate(rate: Float) {
        val adjustedRate = if (rate < 0.6f) 0.85f else rate
        speechRate = adjustedRate
        tts?.setSpeechRate(adjustedRate)
        tts?.setPitch(1.35f) // Enforce little boy pitch
    }

    /**
     * Pronounces the given text using TextToSpeech or updates the visual captions subtitle flow.
     */
    fun speak(text: String) {
        _currentSpokenText.value = text
        if (isTtsInitialized && tts != null) {
            tts?.setPitch(1.35f) // Guarantee realistic young boy tone on play
            // Setup clear speaking options
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "AKRM_SPEECH_ID")
            } else {
                @Suppress("DEPRECATION")
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null)
            }
        } else {
            Log.w("VoiceManager", "TTS not initialized yet. Subtitle shown: $text")
        }

        // Clear subtitle after 5 seconds of inactivity
        CoroutineScope(Dispatchers.Main).launch {
            delay(5000)
            if (_currentSpokenText.value == text) {
                _currentSpokenText.value = ""
            }
        }
    }

    /**
     * Plays local voice file recorded by family, otherwise speaks the fallback text string.
     */
    fun playVoiceOrSpeak(filePath: String?, fallbackText: String) {
        if (filePath != null && File(filePath).exists()) {
            _currentSpokenText.value = "🗣️ [صوت عائلي مسجل]"
            stopAllPlayback()
            try {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(filePath)
                    prepare()
                    start()
                    setOnCompletionListener {
                        _currentSpokenText.value = ""
                        it.release()
                        mediaPlayer = null
                    }
                }
            } catch (e: Exception) {
                Log.e("VoiceManager", "Error playing local audio file, falling back to TTS", e)
                speak(fallbackText)
            }
        } else {
            speak(fallbackText)
        }
    }

    // --- MediaRecorder Section for Custom Family Recording ---

    fun startRecording(outputFile: File): Boolean {
        if (isRecording) return false
        stopAllPlayback()

        try {
            // Delete existing if any
            if (outputFile.exists()) {
                outputFile.delete()
            }

            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(outputFile.absolutePath)
                prepare()
                start()
            }

            isRecording = true
            return true
        } catch (e: Exception) {
            Log.e("VoiceManager", "Recording start failed", e)
            Toast.makeText(context, "فشل بدء التسجيل: تأكد من إذن المايكروفون", Toast.LENGTH_SHORT).show()
            mediaRecorder = null
            isRecording = false
            return false
        }
    }

    fun stopRecording(): String? {
        if (!isRecording || mediaRecorder == null) return null
        try {
            mediaRecorder?.stop()
        } catch (e: RuntimeException) {
            Log.e("VoiceManager", "Exception during stop recording (often short file)", e)
        } finally {
            mediaRecorder?.release()
            mediaRecorder = null
            isRecording = false
        }
        return "Taped successfully"
    }

    fun stopAllPlayback() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null

        tts?.stop()
    }

    // --- Synthesizer calm breathing sound generation (432Hz Meditation Sine Wave) ---

    fun startMeltdownCalmingSynth() {
        stopMeltdownCalmingSynth()

        synthJob = synthScope.launch {
            val sampleRate = 44100
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )

            // Dynamic AudioTrack configuration
            audioTrack = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AudioTrack(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build(),
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build(),
                    minBufferSize,
                    AudioTrack.MODE_STREAM,
                    0
                )
            } else {
                @Suppress("DEPRECATION")
                AudioTrack(
                    android.media.AudioManager.STREAM_MUSIC,
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minBufferSize,
                    AudioTrack.MODE_STREAM
                )
            }

            audioTrack?.play()

            val baseFrequency = 220.0 // Calm A3 note (220 Hz is very soothing, low resonance)
            var phase = 0.0
            val bufferSize = 1024
            val buffer = ShortArray(bufferSize)

            try {
                while (synthJob?.isActive == true) {
                    // Create slow sinusoidal volume modulation to simulate gentle breathing cycles (6 seconds cycle: 3s inhale, 3s exhale)
                    val timeSecs = System.currentTimeMillis() / 1000.0
                    val breathingModulator = 0.5 + 0.4 * sin(2.0 * Math.PI * timeSecs / 6.0) // 0.1 to 0.9 amplitude modulation

                    // Generate waves
                    for (i in 0 until bufferSize) {
                        // Modulate a standard double sine wave to enrich sound harmonics (makes it a beautiful, rich humming tone instead of a piercing simple beep)
                        val primary = sin(2.0 * Math.PI * baseFrequency * phase)
                        val subHarmonic = 0.4 * sin(2.0 * Math.PI * (baseFrequency / 2.0) * phase)
                        val sample = (primary + subHarmonic) * Short.MAX_VALUE * breathingModulator * 0.35

                        buffer[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                        phase += 1.0 / sampleRate
                        if (phase > 1000.0) phase = 0.0 // avoid float precision loss
                    }

                    audioTrack?.write(buffer, 0, bufferSize)
                    delay(5) // Non-blocking buffer supply
                }
            } catch (e: Exception) {
                Log.e("VoiceManager", "Synth write exception", e)
            } finally {
                try {
                    audioTrack?.stop()
                    audioTrack?.release()
                } catch (e: Exception) {
                    // ignore
                }
                audioTrack = null
            }
        }
    }

    fun stopMeltdownCalmingSynth() {
        synthJob?.cancel()
        synthJob = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            // ignore
        }
        audioTrack = null
    }

    /**
     * Releases everything upon shutdown
     */
    fun release() {
        stopAllPlayback()
        stopMeltdownCalmingSynth()
        tts?.shutdown()
        tts = null
    }
}
