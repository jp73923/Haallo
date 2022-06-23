package com.haallo.service

import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import androidx.core.app.JobIntentService
import com.haallo.database.db.MyRoomDatabase
import com.haallo.database.entity.ContactEntity

class FetchContactsService : JobIntentService() {


    companion object {
        private const val TAG = "FetchContactsService"
        private const val JOB_ID = 1212     //Unique job ID for this service.
        private var isUpdateContacts: Boolean = false

        fun enqueueWork(context: Context, intent: Intent, isUpdateContacts: Boolean) {
            Companion.isUpdateContacts = isUpdateContacts
            enqueueWork(context, FetchContactsService::class.java, JOB_ID, intent)
        }
    }

    override fun onHandleWork(intent: Intent) {
        val contactDao = MyRoomDatabase.getDatabase(application).contactsDao()
        if (contactDao.getAllContacts().isEmpty() || isUpdateContacts) {
            val contactList = getAllContacts()
            contactDao.emptyContactTable()
            contactDao.insertMultipleContacts(contactList)
        }
    }


    private fun getAllContacts(): List<ContactEntity> {
        val contactDetailsList = mutableListOf<ContactEntity>()
        with(application.contentResolver) {
            query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                null
            )?.use { cursor ->

                val indexColId = cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID)
                val indexColName =
                    cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
                val indexColHasPhoneNumber =
                    cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)

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
                                    val indexColNumber =
                                        phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                    val phoneNumber = phoneCursor.getString(indexColNumber)
                                        .replace("\\s".toRegex(), "")

                                    val contactEntity = ContactEntity(
                                        name = name,
                                        phone_number = phoneNumber
                                    )
                                    contactDetailsList.add(contactEntity)
                                    //Log.e("Contact Detail", "$name : $phoneNumber")
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        /// Log.e("Total Contacts", "${contactDetailsList.size}")

        return contactDetailsList
    }
}