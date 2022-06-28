package com.haallo.api.contact

import android.content.Context
import android.provider.ContactsContract
import com.haallo.database.db.HaalloDatabase
import com.haallo.database.entity.ContactEntity
import io.reactivex.Single
import org.jetbrains.anko.doAsync

class ContactRepository {

    fun getAllPhoneContacts(): Single<List<ContactEntity>> {
        val contactDao = HaalloDatabase.haalloDatabase.contactDao()
        return contactDao.getAllContactList()
    }

    fun fetchPhoneContacts(context: Context) {
        doAsync {
            val phoneContactArrayList = ArrayList<ContactEntity>()
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
                                        val phoneContact = ContactEntity(
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

            val contactDao = HaalloDatabase.haalloDatabase.contactDao()
            contactDao.emptyContactTable()
            contactDao.insertContactList(phoneContactArrayList)
        }
    }
}