package app.aura.clckt.presentation.features.dashboard.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavDestination(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Home : BottomNavDestination(
        route = "home",
        title = "Home",
        icon = Icons.Default.Home
    )
    data object Explore : BottomNavDestination(
        route = "explore",
        title = "Explore",
        icon = Icons.Default.Search
    )
    data object Feed : BottomNavDestination(
        route = "feed",
        title = "Feed",
        icon = Icons.Default.Build
    )
    data object Profile : BottomNavDestination(
        route = "profile",
        title = "Profile",
        icon = Icons.Default.Person
    )
}
