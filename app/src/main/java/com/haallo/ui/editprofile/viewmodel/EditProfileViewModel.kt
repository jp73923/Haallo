package com.haallo.ui.editprofile.viewmodel

import androidx.lifecycle.MutableLiveData
import com.haallo.api.profile.model.UpdateProfileResponse
import com.haallo.base.OldBaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody

class EditProfileViewModel : OldBaseViewModel() {

    var updateProfileResponse = MutableLiveData<UpdateProfileResponse>()
    var onError = MutableLiveData<Throwable>()

    //update Profile Api
    fun updateProfile(
        accessToken: String,
        name: RequestBody,
        about: RequestBody,
        image: MultipartBody.Part?
    ) {
        val disposable: Disposable = apiInterface.updateProfile(
            accessToken = accessToken,
            name = name,
            about = about,
            image = image
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onUpdateProfileSuccess(it) },
                { onError(it) })
    }

    //update Profile Api Success
    private fun onUpdateProfileSuccess(it: UpdateProfileResponse?) {
        updateProfileResponse.value = it
    }

    //On Error
    private fun onError(it: Throwable) {
        onError.value = it
    }
}