package com.haallo.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.haallo.api.coroutine.ApiInterface
import com.haallo.util.RetrofitUtil

open class OldBaseViewModel : ViewModel() {
    var mError = MutableLiveData<Throwable>()

    val apiInterface: ApiInterface by lazy {
        RetrofitUtil.apiService()
    }
}