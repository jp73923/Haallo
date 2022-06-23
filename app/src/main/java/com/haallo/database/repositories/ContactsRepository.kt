package com.haallo.database.repositories

import com.haallo.database.dao.ContactsDao
import com.haallo.database.entity.ContactEntity

class ContactsRepository(private val contactsDao: ContactsDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    fun getAllContacts() = contactsDao.getAllContactsLiveData()

    suspend fun insertSingleContact(contactEntity: ContactEntity) {

        val id = contactsDao.insertSingleContact(contactEntity)
        //Log.e("row id", "$id")
    }

    suspend fun insertMultipleContacts(contactList: ArrayList<ContactEntity>) {
        val id = contactsDao.insertMultipleContacts(contactList)
        //Log.e("row ids", "$id")
    }

    suspend fun updateContact(contactEntity: ContactEntity) = contactsDao.updateContact(contactEntity)

    suspend fun deleteAllContacts() {
        contactsDao.deleteAllContacts()
    }
}