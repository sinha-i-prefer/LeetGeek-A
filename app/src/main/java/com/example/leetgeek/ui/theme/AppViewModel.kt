package com.example.leetgeek.ui.theme

import LeetCodeUser
import LastSubmission // Make sure this is imported if in another file
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// --- State for a single user (existing) ---
sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val user: LeetCodeUser) : UiState()
    data class Error(val message: String) : UiState()
}

// --- State for the leaderboard (new) ---
sealed class LeaderboardUiState {
    object Idle : LeaderboardUiState()
    object Loading : LeaderboardUiState()
    data class Success(val users: List<LeetCodeUser>) : LeaderboardUiState()
    data class Error(val message: String) : LeaderboardUiState()
}

class AppViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val httpClient = HttpClient()
    private val vercelApiUrl = "https://leet-seek.vercel.app/api"

    // --- State and function for a single user (existing) ---
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    // --- State and function for the leaderboard (new) ---
    private val _leaderboardUiState = MutableStateFlow<LeaderboardUiState>(LeaderboardUiState.Idle)
    val leaderboardUiState = _leaderboardUiState.asStateFlow()


    fun fetchData(username: String) {
        // ... existing fetchData logic remains the same
    }

    /**
     * Fetches all users from the 'leetcodeUsers' collection,
     * processes the data, and updates the leaderboard UI state.
     */
    fun fetchLeaderboard() {
        viewModelScope.launch {
            _leaderboardUiState.value = LeaderboardUiState.Loading
            try {
                val snapshot = db.collection("leetcodeUsers").get().await()

                // Using your original, more robust parsing logic
                val fetchedUsers = mutableListOf<LeetCodeUser>()
                for (document in snapshot.documents) {
                    try {
                        val name = document.getString("name") ?: ""
                        val username = document.getString("username") ?: ""

                        // Handle potential number type issues (e.g., Int, Long, Double)
                        val problemsSolvedRaw = document.get("problems_solved") as? Map<String, Any> ?: emptyMap()
                        val problemsSolved = problemsSolvedRaw.mapValues { (_, value) ->
                            (value as? Number)?.toLong() ?: 0L
                        }

                        val lastSubmissionData = document.get("last_submission") as? Map<String, Any>
                        val lastSubmission = lastSubmissionData?.let { data ->
                            LastSubmission(
                                title = data["title"] as? String ?: "",
                                lang = data["lang"] as? String ?: "",
                                timestamp = data["timestamp"] as? String ?: "",
                                url = data["url"] as? String ?: ""
                            )
                        }

                        fetchedUsers.add(
                            LeetCodeUser(
                                name = name,
                                username = username,
                                problems_solved = problemsSolved,
                                last_submission = lastSubmission
                            )
                        )
                    } catch (e: Exception) {
                        Log.e("Leaderboard", "Failed to parse document ${document.id}", e)
                    }
                }

                val rankedUsers = fetchedUsers
                    .sortedByDescending { it.problems_solved["All"] ?: 0L }
                    .mapIndexed { index, user ->
                        user.copy(rank = index + 1)
                    }

                _leaderboardUiState.value = LeaderboardUiState.Success(rankedUsers)

            } catch (e: Exception) {
                Log.e("Leaderboard", "Error loading leaderboard", e)
                _leaderboardUiState.value = LeaderboardUiState.Error("Failed to load leaderboard.")
            }
        }
    }
}