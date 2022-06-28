package com.haallo.database.dao

import androidx.room.*
import com.haallo.database.entity.ContactEntity
import io.reactivex.Single

@Dao
interface ContactDao {

    @Query("Select * From ContactTable Order By name Asc")
    fun getAllContactList(): Single<List<ContactEntity>>

    //Insert
    @Insert
    fun insertContact(contactEntity: ContactEntity): Single<Long>

    @Insert
    fun insertContactList(contactList: List<ContactEntity>): List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateContact(contactEntity: ContactEntity): Single<Int>

    @Query("Delete From ContactTable")
    fun emptyContactTable(): Int
}