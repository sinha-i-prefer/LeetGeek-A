// In ProfileScreen.kt
package com.example.leetgeek

import LeetCodeUser
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.leetgeek.ui.theme.AppViewModel
import com.example.leetgeek.ui.theme.UiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    username: String,
    viewModel: AppViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val updateStatus by viewModel.updateStatus.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Observe the update status and show a Snackbar when it changes
    LaunchedEffect(updateStatus) {
        updateStatus?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
                viewModel.clearUpdateStatus() // Clear status after showing
            }
        }
    }

    // Use a Scaffold to host the TopAppBar and Snackbar
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                actions = {
                    // This is the button to update all users
                    IconButton(onClick = { viewModel.triggerAllUsersUpdate() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Update all users"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        // Fetch data for the specific user profile
        LaunchedEffect(username) {
            if (username.isNotBlank()) {
                viewModel.fetchData(username)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Apply padding from the Scaffold
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val state = uiState) {
                is UiState.Idle, is UiState.Loading -> {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Error -> {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = "Error: ${state.message}",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                is UiState.Success -> {
                    ProfileCard(user = state.user)
                }
            }
        }
    }
}

@Composable
fun ProfileCard(user: LeetCodeUser) {
    // This composable from your old MainSc.kt can be moved here
    // No changes needed for ProfileCard itself.
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(user.name, style = MaterialTheme.typography.titleLarge)
            Text("@${user.username}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            Text("Problems Solved", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))

            val total = user.problems_solved["All"] ?: 0
            val easy = user.problems_solved["Easy"] ?: 0
            val medium = user.problems_solved["Medium"] ?: 0
            val hard = user.problems_solved["Hard"] ?: 0

            Text("Total: $total")
            Text("Easy: $easy", color = MaterialTheme.colorScheme.primary)
            Text("Medium: $medium", color = MaterialTheme.colorScheme.tertiary)
            Text("Hard: $hard", color = MaterialTheme.colorScheme.error)

            user.last_submission?.let {
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                Text("Last Submission", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
                Text("Title: ${it.title}")
                Text("Language: ${it.lang}")
                val date = it.timestamp.split("T").firstOrNull() ?: it.timestamp
                Text("Date: $date")
            }
        }
    }
}