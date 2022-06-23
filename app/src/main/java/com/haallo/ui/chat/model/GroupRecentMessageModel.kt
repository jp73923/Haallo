package com.haallo.ui.chat.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GroupRecentMessageModel(
    var id: String? = "",
    var group_id: String? = "",
    var isBroadcast: Boolean = false,
    var isLeaved: String? = "",
    var lastmessage: String? = "",
    var messageType: String? = "",
    var name: String? = "",
    var profile_image: String? = "",
    var readState: String? = "",
    var timeStamp: String? = ""
) : Parcelable