package com.haallo.api.coroutine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.haallo.ui.home.setting.SettingViewModel
import java.lang.IllegalArgumentException

class AppViewModelFactory(private val API: APIHelper) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingViewModel::class.java)){
            return SettingViewModel(AppRepository(API)) as T
        }

        throw  IllegalArgumentException("Unknown class name")

    }
}