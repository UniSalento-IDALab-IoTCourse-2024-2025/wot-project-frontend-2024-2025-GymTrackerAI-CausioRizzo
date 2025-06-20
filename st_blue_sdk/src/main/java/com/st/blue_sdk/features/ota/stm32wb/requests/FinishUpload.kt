/*
 * Copyright (c) 2022(-0001) STMicroelectronics.
 * All rights reserved.
 * This software is licensed under terms that can be found in the LICENSE file in
 * the root directory of this software component.
 * If no LICENSE file comes with this software, it is provided AS-IS.
 */
package com.st.blue_sdk.features.ota.stm32wb.requests

import com.st.blue_sdk.features.Feature
import com.st.blue_sdk.features.FeatureCommand
import com.st.blue_sdk.features.ota.stm32wb.OTAControl.Companion.UPLOAD_FINISHED_COMMAND

class FinishUpload(feature: Feature<*>, commandId: Byte = UPLOAD_FINISHED_COMMAND) :
    FeatureCommand(feature, commandId)