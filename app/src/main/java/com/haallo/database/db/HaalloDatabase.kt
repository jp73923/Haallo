package com.haallo.database.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.haallo.database.dao.ContactDao
import com.haallo.database.entity.ContactEntity
import timber.log.Timber

@Database(entities = [ContactEntity::class], version = 2)
abstract class HaalloDatabase : RoomDatabase() {

    abstract fun contactDao(): ContactDao

    companion object {
        private const val DATABASE_NAME = "haallo"
        lateinit var haalloDatabase: HaalloDatabase

        fun initDatabase(context: Context) {
            haalloDatabase = databaseBuilder(context.applicationContext, HaalloDatabase::class.java, DATABASE_NAME).addCallback(object : Callback() {
                override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                    super.onDestructiveMigration(db)
                    Timber.e("onDestructiveMigration")
                }
            }).fallbackToDestructiveMigration()
//                .allowMainThreadQueries()
                .build()
        }
    }
}
