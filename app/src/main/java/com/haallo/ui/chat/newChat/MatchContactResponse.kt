package com.haallo.ui.chat.newChat

data class MatchContactResponse(
    val message: String,
    val result: ArrayList<Contact>
)

data class Contact(
    val countryCode: String,
    val fullName: String,
    val id: Int,
    val mobile: String
)