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

@Parcelize
data class CreateGroupModel(
    var admin_id: String = "",
    var admin_name: String = "",
    var group_id: String = "",
    var group_name: String = "",
    var group_image: String = "",
    var lastmessage: String = "",
    var timeStamp: String = "",
    var members: ArrayList<MemberData> = ArrayList()
) : Parcelable {
    @Parcelize
    data class MemberData(
        var id: String = "",
        var name: String = "",
        var mobile: String = "",
        var profile_image: String = "",
        var unread_count: Int = 0
    ) : Parcelable
}
