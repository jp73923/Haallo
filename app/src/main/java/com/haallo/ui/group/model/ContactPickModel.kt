package com.haallo.ui.group.model

data class ContactPickModel(
    val number: String = "",
    val name: String = "",
    val pic: String = "",
    val id:String = "",
    var isSelected:Boolean = false,
    val isOnHallo:Boolean = false
)