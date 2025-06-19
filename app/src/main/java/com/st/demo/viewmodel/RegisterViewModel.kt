package com.st.demo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st.demo.api.ApiClient
import com.st.demo.model.RegisterRequest
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    fun register(request: RegisterRequest, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.register(request)
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError(response.errorBody()?.string() ?: "Errore durante la registrazione")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Errore di rete")
            }
        }
    }
}