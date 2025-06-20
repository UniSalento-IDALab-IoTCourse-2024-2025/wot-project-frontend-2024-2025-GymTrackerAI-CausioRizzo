/*
 * Copyright (c) 2022(-0001) STMicroelectronics.
 * All rights reserved.
 * This software is licensed under terms that can be found in the LICENSE file in
 * the root directory of this software component.
 * If no LICENSE file comes with this software, it is provided AS-IS.
 */
package com.st.blue_sdk.features.mems_gesture

import com.st.blue_sdk.features.FeatureField
import com.st.blue_sdk.logger.Loggable
import kotlinx.serialization.Serializable

@Serializable
data class MemsGestureInfo(
    val gesture: FeatureField<MemsGestureType>,
) : Loggable {
    override val logHeader: String = gesture.logHeader

    override val logValue: String = gesture.logValue


    override val logDoubleValues: List<Double> =
        listOf(MemsGesture.getGestureTypeCode(gesture.value).toDouble())

    override fun toString(): String {
        val sampleValue = StringBuilder()
        sampleValue.append("\t${gesture.name} = ${gesture.value}\n")
        return sampleValue.toString()
    }
}

enum class MemsGestureType {
    Unknown,
    PickUp,
    Glance,
    WakeUp,
    Error
}
