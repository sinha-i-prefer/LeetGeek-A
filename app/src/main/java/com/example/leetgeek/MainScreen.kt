package com.example.leetgeek

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*

@Composable
fun MainScreen(username: String) {
    val navController = rememberNavController()
    val bottomNavItems = listOf(BottomNavItem.Profile, BottomNavItem.Leaderboard)

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.Transparent, // Make container transparent to show gradient
                modifier = Modifier.background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF6A1B9A), // BlueViolet - top
                            Color(0xFF4B0082), // Indigo - middle
                            Color(0xFF000000)  // Black - bottom
                        )
                    )
                )
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) }
                    )
                }
            }

        }
    ) { innerPadding ->
        // This NavHost is nested inside the MainScreen
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Profile.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Profile.route) {
                // Pass the username from MainScreen to ProfileScreen
                ProfileScreen(username = username)
            }
            composable(BottomNavItem.Leaderboard.route) {
                // Your existing MemberList composable can be called here
                MemberList()
            }
        }
    }
}