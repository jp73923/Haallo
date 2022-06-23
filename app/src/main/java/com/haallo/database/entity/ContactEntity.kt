package com.haallo.database.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Class which provides a model for post
 */
@Entity(tableName = "ContactTable")
@Parcelize
data class ContactEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String = "",
    var phone_number: String = "",

    @Ignore
    var isSelected: Boolean = false
) : Parcelable {

    fun getFirstCharOfName(): String {
        return if (name.isNotEmpty()) {
            name[0].toUpperCase().toString()
        } else {
            "!"
        }
    }
}