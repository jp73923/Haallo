package com.haallo.api.phonecontact

import android.content.Context
import android.provider.ContactsContract
import com.haallo.api.phonecontact.model.PhoneContact
import io.reactivex.Single

class PhoneContactRepository {

    fun getAllPhoneContacts(context: Context): Single<ArrayList<PhoneContact>> {
        val phoneContactArrayList = ArrayList<PhoneContact>()
        with(context.contentResolver) {
            query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)?.use { cursor ->
                val indexColId = cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID)
                val indexColName = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
                val indexColHasPhoneNumber = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)
                while (cursor.moveToNext()) {
                    try {
                        val id = cursor.getString(indexColId)
                        val name = cursor.getString(indexColName)
                        if (cursor.getInt(indexColHasPhoneNumber) > 0) {
                            query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                arrayOf(id),
                                null
                            )?.use { phoneCursor ->
                                if (phoneCursor.moveToFirst()) {
                                    val indexColNumber = phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                    val phoneNumber = phoneCursor.getString(indexColNumber).replace("\\s".toRegex(), "")
                                    val phoneContact = PhoneContact(
                                        name = name,
                                        phoneNo = phoneNumber
                                    )
                                    phoneContactArrayList.add(phoneContact)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        return Single.just(phoneContactArrayList)
    }
}