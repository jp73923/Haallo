package com.haallo.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.haallo.api.coroutine.ApiInterface
import com.haallo.util.RetrofitUtil

abstract class OldBaseAppViewModel(application: Application) : AndroidViewModel(application) {

    val apiInterface: ApiInterface by lazy {
        RetrofitUtil.apiService()
    }
}