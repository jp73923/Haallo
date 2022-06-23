package com.haallo.ui.chat.model

data class ChatMsgModel(
    var chat_id: String = "",
    var deleted: String = "",
    var group_id: String = "",
    var group_name: String = "",
    var mediaurl: String = "",
    var message: String = "",
    var messageType: String = "",
    var message_id: String = "",
    var receiverImage: String = "",
    var receiverMobile: String = "",
    var receivername: String = "",
    var senderImage: String = "",
    var senderMobile: String = "",
    var senderid: String = "",
    var sendername: String = "",
    var status: String = "",
    var timeStamp: String = "",
    var video_thumbnail: String = "",
    var receiverid: String = "",
    var contactName: String = "",
    var contactNumber: String = "",
    var content: String = "",
    var starmessage: String = "",
    var isForwarded: Boolean = false

)