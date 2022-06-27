package com.haallo.ui.resetpassword.viewmodel

import androidx.lifecycle.MutableLiveData
import com.haallo.api.authentication.model.ResetPasswordResponse
import com.haallo.base.OldBaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ResetPasswordViewModel : OldBaseViewModel() {

    var resetPasswordResponse = MutableLiveData<ResetPasswordResponse>()
    var onError = MutableLiveData<Throwable>()

    //Reset Password Api
    fun resetPasswordApi(mobile: String, password: String) {
        val disposable: Disposable = apiInterface.resetPassword(
            mobile = mobile,
            password = password
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onResetPasswordSuccess(it) },
                { onError(it) })
    }

    //Reset Password Success
    private fun onResetPasswordSuccess(it: ResetPasswordResponse) {
        resetPasswordResponse.value = it
    }

    //On Error
    private fun onError(it: Throwable) {
        onError.value = it
    }
}