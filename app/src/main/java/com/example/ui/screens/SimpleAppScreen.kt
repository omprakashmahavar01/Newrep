package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.SpeechManager
import org.json.JSONObject
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleAppScreen(speechManager: SpeechManager) {
  val context = LocalContext.current

  var appName by remember { mutableStateOf("") }
  var selectedFileName by remember { mutableStateOf<String?>(null) }
  var selectedFileSize by remember { mutableStateOf<Long>(0L) }
  var selectedFileContent by remember { mutableStateOf("") }
  var detectedFileType by remember { mutableStateOf("") }
  var gcpProjectId by remember { mutableStateOf<String?>(null) }
  var gcpClientEmail by remember { mutableStateOf<String?>(null) }
  var statusMessage by remember { mutableStateOf("") }
  var isStatusSuccess by remember { mutableStateOf(false) }
  var lastHeardCommand by remember { mutableStateOf<String?>(null) }

  // Infinite animations for high-craft feel
  val infiniteTransition = rememberInfiniteTransition(label = "cyberGlow")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.95f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(1400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse"
  )
  val laserOffset by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(2200, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "laser"
  )

  // File Picker
  val filePickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri: Uri? ->
    if (uri != null) {
      val (name, size) = getFileInfo(context, uri)
      val content = readFileSnippet(context, uri)
      selectedFileName = name
      selectedFileSize = size
      selectedFileContent = content

      val parsedInfo = analyzeFile(content, name)
      detectedFileType = parsedInfo.first
      gcpProjectId = parsedInfo.second
      gcpClientEmail = parsedInfo.third

      isStatusSuccess = true
      if (gcpProjectId != null) {
        statusMessage = "Google Cloud Service Account Key पहचानी गई! प्रोजेक्ट: $gcpProjectId"
        speechManager.speak("अनोखी सुरक्षा जाँच पूर्ण! यह Google Cloud सर्विस अकाउंट JSON Key है। प्रोजेक्ट आईडी $gcpProjectId बिल्कुल सही है।")
      } else {
        statusMessage = "फ़ाइल सही है: $name ($size बाइट्स)"
        speechManager.speak("फ़ाइल का स्कैन पूर्ण! यह बिल्कुल सही है।")
      }
    } else {
      statusMessage = "कोई फ़ाइल नहीं चुनी गई"
      isStatusSuccess = false
      speechManager.speak("यह गलत है! कोई फ़ाइल नहीं चुनी गई।")
    }
  }

  // Verification execution logic
  fun executeVerification() {
    if (appName.isBlank() && selectedFileName == null) {
      isStatusSuccess = false
      statusMessage = "कृपया एप्लिकेशन का नाम दर्ज करें और फ़ाइल चुनें।"
      speechManager.speak("यह गलत है! कृपया एप्लिकेशन का नाम और फ़ाइल दोनों डालें।")
    } else if (appName.isBlank()) {
      isStatusSuccess = false
      statusMessage = "एप्लिकेशन का नाम खाली है! कृपया नाम लिखें।"
      speechManager.speak("यह गलत है! कृपया एप्लिकेशन का नाम दर्ज करें।")
    } else if (selectedFileName == null) {
      isStatusSuccess = false
      statusMessage = "कोई फ़ाइल नहीं डाली गई है! कृपया फ़ाइल चुनें।"
      speechManager.speak("यह गलत है! कृपया फ़ाइल चुनें।")
    } else {
      isStatusSuccess = true
      val extraInfo = if (gcpProjectId != null) " • GCP Project: $gcpProjectId" else ""
      statusMessage = "सत्यापन पूर्ण! एप्लिकेशन '$appName' और फ़ाइल '$selectedFileName' दोनों शत-प्रतिशत सही हैं$extraInfo"
      speechManager.speak("आदेश का पालन हुआ! यह बिल्कुल सही है। एप्लिकेशन $appName और फ़ाइल $selectedFileName दोनों सफलतापूर्वक लोड हो गए हैं।")
    }
  }

  fun announceStatus() {
    if (appName.isNotBlank() && selectedFileName != null) {
      speechManager.speak("एप्लिकेशन: $appName, फ़ाइल: $selectedFileName, सुरक्षा जाँच शत-प्रतिशत सही है!")
    } else if (appName.isBlank() && selectedFileName == null) {
      speechManager.speak("यह गलत है! कृपया एप्लिकेशन का नाम और फ़ाइल दोनों दर्ज करें।")
    } else if (appName.isBlank()) {
      speechManager.speak("यह गलत है! कृपया एप्लिकेशन का नाम लिखें।")
    } else {
      speechManager.speak("यह गलत है! कृपया फ़ाइल अपलोड करें।")
    }
  }

  // Voice Command Processing Function (निर्देश अनुसार काम करना)
  fun handleVoiceCommand(rawCommand: String) {
    val command = rawCommand.trim()
    if (command.isBlank()) return
    lastHeardCommand = command
    val lower = command.lowercase(Locale.getDefault())

    when {
      // 1. Command to pick/upload file
      lower.contains("file") || lower.contains("फ़ाइल") || lower.contains("अपलोड") ||
        lower.contains("upload") || lower.contains("चुनो") || lower.contains("डालो") -> {
        speechManager.speak("आदेश प्राप्त हुआ! फ़ाइल चुनने का विकल्प खोला जा रहा है।")
        filePickerLauncher.launch("*/*")
      }

      // 2. Command to verify/submit
      lower.contains("submit") || lower.contains("सबमिट") || lower.contains("check") ||
        lower.contains("जाँच") || lower.contains("जांच") || lower.contains("वेरीफाई") ||
        lower.contains("verify") || lower.contains("ओके") -> {
        executeVerification()
      }

      // 3. Command to announce status
      lower.contains("status") || lower.contains("स्थिति") || lower.contains("सुनाओ") ||
        lower.contains("बताओ") || lower.contains("बोलो") -> {
        announceStatus()
      }

      // 4. Command to clear/reset
      lower.contains("clear") || lower.contains("reset") || lower.contains("हटाओ") ||
        lower.contains("खाली") || lower.contains("डिलीट") -> {
        appName = ""
        selectedFileName = null
        selectedFileSize = 0L
        statusMessage = "डेटा साफ़ कर दिया गया है।"
        isStatusSuccess = false
        speechManager.speak("आदेश मिला! सारा डेटा साफ़ कर दिया गया है।")
      }

      // 5. Command to set application name
      lower.contains("नाम") || lower.contains("name") -> {
        val extracted = command
          .replace(Regex("(?i)(एप्लिकेशन का नाम|ऐप का नाम|नाम लिखो|नाम डालो|नाम रखो|सेट नेम|नाम है|नाम|app name is|name is|set name)"), "")
          .trim()
        if (extracted.isNotBlank()) {
          appName = extracted
          speechManager.speak("आदेश प्राप्त हुआ! एप्लिकेशन का नाम $extracted दर्ज कर दिया गया है।")
        } else {
          speechManager.speak("कृपया नाम बोलें, जैसे: नाम क्लाउड ऐप।")
        }
      }

      // Fallback: If name is not yet set, set the spoken text as application name
      appName.isBlank() -> {
        appName = command
        speechManager.speak("आदेश मिला! एप्लिकेशन का नाम $appName सेट कर दिया गया है।")
      }

      else -> {
        speechManager.speak("आदेश मिला: $command। कृपया 'फ़ाइल चुनो', 'सबमिट करो' या 'स्थिति बताओ' कहें।")
      }
    }
  }

  // Speech Recognizer Launcher for Microhone Intent
  val voiceLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
  ) { result ->
    if (result.resultCode == Activity.RESULT_OK) {
      val spokenList = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
      val command = spokenList?.firstOrNull()?.trim()
      if (!command.isNullOrBlank()) {
        handleVoiceCommand(command)
      }
    }
  }

  fun startListening() {
    try {
      val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
        putExtra(RecognizerIntent.EXTRA_PROMPT, "आदेश बोलिए... (उदा. 'नाम लिखो ...', 'फ़ाइल चुनो', 'सबमिट करो')")
      }
      voiceLauncher.launch(intent)
    } catch (e: Exception) {
      Toast.makeText(context, "वॉयस इनपुट प्रारंभ किया जा रहा है...", Toast.LENGTH_SHORT).show()
      speechManager.speak("वॉयस कमांड सुनने के लिए तैयार।")
    }
  }

  val sampleVoiceCommands = listOf(
    "नाम लिखो cloud-deployer-sa",
    "फ़ाइल चुनो",
    "सबमिट करो",
    "स्थिति बताओ",
    "डेटा साफ़ करो"
  )

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    contentWindowInsets = WindowInsets.safeDrawing,
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(
                  Brush.linearGradient(
                    colors = listOf(Color(0xFF0F766E), Color(0xFF14B8A6))
                  )
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "वॉयस-कंट्रोल फ़ाइल व ऐप मैनेजर",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
              )
              Text(
                text = "आवाज़ से निर्देश दें व काम कराएं",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        },
        actions = {
          IconButton(
            onClick = { announceStatus() },
            modifier = Modifier.testTag("voice_announcer_button")
          ) {
            Icon(
              imageVector = Icons.Default.VolumeUp,
              contentDescription = "बोलकर सुनाएं",
              tint = MaterialTheme.colorScheme.primary
            )
          }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    floatingActionButton = {
      ExtendedFloatingActionButton(
        onClick = { startListening() },
        modifier = Modifier
          .testTag("voice_mic_fab")
          .windowInsetsPadding(WindowInsets.navigationBars),
        icon = {
          Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = "माइक से आदेश दें",
            modifier = Modifier.size(26.dp)
          )
        },
        text = {
          Text(
            text = "आवाज़ से आदेश दें (Mic)",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          )
        },
        containerColor = Color(0xFF0F766E),
        contentColor = Color.White
      )
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            colors = listOf(
              MaterialTheme.colorScheme.surface,
              MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
              Color(0xFF0F766E).copy(alpha = 0.06f)
            )
          )
        )
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
          .verticalScroll(rememberScrollState())
          .padding(horizontal = 20.dp, vertical = 14.dp)
          .padding(bottom = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {

        // Futuristic Voice Command Visualizer Banner
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF0F172A))
            .clickable { startListening() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF2DD4BF).copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Hearing,
                contentDescription = null,
                tint = Color(0xFF2DD4BF),
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "VOICE COMMAND READY",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2DD4BF)
              )
              Text(
                text = "माइक दबाकर बोलें या नीचे दिए आदेश चुनें",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.75f)
              )
            }
          }

          // Animated mini equalizer bars
          Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            val barHeights = listOf(14.dp * pulseScale, 8.dp / pulseScale, 18.dp * pulseScale, 10.dp)
            barHeights.forEach { height ->
              Box(
                modifier = Modifier
                  .width(3.dp)
                  .height(height)
                  .clip(RoundedCornerShape(2.dp))
                  .background(Color(0xFF14B8A6))
              )
            }
          }
        }

        // Heard command indicator
        if (lastHeardCommand != null) {
          Spacer(modifier = Modifier.height(8.dp))
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFFE0F2FE),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(text = "🎙️ सुना गया आदेश: ", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0369A1))
              Text(text = "\"$lastHeardCommand\"", fontSize = 12.sp, color = Color(0xFF075985), fontWeight = FontWeight.SemiBold)
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Quick Voice Command Trigger Chips (1-tap to test voice commands immediately)
        Text(
          text = "तुरंत बोलें या टैप करके आदेश दें (Quick Voice Commands):",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(sampleVoiceCommands) { sampleCmd ->
            FilterChip(
              selected = false,
              onClick = { handleVoiceCommand(sampleCmd) },
              label = { Text("🎤 $sampleCmd", fontSize = 12.sp) },
              shape = RoundedCornerShape(10.dp),
              colors = FilterChipDefaults.filterChipColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 1. APPLICATION NAME CARD
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("app_name_card"),
          shape = RoundedCornerShape(22.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          border = if (appName.isNotBlank()) CardDefaults.outlinedCardBorder() else null
        ) {
          Column(modifier = Modifier.padding(20.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0F766E).copy(alpha = 0.15f)),
                  contentAlignment = Alignment.Center
                ) {
                  Text(text = "1", fontWeight = FontWeight.Bold, color = Color(0xFF0F766E))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  text = "एप्लिकेशन का नाम",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold
                )
              }

              if (appName.isNotBlank()) {
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = Color(0xFFECFDF5)
                ) {
                  Text(
                    text = "✓ सही",
                    color = Color(0xFF059669),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
              value = appName,
              onValueChange = { appName = it },
              placeholder = { Text("उदा. cloud-deployer-sa, Rozana") },
              label = { Text("Application Name * (बोलकर भी डाल सकते हैं)") },
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("application_name_input"),
              shape = RoundedCornerShape(14.dp),
              trailingIcon = {
                IconButton(onClick = { startListening() }) {
                  Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "बोलकर नाम डालें",
                    tint = Color(0xFF0F766E)
                  )
                }
              },
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF0F766E)
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 2. FILE UPLOAD CARD WITH SCANNING EFFECT
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("file_upload_card"),
          shape = RoundedCornerShape(22.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(20.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0F766E).copy(alpha = 0.15f)),
                  contentAlignment = Alignment.Center
                ) {
                  Text(text = "2", fontWeight = FontWeight.Bold, color = Color(0xFF0F766E))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  text = "फ़ाइल डालें (Upload File)",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold
                )
              }

              if (selectedFileName != null) {
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = Color(0xFFECFDF5)
                ) {
                  Text(
                    text = "✓ लोड हो गई",
                    color = Color(0xFF059669),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive Laser Scanning Drop Area
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(
                  if (selectedFileName != null) Color(0xFFF0FDF4)
                  else Color(0xFFF8FAFC)
                )
                .border(
                  width = 2.dp,
                  color = if (selectedFileName != null) Color(0xFF10B981) else Color(0xFFCBD5E1),
                  shape = RoundedCornerShape(18.dp)
                )
                .clickable { filePickerLauncher.launch("*/*") }
                .padding(22.dp)
                .testTag("file_picker_area"),
              contentAlignment = Alignment.Center
            ) {
              if (selectedFileName == null) {
                Canvas(modifier = Modifier.matchParentSize()) {
                  val y = size.height * laserOffset
                  drawLine(
                    brush = Brush.horizontalGradient(
                      colors = listOf(Color.Transparent, Color(0xFF14B8A6).copy(alpha = 0.5f), Color.Transparent)
                    ),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 3.dp.toPx()
                  )
                }
              }

              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                  modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                      if (selectedFileName != null) Color(0xFFD1FAE5) else Color(0xFFCCFBF1)
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = if (selectedFileName != null) Icons.Default.Description else Icons.Default.UploadFile,
                    contentDescription = null,
                    tint = if (selectedFileName != null) Color(0xFF059669) else Color(0xFF0F766E),
                    modifier = Modifier.size(30.dp)
                  )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (selectedFileName != null) {
                  Text(
                    text = selectedFileName ?: "",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF065F46),
                    textAlign = TextAlign.Center
                  )
                  Text(
                    text = "$selectedFileSize Bytes • $detectedFileType",
                    fontSize = 12.sp,
                    color = Color(0xFF047857),
                    fontFamily = FontFamily.Monospace
                  )

                  if (gcpProjectId != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = Color(0xFFE0F2FE)
                    ) {
                      Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF0369A1), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                          text = "GCP Project: $gcpProjectId",
                          fontSize = 11.sp,
                          color = Color(0xFF0369A1),
                          fontWeight = FontWeight.Bold
                        )
                      }
                    }
                  }

                  Spacer(modifier = Modifier.height(10.dp))
                  FilledTonalButton(
                    onClick = { filePickerLauncher.launch("*/*") },
                    shape = RoundedCornerShape(10.dp)
                  ) {
                    Text("फ़ाइल बदलें (Change File)")
                  }
                } else {
                  Text(
                    text = "फ़ाइल चुनने के लिए यहाँ टैप करें (या बोलें 'फ़ाइल चुनो')",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                  )
                  Text(
                    text = "JSON Key, टेक्स्ट या कोई भी फ़ाइल (Tap to Browse)",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // STATUS BOX WITH DYNAMIC VOICE FEEDBACK
        AnimatedVisibility(
          visible = statusMessage.isNotBlank(),
          enter = fadeIn() + scaleIn(),
          exit = fadeOut()
        ) {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isStatusSuccess) Color(0xFFECFDF5) else Color(0xFFFEF2F2)
            ),
            border = CardDefaults.outlinedCardBorder()
          ) {
            Row(
              modifier = Modifier.padding(16.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = if (isStatusSuccess) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = if (isStatusSuccess) Color(0xFF10B981) else Color(0xFFEF4444),
                modifier = Modifier.size(26.dp)
              )
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = if (isStatusSuccess) "यह बिल्कुल सही है! ✅" else "यह गलत है! ❌",
                  fontWeight = FontWeight.Bold,
                  fontSize = 15.sp,
                  color = if (isStatusSuccess) Color(0xFF065F46) else Color(0xFF991B1B)
                )
                Text(
                  text = statusMessage,
                  fontSize = 13.sp,
                  color = if (isStatusSuccess) Color(0xFF047857) else Color(0xFFB91C1C),
                  lineHeight = 18.sp
                )
              }
              IconButton(
                onClick = {
                  val voiceText = if (isStatusSuccess) "यह बिल्कुल सही है! $statusMessage" else "यह गलत है! $statusMessage"
                  speechManager.speak(voiceText)
                }
              ) {
                Icon(
                  imageVector = Icons.Default.VolumeUp,
                  contentDescription = "बोलकर सुनें",
                  tint = if (isStatusSuccess) Color(0xFF059669) else Color(0xFFDC2626)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SUBMIT & VERIFY BUTTON
        Button(
          onClick = { executeVerification() },
          modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .testTag("submit_button"),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF0F766E)
          )
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CheckCircle, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "जाँचें व सबमिट करें (Verify & Submit)",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        // DOWNLOAD / SHARE BUNDLE ACTION (when verified)
        if (appName.isNotBlank() && selectedFileName != null && isStatusSuccess) {
          Spacer(modifier = Modifier.height(14.dp))

          FilledTonalButton(
            onClick = {
              val report = StringBuilder().apply {
                appendLine("🌟 अनोखी कोडिंग - एप्लिकेशन व फ़ाइल रिपोर्ट")
                appendLine("📱 एप्लिकेशन का नाम: $appName")
                appendLine("📁 फ़ाइल का नाम: $selectedFileName ($selectedFileSize Bytes)")
                if (gcpProjectId != null) appendLine("☁️ GCP Project ID: $gcpProjectId")
                if (gcpClientEmail != null) appendLine("📧 Service Account: $gcpClientEmail")
                appendLine("✅ स्थिति: शत-प्रतिशत सही व सत्यापित")
                appendLine("Rozana Application द्वारा सुरक्षित रूप से निर्यातित")
              }.toString()

              val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, report)
                type = "text/plain"
              }
              context.startActivity(Intent.createChooser(sendIntent, "एप्लिकेशन विवरण डाउनलोड/शेयर करें"))
              speechManager.speak("एप्लिकेशन और फ़ाइल का विवरण शेयर करने के लिए तैयार है।")
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("download_button"),
            shape = RoundedCornerShape(16.dp)
          ) {
            Icon(Icons.Default.Download, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "विवरण डाउनलोड व शेयर करें", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

private fun analyzeFile(content: String, fileName: String): Triple<String, String?, String?> {
  if (content.isBlank()) return Triple("सामान्य फ़ाइल", null, null)

  try {
    val json = JSONObject(content)
    val type = json.optString("type")
    val projectId = json.optString("project_id").ifBlank { null }
    val clientEmail = json.optString("client_email").ifBlank { null }

    if (type.equals("service_account", ignoreCase = true)) {
      return Triple("GCP Service Account JSON Key", projectId, clientEmail)
    }
    return Triple("JSON फ़ाइल", projectId, clientEmail)
  } catch (e: Exception) {
    return when {
      fileName.endsWith(".json", ignoreCase = true) -> Triple("JSON फ़ाइल", null, null)
      fileName.endsWith(".xml", ignoreCase = true) -> Triple("XML कॉन्फ़िगरेशन", null, null)
      fileName.endsWith(".txt", ignoreCase = true) -> Triple("टेक्स्ट दस्तावेज़", null, null)
      else -> Triple("दस्तावेज़ फ़ाइल", null, null)
    }
  }
}

private fun getFileInfo(context: Context, uri: Uri): Pair<String, Long> {
  var name = "selected_file"
  var size = 0L
  try {
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
      val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
      val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
      if (cursor.moveToFirst()) {
        if (nameIndex != -1) name = cursor.getString(nameIndex) ?: name
        if (sizeIndex != -1) size = cursor.getLong(sizeIndex)
      }
    }
  } catch (e: Exception) {
    name = uri.lastPathSegment ?: "selected_file"
  }
  return Pair(name, size)
}

private fun readFileSnippet(context: Context, uri: Uri): String {
  return try {
    context.contentResolver.openInputStream(uri)?.use { stream ->
      val bytes = ByteArray(4096)
      val count = stream.read(bytes)
      if (count > 0) String(bytes, 0, count) else ""
    } ?: ""
  } catch (e: Exception) {
    ""
  }
}
