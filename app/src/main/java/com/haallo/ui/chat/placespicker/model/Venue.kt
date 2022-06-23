package com.haallo.ui.chat.placespicker.model

import com.google.gson.annotations.SerializedName
import com.haallo.ui.chat.placespicker.model.Category
import com.haallo.ui.chat.placespicker.model.Location

data class Venue(
    @SerializedName("categories")
        val categories: List<Category>,
    @SerializedName("hasPerk")
        val hasPerk: Boolean,
    @SerializedName("id")
        val id: String,
    @SerializedName("location")
        val location: Location,
    @SerializedName("name")
        val name: String,
    @SerializedName("referralId")
        val referralId: String,
    @SerializedName("venuePage")
        val venuePage: VenuePage
)