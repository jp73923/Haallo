package com.haallo.ui.chat.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class StatusModel(
    var content: String? = "",
    var duration: String? = "",
    var mobile: String? = "",
    var type: String? = "",
    var name: String? = "",
    var profile_image: String? = "",
    var thumbImg: String? = "",
    var timeStamp: String? = ""
)