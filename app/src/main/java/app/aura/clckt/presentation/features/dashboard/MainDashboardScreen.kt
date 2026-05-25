package app.aura.clckt.presentation.features.dashboard

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.ui.draw.drawBehind
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.aura.clckt.presentation.features.dashboard.navigation.BottomNavDestination
import app.aura.clckt.presentation.features.dashboard.screens.ExploreScreen
import app.aura.clckt.presentation.features.dashboard.screens.FeedScreen
import app.aura.clckt.presentation.features.dashboard.screens.HomeScreen
import app.aura.clckt.presentation.features.dashboard.screens.ProfileScreen
import app.aura.clckt.presentation.features.details.TrendingItemDetailActivity
import app.aura.clckt.presentation.features.dashboard.ui.theme.BackGroundColor
import app.aura.clckt.presentation.features.dashboard.ui.theme.BorderColor
import app.aura.clckt.presentation.features.dashboard.ui.theme.PrimaryColor
import app.aura.clckt.presentation.features.dashboard.ui.theme.SurfaceColor
import app.aura.clckt.presentation.features.dashboard.ui.theme.TextColorMuted
import app.aura.clckt.presentation.features.dashboard.ui.theme.WhiteColorText

@Composable
fun MainDashboardScreen() {
    val navController = rememberNavController()

    val screens = listOf(
        BottomNavDestination.Home,
        BottomNavDestination.Explore,
        BottomNavDestination.Feed,
        BottomNavDestination.Profile
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(color = BackGroundColor),
        bottomBar = {
            NavigationBar(
                modifier = Modifier.drawBehind {
                    drawLine(
                        color = BorderColor,
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                        strokeWidth = 4.dp.toPx()
                    )
                },
                containerColor = SurfaceColor,
            ) {

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                screens.forEach { screen ->
                    NavigationBarItem(
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryColor,
                            selectedTextColor = PrimaryColor,
                            unselectedIconColor = TextColorMuted,
                            unselectedTextColor = TextColorMuted,
                            indicatorColor = Color.Transparent
                        ),
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = PrimaryColor,
                onClick = { /* TODO: Add post action */ }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Post",
                    tint = BackGroundColor
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = BottomNavDestination.Home.route
            ) {
                composable(BottomNavDestination.Home.route) {
                    val context = LocalContext.current
                    HomeScreen(onTrendingItemClick = { event: app.aura.clckt.data.model.PlacesItem ->
                        val intent = Intent(context, TrendingItemDetailActivity::class.java).apply {
                            putExtra("event", event)
                        }
                        context.startActivity(intent)
                    })
                }
                composable(BottomNavDestination.Explore.route) { ExploreScreen() }
                composable(BottomNavDestination.Feed.route) { FeedScreen() }
                composable(BottomNavDestination.Profile.route) { ProfileScreen() }
            }
        }
    }
}
