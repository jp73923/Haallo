package com.haallo.ui.splashToHome.signIn

data class SignInResponse(
    val message: String,
    val result: ResultSignIn
)

data class ResultSignIn(
    val about: String?,
    val access_token: String?,
    val address: String,
    val background_image: String?,
    val country_code: String?,
    val created_at: String,
    val deleted_at: Any,
    val device_token: String,
    val device_type: String,
    val email: Any,
    val gender: String?,
    val id: Int?,
    val image: String?,
    val isRegister: String,
    val is_block: Int,
    val is_online: Int,
    val mobile: String?,
    val name: String?,
    val otp: String,
    val otp_verify_status: Int,
    val profile_status: Int,
    val updated_at: String,
    val user_name: String?,
    val user_type: Any,
    val verification_token: Any
)