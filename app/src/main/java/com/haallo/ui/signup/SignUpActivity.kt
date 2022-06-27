package com.haallo.ui.signup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.FirebaseDatabase
import com.haallo.R
import com.haallo.base.BaseActivity
import com.haallo.base.extension.startActivityWithDefaultAnimation
import com.haallo.base.extension.subscribeAndObserveOnMainThread
import com.haallo.base.extension.throttleClicks
import com.haallo.databinding.ActivitySignUpBinding
import com.haallo.ui.otpverify.OtpVerifyActivity
import com.haallo.ui.signin.SignInActivity
import com.haallo.ui.signup.viewmodel.SignUpViewModel
import com.haallo.util.*

class SignUpActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, SignUpActivity::class.java)
        }
    }

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var signUpViewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listenToViewEvent()
        observer()
    }

    private fun listenToViewEvent() {
        signUpViewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)
        storeFirebaseToken(this)

        binding.ivBack.throttleClicks().subscribeAndObserveOnMainThread {
            onBackPressed()
        }.autoDispose()

        binding.btnRegister.throttleClicks().subscribeAndObserveOnMainThread {
            if (isValidInput()) {
                registerAPI()
            }
        }.autoDispose()

        binding.tvLogin.throttleClicks().subscribeAndObserveOnMainThread {
            startActivityWithDefaultAnimation(SignInActivity.getIntent(this))
            finish()
        }.autoDispose()
    }

    //Observer
    private fun observer() {
        signUpViewModel.signUpResponse.observe(this) {
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
            if (it.result.id != null) {
                sharedPreference.userId = it.result.id.toString()
            }
            startActivityWithDefaultAnimation(OtpVerifyActivity.getIntent(this))

            //Set Default Value for The Seen Status
            if (it.result.id != null) {
                val sharedId = "u_" + it.result.id
                FirebaseDatabase.getInstance().reference.child("users").child(sharedId)
                    .child("storyPrivacy")
                    .setValue("myContacts")

                FirebaseDatabase.getInstance().reference.child("users").child(sharedId)
                    .child("lastSeen")
                    .setValue("myContacts")

                FirebaseDatabase.getInstance().reference.child("users").child(sharedId)
                    .child("profilePicture")
                    .setValue("myContacts")

                FirebaseDatabase.getInstance().reference.child("users").child(sharedId)
                    .child("about")
                    .setValue("myContacts")
            }
        }

        signUpViewModel.onError.observe(this) {
            hideLoading()
            showError(this, binding.root, it)
        }
    }

    //Input Validation Function
    private fun isValidInput(): Boolean {
        if (binding.etMobile.getString().isEmpty()) {
            binding.etMobile.requestFocus()
            showToast(getString(R.string.error_enter_mobile_number))
            return false
        }

        if (!binding.etMobile.isValidMobileNumber()) {
            binding.etMobile.requestFocus()
            showToast(getString(R.string.error_enter_valid_mobile_number))
            return false
        }

        if (binding.etPassword.getString().isEmpty()) {
            binding.etPassword.requestFocus()
            showToast(getString(R.string.error_enter_password))
            return false
        }

        if (!binding.etPassword.isValidPassword()) {
            binding.etPassword.requestFocus()
            showToast(getString(R.string.error_password_length_must_be_more_than_characters))
            return false
        }

        if (binding.etConfirmPassword.getString().isEmpty()) {
            binding.etConfirmPassword.requestFocus()
            showToast(getString(R.string.error_please_enter_confirm_password))
            return false
        }

        if (binding.etConfirmPassword.getString() != binding.etPassword.getString()) {
            binding.etConfirmPassword.requestFocus()
            showToast(getString(R.string.error_password_did_not_match))
            return false
        }
        return true
    }

    private fun registerAPI() {
        showLoading()
        signUpViewModel.signUp(
            countryCode = binding.countryCodePicker.selectedCountryCodeWithPlus.toString(),
            mobile = binding.etMobile.getString(),
            password = binding.etConfirmPassword.getString(),
            deviceToken = sharedPreference.deviceToken
        )
    }
}