package com.example.leetgeek.ui.theme

import LeetCodeUser
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val user: LeetCodeUser) : UiState()
    data class Error(val message: String) : UiState()
}

class AppViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val httpClient = HttpClient()

    private val vercelApiUrl = "https://leet-seek.vercel.app/api"

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun fetchData(username: String) {
        if (username.isBlank()) {
            _uiState.value = UiState.Error("Username cannot be empty.")
            return
        }

        viewModelScope.launch {
            _uiState.value = UiState.Loading

            val docRef = db.collection("leetcodeUsers").document(username)
            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    _uiState.value = UiState.Error("Firestore listener failed: ${e.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val user = snapshot.toObject<LeetCodeUser>()
                    if (user != null) {
                        _uiState.value = UiState.Success(user)
                    } else {
                        _uiState.value = UiState.Error("Failed to parse user data.")
                    }
                }
            }
            try {
                httpClient.get(vercelApiUrl) {
                    parameter("username", username)
                }

            } catch (e: Exception) {

                if (_uiState.value is UiState.Loading) {
                    _uiState.value = UiState.Error("Failed to trigger Vercel function: ${e.message}")
                }
            }
        }
    }
}