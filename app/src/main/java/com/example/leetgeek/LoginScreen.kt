package com.example.leetgeek

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun LoginScreen(navController: NavController) {
    var username by remember { mutableStateOf("sinha_i_prefer") }
    val keyboardController = LocalSoftwareKeyboardController.current

    val onLogin = {
        if (username.isNotBlank()) {
            // Navigate to the main screen, clearing the login screen from the back stack
            navController.navigate(Screen.Main.withArgs(username)) {
                popUpTo(Screen.Login.route) {
                    inclusive = true
                }
            }
            keyboardController?.hide()
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "LeetGeek",
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Enter LeetCode Username") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                keyboardActions = KeyboardActions(onGo = { onLogin() }),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onLogin,
                modifier = Modifier.fillMaxWidth(),
                enabled = username.isNotBlank()
            ) {
                Text("Continue")
            }
        }
    }
}