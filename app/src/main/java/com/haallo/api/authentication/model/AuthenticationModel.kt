package com.haallo.api.authentication.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class HaalloUser(
    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("user_type")
    val userType: String,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("username")
    val username: String? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("thumbnail")
    val thumbnail: String? = null,

    @field:SerializedName("avatar")
    val avatar: String? = null,

    @field:SerializedName("avatarUrl")
    val avatarUrl: String? = null,

    @field:SerializedName("about")
    val about: String? = null,

    @field:SerializedName("latitude")
    val latitude: String? = null,

    @field:SerializedName("longitude")
    val longitude: String? = null,

    @field:SerializedName("thumbnailUrl")
    val thumbnailUrl: String? = null,

    @field:SerializedName("total_post")
    val totalPost: Int? = null,

    @field:SerializedName("total_reels")
    val totalReels: Int? = null,

    @field:SerializedName("total_followers")
    val totalFollowers: Int? = null,

    @field:SerializedName("total_following")
    val totalFollowing: Int? = null,

    @field:SerializedName("platform")
    val platform: String? = null,

    @field:SerializedName("push_token")
    val pushToken: String? = null,

    @field:SerializedName("deactive")
    val deactive: Int? = null,

    @field:SerializedName("profile_verified")
    val profileVerified: Int? = null,

    @field:SerializedName("email_verified")
    val emailVerified: Int? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("deleted_at")
    val deletedAt: String? = null,

    @field: SerializedName("follow_status")
    var followStatus: Int? = null,

    @field: SerializedName("following_status")
    var followingStatus: Int? = null,
) : Parcelable

data class LoggedInUser(
    val loggedInUser: HaalloUser,
    val loggedInUserToken: String?,
)

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

data class SignUpResponse(
    val message: String,
    val result: ResultSignUp
)

data class ResultSignUp(
    val access_token: String?,
    val address: Any,
    val country_code: String?,
    val created_at: String,
    val deleted_at: String,
    val device_token: String,
    val device_type: Any,
    val email: Any,
    val gender: Any,
    val id: Int?,
    val image: String,
    val isRegister: String,
    val is_block: Any,
    val mobile: String?,
    val otp: String,
    val otp_verify_status: Any,
    val profile_status: String,
    val updated_at: String,
    val user_name: Any,
    val user_type: Any,
    val verification_token: Any
)

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

data class ResendOtpResponse(
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
    val otp_verify_status: String,
    val profile_status: Int,
    val access_token: String,
    val user_type: String,
    val isRegister: Int,
    val is_block: String,
    val created_at: String,
    val updated_at: String,
    val deleted_at: String
)

data class ForgotPasswordResponse(
    val message: String,
    val result: ResultForgotPassword
)

data class ResultForgotPassword(
    val access_token: String?,
    val address: Any,
    val country_code: String?,
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
    val mobile: String?,
    val otp: Int,
    val otp_verify_status: Any,
    val profile_status: String,
    val updated_at: String,
    val user_name: Any,
    val user_type: Any,
    val verification_token: Any
)

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