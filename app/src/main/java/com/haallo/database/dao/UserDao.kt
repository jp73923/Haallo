package com.haallo.database.dao

import androidx.room.*
import com.haallo.database.entity.UserEntity

@Dao
interface UserDao {
    @get:Query("SELECT * FROM UserTable")
    val getUserData: UserEntity?

    @Insert()
    suspend fun insertUserData(vararg users: UserEntity)

    @Query("DELETE FROM UserTable")
    suspend fun deleteUserData()

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateUserData(userData: UserEntity)
}