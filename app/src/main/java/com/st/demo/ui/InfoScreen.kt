package com.st.demo.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.st.demo.R

@Composable
fun InfoScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Sfondo immagine
        Image(
            painter = painterResource(id = R.drawable.gym_wallpaper),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Overlay scuro
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Info GymTrackerAI",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            // Primo riquadro "A cosa serve"
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(0.9f),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.22f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_info),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "A cosa serve?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "GymTrackerAI monitora e riconosce le attività fisiche tramite SensorTile.Box PRO e smartphone Android, grazie all’utilizzo di algoritmi di Machine Learning per il conteggio ripetizioni e la misurazione del tempo. \nLe attività svolte sono:",
                        color = Color.White,
                        fontSize = 15.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    ActivityRow(
                        iconRes = R.drawable.ic_plank,
                        text = "Plank",
                        size1 = Color.White,
                        size = 30.dp
                    )
                    ActivityRow(
                        iconRes = R.drawable.ic_jumping_jack,
                        text = "JumpingJack",
                        size1 = Color.White,
                        size = 28.dp
                    )
                    ActivityRow(
                        iconRes = R.drawable.ic_squat_jack,
                        text = "SquatJack",
                        size1 = Color.White,
                        size = 28.dp
                    )
                }
            }

            // Secondo riquadro "Quali funzionalità offre"
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(0.9f),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.22f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_function),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Quali funzionalità offre?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "• Riconoscimento in tempo reale delle attività sopra elencate.\n" +
                                "• Conteggio preciso delle ripetizioni e del tempo di ogni esercizio.\n" +
                                "• Storico giornaliero delle performance.\n" +
                                "• Supporto sia per SensorTile.Box PRO che per sensori integrati nello smartphone.",
                        color = Color.White,
                        fontSize = 15.sp
                    )
                }
            }

            // Pulsante Indietro con colore fitness (usiamo lo stesso colore di HomeScreen)
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BFA5)) // stesso colore usato su Accedi/Registrati
            ) {
                ActivityRow(iconRes = R.drawable.ic_back, text = "Indietro", Color.White, size = 20.dp)
            }
        }
    }
}

@Composable
fun ActivityRow(iconRes: Int, text: String, size1: Color, size: Dp) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(size)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = Color.White,
            fontSize = 15.sp
        )
    }
}
