package com.haallo.database.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.haallo.database.dao.ContactsDao
import com.haallo.database.dao.UserDao
import com.haallo.database.entity.ContactEntity
import com.haallo.database.entity.UserEntity

@Database(entities = [UserEntity::class, ContactEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class MyRoomDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun contactsDao(): ContactsDao

    companion object {

        private const val DATABASE_NAME = "emoji_zone_db"

        @Volatile
        private var noteRoomInstance: MyRoomDatabase? = null

        internal fun getDatabase(
            context: Context
        ): MyRoomDatabase {
            if (noteRoomInstance == null) {
                synchronized(this) {
                    if (noteRoomInstance == null) {
                        noteRoomInstance = Room.databaseBuilder(
                            context.applicationContext,
                            MyRoomDatabase::class.java, DATABASE_NAME
                        ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
                    }
                }
            }
            return noteRoomInstance!!
        }
    }
}
