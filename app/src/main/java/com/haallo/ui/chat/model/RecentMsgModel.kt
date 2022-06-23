package com.haallo.ui.chat.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RecentMsgModel(
    var id: String? = "",
    var is_Archived: String? = "",
    var isBroadcast: Boolean = false,
    var is_block: String? = "",
    var lastmessage: String? = "",
    var messageType: String? = "",
    var mobile: String? = "",
    var name: String? = "",
    var group_id: String? = "",
    var isLeaved: String? = "",
    var profile_image: String? = "",
    var readState: String? = "",
    var receiverid: String? = "",
    var senderid: String? = "",
    var timeStamp: String? = ""
) : Parcelable