package com.haallo.ui.signin.viewmodel

import androidx.lifecycle.MutableLiveData
import com.haallo.api.authentication.model.SignInResponse
import com.haallo.base.OldBaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SignInViewModel : OldBaseViewModel() {

    var signInResponse = MutableLiveData<SignInResponse>()
    var onError = MutableLiveData<Throwable>()

    //SignIn Api
    fun signIn(mobile: String, password: String, deviceToken: String) {
        val disposable: Disposable = apiInterface.signIn(
            mobile = mobile,
            password = password,
            device_token = deviceToken,
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onSignInSuccess(it) },
                { onError(it) })
    }

    //SignIn Otp Success
    private fun onSignInSuccess(it: SignInResponse) {
        signInResponse.value = it
    }

    //On Error
    private fun onError(it: Throwable) {
        onError.value = it
    }
}