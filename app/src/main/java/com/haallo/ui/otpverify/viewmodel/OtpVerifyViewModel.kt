package com.haallo.ui.otpverify.viewmodel

import androidx.lifecycle.MutableLiveData
import com.haallo.api.authentication.model.OtpVerifyResponse
import com.haallo.api.authentication.model.ResendOtpResponse
import com.haallo.base.OldBaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class OtpVerifyViewModel : OldBaseViewModel() {

    var otpVerifyResponse = MutableLiveData<OtpVerifyResponse>()
    var resendOtpResponse = MutableLiveData<ResendOtpResponse>()
    var onError = MutableLiveData<Throwable>()

    //Verify Otp Api
    fun verifyOtpApi(accessToken: String, mobile: String, otp: String) {
        val disposable: Disposable = apiInterface.otpVerify(
            accessToken = accessToken,
            mobile = mobile,
            otp = otp
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onVerifyOtpSuccess(it) },
                { onError(it) })
    }

    //Verify Otp Success
    private fun onVerifyOtpSuccess(it: OtpVerifyResponse) {
        otpVerifyResponse.value = it
    }

    //Resend Otp Api
    fun resendOtpApi(accessToken: String, mobile: String) {
        val disposable: Disposable = apiInterface.resendOtp(
            accessToken = accessToken,
            mobile = mobile
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onResendOtpSuccess(it) },
                { onError(it) })
    }

    //Resend Otp Success
    private fun onResendOtpSuccess(it: ResendOtpResponse) {
        resendOtpResponse.value = it
    }

    //On Error
    private fun onError(it: Throwable) {
        onError.value = it
    }
}