package com.haallo.ui.splashToHome

import androidx.lifecycle.MutableLiveData
import com.haallo.base.OldBaseViewModel
import com.haallo.ui.splashToHome.forgotAndResetPassword.ForgotPasswordResponse
import com.haallo.ui.splashToHome.forgotAndResetPassword.ResetPasswordResponse
import com.haallo.ui.splashToHome.otp.OtpVerifyResponse
import com.haallo.ui.splashToHome.otp.ResendOtpResponse
import com.haallo.ui.splashToHome.profile.CreateProfileResponse
import com.haallo.ui.splashToHome.registration.RegistrationResponse
import com.haallo.ui.splashToHome.signIn.SignInResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.RequestBody
import okhttp3.MultipartBody

class SignInToHomeViewModelOld : OldBaseViewModel() {

    var registrationResponse = MutableLiveData<RegistrationResponse>()
    var otpVerifyResponse = MutableLiveData<OtpVerifyResponse>()
    var resendOtpResponse = MutableLiveData<ResendOtpResponse>()
    var createProfileResponse = MutableLiveData<CreateProfileResponse>()
    var signInResponse = MutableLiveData<SignInResponse>()
    var forgotPasswordResponse = MutableLiveData<ForgotPasswordResponse>()
    var resetPasswordResponse = MutableLiveData<ResetPasswordResponse>()
    var onError = MutableLiveData<Throwable>()


    //Registration Api
    fun registrationApi(
        mobile: String,
        password: String,
        countryCode: String,
        deviceToken: String
    ) {
        val disposable: Disposable = apiInterface.registration(
            country_code = countryCode,
            mobile = mobile,
            password = password,
            device_token = deviceToken
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onRegistrationSuccess(it) },
                { onError(it) })
    }

    //Registration Success
    private fun onRegistrationSuccess(it: RegistrationResponse) {
        registrationResponse.value = it
    }

    //Verify Otp Api
    fun verifyOtpApi(accessToken: String, mobile: String, otp: String) {
        val disposable: Disposable = apiInterface.verifyOtp(
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

    //SignIn Api
    fun signInApi(mobile: String, password: String, deviceToken: String) {
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