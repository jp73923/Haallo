package com.haallo.ui.splashToHome.splash

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
import com.haallo.service.FetchContactsService
import com.haallo.ui.home.HomeActivity
import com.haallo.ui.splashToHome.registration.RegistrationActivityOld
import com.haallo.ui.splashToHome.welcome.WelcomeActivityOld
import com.haallo.util.AppSignatureHelper
import com.haallo.util.DetachableClickListener
import com.haallo.util.PermissionsUtil
import com.haallo.util.findScreenWidth

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

        initView()
    }

    //All UI Changes From Here
    private fun initView() {
        if (sharedPreference.isFirstTime == 1) {
            if (sharedPreference.halloFlag == 1) {
                startHomeActivity()
            } else {
                startActivity(Intent(this, RegistrationActivityOld::class.java))
                finish()
            }
        } else {
            sharedPreference.isFirstTime = 1
            checkAppPermission()
            sharedPreference.screenWidth = findScreenWidth(window)
            hashKey()
        }
    }

    private fun startContactFetch() {
        FetchContactsService.enqueueWork(this, Intent(this, FetchContactsService::class.java), true)
    }

    //Check permissions
    private fun checkAppPermission() {
        if (PermissionsUtil.hasPermissions(this)) {
            setSplashProgress()
            handlerFunction()
            startContactFetch()
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
            if (sharedPreference.halloFlag == 1) {
                startHomeActivity()
            } else {
                startActivity(Intent(this, WelcomeActivityOld::class.java))
                finish()
            }
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
            if (sharedPreference.halloFlag == 1) {
                startHomeActivity()
            } else {
                startActivity(Intent(this, WelcomeActivityOld::class.java))
                finish()
            }
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
            if (sharedPreference.halloFlag == 1) {
                startHomeActivity()
            } else {
                startActivity(Intent(this, WelcomeActivityOld::class.java))
                finish()
            }
        }, 1200)
    }

    //If Progress = 75
    private fun progress75() {
        handler.postDelayed({
            binding.pbLoading.progress = 100
            sharedPreference.splashProgress = 100
        }, 500)

        handler.postDelayed({
            if (sharedPreference.halloFlag == 1) {
                startHomeActivity()
            } else {
                startActivity(Intent(this, WelcomeActivityOld::class.java))
                finish()
            }
        }, 700)
    }

    //If Progress = 100
    private fun progress100() {
        if (sharedPreference.halloFlag == 1) {
            startHomeActivity()
        } else {
            startActivity(Intent(this, WelcomeActivityOld::class.java))
            finish()
        }
    }

    //On Back Pressed Method
    override fun onBackPressed() {

    }

    //Find Data From the SharedPreferences
    private fun getDataFromSharedPreferences() {
        splashProgress = sharedPreference.splashProgress
    }

    private fun startHomeActivity() {
        startActivityWithDefaultAnimation(HomeActivity.getIntent(this))
        finish()
    }
}