package com.st.demo.machine_learning

import kotlin.math.pow

class FeatureExtractor {

    private val bufferSize = 60
    private val bufferX = mutableListOf<Float>()
    private val bufferY = mutableListOf<Float>()
    private val bufferZ = mutableListOf<Float>()

    fun addSample(x: Float, y: Float, z: Float) {
        bufferX.add(x)
        bufferY.add(y)
        bufferZ.add(z)

        if (bufferX.size > bufferSize) bufferX.removeAt(0)
        if (bufferY.size > bufferSize) bufferY.removeAt(0)
        if (bufferZ.size > bufferSize) bufferZ.removeAt(0)
    }

    fun isReady(): Boolean {
        return bufferX.size >= bufferSize
    }

    fun extractFeatures(): FloatArray {
        val meanX = mean(bufferX)
        val meanY = mean(bufferY)
        val meanZ = mean(bufferZ)

        val varX = variance(bufferX, meanX)
        val varY = variance(bufferY, meanY)
        val varZ = variance(bufferZ, meanZ)

        val ptpX = peakToPeak(bufferX)
        val ptpY = peakToPeak(bufferY)
        val ptpZ = peakToPeak(bufferZ)

        val zeroX = zeroCrossing(bufferX)
        val zeroY = zeroCrossing(bufferY)
        val zeroZ = zeroCrossing(bufferZ)

        return floatArrayOf(
            meanX, meanY, meanZ,
            varX, varY, varZ,
            ptpX, ptpY, ptpZ,
            zeroX.toFloat(), zeroY.toFloat(), zeroZ.toFloat()
        )
    }

    private fun mean(data: List<Float>) = data.sum() / data.size

    private fun variance(data: List<Float>, mean: Float) =
        data.map { (it - mean).pow(2) }.sum() / data.size

    private fun peakToPeak(data: List<Float>) = data.maxOrNull()!! - data.minOrNull()!!

    private fun zeroCrossing(data: List<Float>): Int {
        var count = 0
        for (i in 1 until data.size) {
            if ((data[i - 1] >= 0 && data[i] < 0) || (data[i - 1] < 0 && data[i] >= 0)) {
                count++
            }
        }
        return count
    }

    fun reset() {
        bufferX.clear()
        bufferY.clear()
        bufferZ.clear()
    }

}