package com.haallo.api.fbrtdb.model

data class FirebaseUser(
    var countryCode: String = "",
    var name: String = "",
    var phone: String = "",
    var photo: String = "",
    var status: String = "",
    var uid: Long = 0,
    var userName: String = "",
    var ver: String = ""
)