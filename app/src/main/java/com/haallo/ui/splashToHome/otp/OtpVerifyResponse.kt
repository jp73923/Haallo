package com.haallo.ui.splashToHome.otp

data class OtpVerifyResponse(
    val id: Int,
    val email: String,
    val user_name: String,
    val image: String,
    val country_code: Int,
    val gender: String,
    val mobile: Int,
    val otp: Int,
    val address: String,
    val device_type: String,
    val verification_token: String,
    val device_token: String,
    val otp_verify_status: Int,
    val profile_status: Int,
    val access_token: String,
    val user_type: String,
    val isRegister: Int,
    val is_block: String,
    val created_at: String,
    val updated_at: String,
    val deleted_at: String
)