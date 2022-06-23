package com.haallo.ui.group.model

data class CreateGroupModel(
    var admin_id: String = "",
    var admin_name: String = "",
    var group_id: String = "",
    var group_name: String = "",
    var group_image: String = "",
    var lastmessage: String = "",
    var timeStamp: String = "",
    var members: ArrayList<MemberData> = ArrayList()
) {
    data class MemberData(
        var id: String = "",
        var name: String = "",
        var mobile: String = "",
        var profile_image: String = "",
        var unread_count: Int = 0
    )
}