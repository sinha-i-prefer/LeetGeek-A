import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.leetgeek.ui.theme.AppViewModel
import com.example.leetgeek.ui.theme.UiState

@Composable
fun LeetCodeUserScreen(viewModel: AppViewModel = viewModel(), modifier: Modifier) {
    val uiState by viewModel.uiState.collectAsState()
    var username by remember { mutableStateOf("sinha_i_prefer") }
    val keyboardController = LocalSoftwareKeyboardController.current
    Surface(
        modifier = Modifier.fillMaxSize()
            .padding(top = 16.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "LeetCode Profile Fetcher",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Enter LeetCode Username") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    viewModel.fetchData(username)
                    keyboardController?.hide()
                }),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.fetchData(username)
                    keyboardController?.hide()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Fetch Profile")
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (val state = uiState) {
                is UiState.Idle -> {
                    Text("Enter a username to get started.", textAlign = TextAlign.Center)
                }
                is UiState.Loading -> {
                    CircularProgressIndicator()
                    Text("Fetching data...", modifier = Modifier.padding(top=8.dp))
                }
                is UiState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
                is UiState.Success -> {
                    ProfileCard(user = state.user)
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    viewModel.fetchData(username)
                    keyboardController?.hide()
                }
            ) {
                Text(
                    text = "Update data"
                )
            }
        }
    }
}

@Composable
fun ProfileCard(user: LeetCodeUser) {
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
                // Safely get just the date part of the ISO string
                val date = it.timestamp.split("T").firstOrNull() ?: it.timestamp
                Text("Date: $date")
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun LoginPreview() {
    LeetCodeUserScreen(viewModel = viewModel(), modifier = Modifier)
}