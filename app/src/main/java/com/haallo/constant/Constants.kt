package com.haallo.constant

interface Constants {
    companion object {
        const val BACK_PRESS_TIME_INTERVAL: Long = 2000
        const val CAMERA = 2222
        const val PASSWORD_PATTERN = "^(?=.*\\d)(?=.*[a-z]).{8,255}\$"
        const val EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
    }
}