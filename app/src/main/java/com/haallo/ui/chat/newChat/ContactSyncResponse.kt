package com.haallo.ui.chat.newChat

data class ContactSyncResponse(
    val result: List<Data>?,
    val message: String,
    val status: Int
)

data class Data(
    val countryCode: String,
    val fullName: String?,
    val id: Int,
    val mobile: String

)