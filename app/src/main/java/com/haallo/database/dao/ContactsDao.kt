package com.haallo.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.haallo.database.entity.ContactEntity

@Dao
interface ContactsDao {

    // Get Query
    @Query("SELECT * from ContactTable ORDER BY name ASC")
    fun getAllContactsLiveData(): LiveData<List<ContactEntity>>

    @Query("SELECT * from ContactTable ORDER BY name ASC")
    fun getAllContacts(): List<ContactEntity>

    //Insert
    @Insert()
    suspend fun insertSingleContact(contactEntity: ContactEntity): Long

    @Insert()
    suspend fun insertMultipleContacts(contactList: ArrayList<ContactEntity>): List<Long>

    @Insert()
    fun insertMultipleContacts(contactList: List<ContactEntity>): List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateContact(contactEntity: ContactEntity)

    // Delete
    @Query("DELETE FROM ContactTable")
    suspend fun deleteAllContacts()

    @Query("DELETE FROM ContactTable")
    fun emptyContactTable()
}