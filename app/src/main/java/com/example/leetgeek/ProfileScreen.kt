// In ProfileScreen.kt
package com.example.leetgeek

import LeetCodeUser
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
    LaunchedEffect(updateStatus) {
        updateStatus?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
                viewModel.clearUpdateStatus()
            }
        }
    }
    Box {
        Image(
            painter = painterResource(R.drawable.profbg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Profile") },
                    actions = {
                        IconButton(onClick = { viewModel.triggerAllUsersUpdate() }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Update all users"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
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
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }

                    is UiState.Error -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = "Error: ${state.message}",
                                color = Color.Red,
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
}

@Composable
fun ProfileCard(user: LeetCodeUser) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.2f) // Semi-transparent dark background
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                user.name,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
            Text(
                "@${user.username}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f)
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color.White.copy(alpha = 0.3f)
            )

            Text(
                "Problems Solved",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp),
                color = Color.White
            )

            val total = user.problems_solved["All"] ?: 0
            val easy = user.problems_solved["Easy"] ?: 0
            val medium = user.problems_solved["Medium"] ?: 0
            val hard = user.problems_solved["Hard"] ?: 0

            Text("Total: $total", color = Color.White)
            Text("Easy: $easy", color = Color.Green)
            Text("Medium: $medium", color = Color.Yellow)
            Text("Hard: $hard", color = Color.Red)

            user.last_submission?.let {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = Color.White.copy(alpha = 0.3f)
                )
                Text(
                    "Last Submission",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = Color.White
                )
                Text("Title: ${it.title}", color = Color.White)
                Text("Language: ${it.lang}", color = Color.White)
                val date = it.timestamp.split("T").firstOrNull() ?: it.timestamp
                Text("Date: $date", color = Color.White)
            }
        }
    }
}