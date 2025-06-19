package com.st.demo.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st.demo.api.ApiClient
import com.st.demo.api.TokenManager
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    fun logout(context: Context, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val token = TokenManager.getToken(context)
                if (token == null) {
                    onError("Token mancante")
                    return@launch
                }

                val response = ApiClient.apiService.logout("Bearer $token")
                if (response.isSuccessful) {
                    TokenManager.clearToken(context)
                    onSuccess()
                } else {
                    onError("Errore durante il logout")
                }
            } catch (e: Exception) {
                onError("Errore di connessione")
            }
        }
    }
}