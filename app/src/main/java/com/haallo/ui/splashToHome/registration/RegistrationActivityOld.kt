package com.haallo.ui.splashToHome.registration

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.FirebaseDatabase
import com.haallo.R
import com.haallo.base.OldBaseActivity
import com.haallo.constant.IntentConstant
import com.haallo.databinding.ActivityRegistrationBinding
import com.haallo.ui.splashToHome.SignInToHomeViewModelOld
import com.haallo.ui.splashToHome.chooseLanguage.ChooseLanguageActivityOld
import com.haallo.ui.splashToHome.otp.OtpActivityOld
import com.haallo.ui.splashToHome.signIn.SignInActivityOld
import com.haallo.util.*

class RegistrationActivityOld : OldBaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityRegistrationBinding

    private lateinit var signInToHomeViewModel: SignInToHomeViewModelOld

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initControl()
    }

    override fun initView() {
        signInToHomeViewModel = ViewModelProvider(this).get(SignInToHomeViewModelOld::class.java)
        checkTheme()
        setText()
        observer()

        storeFirebaseToken(this)
    }

    //Check App Theme
    private fun checkTheme() {
        if (sharedPreference.nightTheme) {
            binding.rootLayoutRegistration.setBackgroundColor(ContextCompat.getColor(this, R.color.appNightModeBackground))
        } else if (!sharedPreference.nightTheme) {
            binding.rootLayoutRegistration.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        }
    }

    //Set the Heading Text
    private fun setText() {
        binding.tvHeading.text = getString(R.string.registration)
    }

    //Get Device token
    private fun getDeviceToken() {
        try {
            /*  // deviceToken = FirebaseInstanceId.getInstance().token!!
              FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(
                  this@RegistrationActivity
              ) { instanceIdResult ->
                  val newToken = instanceIdResult.token
                  sharedPreference.deviceToken = newToken
              }*//*
            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                if (it.isComplete) {
                    var firebaseToken = it.result.toString()
                    if (firebaseToken != null)
                        sharedPreference.deviceToken = firebaseToken
                }
            }*/
        } catch (e: Exception) {
        }
    }

    //Observer
    private fun observer() {
        signInToHomeViewModel.registrationResponse.observe(this) {
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
            startActivity(
                Intent(this, OtpActivityOld::class.java)
                    .putExtra(IntentConstant.IS_FROM, IntentConstant.REGISTRATION)
            )

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

        signInToHomeViewModel.onError.observe(this) {
            hideLoading()
            showError(this, binding.rootLayoutRegistration, it)
        }
    }

    //All Control Defines Here
    override fun initControl() {
        binding.btnBack.setOnClickListener(this)
        binding.btnRegister.setOnClickListener(this)
        binding.tvLogin.setOnClickListener(this)
    }

    //OnClick Listener Function
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnBack -> {
                startActivity(Intent(this, ChooseLanguageActivityOld::class.java))
                finish()
            }
            R.id.btnRegister -> {
                if (isValidInput()) {
                    registerAPI()
                }
            }
            R.id.tvLogin -> {
                startActivity(Intent(this, SignInActivityOld::class.java))
            }
        }
    }

    //Input Validation Function
    private fun isValidInput(): Boolean {
        if (binding.etMobile.getString().isEmpty()) {
            binding.etMobile.requestFocus()
            showToast(getString(R.string.please_enter_mobile_number))
            return false
        }

        if (!binding.etMobile.isValidMobileNumber()) {
            binding.etMobile.requestFocus()
            showToast(getString(R.string.please_enter_valid_mobile_number))
            return false
        }

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

    //Registration Api
    private fun registerAPI() {
        showLoading()
        signInToHomeViewModel.registrationApi(
            countryCode = binding.countryCodePicker.selectedCountryCodeWithPlus.toString(),
            mobile = binding.etMobile.getString(),
            password = binding.etConfirmPassword.getString(),
            deviceToken = sharedPreference.deviceToken
        )
    }
}