package app.aura.clckt.presentation.features.details

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.aura.clckt.presentation.features.dashboard.ui.theme.BackGroundColor
import app.aura.clckt.presentation.features.dashboard.ui.theme.CLCKTTheme
import app.aura.clckt.presentation.features.dashboard.ui.theme.PrimaryColor
import app.aura.clckt.presentation.features.dashboard.ui.theme.SurfaceColor
import app.aura.clckt.presentation.features.dashboard.ui.theme.TextColorMuted
import app.aura.clckt.presentation.features.dashboard.ui.theme.WhiteColorText

class TrendingItemDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val name = intent.getStringExtra("name") ?: "Event Detail"
        val location = intent.getStringExtra("location") ?: "Unknown Location"
        val aura = intent.getIntExtra("aura", 0)

        setContent {
            CLCKTTheme {
                TrendingDetailScreen(
                    name = name,
                    location = location,
                    aura = aura,
                    onBack = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrendingDetailScreen(
    name: String,
    location: String,
    aura: Int,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackGroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Event Details", color = WhiteColorText) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = WhiteColorText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackGroundColor)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Visual Banner Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(PrimaryColor.copy(alpha = 0.6f), SurfaceColor)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "EVENT IMAGE",
                    color = WhiteColorText.copy(alpha = 0.3f),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = name,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = WhiteColorText
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = location,
                fontSize = 18.sp,
                color = TextColorMuted
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Aura Card
            Surface(
                color = SurfaceColor,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Aura Boost", color = TextColorMuted, fontSize = 14.sp)
                        Text("+$aura points", color = PrimaryColor, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Claim", color = BackGroundColor, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}