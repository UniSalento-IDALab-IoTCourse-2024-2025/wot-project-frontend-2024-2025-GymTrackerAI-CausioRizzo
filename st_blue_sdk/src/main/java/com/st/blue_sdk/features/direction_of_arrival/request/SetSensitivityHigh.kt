/*
 * Copyright (c) 2022(-0001) STMicroelectronics.
 * All rights reserved.
 * This software is licensed under terms that can be found in the LICENSE file in
 * the root directory of this software component.
 * If no LICENSE file comes with this software, it is provided AS-IS.
 */
package com.st.blue_sdk.features.direction_of_arrival.request

import com.st.blue_sdk.features.FeatureCommand
import com.st.blue_sdk.features.direction_of_arrival.DirectionOfArrival

class SetSensitivityHigh(feature: DirectionOfArrival) :
    FeatureCommand(feature = feature, commandId = DirectionOfArrival.COMMAND_SENSITIVITY_HIGH)