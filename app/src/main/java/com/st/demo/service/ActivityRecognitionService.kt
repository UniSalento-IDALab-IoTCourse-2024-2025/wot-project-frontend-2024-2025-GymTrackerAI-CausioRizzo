package com.st.demo.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import com.st.demo.machine_learning.FeatureExtractor
import com.st.demo.machine_learning.OnnxClassifier
import com.st.demo.notifications.NotificationUtils
import com.st.demo.training.TrainingSessionManager

class ActivityRecognitionService : Service() {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private lateinit var classifier: OnnxClassifier
    private lateinit var extractor: FeatureExtractor
    //private var lastActivity: String? = null
    private var lastDetectedActivity: String? = null

    companion object {
        const val ACTION_NEW_ACTIVITY = "com.st.demo.NEW_ACTIVITY"
        const val EXTRA_ACTIVITY = "activity_name"
    }

    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val x_mg = x * 1000 / 9.80665f
            val y_mg = y * 1000 / 9.80665f
            val z_mg = z * 1000 / 9.80665f

            extractor.addSample(x_mg, y_mg, z_mg)

            if (extractor.isReady()) {
                val features = extractor.extractFeatures()
                val prediction = classifier.predict(features)
                NotificationUtils.updateNotification(applicationContext, prediction)

                if (prediction != lastDetectedActivity) {
                    lastDetectedActivity?.let {
                        TrainingSessionManager.stopTimer(it)
                    }
                    TrainingSessionManager.increment(prediction)
                    lastDetectedActivity = prediction
                }

                //gestione per plank
                /*if (prediction == "Plank") {
                    TrainingSessionManager.startPlankTimer()
                }
                else {
                    TrainingSessionManager.stopPlankTimer()
                }

                //ripetizioni SOLO per jumpingjack e squatjack.
                if (prediction == "JumpingJack" || prediction == "SquatJack") {
                    if (prediction != lastActivity) {
                        TrainingSessionManager.increment(prediction)
                        lastActivity = prediction
                    }
                }*/

                val intent = Intent(ACTION_NEW_ACTIVITY)
                intent.putExtra(EXTRA_ACTIVITY, prediction)
                sendBroadcast(intent)
                extractor.reset()
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    override fun onCreate() {
        super.onCreate()

        classifier = OnnxClassifier(this)
        extractor = FeatureExtractor()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        startForeground(
            NotificationUtils.NOTIFICATION_ID,
            NotificationUtils.createNotification(this, "IN ATTESA...")
        )

        accelerometer?.let {
            sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(sensorListener)
        classifier.close()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf() // chiude il servizio se l'app viene swippata via
    }
}