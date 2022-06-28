package com.haallo.api.phonecontact.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhoneContact(
    var name: String = "",
    var phoneNo: String = ""
) : Parcelable