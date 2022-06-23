package com.haallo.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.haallo.api.coroutine.ApiInterface
import com.haallo.database.db.MyRoomDatabase
import com.haallo.util.RetrofitUtil

abstract class OldBaseAppViewModel(application: Application): AndroidViewModel(application) {

    var mDataBase = MyRoomDatabase.getDatabase(application)

    var mError = MutableLiveData<Throwable>()

    val apiInterface: ApiInterface by lazy {
        RetrofitUtil.apiService()
    }
}