package com.st.demo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st.demo.api.ApiClient
import com.st.demo.model.ResetPasswordRequest
import kotlinx.coroutines.launch

class ResetPasswordViewModel : ViewModel() {

    fun resetPassword(request: ResetPasswordRequest, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val response = ApiClient.apiService.resetPassword(request)
            if (response.isSuccessful) {
                onSuccess()
            } else {
                onError("Errore durante il reset della password")
            }
        }
    }
}