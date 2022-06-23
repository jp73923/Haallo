package com.haallo.ui.chat.response

data class ReportUserResponse(
    val message: String,
    val response: ReportUser
)

data class ReportUser(
    val _id: String,
    val comment: String,
    val createdAt: String,
    val reason: String,
    val reportSenderId: String,
    val reportedUserId: String,
    val updatedAt: String
)