package com.haallo.ui.forgotpassword

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.haallo.R
import com.haallo.base.BaseActivity
import com.haallo.base.extension.startActivityWithDefaultAnimation
import com.haallo.base.extension.subscribeAndObserveOnMainThread
import com.haallo.base.extension.throttleClicks
import com.haallo.databinding.ActivityForgotPasswordBinding
import com.haallo.ui.forgotpassword.viewmodel.ForgotPasswordViewModel
import com.haallo.ui.forgotpasswordotpverify.ForgotPasswordOtpVerifyActivity
import com.haallo.util.getString
import com.haallo.util.isValidMobileNumber
import com.haallo.util.showToast

class ForgotPasswordActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, ForgotPasswordActivity::class.java)
        }
    }

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var forgotPasswordViewModel: ForgotPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listenToViewEvent()
        observer()
    }

    private fun listenToViewEvent() {
        forgotPasswordViewModel = ViewModelProvider(this).get(ForgotPasswordViewModel::class.java)

        binding.ivBack.throttleClicks().subscribeAndObserveOnMainThread {
            onBackPressed()
        }.autoDispose()

        binding.btnSend.throttleClicks().subscribeAndObserveOnMainThread {
            if (isValidInput()) {
                forgotApi()
            }
        }.autoDispose()
    }

    //Observer
    private fun observer() {
        forgotPasswordViewModel.forgotPasswordResponse.observe(this) {
            hideLoading()
            hideLoading()
            if ((it.result.access_token != null) && (it.result.access_token != "")) {
                sharedPreference.accessToken = it.result.access_token
            }
            if ((it.result.mobile != null) && (it.result.mobile != "")) {
                sharedPreference.mobileNumber = it.result.mobile
            }
            if ((it.result.country_code != null) && (it.result.country_code != "")) {
                sharedPreference.countryCode = it.result.country_code
            }
            startActivityWithDefaultAnimation(ForgotPasswordOtpVerifyActivity.getIntent(this))
        }

        forgotPasswordViewModel.onError.observe(this) {
            hideLoading()
            showError(this, binding.root, it)
        }
    }

    //Input Validation Function
    private fun isValidInput(): Boolean {
        if (binding.etMobile.getString().isEmpty()) {
            showToast(getString(R.string.please_enter_mobile_number))
            return false
        }
        if (!binding.etMobile.isValidMobileNumber()) {
            showToast(getString(R.string.please_enter_valid_mobile_number))
            return false
        }
        return true
    }

    //Forgot Password Api
    private fun forgotApi() {
        showLoading()
        forgotPasswordViewModel.forgotPasswordApi(binding.etMobile.getString())
    }
}