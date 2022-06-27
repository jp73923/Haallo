package com.haallo.ui.splashToHome.forgotAndResetPassword

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.haallo.R
import com.haallo.base.OldBaseActivity
import com.haallo.constant.IntentConstant
import com.haallo.databinding.ActivityForgotPasswordBinding
import com.haallo.ui.splashToHome.SignInToHomeViewModelOld
import com.haallo.ui.splashToHome.otp.OtpActivityOld
import com.haallo.util.getString
import com.haallo.util.isValidMobileNumber
import com.haallo.util.showToast

class ForgotPasswordActivityOld : OldBaseActivity(), View.OnClickListener {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, ForgotPasswordActivityOld::class.java)
        }
    }

    private lateinit var binding: ActivityForgotPasswordBinding

    private lateinit var signInToHomeViewModel: SignInToHomeViewModelOld

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initControl()
    }

    override fun initView() {
        signInToHomeViewModel = ViewModelProvider(this).get(SignInToHomeViewModelOld::class.java)
        checkTheme()
        setHeading()
        observer()
    }

    //Check App Theme
    private fun checkTheme() {
        if (sharedPreference.nightTheme) {
            binding.rootLayoutForgotPassword.setBackgroundColor(ContextCompat.getColor(this, R.color.appNightModeBackground))
        } else if (!sharedPreference.nightTheme) {
            binding.rootLayoutForgotPassword.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        }
    }

    //Set Heading Text
    private fun setHeading() {
        binding.tvHeading.text = getString(R.string.forgot_password)
    }

    //Observer
    private fun observer() {
        signInToHomeViewModel.forgotPasswordResponse.observe(this) {
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
            startActivity(
                Intent(this, OtpActivityOld::class.java)
                    .putExtra(IntentConstant.IS_FROM, IntentConstant.FORGOT_PASSWORD)
            )
        }

        signInToHomeViewModel.onError.observe(this) {
            hideLoading()
            showError(this, binding.rootLayoutForgotPassword, it)
        }
    }

    //All Controls Defines Here
    override fun initControl() {
        binding.btnBack.setOnClickListener(this)
        binding.btnSend.setOnClickListener(this)
    }

    //OnClick Listener Function
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnBack -> {
                onBackPressed()
            }
            R.id.btnSend -> {
                if (isValidInput()) {
                    forgotApi()
                }
            }
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
        signInToHomeViewModel.forgotPasswordApi(binding.etMobile.getString())
    }
}