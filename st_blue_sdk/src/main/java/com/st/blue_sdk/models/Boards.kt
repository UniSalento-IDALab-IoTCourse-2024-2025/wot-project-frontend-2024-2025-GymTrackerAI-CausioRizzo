/*
 * Copyright (c) 2022(-0001) STMicroelectronics.
 * All rights reserved.
 * This software is licensed under terms that can be found in the LICENSE file in
 * the root directory of this software component.
 * If no LICENSE file comes with this software, it is provided AS-IS.
 */
package com.st.blue_sdk.models

object Boards {

    enum class Model {
        /** unknown board type  */
        GENERIC,

        /** STEVAL-WESU1 board  */
        STEVAL_WESU1,

        /** SensorTile board  */
        SENSOR_TILE,

        /** Blue Coin board  */
        BLUE_COIN,

        /** BlueNRG1 & BlueNRG2 ST eval board */
        STEVAL_IDB008VX,

        /** ST BlueNRG-Tile eval board */
        STEVAL_BCN002V1,

        /** SensorTile.Box board  */
        SENSOR_TILE_BOX,

        /** B-L475E-IOT01A board  */
        DISCOVERY_IOT01A,

        /** STEVAL-STWINKIT1 board  */
        STEVAL_STWINKIT1,

        /** STEVAL-STWINKT1B board  */
        STEVAL_STWINKT1B,

        /** B-L475E-IOT01A IoT Node 1.5  */
        B_L475E_IOT01A,

        /** B-U585I-IOT02A IoT Node 2.0  */
        B_U585I_IOT02A,

        /** Astra1 (WL + WB  */
        ASTRA1,

        /** SensorTile.Box PRO  */
        SENSOR_TILE_BOX_PRO,

        /** SensorTile.Box PROB  */
        SENSOR_TILE_BOX_PROB,

        /** STWIN.BOX  */
        STWIN_BOX,

        /** STWIN.BOXB  */
        STWIN_BOXB,

        /** Proteus  */
        PROTEUS,

        /** STDES-CBMLoRaBLE  */
        STDES_CBMLORABLE,

        /** WB Boards  */
        WB55_NUCLEO_BOARD, //0x81
        WB5M_DISCOVERY_BOARD, //0x82
        WB55_USB_DONGLE_BOARD, //0x83
        WB15_NUCLEO_BOARD, //0x84
        WB1M_DISCOVERY_BOARD, //0x85
        WBA5X_NUCLEO_BOARD, //0x8B
        WBA_DISCOVERY_BOARD, //0x8C
        WBA6_NUCLEO_BOARD, //0x8E

        /** Nucleo with STM32WB0X */
        WB0X_NUCLEO_BOARD, //0x8D
        WB05_NUCLEO_BOARD, //0x8F

        /** NUCLEO boards  */
        NUCLEO, NUCLEO_F401RE, NUCLEO_L476RG, NUCLEO_L053R8, NUCLEO_F446RE,NUCLEO_U575ZIQ,NUCLEO_U5A5ZJQ
    } //Type

    enum class Family {
        OTHER_FAMILY,
        NUCLEO_FAMILY,
        IOT_FAMILY,
        BLUENRG_FAMILY,
        WB_BASED_FAMILY,
        WB_FAMILY,
        WBA_FAMILY
    }

    fun containsRemoteFeatures(id:Int,sDkVersion: Int): Boolean {
        //Used only for SDK V1
        return ((sDkVersion==1) && (id==0x81))
    }

    fun getFamilyFromModel(model: Model): Family {
        return when(model) {

            Model.SENSOR_TILE,
            Model.BLUE_COIN,
            Model.SENSOR_TILE_BOX,
            Model.STEVAL_STWINKIT1,
            Model.STEVAL_STWINKT1B,
            Model.SENSOR_TILE_BOX_PRO,
            Model.SENSOR_TILE_BOX_PROB,
            Model.STWIN_BOX,
            Model.STWIN_BOXB,
            Model.WB05_NUCLEO_BOARD-> Family.BLUENRG_FAMILY

            Model.DISCOVERY_IOT01A,
            Model.B_L475E_IOT01A,
            Model.B_U585I_IOT02A -> Family.IOT_FAMILY

            Model.ASTRA1,
            Model.STDES_CBMLORABLE,
            Model.PROTEUS -> Family.WB_BASED_FAMILY

            Model.NUCLEO,
            Model.NUCLEO_F401RE,
            Model.NUCLEO_L476RG,
            Model.NUCLEO_L053R8,
            Model.NUCLEO_F446RE,
            Model.NUCLEO_U575ZIQ,
            Model.NUCLEO_U5A5ZJQ-> Family.NUCLEO_FAMILY

            Model.WB55_NUCLEO_BOARD,
            Model.WB5M_DISCOVERY_BOARD,
            Model.WB55_USB_DONGLE_BOARD,
            Model.WB15_NUCLEO_BOARD,
            Model.WB1M_DISCOVERY_BOARD,
                -> Family.WB_FAMILY

            Model.WBA5X_NUCLEO_BOARD,
            Model.WBA_DISCOVERY_BOARD,
            Model.WBA6_NUCLEO_BOARD,
                -> Family.WBA_FAMILY

            Model.WB0X_NUCLEO_BOARD,
            Model.GENERIC,
            Model.STEVAL_WESU1,
            Model.STEVAL_IDB008VX,
            Model.STEVAL_BCN002V1 -> Family.OTHER_FAMILY
        }
    }

    fun getModelFromIdentifier(id: Int,sDkVersion: Int): Model {
        val retVal: Model =
            if(sDkVersion==1) {
                when (val temp = (id and 0xFF)) {
                    0x01 -> Model.STEVAL_WESU1
                    0x02 -> Model.SENSOR_TILE
                    0x03 -> Model.BLUE_COIN
                    0x04 -> Model.STEVAL_IDB008VX
                    0x05 -> Model.STEVAL_BCN002V1
                    0x06 -> Model.SENSOR_TILE_BOX
                    0x07 -> Model.DISCOVERY_IOT01A
                    0x08 -> Model.STEVAL_STWINKIT1
                    0x09 -> Model.STEVAL_STWINKT1B
                    0x0A -> Model.B_L475E_IOT01A
                    0x0B -> Model.B_U585I_IOT02A
                    0x0C -> Model.ASTRA1
                    0x0D -> Model.SENSOR_TILE_BOX_PRO
                    0x0E -> Model.STWIN_BOX
                    0x0F -> Model.PROTEUS
                    0x10 -> Model.STDES_CBMLORABLE
                    0x11 -> Model.SENSOR_TILE_BOX_PROB
                    0x12 -> Model.STWIN_BOXB
                    0x80 -> Model.NUCLEO
                    0x7F -> Model.NUCLEO_F401RE
                    0x7E -> Model.NUCLEO_L476RG
                    0x7D -> Model.NUCLEO_L053R8
                    0x7C -> Model.NUCLEO_F446RE
                    0x7B -> Model.NUCLEO_U575ZIQ
                    0x7A -> Model.NUCLEO_U5A5ZJQ
                    0x8D -> Model.WB0X_NUCLEO_BOARD
                    0x8E -> Model.WBA6_NUCLEO_BOARD
                    0x8F -> Model.WB05_NUCLEO_BOARD
                    else -> {
                        if (temp in 0x81..0x8A) {
                            Model.WB55_NUCLEO_BOARD
                        } else {
                            if (temp in 0x8B..0x8C) {
                                Model.WBA5X_NUCLEO_BOARD
                            } else {
                                Model.GENERIC
                            }
                        }
                    }
                }
            } else {
                when (id and 0xFF) {
                    0x01 -> Model.STEVAL_WESU1
                    0x02 -> Model.SENSOR_TILE
                    0x03 -> Model.BLUE_COIN
                    0x04 -> Model.STEVAL_IDB008VX
                    0x05 -> Model.STEVAL_BCN002V1
                    0x06 -> Model.SENSOR_TILE_BOX
                    0x07 -> Model.DISCOVERY_IOT01A
                    0x08 -> Model.STEVAL_STWINKIT1
                    0x09 -> Model.STEVAL_STWINKT1B
                    0x0A -> Model.B_L475E_IOT01A
                    0x0B -> Model.B_U585I_IOT02A
                    0x0C -> Model.ASTRA1
                    0x0D -> Model.SENSOR_TILE_BOX_PRO
                    0x0E -> Model.STWIN_BOX
                    0x0F -> Model.PROTEUS
                    0x10 -> Model.STDES_CBMLORABLE
                    0x11 -> Model.SENSOR_TILE_BOX_PROB
                    0x12 -> Model.STWIN_BOXB
                    0x80 -> Model.NUCLEO
                    0x7F -> Model.NUCLEO_F401RE
                    0x7E -> Model.NUCLEO_L476RG
                    0x7D -> Model.NUCLEO_L053R8
                    0x7C -> Model.NUCLEO_F446RE
                    0x7B -> Model.NUCLEO_U575ZIQ
                    0x7A -> Model.NUCLEO_U5A5ZJQ


                    0x81 -> Model.WB55_NUCLEO_BOARD
                    0x82 -> Model.WB5M_DISCOVERY_BOARD
                    0x83 -> Model.WB55_USB_DONGLE_BOARD
                    0x84 -> Model.WB15_NUCLEO_BOARD
                    0x85 -> Model.WB1M_DISCOVERY_BOARD
                    0x8B -> Model.WBA5X_NUCLEO_BOARD
                    0x8C -> Model.WBA_DISCOVERY_BOARD
                    0x8D -> Model.WB0X_NUCLEO_BOARD
                    0x8E -> Model.WBA6_NUCLEO_BOARD
                    0x8F -> Model.WB05_NUCLEO_BOARD

                    else -> Model.GENERIC
                }
            }
        return retVal
    }
}