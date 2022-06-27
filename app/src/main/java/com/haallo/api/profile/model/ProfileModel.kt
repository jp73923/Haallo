package com.haallo.api.profile.model

import com.google.gson.annotations.SerializedName

sealed class EditProfilePhotoState {
    object OpenCamera : EditProfilePhotoState()
    object OpenGallery : EditProfilePhotoState()
    object DeletePhoto : EditProfilePhotoState()
}

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

data class UpdateProfileResponse(
    val message: String,
    val result: ResultUpdateProfile
)

data class ResultUpdateProfile(
    @field:SerializedName("gender")
    val gender: String? = null,

    @field:SerializedName("is_block")
    val isBlock: Int? = null,

    @field:SerializedName("user_name")
    val userName: String? = null,

    @field:SerializedName("about")
    val about: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("device_type")
    val deviceType: String? = null,

    @field:SerializedName("otp_verify_status")
    val otpVerifyStatus: Int? = null,

    @field:SerializedName("profile_status")
    val profileStatus: Int? = null,

    @field:SerializedName("user_type")
    val userType: Any? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("is_online")
    val isOnline: Int? = null,

    @field:SerializedName("verification_token")
    val verificationToken: Any? = null,

    @field:SerializedName("email")
    val email: Any? = null,

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("address")
    val address: Any? = null,

    @field:SerializedName("mobile")
    val mobile: String? = null,

    @field:SerializedName("otp")
    val otp: String? = null,

    @field:SerializedName("deleted_at")
    val deletedAt: Any? = null,

    @field:SerializedName("background_image")
    val backgroundImage: String? = null,

    @field:SerializedName("access_token")
    val accessToken: String? = null,

    @field:SerializedName("country_code")
    val countryCode: String? = null,

    @field:SerializedName("device_token")
    val deviceToken: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("isRegister")
    val isRegister: String? = null
)