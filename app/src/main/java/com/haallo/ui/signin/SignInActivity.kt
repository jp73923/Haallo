package com.haallo.ui.signin

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.haallo.R
import com.haallo.api.fbrtdb.model.FirebaseUser
import com.haallo.base.BaseActivity
import com.haallo.base.extension.startActivityWithDefaultAnimation
import com.haallo.base.extension.subscribeAndObserveOnMainThread
import com.haallo.base.extension.throttleClicks
import com.haallo.databinding.ActivitySignInBinding
import com.haallo.ui.forgotpassword.ForgotPasswordActivity
import com.haallo.ui.home.HomeActivity
import com.haallo.ui.otpverify.OtpVerifyActivity
import com.haallo.ui.signin.viewmodel.SignInViewModel
import com.haallo.ui.signup.SignUpActivity
import com.haallo.ui.createprofile.CreateProfileActivity
import com.haallo.util.*
import com.tbruyelle.rxpermissions2.Permission
import com.tbruyelle.rxpermissions2.RxPermissions

class SignInActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, SignInActivity::class.java)
        }

        fun getIntentWithClear(context: Context): Intent {
            val intent = Intent(context, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            return intent
        }
    }

    private lateinit var binding: ActivitySignInBinding
    private lateinit var signInViewModel: SignInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listenToViewEvent()
        observer()
    }

    private fun listenToViewEvent() {
        signInViewModel = ViewModelProvider(this).get(SignInViewModel::class.java)
        storeFirebaseToken(this)

        binding.ivBack.throttleClicks().subscribeAndObserveOnMainThread {
            onBackPressed()
        }.autoDispose()

        binding.tvForgotPassword.throttleClicks().subscribeAndObserveOnMainThread {
            startActivityWithDefaultAnimation(ForgotPasswordActivity.getIntent(this))
        }.autoDispose()

        binding.btnLogin.throttleClicks().subscribeAndObserveOnMainThread {
            if (isInputValid()) {
                loginApi()
            }
        }.autoDispose()

        binding.tvRegister.throttleClicks().subscribeAndObserveOnMainThread {
            startActivityWithDefaultAnimation(SignUpActivity.getIntent(this))
            finish()
        }.autoDispose()

        if (!PermissionsUtil.hasPermissions(this)) {
            requestPermissions()
        }
    }

    //Permission
    private fun requestPermissions() {
        RxPermissions(this).requestEachCombined(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ).subscribe { permission: Permission ->
            if (permission.granted) {
                //All Permissions Grated
            } else {
                if (permission.shouldShowRequestPermissionRationale) {
                    showLongToast(getString(R.string.msg_please_allow_all_required_permission))
                    showAlertDialog()
                } else {
                    showLongToast(getString(R.string.msg_allow_permission_from_settings))
                }
            }
        }.autoDispose()
    }

    private fun showAlertDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.missing_permissions)
            .setMessage(R.string.you_have_to_grant_permissions)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
                requestPermissions()
            }
            .setNegativeButton(R.string.no_close_the_app) { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .create().show()
    }

    private fun isInputValid(): Boolean {
        if (binding.etMobile.getString().isEmpty()) {
            binding.etMobile.requestFocus()
            showToast(getString(R.string.error_enter_mobile_number))
            return false
        }

        if (!binding.etMobile.isValidMobileNumber()) {
            showToast(getString(R.string.error_enter_valid_mobile_number))
            return false
        }

        if (binding.etPassword.getString().isEmpty()) {
            showToast(getString(R.string.error_enter_password))
            return false
        }

        if (!binding.etPassword.isValidPassword()) {
            showToast(getString(R.string.error_password_length_must_be_more_than_characters))
            return false
        }
        return true
    }

    private fun loginApi() {
        showLoading()

        signInViewModel.signIn(
            mobile = binding.etMobile.getString(),
            password = binding.etPassword.getString(),
            deviceToken = sharedPreference.deviceToken,
        )
    }

    private fun observer() {
        signInViewModel.onError.observe(this) {
            hideLoading()
            ErrorUtil.handlerGeneralError(this, binding.root, it)
        }

        signInViewModel.signInResponse.observe(this) {
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

            if ((it.result.user_name != null) && (it.result.user_name != "")) {
                sharedPreference.userName = it.result.user_name
            }

            if ((it.result.image != null) && (it.result.image != "")) {
                sharedPreference.profilePic = it.result.image
            }

            if ((it.result.gender != null) && (it.result.gender != "")) {
                sharedPreference.userGender = it.result.gender
            }

            if (it.result.id != null) {
                sharedPreference.userId = it.result.id.toString()
            }

            if ((it.result.name != null) && (it.result.name != "")) {
                sharedPreference.name = it.result.name
            } else {
                sharedPreference.name = it.result.user_name ?: ""
            }

            if ((it.result.about != null) && (it.result.about != "")) {
                sharedPreference.about = it.result.about
            }

            if (sharedPreference.chatWallpaperSet) {
                if ((it.result.background_image != null) && (it.result.background_image != "")) {
                    sharedPreference.chatWallpaper = it.result.background_image
                }
            }

            when {
                it.result.otp_verify_status == 0 -> {
                    startActivityWithDefaultAnimation(OtpVerifyActivity.getIntent(this))
                }
                it.result.profile_status == 0 -> {
                    startActivityWithDefaultAnimation(CreateProfileActivity.getIntent(this))
                }
                it.result.profile_status == 1 -> {
                    val firebaseUser = FirebaseUser()
                    firebaseUser.countryCode = sharedPreference.countryCode
                    firebaseUser.name = sharedPreference.name
                    firebaseUser.uid = sharedPreference.userId.toLong()
                    firebaseUser.phone = sharedPreference.mobileNumber
                    firebaseUser.photo = sharedPreference.profilePic
                    firebaseUser.status = "Hey I am using Haallo!!"
                    firebaseUser.userName = sharedPreference.userName
                    firebaseUser.ver = "1.0"
                    val userId = "u_${sharedPreference.userId}"
                    firebaseDbHandler.saveUser(userId, firebaseUser)

                    startActivityWithDefaultAnimation(HomeActivity.getIntent(this))
                    finish()
                }
            }
        }
    }
}