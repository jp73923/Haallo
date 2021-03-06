package com.haallo.ui.splash

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.haallo.R
import com.haallo.base.BaseActivity
import com.haallo.base.extension.startActivityWithDefaultAnimation
import com.haallo.databinding.ActivitySplashBinding
import com.haallo.ui.home.HomeActivity
import com.haallo.ui.signup.SignUpActivity
import com.haallo.ui.welcome.WelcomeActivity
import com.haallo.util.AppSignatureHelper
import com.haallo.util.DetachableClickListener
import com.haallo.util.PermissionsUtil

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context): Intent {
            val intent = Intent(context, SplashActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            return intent
        }
    }

    private lateinit var binding: ActivitySplashBinding

    private val handler: Handler = Handler(Looper.myLooper()!!)
    private var splashProgress: Int = 25
    private val permissionRequestCode = 451

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listenToViewEvent()
    }

    //All UI Changes From Here
    private fun listenToViewEvent() {
        if (sharedPreference.isFirstTime == 1) {
            startNextActivity()
        } else {
            sharedPreference.isFirstTime = 1
            checkAppPermission()
            hashKey()
        }
    }

    //Check permissions
    private fun checkAppPermission() {
        if (PermissionsUtil.hasPermissions(this)) {
            setSplashProgress()
            handlerFunction()
        } else {
            requestPermissions()
        }//request permissions if there are no permissions granted
    }

    //Request Permission
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, PermissionsUtil.permissions, permissionRequestCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        /*   if (PermissionsUtil.permissionsGranted(grantResults)) {
               setSplashProgress()
               handlerFunction()
           } else
               showAlertDialog()*/
        setSplashProgress()
        handlerFunction()
    }

    private fun showAlertDialog() {
        val positiveClickListener = DetachableClickListener.wrap { _, i -> requestPermissions() }
        val negativeClickListener = DetachableClickListener.wrap { _, i -> finish() }
        val builder = AlertDialog.Builder(this)
            .setTitle(R.string.missing_permissions)
            .setMessage(R.string.you_have_to_grant_permissions)
            .setPositiveButton(R.string.ok, positiveClickListener)
            .setNegativeButton(R.string.no_close_the_app, negativeClickListener)
            .create()
        //avoid memory leaks
        positiveClickListener.clearOnDetach(builder)
        negativeClickListener.clearOnDetach(builder)
        builder.show()
    }

    //Set Splash Progress = 25
    private fun setSplashProgress() {
        sharedPreference.splashProgress = 25
    }

    //Find the Hash Key for OTP Automatic Deduct
    private fun hashKey() {
        val appSignatureHelper = AppSignatureHelper(this)
        Log.e("keyHash", appSignatureHelper.appSignatures[0])
    }

    //Function for the Handler
    private fun handlerFunction() {
        handler.postDelayed({
            binding.pbLoading.progress = 25
            sharedPreference.splashProgress = 25
        }, 1000)

        handler.postDelayed({
            binding.pbLoading.progress = 50
            sharedPreference.splashProgress = 50
        }, 1500)

        handler.postDelayed({
            binding.pbLoading.progress = 75
            sharedPreference.splashProgress = 75
        }, 2000)

        handler.postDelayed({
            binding.pbLoading.progress = 100
            sharedPreference.splashProgress = 100
        }, 2500)

        handler.postDelayed({
            startNextActivity()
        }, 2700)
    }

    //On Pause Method
    override fun onPause() {
        handler.removeCallbacksAndMessages(null)
        super.onPause()
    }

    //On Restart Method
    override fun onRestart() {
        getDataFromSharedPreferences()
        when (splashProgress) {
            25 -> {
                progress25()
            }
            50 -> {
                progress50()
            }
            75 -> {
                progress75()
            }
            100 -> {
                progress100()
            }
        }
        super.onRestart()
    }

    //If Progress = 25
    private fun progress25() {
        handler.postDelayed({
            binding.pbLoading.progress = 50
            sharedPreference.splashProgress = 50
        }, 500)

        handler.postDelayed({
            binding.pbLoading.progress = 75
            sharedPreference.splashProgress = 75
        }, 1000)

        handler.postDelayed({
            binding.pbLoading.progress = 100
            sharedPreference.splashProgress = 100
        }, 1500)

        handler.postDelayed({
            startNextActivity()
        }, 1700)
    }

    //If Progress = 50
    private fun progress50() {
        handler.postDelayed({
            binding.pbLoading.progress = 75
            sharedPreference.splashProgress = 75
        }, 500)

        handler.postDelayed({
            binding.pbLoading.progress = 100
            sharedPreference.splashProgress = 100
        }, 1000)

        handler.postDelayed({
            startNextActivity()
        }, 1200)
    }

    //If Progress = 75
    private fun progress75() {
        handler.postDelayed({
            binding.pbLoading.progress = 100
            sharedPreference.splashProgress = 100
        }, 500)

        handler.postDelayed({
            startNextActivity()
        }, 700)
    }

    //If Progress = 100
    private fun progress100() {
        startNextActivity()
    }

    //On Back Pressed Method
    override fun onBackPressed() {

    }

    //Find Data From the SharedPreferences
    private fun getDataFromSharedPreferences() {
        splashProgress = sharedPreference.splashProgress
    }

    private fun startNextActivity() {
        val selectedLanguage = sharedPreference.selectedLanguage
        if (selectedLanguage.isNotEmpty()) {
            val userId = sharedPreference.userId
            if (userId.isNotEmpty()) {
                startActivityWithDefaultAnimation(HomeActivity.getIntent(this))
            } else {
                startActivityWithDefaultAnimation(SignUpActivity.getIntent(this))
            }
        } else {
            startActivityWithDefaultAnimation(WelcomeActivity.getIntent(this))
        }
        finish()
    }
}