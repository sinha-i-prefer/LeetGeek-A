package com.example.leetgeek

import LastSubmission
import LeetCodeUser
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.leetgeek.ui.theme.AppViewModel
import com.example.leetgeek.ui.theme.LeaderboardUiState
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun MemberCard(user: LeetCodeUser, modifier: Modifier = Modifier) {
    var expand by rememberSaveable { mutableStateOf(false) }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { expand = !expand }
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "#${user.rank}",
                modifier = Modifier.padding(4.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(24.dp))
            Column {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "@${user.username}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (expand) {
                    Spacer(modifier = Modifier.padding(4.dp))
                    val total = user.problems_solved["All"] ?: 0
                    val easy = user.problems_solved["Easy"] ?: 0
                    val medium = user.problems_solved["Medium"] ?: 0
                    val hard = user.problems_solved["Hard"] ?: 0

                    Text(
                        text = "Total: $total",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Easy: $easy | Medium: $medium | Hard: $hard",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    user.last_submission?.let { submission ->
                        Text(
                            text = "Last: ${submission.title} (${submission.lang})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

// In MemberList.kt

@Composable
fun MemberList(
    modifier: Modifier = Modifier,
    viewModel: AppViewModel = viewModel()
) {
    val uiState by viewModel.leaderboardUiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchLeaderboard()
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (val state = uiState) {
            // Treat Idle and Loading the same to prevent a blank flash
            is LeaderboardUiState.Idle,
            is LeaderboardUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Text(
                            text = "Loading leaderboard...",
                            modifier = Modifier.padding(top = 8.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            is LeaderboardUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            is LeaderboardUiState.Success -> {
                if (state.users.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No users found in leaderboard.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    LazyColumn {
                        items(state.users) { user ->
                            MemberCard(user = user)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MemberListPreview() {
    val mockUsers = listOf(
        LeetCodeUser(
            name = "John Smith",
            username = "jsmith",
            problems_solved = mapOf("All" to 180L, "Easy" to 80L, "Medium" to 80L, "Hard" to 20L),
            rank = 1
        ),
        LeetCodeUser(
            name = "Himanshu Sinha",
            username = "sinha_i_prefer",
            problems_solved = mapOf("All" to 307L, "Easy" to 69L, "Medium" to 150L, "Hard" to 88L),
            rank = 2
        ),
        LeetCodeUser(
            name = "Jane Doe",
            username = "janedoe",
            problems_solved = mapOf("All" to 250L, "Easy" to 100L, "Medium" to 100L, "Hard" to 50L),
            rank = 3
        )
    )

    LazyColumn {
        items(mockUsers) { user ->
            MemberCard(user = user)
        }
    }
}