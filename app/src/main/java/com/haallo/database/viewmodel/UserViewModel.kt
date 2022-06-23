package com.haallo.database.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.haallo.database.dao.UserDao
import com.haallo.database.db.MyRoomDatabase
import com.haallo.database.entity.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class UserViewModel(
    application: Application
) : AndroidViewModel(application) {

    private var userDao: UserDao
    private var db: MyRoomDatabase = MyRoomDatabase.getDatabase(application)

    init {
        userDao = db.userDao()
    }

    fun insertUserData(data: UserEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            userDao.insertUserData(data)
        }
    }

    fun deleteUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            userDao.deleteUserData()
        }
    }

    fun getUserData(): UserEntity? {
        return userDao.getUserData
    }

    fun updateUserData(data: UserEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            userDao.updateUserData(data)
        }
    }

    fun updateFriendsList(friendsId: ArrayList<Int>) {
        val userData = getUserData()
        if(userData != null){
            userData.friends = friendsId
            updateUserData(userData)
        }
    }

    fun updateBlockedUsersList(blockedUsersId: ArrayList<Int>) {
        val userData = getUserData()
        if(userData != null){
            userData.blockedUser = blockedUsersId
            updateUserData(userData)
        }
    }
}