package com.haallo.ui.home.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.haallo.api.coroutine.AppRepository
import com.haallo.api.coroutine.Resource
import kotlinx.coroutines.Dispatchers

class SettingViewModel(val appRepository: AppRepository) : ViewModel() {

    fun removeAccount(token: String,mobile: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data =appRepository.removeAccount(token,mobile)))
        }catch(e: Exception) {
            emit(Resource.error(data = null, message = e.message ?: " Error Occurred!"))
        }
    }
}