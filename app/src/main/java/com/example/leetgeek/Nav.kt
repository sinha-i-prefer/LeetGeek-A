package com.example.leetgeek

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Main : Screen("main/{username}") {
        fun withArgs(username: String): String {
            return "main/$username"
        }
    }
}

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Profile : BottomNavItem("profile", Icons.Default.Person, "Profile")
    object Leaderboard : BottomNavItem("leaderboard", Icons.Default.Star, "Leaderboard")
    object Friends : BottomNavItem("friends", Icons.Default.Face, "Friends")
}
