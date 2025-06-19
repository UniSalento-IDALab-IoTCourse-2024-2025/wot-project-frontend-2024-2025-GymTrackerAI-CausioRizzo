package com.st.demo

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.st.demo.audio.AudioScreen
import com.st.demo.device_detail.BleDeviceDetail
import com.st.demo.device_list.BleDeviceList
import com.st.demo.feature_detail.FeatureDetail
import com.st.demo.ui.DashboardScreen
import com.st.demo.ui.HistoryActivityScreen
import com.st.demo.ui.WelcomeScreen
import com.st.demo.ui.LoginScreen
import com.st.demo.ui.RegistrationScreen
import com.st.demo.ui.InfoScreen
import com.st.demo.training.TrainingSessionManager
import com.st.demo.ui.RecoveryPasswordScreen
import com.st.demo.ui.theme.StDemoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle? ) {
        super.onCreate(savedInstanceState)

        //inizializza il training manager
        TrainingSessionManager.initialize(applicationContext)

        //TrainingSessionManager.checkAndResetDaily()

        //per avere i dati in dashboard aggiornati all'allenamento del giorno
        TrainingSessionManager.loadTodayStateFromHistory()

        // Disabilita il contrasto navigation bar per versioni recenti
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        // Attiviamo l'immersive mode
        enableFullScreen()

        setContent {
            MainScreen()
        }
    }

    private fun enableFullScreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

@Composable
private fun MainScreen() {
    val navController = rememberNavController()

    StDemoTheme {
        NavHost(
            navController = navController,
            startDestination = "welcome"
        ) {
            composable("welcome") { WelcomeScreen(navController = navController) }
            composable("login") { LoginScreen(navController = navController) }
            composable("register") { RegistrationScreen(navController = navController) }
            composable("recovery") { RecoveryPasswordScreen(navController = navController) }
            composable("dashboard") { DashboardScreen(navController) }
            composable("history") { HistoryActivityScreen(navController) }
            composable("info") { InfoScreen(navController = navController) }
            composable("list") {
                BleDeviceList(
                    viewModel = hiltViewModel(),
                    navController = navController
                )
            }
            composable(
                route = "detail/{deviceId}",
                arguments = listOf(navArgument("deviceId") { type = NavType.StringType })
            ) { backStackEntry ->
                backStackEntry.arguments?.getString("deviceId")?.let { deviceId ->
                    BleDeviceDetail(
                        viewModel = hiltViewModel(),
                        navController = navController,
                        deviceId = deviceId
                    )
                }
            }
            composable(
                route = "feature/{deviceId}/{featureName}",
                arguments = listOf(
                    navArgument("deviceId") { type = NavType.StringType },
                    navArgument("featureName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                backStackEntry.arguments?.getString("deviceId")?.let { deviceId ->
                    backStackEntry.arguments?.getString("featureName")?.let { featureName ->
                        FeatureDetail(
                            viewModel = hiltViewModel(),
                            navController = navController,
                            deviceId = deviceId,
                            featureName = featureName
                        )
                    }
                }
            }
            composable(
                route = "audio/{deviceId}",
                arguments = listOf(navArgument("deviceId") { type = NavType.StringType })
            ) { backStackEntry ->
                backStackEntry.arguments?.getString("deviceId")?.let { deviceId ->
                    AudioScreen(
                        viewModel = hiltViewModel(),
                        navController = navController,
                        deviceId = deviceId
                    )
                }
            }
        }
    }
}