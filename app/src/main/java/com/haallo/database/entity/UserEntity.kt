package com.haallo.database.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude
import kotlinx.android.parcel.Parcelize

/**
 * Class which provides a model for post
 */
@Entity(tableName = "UserTable")
@Parcelize
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var token: String? = "",
    var email: String? = "",
    @Ignore
    val password: String? = "",
    @Ignore
    val socialId: String? = "",
    @Ignore
    val friend_status: String? = "",
    @Ignore
    var request_id: String? = "",
    var user_id: String? = "",
    var username: String? = "",
    var firstName: String = "",
    var lastName: String = "",
    var device_type: String? = "",
    @Ignore
    var login_type: String? = "",
    var other_language: String? = "",
    var other_qualification: String? = "",
    var country_code: String? = "",
    var device_token: String? = "",
    var profile_status: String? = "",
    var selected_language: String? = "",
    var isotp_verified: Boolean? = false,
    var mobile_number: String? = "",
    var country_name: String? = "",
    var gender: String? = "",
    var i_speak: String? = "",
    var peoplecallme: String? = "",
    var qualification: String? = "",
    var personal_status: String? = "",
    var avatar_url: String? = "",
    var interests: String? = "",
    var bio: String? = "",
    var facebook_link: String? = "",
    var isprofile_photo: String? = "",
    var instagram_link: String? = "",
    var friends: ArrayList<Int> = ArrayList(),
    var blockedUser: ArrayList<Int> = ArrayList(),
    var profile_visibility: String = "",
    var custom_friend_ids: ArrayList<Int> = ArrayList(),
    var profileimg: String = "",
//    @Ignore
//    var user_interest: ArrayList<ListData> = ArrayList()
) : Parcelable {

    @Ignore
    @Exclude
    fun getFullName(): String {
        return if (firstName.isNotEmpty()) {
            "$firstName $lastName"
        } else {
            username ?: ""
        }
    }

    @Ignore
    @Exclude
    fun getMobileWithCountryCode(): String {
        return "+$country_code-$mobile_number"
    }
}