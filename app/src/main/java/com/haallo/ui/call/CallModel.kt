package com.haallo.ui.call

data class CallModel(
    var call_id: String = "",
    var call_status: String = "",
    var call_type: String="",
    var isGroup: String = "",
    var message: String="",
    var receiverImage: String?="",
    var receiverid: String="",
    var receivername: String="",
    var senderImage: String="",
    var senderid: String="",
    var sendername: String="",
    var timeStamp: Long = 0L
)