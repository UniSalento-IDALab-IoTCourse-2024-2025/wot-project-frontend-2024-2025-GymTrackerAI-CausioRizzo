package com.st.demo.device_detail

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.st.blue_sdk.models.NodeState
import com.st.demo.R

@SuppressLint("MissingPermission")
@Composable
fun BleDeviceDetail(
    navController: NavHostController,
    viewModel: BleDeviceDetailViewModel,
    deviceId: String
) {
    LaunchedEffect(key1 = deviceId) {
        viewModel.connect(deviceId = deviceId)
    }

    val bleDevice = viewModel.bleDevice(deviceId = deviceId).collectAsState(null)
    val features = viewModel.features.collectAsState()

    if (bleDevice.value?.connectionStatus?.current == NodeState.Ready) {
        viewModel.getFeatures(deviceId = deviceId)
    }

    BackHandler {
        viewModel.disconnect(deviceId = deviceId)
        navController.popBackStack()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.gym_wallpaper),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header BLE
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_ble),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Dispositivo collegato:", fontSize = 16.sp, color = Color.White)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = bleDevice.value?.device?.name?.uppercase() ?: "",
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Stato attuale (solo icona visibile se READY)
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (bleDevice.value?.connectionStatus?.current == NodeState.Ready) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_status),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        colorFilter = ColorFilter.tint(Color(0xFF00FF00))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text("Stato attuale:", fontSize = 16.sp, color = Color.White)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = bleDevice.value?.connectionStatus?.current?.name?.uppercase() ?: "",
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(18.dp))

            if (bleDevice.value?.connectionStatus?.current == NodeState.Ready) {
                Text("Funzioni disponibili:", fontSize = 16.sp, color = Color.White)
                Spacer(modifier = Modifier.height(33.dp))

                val filteredFeatures = features.value
                    .filter { it.isDataNotifyFeature }
                    .filter {
                        it.name in listOf("Accelerometer", "Gyroscope", "Magnetometer", "HER on SensorTile.box PRO")
                    }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(filteredFeatures) { feature ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .clickable {
                                    navController.navigate("feature/${deviceId}/${feature.name}")
                                },
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
                        ) {
                            Text(
                                modifier = Modifier.padding(15.dp),
                                text = when (feature.name) {
                                    "Accelerometer" -> "Accelerometro"
                                    "Gyroscope" -> "Giroscopio"
                                    "Magnetometer" -> "Magnetometro"
                                    "HER on SensorTile.box PRO" -> "HER su SensorTile.box PRO"
                                    else -> feature.name
                                },
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }
                    }
                    item {
                        FeatureStaticItemClickable("HER su Android") {
                            navController.navigate("feature/${deviceId}/HER on Android")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(85.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = { navController.navigate("dashboard") },
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BFA5)),
                        modifier = Modifier.height(45.dp).weight(1f)
                    ) {
                        Image(painter = painterResource(id = R.drawable.ic_dashboard), contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Dashboard", fontSize = 14.sp, color = Color.White)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            viewModel.disconnect(deviceId = deviceId)
                            navController.popBackStack()
                        },
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        modifier = Modifier.height(45.dp).weight(1f)
                    ) {
                        Image(painter = painterResource(id = R.drawable.ic_disconnect), contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Disconnetti", fontSize = 14.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun FeatureStaticItemClickable(title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
    ) {
        Text(
            modifier = Modifier.padding(12.dp),
            text = title,
            color = Color.White,
            fontSize = 16.sp
        )
    }
}