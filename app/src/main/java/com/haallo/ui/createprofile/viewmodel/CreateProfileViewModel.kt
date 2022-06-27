package com.haallo.ui.createprofile.viewmodel

import androidx.lifecycle.MutableLiveData
import com.haallo.api.profile.model.CreateProfileResponse
import com.haallo.base.OldBaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody

class CreateProfileViewModel : OldBaseViewModel() {

    var createProfileResponse = MutableLiveData<CreateProfileResponse>()
    var onError = MutableLiveData<Throwable>()

    //Create Profile Api
    fun createProfileApi(
        accessToken: String,
        userName: RequestBody,
        mobile: RequestBody,
        address: RequestBody,
        gender: RequestBody,
        image: MultipartBody.Part?
    ) {
        val disposable: Disposable = apiInterface.createProfile(
            accessToken = accessToken,
            user_name = userName,
            mobile = mobile,
            address = address,
            gender = gender,
            image = image
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onCreateProfileSuccess(it) },
                { onError(it) })
    }

    //Create Profile Api Success
    private fun onCreateProfileSuccess(it: CreateProfileResponse?) {
        createProfileResponse.value = it
    }

    //On Error
    private fun onError(it: Throwable) {
        onError.value = it
    }
}