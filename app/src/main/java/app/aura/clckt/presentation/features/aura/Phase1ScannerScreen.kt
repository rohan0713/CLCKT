package app.aura.clckt.presentation.features.aura

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import app.aura.clckt.data.local.LocalLimitManager
import app.aura.clckt.data.remote.GeminiAnalysisResult
import app.aura.clckt.data.remote.GeminiService
import app.aura.clckt.data.remote.LookupResult
import app.aura.clckt.presentation.features.dashboard.ui.theme.*
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Phase1ScannerScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Limits
    val limitManager = remember { LocalLimitManager(context) }
    var usageCount by remember { mutableStateOf(limitManager.getUsageCount()) }
    val canScan = usageCount < 5

    // State
    var eventName by remember { mutableStateOf("") }
    var eventDescription by remember { mutableStateOf("") }
    var isVerifyingVibe by remember { mutableStateOf(false) }
    var isAnalysisRunning by remember { mutableStateOf(false) }
    var verifiedDescription by remember { mutableStateOf<String?>(null) }
    var showDescriptionInput by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Multi-step Loading roasting messages
    val loadingTexts = listOf(
        "Initiating dynamic connection to Gemini...",
        "Scanning outfit silhouette and fit alignment...",
        "Analyzing fashion color coordinates...",
        "Brutally roasting footwear selection...",
        "Verifying venue aesthetic dress code...",
        "Formulating Gen-Z vibe status card..."
    )
    var currentLoadingText by remember { mutableStateOf(loadingTexts[0]) }

    LaunchedEffect(isAnalysisRunning) {
        if (isAnalysisRunning) {
            var index = 0
            while (isAnalysisRunning) {
                currentLoadingText = loadingTexts[index % loadingTexts.size]
                delay(1800)
                index++
            }
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                selectedImageUri = uri
            }
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackGroundColor)
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            // App Brand Title
            Text(
                text = "CLCKT",
                color = PrimaryColor,
                fontSize = 38.sp,
                fontFamily = ClashDisplayFontFamily,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            
            Text(
                text = "Measure Your Main Character Energy",
                color = TextColorMuted,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Limit Control Card
            Surface(
                color = SurfaceColor,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "DAILY SCAN TRACKER",
                                color = TextColorMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (canScan) "${5 - usageCount} scans remaining today" else "Limit reached for today!",
                                color = if (canScan) WhiteColorText else Color.Red,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Surface(
                            color = if (canScan) PrimaryColor.copy(alpha = 0.15f) else Color.Red.copy(alpha = 0.15f),
                            shape = CircleShape,
                            modifier = Modifier.size(40.dp),
                            contentColor = if (canScan) PrimaryColor else Color.Red
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "$usageCount/5",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    
                    val progress = usageCount.toFloat() / 5f
                    LinearProgressIndicator(
                        progress = { progress },
                        color = if (canScan) PrimaryColor else Color.Red,
                        trackColor = BorderColor2,
                        strokeCap = StrokeCap.Round,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                    )
                }
            }

            if (!canScan) {
                // Locked Out Alert
                Surface(
                    color = Color.Red.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Lock",
                            tint = Color.Red,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Aura Scan Capped!",
                            color = WhiteColorText,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = ClashDisplayFontFamily
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Your style is too immaculate! To prevent crashing the fashion matrix, come back tomorrow to calculate more aura points.",
                            color = TextColorMuted,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }
            } else {
                // Active Entry Form
                Surface(
                    color = SurfaceColor,
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "WHERE ARE YOU HEADING TONIGHT?",
                            color = PrimaryColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = ClashDisplayFontFamily,
                            letterSpacing = 1.sp
                        )
                        
                        Spacer(modifier = Modifier.height(14.dp))

                        // Event Name
                        OutlinedTextField(
                            value = eventName,
                            onValueChange = {
                                eventName = it
                                // Reset lookup status if event name changes
                                verifiedDescription = null
                                showDescriptionInput = false
                            },
                            label = { Text("Enter venue or event name", color = TextColorMuted) },
                            textStyle = LocalTextStyle.current.copy(color = WhiteColorText),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryColor,
                                unfocusedBorderColor = BorderColor2,
                                focusedContainerColor = SurfaceColor2,
                                unfocusedContainerColor = SurfaceColor2
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Gemini Event Verification State
                        if (isVerifyingVibe) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(color = PrimaryColor, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Gemini Vibe Check running...", color = TextColorMuted, fontSize = 13.sp)
                            }
                        } else if (verifiedDescription != null) {
                            Surface(
                                color = PrimaryColor.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, PrimaryColor.copy(alpha = 0.3f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Row(modifier = Modifier.padding(12.dp)) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Success",
                                        tint = PrimaryColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "GEMINI VIBE CHECK CONFIRMED",
                                            color = PrimaryColor,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = verifiedDescription!!,
                                            color = WhiteColorText,
                                            fontSize = 13.sp,
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Event Description Field animates in if lookup doesn't find details
                        AnimatedVisibility(visible = showDescriptionInput) {
                            Column(modifier = Modifier.padding(top = 12.dp)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.Yellow.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Info, contentDescription = "Info", tint = Color.Yellow, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Vibe details sparse. Tell us a bit about this occasion!",
                                        color = Color.Yellow,
                                        fontSize = 12.sp
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = eventDescription,
                                    onValueChange = { eventDescription = it },
                                    label = { Text("What is the dress code or setup?", color = TextColorMuted) },
                                    textStyle = LocalTextStyle.current.copy(color = WhiteColorText),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PrimaryColor,
                                        unfocusedBorderColor = BorderColor2,
                                        focusedContainerColor = SurfaceColor2,
                                        unfocusedContainerColor = SurfaceColor2
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Verify / Verify Vibe Button
                        if (verifiedDescription == null && !showDescriptionInput) {
                            Button(
                                onClick = {
                                    if (eventName.trim().isEmpty()) {
                                        Toast.makeText(context, "Please enter an event name first!", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    isVerifyingVibe = true
                                    scope.launch {
                                        val result = GeminiService.lookupEvent(eventName)
                                        isVerifyingVibe = false
                                        if (result.found && result.description.isNotEmpty()) {
                                            verifiedDescription = result.description
                                        } else {
                                            showDescriptionInput = true
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("VERIFY VIBE", color = BackGroundColor, fontWeight = FontWeight.Bold, fontFamily = ClashDisplayFontFamily)
                            }
                        } else {
                            // Outfit selection and submit
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                if (selectedImageUri == null) {
                                    Button(
                                        onClick = { photoPickerLauncher.launch("image/*") },
                                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceColor2),
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, PrimaryColor),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("SELECT OUTFIT PHOTO", color = PrimaryColor, fontWeight = FontWeight.Bold, fontFamily = ClashDisplayFontFamily)
                                    }
                                } else {
                                    // Image preview card
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(280.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color.Black)
                                            .border(2.dp, PrimaryColor, RoundedCornerShape(16.dp))
                                    ) {
                                        AsyncImage(
                                            model = selectedImageUri,
                                            contentDescription = "Selected Fit",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = androidx.compose.ui.layout.ContentScale.Fit
                                        )
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(6.dp)
                                                .size(24.dp)
                                                .background(Color.Black.copy(alpha = 0.7f), CircleShape)
                                                .clickable { selectedImageUri = null },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("✕", color = Color.Red, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = {
                                            if (selectedImageUri == null) return@Button
                                            isAnalysisRunning = true
                                            scope.launch {
                                                val finalDescription = if (verifiedDescription != null) verifiedDescription!! else eventDescription
                                                val result = GeminiService.analyzeOutfit(
                                                    context = context,
                                                    imageUri = selectedImageUri!!,
                                                    locationName = eventName,
                                                    eventTitle = eventName,
                                                    eventDescription = finalDescription
                                                )
                                                isAnalysisRunning = false
                                                
                                                // Success flow
                                                limitManager.incrementUsageCount()
                                                usageCount = limitManager.getUsageCount() // update UI state
                                                
                                                val intent = android.content.Intent(context, AuraScanResultActivity::class.java).apply {
                                                    putExtra("result_json", com.google.gson.Gson().toJson(result))
                                                    putExtra("image_uri", selectedImageUri!!.toString())
                                                    putExtra("event_title", eventName)
                                                    putExtra("location_name", if (verifiedDescription != null) eventName else "Hauz Khas Village")
                                                }
                                                context.startActivity(intent)

                                                // Reset state
                                                eventName = ""
                                                eventDescription = ""
                                                verifiedDescription = null
                                                showDescriptionInput = false
                                                selectedImageUri = null
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("CALCULATE AURA", color = BackGroundColor, fontWeight = FontWeight.Bold, fontFamily = ClashDisplayFontFamily)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

        // Animated Roasting Loading Overlay
        if (isAnalysisRunning) {
            Dialog(onDismissRequest = {}) {
                Surface(
                    color = SurfaceColor,
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(2.dp, PrimaryColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = PrimaryColor,
                            strokeWidth = 4.dp,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "CALCULATING VIBE AURA...",
                            color = PrimaryColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = ClashDisplayFontFamily,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentLoadingText,
                            color = WhiteColorText,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }


    }
}
