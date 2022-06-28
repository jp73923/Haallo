package com.haallo.ui.chat.newChat

data class ContactModel(
    val id: String = "",
    val name: String = "",
    val number: String = "",
    val pic: String = "",
    var isSelected: Boolean = false
)