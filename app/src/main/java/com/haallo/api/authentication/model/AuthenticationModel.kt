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
) : Parcelable {}

data class LoggedInUser(
    val loggedInUser: HaalloUser,
    val loggedInUserToken: String?,
)