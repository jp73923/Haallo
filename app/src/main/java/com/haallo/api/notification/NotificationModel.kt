package com.haallo.api.notification

sealed class VibrateSelectState {
    object VibrateOff : VibrateSelectState()
    object ShortVibrate : VibrateSelectState()
    object LongVibrate : VibrateSelectState()
}