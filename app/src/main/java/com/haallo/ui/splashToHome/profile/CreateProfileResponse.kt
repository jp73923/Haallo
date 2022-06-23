package com.haallo.ui.splashToHome.profile

data class CreateProfileResponse(
    val message: String,
    val result: ResultCreateProfile
)

data class ResultCreateProfile(
    val access_token: String,
    val address: String,
    val country_code: String,
    val created_at: String,
    val deleted_at: String,
    val device_token: String?,
    val device_type: Any,
    val email: String,
    val gender: String?,
    val id: Int?,
    val image: String?,
    val isRegister: String,
    val is_block: Any,
    val mobile: String?,
    val otp: String,
    val otp_verify_status: String,
    val profile_status: String,
    val updated_at: String,
    val user_name: String?,
    val user_type: Any,
    val verification_token: Any
)