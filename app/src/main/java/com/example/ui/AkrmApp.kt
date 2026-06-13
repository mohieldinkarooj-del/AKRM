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
                    Text("لا توجد بطاقات تواصل في هذا القسم", color = Color.Gray)
                }
            } else {
                // We use a fixed-height box and simple local layout to nested-scroll or standard grid layout safely
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Chunk grid rows manually so that we avoid Nested Scroll Exception inside Vertical Scroll Column!
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
                if (task.isCompleted) Color(0xFFF8FAFC) // Sleek slate-50 background for completed items
                else Color.White
            )
            .border(
                width = if (isActive || task.isCompleted) 2.dp else 1.dp,
                color = if (task.isCompleted) Color(0xFF008080).copy(alpha = 0.4f)
                        else if (isActive) Color(0xFF008080)
                        else Color(0xFFE2E8F0), // slate-200
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
            // Task status badge icon
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

                // Check symbol badge
                Icon(
                    imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.Circle,
                    contentDescription = if (task.isCompleted) "مكتمل" else "غير مكتمل",
                    tint = if (task.isCompleted) Color(0xFF008080) else Color(0xFFCBD5E1),
                    modifier = Modifier.size(24.dp)
                )
            }

            // Task illustration (Large customized icon or image)
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        if (task.isCompleted) Color(0xFFE2E8F0) // slate-200 for completed
                        else if (isActive) Color(0xFFCCFBF1)   // active light teal-50
                        else Color(0xFFF1F5F9)                 // inactive slate-100
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

            // Task title
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF1E293B) // slate-800
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
                // Category icon / small marker
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

            // Big adaptive icon
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

            // Large Title styled with vibrant high contrast cardColor
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

    // Sweep percentage calculation
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

        // Custom Radial Countdown Canvas representation
        Box(
            modifier = Modifier
                .size(230.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Background Track grey ring
                drawCircle(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    style = Stroke(width = 16.dp.toPx())
                )

                // Remaining time arc
                drawArc(
                    color = primaryColor,
                    startAngle = -90f,
                    sweepAngle = fractionRemaining * 360f,
                    useCenter = false,
                    style = Stroke(width = 18.dp.toPx())
                )
            }

            // Central time text
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

        // Preset durations choices
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
                val presets = listOf(60, 180, 300, 600, 900) // seconds
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

        // Timer Control Actions
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

        // Fast forward Trigger for Reviewing ease
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
    // Dynamic breathing animation state
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

    // Slow breath instructions based on cycle
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
                .pointerInput(Unit) {} // lock backing gestures
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

                // Guidance Box with SMS notification alert
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

                // Breathing guidance circle
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
                        // Outer ring indicator
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

                // Back to App button
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

    // Input States
    var parentMobile by remember(settings.parentPhoneNumber) { mutableStateOf(settings.parentPhoneNumber) }
    var speechRateSlider by remember(settings.speechRate) { mutableStateOf(settings.speechRate) }

    // Forms controllers: New Task
    var showNewTaskForm by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskPronounce by remember { mutableStateOf("") }
    var newTaskIcon by remember { mutableStateOf("school") }

    // Forms controllers: New AAC Card
    var showNewCardForm by remember { mutableStateOf(false) }
    var newCardCategory by remember { mutableStateOf("أنا أريد...") }
    var newCardTitle by remember { mutableStateOf("") }
    var newCardPronounce by remember { mutableStateOf("") }
    var newCardIcon by remember { mutableStateOf("restaurant") }
    var newCardColorHex by remember { mutableStateOf("#00796B") }

    // Camera bitmap helper for custom task capture (simulated or direct bitmap save)
    var selectedTaskToCapture by remember { mutableStateOf<TaskItem?>(null) }
    var selectedAacCardToCapture by remember { mutableStateOf<AacCard?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            // Write bitmap to file cache and store path in DB!
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

        // CARD 1: PARENT TELEPHONE SMS
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

        // CARD 2: SPEECH SETTINGS
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

                // First time re-enable Test triggers
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

        // CARD 3: MANAGE TASKS (AUDIO & PHOTO CUSTOMIZATION)
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

                // List existing tasks with options to RECORD VOICE or CAPTURE FAMILY PHOTO representing the task
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
                                // Camera Capture Photo of actual room/material!
                                IconButton(
                                    onClick = {
                                        selectedTaskToCapture = task
                                        // Request permission or trigger camera preview directly
                                        cameraLauncher.launch()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "صورة مخصصة من الكاميرا",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }

                                // Custom audio records button
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

        // CARD 4: MANAGE AAC CARDS (CUSTOM DIALECT VOICE RECORD)
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

                        // Category pick
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

                // list matching cards
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
                                // Camera Custom Photo Capture
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

                                // Microphone Record Custom audio Card
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

        // --- CARD 5: CHANNELS OF SHARING AND EASY APK INSTALLATION Link (بدون USB) ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color(0xFFF43F5E), RoundedCornerShape(16.dp)), // Soft crimson border to catch attention and solve the error
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFF1F2), // Light eye-friendly amber/pink background to make instructions clear
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

                // Sub-section 1: The permanent solution (APK Generation & Direct Sharing)
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

                // Sub-section 2: Temporary Preview links
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

// Helper object to decode images stored inside app files directory locally safely in compose threads
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
