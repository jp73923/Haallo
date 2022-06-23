package com.haallo.ui.splashToHome.signIn

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.haallo.R
import com.haallo.base.OldBaseActivity
import com.haallo.constant.IntentConstant
import com.haallo.databinding.ActivitySignInBinding
import com.haallo.ui.chat.model.UserModel
import com.haallo.ui.home.HomeActivityOld
import com.haallo.ui.splashToHome.SignInToHomeViewModelOld
import com.haallo.ui.splashToHome.forgotAndResetPassword.ForgotPasswordActivityOld
import com.haallo.ui.splashToHome.otp.OtpActivityOld
import com.haallo.ui.splashToHome.profile.CreateProfileActivityOld
import com.haallo.ui.splashToHome.registration.RegistrationActivityOld
import com.haallo.util.*

class SignInActivityOld : OldBaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var signInToHomeViewModel: SignInToHomeViewModelOld
    private val permissionRequestCode = 451

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    override fun initView() {
        sharedPreference.screenWidth = findScreenWidth(window)

        if (sharedPreference.nightTheme) {
            binding.rootLayoutLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.appNightModeBackground))
        } else {
            binding.rootLayoutLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        }

        signInToHomeViewModel = ViewModelProvider(this).get(SignInToHomeViewModelOld::class.java)

        binding.tvHeading.text = getString(R.string.log_in)

        binding.btnBack.setOnClickListener(this)
        binding.tvForgotPassword.setOnClickListener(this)
        binding.btnLogin.setOnClickListener(this)
        binding.tvRegister.setOnClickListener(this)

        if (!PermissionsUtil.hasPermissions(this)) {
            requestPermissions()
        }

        observer()
    }

    override fun initControl() {

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnBack -> {
                onBackPressed()
            }
            R.id.tvForgotPassword -> {
                startActivity(Intent(this, ForgotPasswordActivityOld::class.java))
            }
            R.id.btnLogin -> {
                if (isInputValid()) {
                    loginApi()
                }
            }
            R.id.tvRegister -> {
                startActivity(Intent(this, RegistrationActivityOld::class.java))
                finish()
            }
        }
    }

    //Permission
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, PermissionsUtil.permissions, permissionRequestCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (!PermissionsUtil.permissionsGranted(grantResults)) {
            showAlertDialog()
        }
    }

    private fun showAlertDialog() {
        val positiveClickListener = DetachableClickListener.wrap { _, _ -> requestPermissions() }
        val negativeClickListener = DetachableClickListener.wrap { _, _ -> finish() }
        val builder = AlertDialog.Builder(this)
            .setTitle(R.string.missing_permissions)
            .setMessage(R.string.you_have_to_grant_permissions)
            .setPositiveButton(R.string.ok, positiveClickListener)
            .setNegativeButton(R.string.no_close_the_app, negativeClickListener)
            .create()

        //avoid memory leaks
        positiveClickListener.clearOnDetach(builder)
        negativeClickListener.clearOnDetach(builder)
    }

    //Data Validation and API Call
    private fun isInputValid(): Boolean {
        if (binding.etMobile.getString().isEmpty()) {
            binding.etMobile.requestFocus()
            showToast(getString(R.string.please_enter_mobile_number))
            return false
        }

        if (!binding.etMobile.isValidMobileNumber()) {
            showToast(getString(R.string.please_enter_valid_mobile_number))
            return false
        }

        if (binding.etPassword.getString().isEmpty()) {
            showToast(getString(R.string.please_enter_password))
            return false
        }

        if (!binding.etPassword.isValidPassword()) {
            showToast(getString(R.string.password_length_must_be_more_than_characters))
            return false
        }
        return true
    }

    private fun loginApi() {
        showLoading()

        signInToHomeViewModel.signInApi(
            mobile = binding.etMobile.getString(),
            password = binding.etPassword.getString(),
            deviceToken = sharedPreference.deviceToken,
        )
    }

    private fun observer() {
        signInToHomeViewModel.onError.observe(this) {
            hideLoading()
            ErrorUtil.handlerGeneralError(this, binding.rootLayoutLogin, it)
        }

        signInToHomeViewModel.signInResponse.observe(this) {
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
                    startActivity(
                        Intent(this, OtpActivityOld::class.java).putExtra(IntentConstant.IS_FROM, IntentConstant.SIGN_IN)
                    )
                }
                it.result.profile_status == 0 -> {
                    startActivity(Intent(this, CreateProfileActivityOld::class.java))
                }
                it.result.profile_status == 1 -> {
                    val userModel = UserModel()
                    userModel.countryCode = sharedPreference.countryCode
                    userModel.name = sharedPreference.name
                    userModel.uid = sharedPreference.userId
                    userModel.phone = sharedPreference.mobileNumber
                    userModel.photo = sharedPreference.profilePic
                    userModel.status = "Hey I am using Haallo!!"
                    userModel.userName = sharedPreference.userName
                    userModel.ver = "1.0"
                    val userId = "u_${sharedPreference.userId}"
                    firebaseDbHandler.saveUser(userId, userModel)

                    startActivity(Intent(this, HomeActivityOld::class.java))
                    finishAffinity()
                    sharedPreference.halloFlag = 1
                }
            }
        }
    }
}