package app.aura.clckt.presentation.features.dashboard.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.aura.clckt.data.model.TrendingEvent
import app.aura.clckt.data.remote.RemoteConfigManager
import app.aura.clckt.presentation.features.dashboard.ui.theme.BackGroundColor
import app.aura.clckt.presentation.features.dashboard.ui.theme.BorderColor
import app.aura.clckt.presentation.features.dashboard.ui.theme.BorderColor2
import app.aura.clckt.presentation.features.dashboard.ui.theme.ClashDisplayFontFamily
import app.aura.clckt.presentation.features.dashboard.ui.theme.ClashDisplayTitle
import app.aura.clckt.presentation.features.dashboard.ui.theme.IconColor
import app.aura.clckt.presentation.features.dashboard.ui.theme.PrimaryColor
import app.aura.clckt.presentation.features.dashboard.ui.theme.PrimaryTextStyle
import app.aura.clckt.presentation.features.dashboard.ui.theme.SurfaceColor
import app.aura.clckt.presentation.features.dashboard.ui.theme.SurfaceColor2
import app.aura.clckt.presentation.features.dashboard.ui.theme.TextColorMuted
import app.aura.clckt.presentation.features.dashboard.ui.theme.WhiteColorText

@Preview
@Composable
fun HomeScreen() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = BackGroundColor)
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                HeaderSectionComposable()
                Spacer(modifier = Modifier.size(18.dp))
                AuraScoreSection()
                Spacer(modifier = Modifier.size(18.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Trending Now", style = PrimaryTextStyle.copy(
                            color = WhiteColorText,
                            fontSize = 16.sp
                        )
                    )

                    Text(
                        text = "See All", style = PrimaryTextStyle.copy(
                            color = PrimaryColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Spacer(modifier = Modifier.size(12.dp))
                val trendingEvents = remember(RemoteConfigManager.isConfigLoaded.value) {
                    RemoteConfigManager.getTrendingList()
                }

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(trendingEvents, key = { it.name }) { event ->
                        TrendingItems(
                            event = event,
                            modifier = Modifier.size(width = 280.dp, height = 280.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.size(18.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Near You", style = PrimaryTextStyle.copy(
                            color = WhiteColorText,
                            fontSize = 16.sp
                        )
                    )

                    Text(
                        text = "See All", style = PrimaryTextStyle.copy(
                            color = PrimaryColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Spacer(modifier = Modifier.size(12.dp))
            }
            items(5, key = { it }) {
                NearByItems(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            }
        }
    }
}

@Composable
fun HeaderSectionComposable() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = buildAnnotatedString {
                    append(RemoteConfigManager.getString("welcome_message"))
                    withStyle(style = SpanStyle(color = PrimaryColor)) {
                        append(RemoteConfigManager.getString("user_name"))
                    }
                },
                style = ClashDisplayTitle.copy(
                    color = WhiteColorText,
                    fontSize = 24.sp
                )
            )
            Spacer(modifier = Modifier.size(2.dp))
            Text(
                text = "Tuesday, 29 Apr | Delhi", style = PrimaryTextStyle.copy(
                    fontSize = 14.sp,
                    color = TextColorMuted,
                    fontWeight = FontWeight.W200
                )
            )
        }

        Box(
            modifier = Modifier
                .background(
                    color = SurfaceColor2, shape = RoundedCornerShape(
                        size = 16.dp
                    )
                )
                .border(
                    width = 2.dp,
                    color = BorderColor2, shape = RoundedCornerShape(
                        size = 16.dp
                    )
                )
                .padding(all = 16.dp)
        )
        {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Bell_icon",
                tint = IconColor,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun AuraScoreSection() {

    val ptsStyle = remember {
        PrimaryTextStyle.copy(
            fontSize = 16.sp,
            color = PrimaryColor,
            fontWeight = FontWeight.Bold
        )
    }
    val unitStyle = remember {
        PrimaryTextStyle.copy(
            fontSize = 10.sp,
            color = TextColorMuted,
            fontWeight = FontWeight.W500
        )
    }
    val titleStyle = remember {
        PrimaryTextStyle.copy(
            fontSize = 14.sp,
            color = TextColorMuted, fontWeight = FontWeight.W600
        )
    }
    val monarchStyle = remember {
        PrimaryTextStyle.copy(
            fontSize = 18.sp,
            color = WhiteColorText, fontWeight = FontWeight.W500
        )
    }
    val levelStyle = remember {
        PrimaryTextStyle.copy(
            color = PrimaryColor,
            fontWeight = FontWeight.W200,
            fontSize = 12.sp
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = SurfaceColor, shape = RoundedCornerShape(
                    size = 18.dp
                )
            )
            .border(
                width = 2.dp,
                color = BorderColor,
                shape = RoundedCornerShape(
                    size = 18.dp
                )
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(75.dp)
            ) {
                CircularProgressIndicator(
                    progress = {
                        0.7F
                    }, color = PrimaryColor,
                    trackColor = BorderColor2,
                    strokeWidth = 8.dp,
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier.fillMaxSize()
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "720", style = ptsStyle
                    )
                    Text(
                        text = "pts", style = unitStyle
                    )
                }
            }
            Spacer(modifier = Modifier.size(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "My Aura Score", style = titleStyle
                )
                Spacer(modifier = Modifier.size(6.dp))
                Text(
                    text = "Style Monarch", style = monarchStyle
                )
                Spacer(modifier = Modifier.size(8.dp))
                LinearProgressIndicator(
                    progress = {
                        0.7F
                    },
                    modifier = Modifier.fillMaxWidth(),
                    color = PrimaryColor,
                    trackColor = BorderColor2,
                    strokeCap = StrokeCap.Round
                )
            }
            Spacer(modifier = Modifier.size(12.dp))
            Box(
                modifier = Modifier
                    .background(
                        color = PrimaryColor.copy(alpha = 0.1F), shape = RoundedCornerShape(
                            size = 10.dp
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = PrimaryColor.copy(alpha = 0.4F),
                        shape = RoundedCornerShape(
                            size = 10.dp
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Lv.7", style = levelStyle
                )
            }
        }
    }
}

@Composable
fun TrendingItems(event: TrendingEvent, modifier: Modifier = Modifier) {

    val titleStyle = remember {
        PrimaryTextStyle.copy(
            color = WhiteColorText,
            fontSize = 16.sp,
            fontWeight = FontWeight.W500
        )
    }
    val subtitleStyle = remember {
        PrimaryTextStyle.copy(
            color = TextColorMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.W500
        )
    }
    val auraBadgeStyle = remember {
        PrimaryTextStyle.copy(
            color = PrimaryColor,
            fontWeight = FontWeight.W200,
            fontSize = 14.sp
        )
    }

    Box(
        modifier = modifier
            .background(
                color = SurfaceColor, shape = RoundedCornerShape(
                    size = 12.dp
                )
            )
            .border(
                width = 1.dp,
                color = BorderColor, shape = RoundedCornerShape(
                    size = 12.dp
                )
            )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        color = PrimaryColor.copy(alpha = 0.4F), shape = RoundedCornerShape(
                            size = 12.dp
                        )
                    )

            )
            Spacer(modifier = Modifier.size(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = event.name, style = titleStyle
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = event.location, style = subtitleStyle
                )
                Spacer(modifier = Modifier.size(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = PrimaryColor.copy(alpha = 0.1F), shape = RoundedCornerShape(
                                    size = 10.dp
                                )
                            )
                            .border(
                                width = 1.dp,
                                color = PrimaryColor.copy(alpha = 0.4F),
                                shape = RoundedCornerShape(
                                    size = 10.dp
                                )
                            )
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "+${event.aura} aura", style = auraBadgeStyle
                        )
                    }
                    Box(
                        modifier = Modifier

                            .border(
                                width = 1.dp,
                                color = BorderColor2,
                                shape = RoundedCornerShape(
                                    size = 10.dp
                                )
                            )
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Check fit", style = PrimaryTextStyle.copy(
                                color = WhiteColorText,
                                fontSize = 14.sp
                            )
                        )
                    }
                }
            }


        }

    }
}

@Composable
fun NearByItems(modifier: Modifier = Modifier) {

    val titleStyle = remember {
        PrimaryTextStyle.copy(
            color = WhiteColorText,
            fontSize = 16.sp,
            fontWeight = FontWeight.W500
        )
    }
    val subtitleStyle = remember {
        PrimaryTextStyle.copy(
            color = TextColorMuted,
            fontSize = 14.sp,
            fontWeight = FontWeight.W500
        )
    }
    val auraBadgeStyle = remember {
        PrimaryTextStyle.copy(
            color = PrimaryColor,
            fontWeight = FontWeight.W200,
            fontSize = 12.sp
        )
    }

    val aestheticColors = remember {
        listOf(
            Color(0xFFFF6B6B),
            Color(0xFF4ECDC4),
            Color(0xFF45B7D1),
            Color(0xFF96CEB4),
            Color(0xFFFFEEAD),
            Color(0xFFD4A5A5),
            Color(0xFF9B59B6),
            Color(0xFFF1948A),
            Color(0xFF7FB3D5)
        )
    }
    val randomColor = remember { aestheticColors.random().copy(alpha = 0.4f) }

    Box(
        modifier = modifier
            .background(
                color = SurfaceColor, shape = RoundedCornerShape(
                    size = 12.dp
                )
            )
            .border(
                width = 1.dp,
                color = BorderColor, shape = RoundedCornerShape(
                    size = 12.dp
                )
            )
            .padding(all = 16.dp)

    ) {
        Row {
            Box(
                modifier = Modifier
                    .background(
                        color = randomColor, shape = RoundedCornerShape(
                            size = 12.dp
                        )
                    )
                    .padding(all = 28.dp)


            )
            Spacer(modifier = Modifier.size(12.dp))
            Column(
            ) {
                Text(
                    text = "Retro Rewind Fest", style = titleStyle
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = "Hauz Khas Village", style = subtitleStyle
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = "1.2km", style = subtitleStyle
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "+120 aura", style = auraBadgeStyle.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

        }

    }
}