package com.haallo.base.network.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class HaalloResponse<T>(
    @field:SerializedName("access_token")
    val accessToken: String? = null,

    @field:SerializedName("data")
    val data: T? = null,

    @field:SerializedName("success")
    val success: Boolean = false,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("deactive")
    val deactive: Boolean? = null,

    @field:SerializedName("step")
    val step: Int? = null,

    @field:SerializedName("otp")
    val otp: Int? = null,

    @field:SerializedName("token_type")
    val tokenType: String? = null,

    @field:SerializedName("expires_in")
    val expiresIn: Int? = null
)

@Keep
data class HaalloCommonResponse(
    @field:SerializedName("success")
    val success: Boolean = false,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("conversation_id")
    val conversationId: Int,
)