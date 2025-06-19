package com.st.demo.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st.demo.api.ApiClient
import com.st.demo.api.TokenManager
import com.st.demo.model.LoginRequest
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    fun login(email: String, password: String, context: Context, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        TokenManager.saveToken(context, loginResponse.token)
                        onSuccess()
                    } ?: onError("Token mancante")
                } else {
                    onError("Credenziali errate")
                }
            } catch (e: Exception) {
                onError("Errore di connessione")
            }
        }
    }
}