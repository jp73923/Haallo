package com.haallo.ui.chat.response

data class MuteUnMuteStatusResponse(
    val message: String,
    val response: MuteUnMuteStatus
)

data class MuteUnMuteStatus(
    val status: String
)