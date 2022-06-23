package com.haallo.ui.splashToHome.forgotAndResetPassword

data class ResetPasswordResponse(
    val message: String,
    val result: ResultResetPassword
)

data class ResultResetPassword(
    val access_token: String,
    val address: Any,
    val country_code: String,
    val created_at: String,
    val deleted_at: Any,
    val device_token: String,
    val device_type: String,
    val email: Any,
    val gender: Any,
    val id: Int,
    val image: String,
    val isRegister: String,
    val is_block: Int,
    val is_online: Int,
    val mobile: String,
    val otp: String,
    val otp_verify_status: Any,
    val profile_status: String,
    val updated_at: String,
    val user_name: Any,
    val user_type: Any,
    val verification_token: Any
)