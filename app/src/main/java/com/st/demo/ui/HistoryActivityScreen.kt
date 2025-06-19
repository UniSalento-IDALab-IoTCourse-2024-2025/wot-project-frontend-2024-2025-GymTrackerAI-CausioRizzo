package com.st.demo.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.st.demo.R
import com.st.demo.api.ApiClient
import com.st.demo.api.TokenManager
import com.st.demo.training.TrainingSessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryActivityScreen(navController: NavHostController) {

    val historyMap = remember { TrainingSessionManager.getHistory().toSortedMap(reverseOrder()) }
    val scrollState = rememberScrollState()
    val historyState = remember { mutableStateOf(TrainingSessionManager.getHistory().entries.toList().sortedByDescending { it.key }) }

    val context = LocalContext.current
    val token = TokenManager.getToken(context)

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.gym_wallpaper),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.45f)))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("Storico Attività", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))

            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            historyState.value.forEach { (date, record) ->

                val displayDate = runCatching {
                    val parsedDate = inputFormat.parse(date)
                    outputFormat.format(parsedDate ?: Date())
                }.getOrElse { date }

                HistoryItem(
                    date = displayDate,
                    plank = convertSecondsToTime(record.plankTime),
                    jumpingJack = convertSecondsToTime(record.jumpingJackTime),
                    jumpingJackReps = record.jumpingJackReps,
                    squatJack = convertSecondsToTime(record.squatJackTime),
                    squatJackReps = record.squatJackReps,
                    onDelete = {
                        TrainingSessionManager.deleteDay(date)
                        historyState.value = TrainingSessionManager.getHistory().entries.toList().sortedByDescending { it.key }

                        // Converto la data per il backend
                        val backendDate = convertDateForBackend(date)

                        // Chiamata al backend per eliminare
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val response = ApiClient.apiService.deleteWorkoutByDate("Bearer $token", backendDate)
                                if (response.isSuccessful) {
                                    println("Eliminazione lato backend avvenuta con successo.")
                                } else {
                                    println("Errore eliminazione backend: ${response.code()}")
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            //cambiare valore a 300.dp circa se riquadri aumentano (c'è scroll comunque, scendi giù in automatico)
            Spacer(modifier = Modifier.height(110.dp))

            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BFA5)),
                modifier = Modifier.fillMaxWidth(0.70f).height(50.dp),
                shape = RoundedCornerShape(30.dp)
            ) {
                ActivityRow(iconRes = R.drawable.ic_back, text = "Indietro", Color.White, size = 20.dp)
            }
        }
    }
}

fun convertSecondsToTime(totalSeconds: Long): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d min %02d sec", minutes, seconds)
}

@Composable
fun HistoryItem(
    date: String,
    plank: String,
    jumpingJack: String,
    jumpingJackReps: Int,
    squatJack: String,
    squatJackReps: Int,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.22f)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Attività svolta in data: $date", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            //Spacer(modifier = Modifier.height(29.dp))
            Divider(color = Color.White.copy(alpha = 0.2f), thickness = 0.7.dp, modifier = Modifier.padding(vertical = 10.dp))

            ActivityRowHistory(R.drawable.ic_plank, "Plank", plank, 0)
            //Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.White.copy(alpha = 0.2f), thickness = 0.7.dp, modifier = Modifier.padding(vertical = 10.dp))
            ActivityRowHistory(R.drawable.ic_jumping_jack, "JumpingJack", jumpingJack, jumpingJackReps)
            //Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.White.copy(alpha = 0.2f), thickness = 0.7.dp, modifier = Modifier.padding(vertical = 10.dp))
            ActivityRowHistory(R.drawable.ic_squat_jack, "SquatJack", squatJack, squatJackReps)

            TextButton (onClick = onDelete) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text("Elimina", fontSize = 13.sp, color = Color.Red)
                    Image (
                        painter = painterResource(id = R.drawable.ic_trash),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        colorFilter = ColorFilter.tint(Color.Red)
                    )
                }
            }
        }
    }
}

@Composable
fun ActivityRowHistory(
    iconRes: Int,
    title: String,
    duration: String,
    repetitions: Int
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text("$title: $duration", color = Color.White, fontSize = 15.sp)
            if (title=="Plank") {
                Text("Nessuna ripetizione.", color = Color.White, fontSize = 15.sp)
            } else {
                Text("Ripetizioni: $repetitions", color = Color.White, fontSize = 15.sp)
            }
        }
    }
}

fun convertDateForBackend(displayDate: String): String {
    return try {
        val formatterInput = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formatterOutput = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = formatterInput.parse(displayDate)
        formatterOutput.format(date!!)
    } catch (e: Exception) {
        e.printStackTrace()
        displayDate // fallback
    }
}