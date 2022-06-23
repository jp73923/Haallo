package com.haallo.util

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.akexorcist.screenshotdetection.ScreenshotDetectionDelegate
import com.haallo.R
import com.haallo.ui.chat.dialog.CustomProgressDialog
import com.haallo.ui.chat.firebaseDb.FirebaseDbHandler
import com.haallo.constant.Constants

open abstract class ScreenshotDetectionActivity : AppCompatActivity(), ScreenshotDetectionDelegate.ScreenshotDetectionListener {
    private val screenshotDetectionDelegate = ScreenshotDetectionDelegate(this, this)
    lateinit var sharedPreference: SharedPreferenceUtil
    override fun onStart() {
        super.onStart()
        screenshotDetectionDelegate.startScreenshotDetection()
    }

    override fun onStop() {
        super.onStop()
        screenshotDetectionDelegate.stopScreenshotDetection()
    }

    override fun onScreenCaptured(path: String) {
        // Do something when screen was captured
    }

    override fun onScreenCapturedWithDeniedPermission() {
        // Do something when screen was captured but read external storage permission has denied
    }

    abstract fun initView()
    abstract fun initControl()

    private var doubleBackToExitPressedOnce: Boolean = false

    override fun onBackPressed() {
        when {
            supportFragmentManager.backStackEntryCount > 0 -> super.onBackPressed()
            isTaskRoot -> {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed()
                    return
                }
                this.doubleBackToExitPressedOnce = true
                Toast.makeText(
                    this,
                    getString(R.string.back_press_exit_line),
                    Toast.LENGTH_SHORT
                )
                    .show()
                Handler(Looper.getMainLooper()).postDelayed(
                    { doubleBackToExitPressedOnce = false },
                    Constants.BACK_PRESS_TIME_INTERVAL
                )
            }
            else -> super.onBackPressed()
        }
    }

    fun hideLoading() {
        ProgressDialogUtil.getInstance().hideProgress()
    }

    fun showLoading() {
        hideLoading()
        ProgressDialogUtil.getInstance().showProgress(this, false)
    }

    fun showError(context: Context?, view: View?, throwable: Throwable) {
        ErrorUtil.handlerGeneralError(context, view, throwable)
    }

    val firebaseDbHandler by lazy {
        FirebaseDbHandler(this)
    }

    val progressDialog: CustomProgressDialog by lazy {
        CustomProgressDialog(this, "Please wait...")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreference = SharedPreferenceUtil.getInstance(this)
    }
}