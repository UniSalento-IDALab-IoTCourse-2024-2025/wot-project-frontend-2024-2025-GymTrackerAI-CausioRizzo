/*
 * Copyright (c) 2022(-0001) STMicroelectronics.
 * All rights reserved.
 * This software is licensed under terms that can be found in the LICENSE file in
 * the root directory of this software component.
 * If no LICENSE file comes with this software, it is provided AS-IS.
 */
package com.st.blue_sdk.features.carry_position

import com.st.blue_sdk.features.FeatureField
import com.st.blue_sdk.logger.Loggable
import kotlinx.serialization.Serializable
import kotlin.experimental.and

@Serializable
data class CarryPositionInfo(
    val position: FeatureField<CarryPositionType>
) : Loggable {
    override val logHeader: String = position.logHeader

    override val logValue: String = position.logValue
    override val logDoubleValues: List<Double> =
        listOf(getCarryPositionCode(position.value).toDouble())

    override fun toString(): String {
        val sampleValue = StringBuilder()
        sampleValue.append("\t${position.name} = ${position.value}\n")
        return sampleValue.toString()
    }
}

enum class CarryPositionType {
    Unknown,
    OnDesk,
    InHand,
    NearHead,
    ShirtPocket,
    TrousersPocket,
    ArmSwing,
    Error
}

fun getCarryPosition(position: Short) = when ((position and 0x0F).toInt()) {
    0x00 -> CarryPositionType.Unknown
    0x01 -> CarryPositionType.OnDesk
    0x02 -> CarryPositionType.InHand
    0x03 -> CarryPositionType.NearHead
    0x04 -> CarryPositionType.ShirtPocket
    0x05 -> CarryPositionType.TrousersPocket
    0x06 -> CarryPositionType.ArmSwing
    else -> CarryPositionType.Error
}

fun getCarryPositionCode(position: CarryPositionType) = when (position) {
    CarryPositionType.Unknown -> 0x00
    CarryPositionType.OnDesk -> 0x01
    CarryPositionType.InHand -> 0x02
    CarryPositionType.NearHead -> 0x03
    CarryPositionType.ShirtPocket -> 0x04
    CarryPositionType.TrousersPocket -> 0x05
    CarryPositionType.ArmSwing -> 0x06
    CarryPositionType.Error -> 0x0F
}
