package com.haallo.api.chat.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecentMessageModel(
    var id: String? = "",
    var senderid: String? = "",
    var receiverid: String? = "",
    var profile_image: String? = "",
    var name: String? = "",
    var mobile: String? = "",
    var messageType: String? = "",
    var lastmessage: String? = "",
    var readState: String? = "",
    var timeStamp: String? = "",
    var is_Archived: String? = "",
    var isBroadcast: Boolean = false,
    var is_block: String? = "",
    var isLeaved: String? = "",
    var group_id: String? = "",
) : Parcelable