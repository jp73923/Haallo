package com.haallo.ui.chat.placespicker.model

import com.google.gson.annotations.SerializedName

data class PlacesResponse(
        @SerializedName("meta")
        val meta: Meta,
        @SerializedName("response")
        val response: Response
)