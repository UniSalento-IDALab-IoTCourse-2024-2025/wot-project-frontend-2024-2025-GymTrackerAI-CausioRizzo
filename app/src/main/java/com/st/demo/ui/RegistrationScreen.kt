package com.st.demo.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.st.demo.R
import com.st.demo.model.RegisterRequest
import com.st.demo.viewmodel.RegisterViewModel

@Composable
fun RegistrationScreen(navController: NavController) {

    val viewModel: RegisterViewModel = viewModel()

    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.gym_wallpaper),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Registrati", fontSize = 26.sp, color = Color.White)

            Spacer(modifier = Modifier.height(28.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(color = Color.White)
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = surname,
                onValueChange = { surname = it },
                label = { Text("Cognome", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(color = Color.White)
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(color = Color.White)
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = Color.White) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(color = Color.White)
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Conferma Password", color = Color.White) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(color = Color.White)
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (error.isNotEmpty()) {
                Text(error, color = Color.Red, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(14.dp))
            }

            Button(
                onClick = {
                    error = validateRegistration(name, surname, email, password, confirmPassword)
                    if (error.isEmpty()) {
                        isLoading = true
                        val request = RegisterRequest(name, surname, email, password, confirmPassword)
                        viewModel.register(request,
                            onSuccess = {
                                isLoading = false
                                navController.navigate("login")
                            },
                            onError = { errorMsg ->
                                isLoading = false
                                error = errorMsg
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Registrati", fontSize = 18.sp)
            }

            if (isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(color = Color.White)
            }

            Spacer(modifier = Modifier.height(32.dp))

            TextButton(onClick = { navController.navigate("login") }) {
                Text("Hai già un account? Accedi", fontSize = 14.sp, color = Color.White)
            }
        }
    }
}

fun validateRegistration(
    name: String, surname: String, email: String,
    password: String, confirmPassword: String
): String {
    val emailValid = email.contains("@") && email.contains(".")
    fun isPasswordValid(pw: String): Boolean {
        val hasUppercase = pw.any { it.isUpperCase() }
        val hasDigit = pw.any { it.isDigit() }
        val hasLength = pw.length >= 8
        return hasUppercase && hasDigit && hasLength
    }

    return when {
        name.isBlank() -> "Inserisci il nome"
        surname.isBlank() -> "Inserisci il cognome"
        !emailValid -> "Inserisci una email valida"
        password != confirmPassword -> "Le password non coincidono"
        !isPasswordValid(password) -> "Password non valida (maiuscola e numeri, almeno 8 caratteri)"
        else -> ""
    }
}