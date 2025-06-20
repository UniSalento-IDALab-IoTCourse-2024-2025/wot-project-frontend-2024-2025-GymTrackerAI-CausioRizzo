package com.st.blue_sdk.features.extended.binary_content

import com.st.blue_sdk.features.FeatureField
import com.st.blue_sdk.logger.Loggable
import kotlinx.serialization.Serializable

@Serializable
data class RawData(
    val data: FeatureField<ByteArray?>,
    val bytesRec: FeatureField<Int>,
    val numberPackets: FeatureField<Int>
) : Loggable {
    override val logHeader = data.logHeader
    override val logValue = data.logValue

    override val logDoubleValues: List<Double> = listOf()
}
