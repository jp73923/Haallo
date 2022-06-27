package com.haallo.ui.forgotpassword.viewmodel

import androidx.lifecycle.MutableLiveData
import com.haallo.api.authentication.model.ForgotPasswordResponse
import com.haallo.base.OldBaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ForgotPasswordViewModel : OldBaseViewModel() {

    var forgotPasswordResponse = MutableLiveData<ForgotPasswordResponse>()
    var onError = MutableLiveData<Throwable>()

    //Forgot Password Api
    fun forgotPasswordApi(mobile: String) {
        val disposable: Disposable = apiInterface.forgotPassword(
            mobile = mobile
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onForgotPasswordSuccess(it) },
                { onError(it) })
    }

    //Forgot Password Success
    private fun onForgotPasswordSuccess(it: ForgotPasswordResponse) {
        forgotPasswordResponse.value = it
    }

    //On Error
    private fun onError(it: Throwable) {
        onError.value = it
    }
}