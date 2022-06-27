package com.haallo.ui.signup.viewmodel

import androidx.lifecycle.MutableLiveData
import com.haallo.api.authentication.model.SignUpResponse
import com.haallo.base.OldBaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SignUpViewModel : OldBaseViewModel() {

    var signUpResponse = MutableLiveData<SignUpResponse>()
    var onError = MutableLiveData<Throwable>()

    //Registration Api
    fun signUp(
        mobile: String,
        password: String,
        countryCode: String,
        deviceToken: String
    ) {
        val disposable: Disposable = apiInterface.signUp(
            country_code = countryCode,
            mobile = mobile,
            password = password,
            device_token = deviceToken
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onSignUpSuccess(it) },
                { onError(it) })
    }

    //Registration Success
    private fun onSignUpSuccess(it: SignUpResponse) {
        signUpResponse.value = it
    }

    //On Error
    private fun onError(it: Throwable) {
        onError.value = it
    }
}