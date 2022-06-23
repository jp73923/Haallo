package com.haallo.api.coroutine

import com.haallo.ui.chat.response.ChatNotificationResponse
import com.haallo.constant.NetworkConstants
import retrofit2.Response
import retrofit2.http.*

interface APIService {

    @FormUrlEncoded
    @POST(NetworkConstants.REMOVE_ACCOUNT)
    suspend fun removeAccount(
        @Header("accessToken") accessToken: String,
        @Field("mobile") mobile: String,
    ): Response<ChatNotificationResponse>

    @FormUrlEncoded
    @POST(NetworkConstants.muteNotification)
    suspend fun muteNotification(
        @Header("accessToken") accessToken: String,
        @Field("mobile") mobile: String,
    ): Response<ChatNotificationResponse>

    @Multipart
    @POST(NetworkConstants.update_backgound_image)
    suspend fun updateBackgoundImage(
        @Header("accessToken") accessToken: String,
        @Part("image") mobile: String,
    ): Response<ChatNotificationResponse>
}