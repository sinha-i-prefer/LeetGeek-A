package com.example.leetgeek

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.leetgeek.ui.theme.LeetGeekTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LeetGeekTheme {
                val navController = rememberNavController()
                // This is the root NavHost for the entire app
                NavHost(navController = navController, startDestination = Screen.Login.route) {
                    composable(Screen.Login.route) {
                        LoginScreen(navController = navController)
                    }
                    composable(
                        route = Screen.Main.route,
                        arguments = listOf(navArgument("username") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val username = backStackEntry.arguments?.getString("username") ?: ""
                        MainScreen(username = username)
                    }
                }
            }
        }
    }
}