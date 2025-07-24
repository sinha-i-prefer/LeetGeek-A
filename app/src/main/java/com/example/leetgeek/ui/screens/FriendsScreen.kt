package com.example.leetgeek.ui.screens

import LeetCodeUser
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.leetgeek.AppViewModel
import com.example.leetgeek.FriendsUiState
import java.time.Duration
import java.time.Instant
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    username: String,
    viewModel: AppViewModel = viewModel()
) {
    val uiState by viewModel.friendsUiState.collectAsState()

    LaunchedEffect(username) {
        viewModel.fetchFriends(username)
    }

    Box {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Friends") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White
                    )
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (val state = uiState) {
                    is FriendsUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                    is FriendsUiState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = state.message, color = Color.Red)
                        }
                    }
                    is FriendsUiState.Success -> {
                        if (state.friends.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("You haven't added any friends yet.", color = Color.White)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(8.dp)
                            ) {
                                items(state.friends) { friend ->
                                    FriendCard(friend = friend)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FriendCard(friend: LeetCodeUser) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        friend.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                    Text(
                        "@${friend.username}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                friend.last_submission?.let {
                    val submissionTime = try {
                        Instant.from(DateTimeFormatter.ISO_INSTANT.parse(it.timestamp))
                    } catch (e: Exception) {
                        Instant.now() // Fallback
                    }
                    val hoursSince = Duration.between(submissionTime, Instant.now()).toHours()
                    if (hoursSince < 24) { // Highlight if submission is within 24 hours
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "New Activity",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color.White.copy(alpha = 0.3f)
            )

            val total = friend.problems_solved["All"] ?: 0
            val easy = friend.problems_solved["Easy"] ?: 0
            val medium = friend.problems_solved["Medium"] ?: 0
            val hard = friend.problems_solved["Hard"] ?: 0

            Text("Total Solved: $total", color = Color.White)
            Text("Easy: $easy | Medium: $medium | Hard: $hard", color = Color.White.copy(alpha = 0.8f))

            friend.last_submission?.let {
                Text(
                    "Last: ${it.title}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
