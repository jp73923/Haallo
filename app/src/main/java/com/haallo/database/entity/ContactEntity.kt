package com.haallo.database.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "ContactTable")
data class ContactEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String = "",
    var phoneNo: String = "",

    @Ignore
    var isSelected: Boolean = false
) : Parcelable {

    fun getFirstCharOfName(): String {
        return if (name.isNotEmpty()) {
            name[0].uppercaseChar().toString()
        } else {
            "!"
        }
    }
}