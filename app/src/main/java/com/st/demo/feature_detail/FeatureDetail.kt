package com.st.demo.feature_detail

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.st.demo.R
import com.st.blue_sdk.features.acceleration.AccelerationInfo
import com.st.blue_sdk.features.gyroscope.GyroscopeInfo
import com.st.blue_sdk.features.magnetometer.MagnetometerInfo
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import com.st.demo.service.ActivityRecognitionService
import com.st.demo.training.TrainingSessionManager
import com.st.demo.api.ApiClient
import com.st.demo.api.TokenManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.st.demo.model.WorkoutSessionRequest

@SuppressLint("MissingPermission")
@Composable
fun FeatureDetail(
    navController: NavHostController,
    viewModel: FeatureDetailViewModel,
    deviceId: String,
    featureName: String
) {
    val context = LocalContext.current
    val backHandlingEnabled by remember { mutableStateOf(true) }

    // Struttura dati per ogni esercizio
    data class ExerciseSession(
        var repetitions: Int = 0,
        var accumulatedTimeMillis: Long = 0L,
        var startTimeMillis: Long? = null
    )

    val sessions = remember { mutableStateMapOf<String, ExerciseSession>() }
    val activeExercise = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.startCalibration(deviceId, featureName)
    }

    BackHandler(enabled = backHandlingEnabled) {
        viewModel.disconnectFeature(deviceId = deviceId, featureName = featureName)
        navController.popBackStack()
    }

    val features = viewModel.featureUpdates.value

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.gym_wallpaper),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f))
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Funzionalità: $featureName",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (featureName == "HER on SensorTile.box PRO") {

                    features?.let { featureData ->
                        val activity = extractLine(featureData.toString(), "Activity = ") ?: ""

                        if (activity=="Plank"||activity=="JumpingJack"||activity=="SquatJack") {

                            val now = System.currentTimeMillis()

                            if (activeExercise.value != activity) {
                                TrainingSessionManager.increment(activity)
                                activeExercise.value?.let { previous ->
                                    val session = sessions[previous] ?: ExerciseSession()
                                    val elapsed = now - (session.startTimeMillis ?: now)
                                    session.accumulatedTimeMillis += elapsed

                                    saveWorkoutSession(
                                        context,
                                        previous,
                                        TrainingSessionManager.getRepetitions(previous),
                                        session.accumulatedTimeMillis / 1000 // inviamo i secondi al backend
                                    )
                                    sessions[previous] = session
                                }

                                val newSession = sessions.getOrPut(activity) { ExerciseSession() }
                                newSession.startTimeMillis = now
                                newSession.repetitions = TrainingSessionManager.getRepetitions(activity)
                                activeExercise.value = activity
                            } else {
                                val session = sessions.getOrPut(activity) { ExerciseSession() }
                                session.repetitions = TrainingSessionManager.getRepetitions(activity)
                            }
                        }

                        Spacer(modifier = Modifier.height(40.dp))
                        Text("Attività svolta", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(40.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ActivityCard("Plank", R.drawable.ic_plank, activity == "Plank")
                            ActivityCard("JumpingJack", R.drawable.ic_jumping_jack, activity == "JumpingJack")
                            ActivityCard("SquatJack", R.drawable.ic_squat_jack, activity == "SquatJack")
                        }
                    }
                }

                else if (featureName == "HER on Android") {

                    Spacer(modifier = Modifier.height(40.dp))
                    Text("Random Forest su Telefono", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(60.dp))

                    // Avvio il service una sola volta
                    LaunchedEffect(Unit) {
                        val serviceIntent = Intent(context, ActivityRecognitionService::class.java)
                        context.startForegroundService(serviceIntent)
                    }

                    // Stato dell'attività aggiornato tramite BroadcastReceiver
                    val predictedActivity = remember { mutableStateOf("IN ATTESA...") }

                    // Ricevi i broadcast dal service
                    DisposableEffect(Unit) {
                        val receiver = object : BroadcastReceiver() {
                            override fun onReceive(context: Context, intent: Intent) {
                                val activity = intent.getStringExtra(ActivityRecognitionService.EXTRA_ACTIVITY) ?: "IN ATTESA..."
                                predictedActivity.value = activity

                                if (activity=="Plank"||activity=="JumpingJack"||activity=="SquatJack") {

                                    val now = System.currentTimeMillis()

                                    if (activeExercise.value != activity) {
                                        TrainingSessionManager.increment(activity)
                                        activeExercise.value?.let { previous ->
                                            val session = sessions[previous] ?: ExerciseSession()
                                            val elapsed = now - (session.startTimeMillis ?: now)
                                            session.accumulatedTimeMillis += elapsed

                                            saveWorkoutSession(
                                                context,
                                                previous,
                                                TrainingSessionManager.getRepetitions(previous),
                                                session.accumulatedTimeMillis / 1000
                                            )
                                            sessions[previous] = session
                                        }

                                        val newSession = sessions.getOrPut(activity) { ExerciseSession() }
                                        newSession.startTimeMillis = now
                                        newSession.repetitions = TrainingSessionManager.getRepetitions(activity)
                                        activeExercise.value = activity
                                    } else {
                                        val session = sessions.getOrPut(activity) { ExerciseSession() }
                                        session.repetitions = TrainingSessionManager.getRepetitions(activity)
                                    }
                                }
                            }
                        }
                        val filter = IntentFilter(ActivityRecognitionService.ACTION_NEW_ACTIVITY)
                        ContextCompat.registerReceiver(
                            context,
                            receiver,
                            filter,
                            ContextCompat.RECEIVER_NOT_EXPORTED
                        )
                        // Quando esco dalla schermata: deregistra il receiver e stoppa il servizio
                        onDispose {
                            context.unregisterReceiver(receiver)
                            val stopIntent = Intent(context, ActivityRecognitionService::class.java)
                            context.stopService(stopIntent)
                        }
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ActivityCard("Plank", R.drawable.ic_plank, predictedActivity.value == "Plank")
                        ActivityCard("JumpingJack", R.drawable.ic_jumping_jack, predictedActivity.value == "JumpingJack")
                        ActivityCard("SquatJack", R.drawable.ic_squat_jack, predictedActivity.value == "SquatJack")
                    }
                }

                else {
                    Card(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.22f))
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            features?.let { featureData ->
                                // Parsing dinamico
                                val parsedValues = when (featureName) {
                                    "Accelerometer" -> {
                                        val acc = featureData.data as? AccelerationInfo
                                        Triple(
                                            acc?.x?.value ?: 0f,
                                            acc?.y?.value ?: 0f,
                                            acc?.z?.value ?: 0f
                                        )
                                    }
                                    "Gyroscope" -> {
                                        val gyro = featureData.data as? GyroscopeInfo
                                        Triple(
                                            gyro?.x?.value ?: 0f,
                                            gyro?.y?.value ?: 0f,
                                            gyro?.z?.value ?: 0f
                                        )
                                    }
                                    "Magnetometer" -> {
                                        val mag = featureData.data as? MagnetometerInfo
                                        Triple(
                                            mag?.x?.value ?: 0f,
                                            mag?.y?.value ?: 0f,
                                            mag?.z?.value ?: 0f
                                        )
                                    }
                                    else -> Triple(0f, 0f, 0f)
                                }

                                // Etichette unità
                                val unit = when (featureName) {
                                    "Accelerometer" -> "mg"
                                    "Gyroscope" -> "dps"
                                    "Magnetometer" -> "mGa"
                                    else -> ""
                                }

                                Text("Dati in tempo reale:", fontSize = 18.sp, color = Color.White)
                                Divider(color = Color.White.copy(alpha = 0.2f), thickness = 0.7.dp, modifier = Modifier.padding(vertical = 10.dp))
                                Text("X = ${parsedValues.first} $unit", fontSize = 18.sp, color = Color.White)
                                Text("Y = ${parsedValues.second} $unit", fontSize = 18.sp, color = Color.White)
                                Text("Z = ${parsedValues.third} $unit", fontSize = 18.sp, color = Color.White)
                            }
                        }
                    }
                }

            }

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth(0.65f).height(45.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BFA5)),
                shape = RoundedCornerShape(30.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Indietro", fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }

    LaunchedEffect(true) {
        viewModel.observeFeature(deviceId = deviceId, featureName = featureName)
        viewModel.sendExtendedCommand(featureName = featureName, deviceId = deviceId)
    }
}

@Composable
fun ActivityCard(label: String, iconRes: Int, isSelected: Boolean) {
    val backgroundColor = if (isSelected) Color(0xFF00BFA5) else Color.White.copy(alpha = 0.2f)
    val textColor = Color.White

    Card(
        modifier = Modifier.fillMaxWidth(0.8f).height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, fontSize = 16.sp, color = textColor)
        }
    }
}

fun extractLine(fullString: String, keyword: String): String? {
    return fullString.lineSequence()
        .firstOrNull { it.contains(keyword) }
        ?.substringAfter(keyword)
        ?.trim()
}

fun formatDateString(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
        val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}

fun saveWorkoutSession(
    context: Context,
    activity: String,
    repetitions: Int,
    timeInSeconds: Long
) {
    val token = TokenManager.getToken(context)
    val workoutDate = TrainingSessionManager.getTodayDateString()  // yyyy-MM-dd

    val request = WorkoutSessionRequest(
        exerciseType = activity,
        repetitions = repetitions,
        timeInSeconds = timeInSeconds,
        workoutDate = workoutDate
    )

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = ApiClient.apiService.saveWorkout("Bearer ${token.orEmpty()}", request)
            if (response.isSuccessful) println("✅ Workout salvato con successo")
            else println("❌ Errore salvataggio workout: ${response.code()}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}