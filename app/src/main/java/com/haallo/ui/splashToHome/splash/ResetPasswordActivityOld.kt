package com.haallo.ui.splashToHome.splash

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.haallo.R
import com.haallo.base.OldBaseActivity
import com.haallo.databinding.ActivityResetPasswordBinding
import com.haallo.ui.splashToHome.SignInToHomeViewModelOld
import com.haallo.ui.signin.SignInActivity
import com.haallo.util.getString
import com.haallo.util.isValidPassword
import com.haallo.util.showToast

class ResetPasswordActivityOld : OldBaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityResetPasswordBinding
    private lateinit var signInToHomeViewModel: SignInToHomeViewModelOld
    private var accessToken: String = ""
    private var mobile: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initControl()
    }

    override fun initView() {
        signInToHomeViewModel = ViewModelProvider(this).get(SignInToHomeViewModelOld::class.java)
        checkTheme()
        getDataFromSharedPreferences()
        setHeading()
        observer()
    }

    //Check App Theme
    private fun checkTheme() {
        if (sharedPreference.nightTheme) {
            binding.rootLayoutResetPassword.setBackgroundColor(
                ContextCompat.getColor(this, R.color.appNightModeBackground)
            )
        } else if (!sharedPreference.nightTheme) {
            binding.rootLayoutResetPassword.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        }
    }

    //Find Data From the SharedPreferences
    private fun getDataFromSharedPreferences() {
        accessToken = sharedPreference.accessToken
        mobile = sharedPreference.mobileNumber
    }

    //Set Heading
    private fun setHeading() {
        binding.tvHeading.text = getString(R.string.reset_password)
    }

    //Observer
    private fun observer() {
        signInToHomeViewModel.resetPasswordResponse.observe(this) {
            hideLoading()
            showToast(getString(R.string.password_changed_successfully))
            startActivity(Intent(this, SignInActivity::class.java))
            finishAffinity()
        }

        signInToHomeViewModel.onError.observe(this) {
            hideLoading()
            showError(this, binding.rootLayoutResetPassword, it)
        }
    }

    //All Control Defines Here
    override fun initControl() {
        binding.btnBack.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
    }

    //OnClick Listener Function
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnBack -> {
                onBackPressed()
            }

            R.id.btnSubmit -> {
                if (isInputValid()) {
                    resetPasswordApi()
                }
            }
        }
    }

    //Input Validation Function
    private fun isInputValid(): Boolean {
        if (binding.etPassword.getString().isEmpty()) {
            binding.etPassword.requestFocus()
            showToast(getString(R.string.please_enter_password))
            return false
        }

        if (!binding.etPassword.isValidPassword()) {
            binding.etPassword.requestFocus()
            showToast(getString(R.string.password_length_must_be_more_than_characters))
            return false
        }

        if (binding.etConfirmPassword.getString().isEmpty()) {
            binding.etConfirmPassword.requestFocus()
            showToast(getString(R.string.please_enter_confirm_password))
            return false
        }

        if (binding.etConfirmPassword.getString() != binding.etPassword.getString()) {
            binding.etConfirmPassword.requestFocus()
            showToast(getString(R.string.password_did_not_match))
            return false
        }
        return true
    }

    //Reset Password Api
    private fun resetPasswordApi() {
        showLoading()
        signInToHomeViewModel.resetPasswordApi(
            mobile = mobile,
            password = binding.etPassword.getString()
        )
    }
}