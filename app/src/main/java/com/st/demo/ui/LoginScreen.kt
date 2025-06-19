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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.st.demo.R
import com.st.demo.viewmodel.LoginViewModel

@Composable
fun LoginScreen(navController: NavController) {

    val context = LocalContext.current
    val viewModel: LoginViewModel = viewModel()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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
            Text("Accedi", fontSize = 30.sp, color = Color.White)

            Spacer(modifier = Modifier.height(36.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(color = Color.White)
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = Color.White) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(color = Color.White)
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (error.isNotEmpty()) {
                Text(error, color = Color.Red, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    error = validateLogin(email, password)
                    if (error.isEmpty()) {
                        viewModel.login(email,password,context,
                            onSuccess = {
                                navController.navigate("dashboard")
                            },
                            onError = { errMsg ->
                                error = errMsg
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                } else {
                    Text("Accedi", fontSize = 18.sp)
                }
            }

            TextButton(
                onClick = {
                    navController.navigate("recovery")
                },
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Password dimenticata?", fontSize = 14.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(20.dp))

            TextButton(
                onClick = {
                    navController.navigate("register")
                },
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Non hai un account? Registrati", fontSize = 14.sp, color = Color.White)
            }
        }
    }
}

fun validateLogin(email: String, password: String): String {
    val emailRegex = Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})")
    return when {
        email.isBlank() -> "Inserisci l'email"
        password.isBlank() -> "Inserisci la password"
        !emailRegex.matches(email) -> "Email non valida"
        else -> ""
    }
}