package app.aura.clckt.presentation.features.details

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.aura.clckt.data.model.PlacesItem
import app.aura.clckt.data.model.CheckinsItem
import app.aura.clckt.presentation.features.dashboard.ui.theme.*
import app.aura.clckt.presentation.viewmodel.TrendingViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Dialog
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.ui.platform.LocalContext
import app.aura.clckt.data.remote.GeminiService
import app.aura.clckt.data.remote.GeminiAnalysisResult
import androidx.compose.material3.LinearProgressIndicator
import coil.compose.AsyncImage

class TrendingItemDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val event = intent.getSerializableExtra("event") as? PlacesItem

        setContent {
            CLCKTTheme {
                val viewModel: TrendingViewModel = viewModel()
                
                LaunchedEffect(event) {
                    viewModel.setSelectedEvent(event)
                }
                
                val currentEvent by viewModel.selectedEvent.collectAsState()

                if (currentEvent != null) {
                    TrendingDetailScreen(
                        event = currentEvent!!,
                        onBack = { finish() }
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(BackGroundColor), contentAlignment = Alignment.Center) {
                        Text("Event not found", color = WhiteColorText)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrendingDetailScreen(
    event: PlacesItem,
    onBack: () -> Unit
) {
    var showUploadDialog by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var geminiResult by remember { mutableStateOf<GeminiAnalysisResult?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                showUploadDialog = false
                isUploading = true
                scope.launch {
                    val result = GeminiService.analyzeOutfit(
                        context = context,
                        imageUri = uri,
                        locationName = event.location?.city ?: event.location?.address ?: "Unknown Location",
                        eventTitle = event.name ?: "Unknown Event",
                        eventDescription = event.description ?: ""
                    )
                    isUploading = false
                    geminiResult = result
                    showSuccessDialog = true
                }
            }
        }
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackGroundColor,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.padding(start = 8.dp).background(SurfaceColor2, CircleShape)) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = WhiteColorText)
                    }
                },
                actions = {
                    IconButton(onClick = { }, modifier = Modifier.padding(end = 8.dp).background(SurfaceColor2, CircleShape)) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = WhiteColorText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            BottomActionBar(onClockInClick = { showUploadDialog = true })
        }
    ) { innerPadding ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            // Hero Image
            AsyncImage(
                model = event.imageUrl,
                contentDescription = event.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(24.dp)) {
                if (event.timing?.isLive == true) {
                    Surface(
                        color = PrimaryColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryColor.copy(alpha = 0.3f))
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(PrimaryColor, CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Live event · ${event.timing.display?.split("·")?.firstOrNull()?.trim() ?: "Tonight"}", color = PrimaryColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Text(
                    text = event.name ?: "",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = WhiteColorText,
                    fontFamily = ClashDisplayFontFamily
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(
                        color = SurfaceColor,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextColorMuted, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = event.location?.city ?: event.location?.address ?: "", color = TextColorMuted, fontSize = 13.sp)
                        }
                    }

                    Surface(
                        color = SurfaceColor,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DateRange, contentDescription = null, tint = TextColorMuted, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = event.timing?.display?.split("·")?.lastOrNull()?.trim() ?: event.timing?.display ?: "", color = TextColorMuted, fontSize = 13.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    color = PrimaryColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryColor.copy(alpha = 0.4f))
                ) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(6.dp).background(PrimaryColor, CircleShape))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "+${event.aura?.basePoints ?: 0} aura", color = PrimaryColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = event.description ?: "",
                    color = TextColorMuted,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutfitCheckinsSection(checkins = event.checkins?.filterNotNull() ?: emptyList())
            }
        }
    }

    if (showUploadDialog) {
        UploadPhotoBottomSheet(
            onDismissRequest = { showUploadDialog = false },
            onCameraClick = {
                showUploadDialog = false
                photoPickerLauncher.launch("image/*")
            },
            onGalleryClick = {
                showUploadDialog = false
                photoPickerLauncher.launch("image/*")
            }
        )
    }

    if (isUploading) {
        Dialog(onDismissRequest = {}) {
            Surface(
                color = SurfaceColor,
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                modifier = Modifier.width(280.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = PrimaryColor,
                        strokeWidth = 4.dp,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Analyzing outfit...",
                        color = WhiteColorText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = ClashDisplayFontFamily
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Connecting to Gemini AI...",
                        color = TextColorMuted,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }

    if (showSuccessDialog && geminiResult != null) {
        val result = geminiResult!!
        Dialog(onDismissRequest = { showSuccessDialog = false }) {
            Surface(
                color = SurfaceColor,
                shape = RoundedCornerShape(28.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, PrimaryColor.copy(alpha = 0.6f)),
                modifier = Modifier
                    .width(360.dp)
                    .heightIn(max = 620.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "AURA CHECK",
                        color = PrimaryColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Surface(
                        color = PrimaryColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryColor.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = result.vibeTitle.uppercase(),
                            color = PrimaryColor,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${result.score}",
                            color = WhiteColorText,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = ClashDisplayFontFamily
                        )
                        Text(
                            text = "TOTAL AURA POINTS",
                            color = TextColorMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Text(
                        text = result.explanation,
                        color = WhiteColorText,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "BREAKDOWN",
                        color = WhiteColorText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = ClashDisplayFontFamily,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    result.categories.forEach { cat ->
                        val progress = if (cat.maxScore > 0) cat.score.toFloat() / cat.maxScore.toFloat() else 0f
                        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(cat.name, color = TextColorMuted, fontSize = 13.sp)
                                Text("${cat.score}/${cat.maxScore}", color = PrimaryColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                color = PrimaryColor,
                                trackColor = BorderColor2,
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "HOW TO FARM MORE AURA",
                        color = WhiteColorText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = ClashDisplayFontFamily,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    result.suggestions.forEach { suggestion ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "✦",
                                color = PrimaryColor,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = suggestion,
                                color = TextColorMuted,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = { showSuccessDialog = false },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("AWESOME, NOTED!", color = BackGroundColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun OutfitCheckinsSection(checkins: List<CheckinsItem>) {
    Surface(
        color = SurfaceColor,
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Outfit check-ins", color = WhiteColorText, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = ClashDisplayFontFamily)
                Text("${checkins.size} checked in", color = TextColorMuted, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))

            val rows = checkins.chunked(2)
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rows.forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { checkin ->
                            Box(modifier = Modifier.weight(1f)) {
                                CheckinCard(checkin)
                            }
                        }
                        if (rowItems.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CheckinCard(checkin: CheckinsItem) {
    Surface(
        color = BackGroundColor,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor2)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(PrimaryColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = checkin.name?.firstOrNull()?.toString() ?: "U",
                    color = PrimaryColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(checkin.name ?: "", color = WhiteColorText, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(checkin.styleTag ?: "", color = TextColorMuted, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Text("+${checkin.auraPoints ?: 0}", color = PrimaryColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
fun BottomActionBar(onClockInClick: () -> Unit) {
    Surface(
        color = BackGroundColor,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = onClockInClick,
                modifier = Modifier.weight(1f).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.DateRange, contentDescription = null, tint = BackGroundColor)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Clock in my outfit", color = BackGroundColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            
            Surface(
                color = SurfaceColor,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                modifier = Modifier.size(56.dp),
                onClick = {}
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Star, contentDescription = "Favorite", tint = TextColorMuted)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadPhotoBottomSheet(
    onDismissRequest: () -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = SurfaceColor,
        contentColor = WhiteColorText,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = BorderColor)
        },
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Clock in your outfit",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = WhiteColorText,
                fontFamily = ClashDisplayFontFamily
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Upload a photo of your outfit to complete the clock-in and earn aura points!",
                fontSize = 14.sp,
                color = TextColorMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Camera Option Card
                Surface(
                    onClick = onCameraClick,
                    color = BackGroundColor,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor2),
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(PrimaryColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.Build, contentDescription = "Camera", tint = PrimaryColor, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Take Photo", color = WhiteColorText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Gallery Option Card
                Surface(
                    onClick = onGalleryClick,
                    color = BackGroundColor,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor2),
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(PrimaryColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Gallery", tint = PrimaryColor, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("From Gallery", color = WhiteColorText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}