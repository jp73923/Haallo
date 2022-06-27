package com.haallo.ui.resetpassword

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.haallo.R
import com.haallo.base.BaseActivity
import com.haallo.base.extension.subscribeAndObserveOnMainThread
import com.haallo.base.extension.throttleClicks
import com.haallo.databinding.ActivityResetPasswordBinding
import com.haallo.ui.resetpassword.viewmodel.ResetPasswordViewModel
import com.haallo.ui.signin.SignInActivity
import com.haallo.util.getString
import com.haallo.util.isValidPassword
import com.haallo.util.showToast

class ResetPasswordActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, ResetPasswordActivity::class.java)
        }
    }

    private lateinit var binding: ActivityResetPasswordBinding
    private lateinit var resetPasswordViewModel: ResetPasswordViewModel
    private var accessToken: String = ""
    private var mobile: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listenToViewEvent()
    }

    private fun listenToViewEvent() {
        resetPasswordViewModel = ViewModelProvider(this).get(ResetPasswordViewModel::class.java)
        accessToken = sharedPreference.accessToken
        mobile = sharedPreference.mobileNumber

        binding.ivBack.throttleClicks().subscribeAndObserveOnMainThread {
            onBackPressed()
        }.autoDispose()

        binding.btnSubmit.throttleClicks().subscribeAndObserveOnMainThread {
            if (isInputValid()) {
                resetPasswordApi()
            }
        }.autoDispose()

        observer()
    }

    //Observer
    private fun observer() {
        resetPasswordViewModel.resetPasswordResponse.observe(this) {
            hideLoading()
            showToast(getString(R.string.password_changed_successfully))
            startActivity(Intent(this, SignInActivity::class.java))
            finishAffinity()
        }

        resetPasswordViewModel.onError.observe(this) {
            hideLoading()
            showError(this, binding.root, it)
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
        resetPasswordViewModel.resetPasswordApi(mobile, binding.etPassword.getString())
    }
}