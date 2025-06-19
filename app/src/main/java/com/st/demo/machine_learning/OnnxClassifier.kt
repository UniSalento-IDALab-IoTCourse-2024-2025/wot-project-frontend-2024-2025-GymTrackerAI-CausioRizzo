package com.st.demo.machine_learning

import android.content.Context
import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import ai.onnxruntime.OrtException
import java.nio.FloatBuffer

class OnnxClassifier(context: Context) {

    private var session: OrtSession
    private var env: OrtEnvironment
    private var inputName: String

    init {
        env = OrtEnvironment.getEnvironment()
        val modelBytes = context.assets.open("random_forest.onnx").readBytes()
        session = env.createSession(modelBytes)
        inputName = session.inputNames.iterator().next()
    }

    fun predict(features: FloatArray): String {
        return try {
            val tensor = OnnxTensor.createTensor(env, FloatBuffer.wrap(features), longArrayOf(1, 12))
            val results = session.run(mapOf(inputName to tensor))
            val output = results[0].value as Array<String>
            output[0]  // restituisce direttamente la label predetta
        } catch (e: OrtException) {
            "Unknown"
        }
    }

    fun close() {
        session.close()
        env.close()
    }
}