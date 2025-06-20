/*
 * Copyright (c) 2022(-0001) STMicroelectronics.
 * All rights reserved.
 * This software is licensed under terms that can be found in the LICENSE file in
 * the root directory of this software component.
 * If no LICENSE file comes with this software, it is provided AS-IS.
 */
package com.st.blue_sdk.features.carry_position

import com.st.blue_sdk.features.*
import com.st.blue_sdk.utils.NumberConversion

class CarryPosition(
    name: String = NAME,
    type: Type = Type.STANDARD,
    isEnabled: Boolean,
    identifier: Int
) : Feature<CarryPositionInfo>(
    name = name,
    type = type,
    isEnabled = isEnabled,
    identifier = identifier
) {

    companion object {
        const val NAME = "Carry Position"
    }

    override fun extractData(
        timeStamp: Long,
        data: ByteArray,
        dataOffset: Int
    ): FeatureUpdate<CarryPositionInfo> {
        require(data.size - dataOffset > 0) { "There are no bytes available to read for $name feature" }

        val carryPosition = CarryPositionInfo(
            position = FeatureField(
                name = "Carry Position",
                value = getCarryPosition(NumberConversion.byteToUInt8(data, dataOffset))
            )
        )

        return FeatureUpdate(
            featureName = name,
            timeStamp = timeStamp,
            rawData = data,
            readByte = 1,
            data = carryPosition
        )
    }

    override fun packCommandData(featureBit: Int?, command: FeatureCommand): ByteArray? = null

    override fun parseCommandResponse(data: ByteArray): FeatureResponse? = null
}