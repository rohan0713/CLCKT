package app.aura.clckt.presentation.features.aura

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.aura.clckt.data.remote.GeminiAnalysisResult
import app.aura.clckt.presentation.features.dashboard.ui.theme.*
import coil.compose.AsyncImage
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AuraScanResultActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val resultJson = intent.getStringExtra("result_json") ?: ""
        val imageUriString = intent.getStringExtra("image_uri") ?: ""
        val eventTitle = intent.getStringExtra("event_title") ?: "Casual Fit"
        val locationName = intent.getStringExtra("location_name") ?: "Unknown Location"

        val geminiResult = try {
            Gson().fromJson(resultJson, GeminiAnalysisResult::class.java)
        } catch (e: Exception) {
            null
        }

        setContent {
            CLCKTTheme(dynamicColor = false) {
                // Ensure status bar and navigation bar have dark background matching CLCKT
                val view = androidx.compose.ui.platform.LocalView.current
                if (!view.isInEditMode) {
                    androidx.compose.runtime.SideEffect {
                        val window = (view.context as android.app.Activity).window
                        window.statusBarColor = android.graphics.Color.parseColor("#0D0D0D")
                        window.navigationBarColor = android.graphics.Color.parseColor("#0D0D0D")
                    }
                }

                if (geminiResult != null) {
                    AuraScanResultScreen(
                        result = geminiResult,
                        imageUri = if (imageUriString.isNotEmpty()) Uri.parse(imageUriString) else null,
                        eventTitle = eventTitle,
                        locationName = locationName,
                        onBack = { finish() }
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(BackGroundColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Failed to parse aura scan results", color = WhiteColorText)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AuraScanResultScreen(
    result: GeminiAnalysisResult,
    imageUri: Uri?,
    eventTitle: String,
    locationName: String,
    onBack: () -> Unit
) {
    val hotPinkColor = Color(0xFF7C6EFA)
    val customTealColor = Color(0xFF42E6A4)
    val customYellowColor = Color(0xFFFF6B6B)
    val customBlueColor = Color(0xFFF59E0B)
    


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackGroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Aura scan result ✨",
                            color = WhiteColorText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = ClashDisplayFontFamily
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .background(SurfaceColor2, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = WhiteColorText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackGroundColor)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackGroundColor)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            // 1. Outfit Analysed Card
            Surface(
                color = SurfaceColor,
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Outfit Image
                    if (imageUri != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color.Black)
                                .border(1.dp, BorderColor2, RoundedCornerShape(18.dp))
                        ) {
                            AsyncImage(
                                model = imageUri,
                                contentDescription = "Analysed Fit",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Outfit analysed!",
                        color = hotPinkColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = result.vibeTitle,
                        color = WhiteColorText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = ClashDisplayFontFamily,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Stunning Total Aura Points badge
                    Surface(
                        color = PrimaryColor,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "${result.score} AURA POINTS",
                            color = BackGroundColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = ClashDisplayFontFamily,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Dynamic wrapping badges
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalArrangement = Arrangement.Center
                    ) {
                        val finalBadges = result.badges ?: listOf(eventTitle, "Moody", "Event Fit")
                        finalBadges.forEachIndexed { idx, badgeText ->
                            val badgeColor = when (idx % 3) {
                                0 -> hotPinkColor
                                1 -> PrimaryColor
                                else -> customTealColor
                            }
                            Box(modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)) {
                                BadgeItem(text = badgeText, color = badgeColor)
                            }
                        }
                    }
                }
            }

            // 2. Brutally Honest Verdict & Suggestions Card
            Surface(
                color = SurfaceColor,
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "CLCKT VIBE VERDICT",
                        color = PrimaryColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = ClashDisplayFontFamily,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = result.explanation,
                        color = WhiteColorText,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Text(
                        text = "SUGGESTIONS TO UPGRADE AURA",
                        color = hotPinkColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = ClashDisplayFontFamily,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    result.suggestions.forEach { suggestion ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "⚡",
                                color = PrimaryColor,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 1.dp, end = 8.dp)
                            )
                            Text(
                                text = suggestion,
                                color = WhiteColorText,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // 3. Score Breakdown Bars
            Surface(
                color = SurfaceColor,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Score breakdown",
                        color = WhiteColorText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = ClashDisplayFontFamily
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    result.categories.forEachIndexed { index, cat ->
                        val progress = cat.score.toFloat() / 200f
                        val progressColor = when (index % 5) {
                            0 -> hotPinkColor
                            1 -> customTealColor
                            2 -> customYellowColor
                            3 -> customBlueColor
                            else -> PrimaryColor
                        }
                        BreakdownRow(
                            name = cat.name,
                            progress = progress,
                            score = cat.score,
                            progressColor = progressColor
                        )
                    }
                }
            }
//            // 5. Main Action Button (Hot Pink)
//            Button(
//                onClick = onBack,
//                colors = ButtonDefaults.buttonColors(containerColor = hotPinkColor),
//                shape = RoundedCornerShape(16.dp),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(54.dp)
//            ) {
//                Text(
//                    text = "Share to feed + collect aura!",
//                    color = Color.White,
//                    fontSize = 15.sp,
//                    fontWeight = FontWeight.Bold,
//                    fontFamily = ClashDisplayFontFamily
//                )
//            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun BadgeItem(text: String, color: Color) {
    Surface(
        color = Color.Transparent,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.7f)),
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Text(
            text = text,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
        )
    }
}

@Composable
fun StatCard(
    value: String,
    label: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = SurfaceColor,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, BorderColor),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                color = valueColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = ClashDisplayFontFamily
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                color = TextColorMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun BreakdownRow(
    name: String,
    progress: Float,
    score: Int,
    progressColor: Color
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                color = TextColorMuted,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$score/200",
                color = WhiteColorText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { progress },
            color = progressColor,
            trackColor = BorderColor2,
            strokeCap = StrokeCap.Round,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
        )
    }
}

class AuraScanResultPreviewParameterProvider : androidx.compose.ui.tooling.preview.PreviewParameterProvider<GeminiAnalysisResult> {
    override val values: Sequence<GeminiAnalysisResult> = sequenceOf(
        GeminiAnalysisResult(
            score = 880,
            vibeTitle = "Main Character Energy",
            explanation = "Honestly? You ate and left zero crumbs. The color coordination at Hauz Khas Village is matching the background energy perfectly.",
            categories = listOf(
                app.aura.clckt.data.remote.CategoryScore("Outfit Style", 185, 200),
                app.aura.clckt.data.remote.CategoryScore("Vibe & Energy", 190, 200),
                app.aura.clckt.data.remote.CategoryScore("Occasion Fit", 175, 200),
                app.aura.clckt.data.remote.CategoryScore("Trendiness", 180, 200),
                app.aura.clckt.data.remote.CategoryScore("Social Presence", 150, 200)
            ),
            suggestions = listOf(
                "Keep doing exactly what you're doing, the camera loves you.",
                "Add some chunky metal accessories."
            ),
            badges = listOf("NPC Repeller", "Cozy Core", "Ate & Left No Crumbs")
        )
    )
}

@Preview(showBackground = true)
@Composable
fun AuraScanResultScreenPreview(
    @PreviewParameter(AuraScanResultPreviewParameterProvider::class) result: GeminiAnalysisResult
) {
    CLCKTTheme(dynamicColor = false) {
        AuraScanResultScreen(
            result = result,
            imageUri = null,
            eventTitle = "Casual Hangout",
            locationName = "Hauz Khas Village",
            onBack = {}
        )
    }
}
