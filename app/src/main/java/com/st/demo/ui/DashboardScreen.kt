package com.st.demo.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.st.demo.R
import com.st.demo.training.TrainingSessionManager
import com.st.demo.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(navController: NavController) {

    val context = LocalContext.current
    val viewModel: DashboardViewModel = viewModel()

    //stoppo i timer dei tre esercizi ogni volta che vado sulla dashboard
    LaunchedEffect(Unit) {
        TrainingSessionManager.stopTimer("Plank")
        TrainingSessionManager.stopTimer("JumpingJack")
        TrainingSessionManager.stopTimer("SquatJack")

        //salva lo stato aggiornato prima di visualizzare i dati
        TrainingSessionManager.saveTodayToHistory(TrainingSessionManager.getTodayDateString())
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Sfondo
        Image(
            painter = painterResource(id = R.drawable.gym_wallpaper),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Overlay scuro
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Titolo superiore
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "La tua Dashboard Personale",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Card centrale più elegante
                Card(
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.90f)
                        .align(Alignment.CenterHorizontally),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.22f))
                ) {
                    Column(modifier = Modifier.padding(22.dp)) {
                        Text(
                            text = "Attività svolte oggi",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Divider(color = Color.White.copy(alpha = 0.2f), thickness = 0.7.dp, modifier = Modifier.padding(vertical = 10.dp))

                        ActivityRowDashboard(
                            iconRes = R.drawable.ic_plank,
                            title = "Plank",
                            duration = "Tempo: ${TrainingSessionManager.getTime("Plank")}",
                            repetitions = null, //plank non mostra ripetizioni
                            percentage = TrainingSessionManager.getDailyComparison("Plank")
                        )

                        Divider(color = Color.White.copy(alpha = 0.2f), thickness = 0.7.dp, modifier = Modifier.padding(vertical = 10.dp))

                        ActivityRowDashboard(
                            iconRes = R.drawable.ic_jumping_jack,
                            title = "JumpingJack",
                            duration = "Tempo: ${TrainingSessionManager.getTime("JumpingJack")}",
                            repetitions = TrainingSessionManager.getRepetitions("JumpingJack"),
                            percentage = TrainingSessionManager.getDailyComparison("JumpingJack")
                        )

                        Divider(color = Color.White.copy(alpha = 0.2f), thickness = 0.7.dp, modifier = Modifier.padding(vertical = 10.dp))

                        ActivityRowDashboard(
                            iconRes = R.drawable.ic_squat_jack,
                            title = "SquatJack",
                            duration = "Tempo: ${TrainingSessionManager.getTime("SquatJack")}",
                            repetitions = TrainingSessionManager.getRepetitions("SquatJack"),
                            percentage = TrainingSessionManager.getDailyComparison("SquatJack")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Pulsanti in basso migliorati visivamente
            Column(
                modifier = Modifier.fillMaxWidth().padding(bottom = 18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Allenati
                Button(
                    onClick = { navController.navigate("list") },
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BFA5)),
                    modifier = Modifier.fillMaxWidth(0.70f).height(50.dp)
                ) {
                    ActivityRow(iconRes = R.drawable.ic_workout, text = "Allenati!", Color.Black, size = 25.dp)
                }

                // Storico Attività
                Button(
                    onClick = { navController.navigate("history") },
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BFA5)),
                    modifier = Modifier.fillMaxWidth(0.70f).height(50.dp)
                ) {
                    ActivityRow(iconRes = R.drawable.ic_archive, text = "Storico Attività", Color.Black, size = 20.dp)
                }

                // Logout
                Button(
                    onClick = {
                        viewModel.logout(context,
                            onSuccess = {
                                navController.navigate("welcome")
                            },
                            onError = { errorMsg ->
                                Log.e("Logout", errorMsg)
                            }
                        )
                    },
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    modifier = Modifier.fillMaxWidth(0.70f).height(50.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Logout", fontSize = 16.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityRowDashboard(
    iconRes: Int,
    title: String,
    duration: String,
    repetitions: Int?,
    percentage: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = duration,
                color = Color.White,
                fontSize = 14.sp
            )
            repetitions?.let {
                Text(
                    text = "Ripetizioni: $it",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
            Text(
                text = "\n" + percentage,
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}