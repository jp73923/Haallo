package com.haallo.base

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.*
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.akexorcist.screenshotdetection.ScreenshotDetectionDelegate
import com.haallo.R
import com.haallo.ui.agoraGroupVideo.openvcall.AGApplication
import com.haallo.ui.agoraGroupVideo.openvcall.model.AGEventHandler
import com.haallo.ui.agoraGroupVideo.openvcall.model.ConstantApp
import com.haallo.ui.agoraGroupVideo.openvcall.model.CurrentUserSettings
import com.haallo.ui.agoraGroupVideo.openvcall.model.EngineConfig
import com.haallo.ui.agoraGroupVideo.openvcall.ui.BaseActivity
import com.haallo.ui.agoraGroupVideo.propeller.Constant
import com.haallo.ui.chat.dialog.CustomProgressDialog
import com.haallo.ui.chat.firebaseDb.FirebaseDbHandler
import com.haallo.constant.Constants
import com.haallo.util.ErrorUtil
import com.haallo.util.ProgressDialogUtil
import com.haallo.util.SharedPreferenceUtil
import io.agora.rtc.RtcEngine
import io.agora.rtc.internal.EncryptionConfig
import io.agora.rtc.video.VideoCanvas
import io.agora.rtc.video.VideoEncoderConfiguration
import io.agora.rtc.video.VideoEncoderConfiguration.FRAME_RATE
import io.agora.rtc.video.VideoEncoderConfiguration.VideoDimensions
import org.slf4j.LoggerFactory
import java.util.*

abstract class Base : AppCompatActivity(), ScreenshotDetectionDelegate.ScreenshotDetectionListener {

    private val log = LoggerFactory.getLogger(BaseActivity::class.java)
    lateinit var sharedPreference: SharedPreferenceUtil
    private val screenshotDetectionDelegate = ScreenshotDetectionDelegate(this, this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreference = SharedPreferenceUtil.getInstance(this)
        val layout = findViewById<View>(Window.ID_ANDROID_CONTENT)
        val vto = layout.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    layout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                } else {
                    layout.viewTreeObserver.removeGlobalOnLayoutListener(this)
                }
                initUIandEvent()
            }
        })
    }

    protected abstract fun initUIandEvent()

    protected abstract fun deInitUIandEvent()


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

    protected fun permissionGranted() {}

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        Handler().postDelayed(Runnable {
            if (isFinishing) {
                return@Runnable
            }
            val checkPermissionResult = checkSelfPermissions()
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                // so far we do not use OnRequestPermissionsResultCallback
            }
        }, 500)
    }

    private fun checkSelfPermissions(): Boolean {
        return checkSelfPermission(
            Manifest.permission.RECORD_AUDIO,
            ConstantApp.PERMISSION_REQ_ID_RECORD_AUDIO
        ) &&
                checkSelfPermission(
                    Manifest.permission.CAMERA,
                    ConstantApp.PERMISSION_REQ_ID_CAMERA
                )
    }

    override fun onDestroy() {
        deInitUIandEvent()
        super.onDestroy()
    }

    fun closeIME(v: View) {
        val mgr = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        mgr.hideSoftInputFromWindow(v.windowToken, 0) // 0 force close IME
        v.clearFocus()
    }

    fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
        log.debug("checkSelfPermission $permission $requestCode")
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(permission),
                requestCode
            )
            return false
        }
        if (Manifest.permission.CAMERA == permission) {
            permissionGranted()
        }
        return true
    }

    protected fun application(): AGApplication {
        return application as AGApplication
    }

    protected fun rtcEngine(): RtcEngine {
        return application().rtcEngine()
    }

    protected fun config(): EngineConfig {
        return application().config()
    }

    protected fun addEventHandler(handler: AGEventHandler?) {
        application().addEventHandler(handler)
    }

    protected fun removeEventHandler(handler: AGEventHandler?) {
        application().remoteEventHandler(handler)
    }

    protected fun vSettings(): CurrentUserSettings? {
        return application().userSettings()
    }

    fun showLongToast(msg: String?) {
        runOnUiThread { Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show() }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>, grantResults: IntArray
    ) {
        log.debug(
            "onRequestPermissionsResult " + requestCode + " " + Arrays.toString(permissions) + " " + Arrays.toString(
                grantResults
            )
        )
        when (requestCode) {
            ConstantApp.PERMISSION_REQ_ID_RECORD_AUDIO -> {
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    checkSelfPermission(
                        Manifest.permission.CAMERA,
                        ConstantApp.PERMISSION_REQ_ID_CAMERA
                    )
                } else {
                    finish()
                }
            }
            ConstantApp.PERMISSION_REQ_ID_CAMERA -> {
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    permissionGranted()
                } else {
                    finish()
                }
            }
        }
    }

    protected fun virtualKeyHeight(): Int {
        val hasPermanentMenuKey = ViewConfiguration.get(application).hasPermanentMenuKey()
        if (hasPermanentMenuKey) {
            return 0
        }

        // Also can use getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        val metrics = DisplayMetrics()
        val display = windowManager.defaultDisplay
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(metrics)
        } else {
            display.getMetrics(metrics)
        }
        var fullHeight = metrics.heightPixels
        var fullWidth = metrics.widthPixels
        if (fullHeight < fullWidth) {
            fullHeight = fullHeight xor fullWidth
            fullWidth = fullWidth xor fullHeight
            fullHeight = fullHeight xor fullWidth
        }
        display.getMetrics(metrics)
        var newFullHeight = metrics.heightPixels
        var newFullWidth = metrics.widthPixels
        if (newFullHeight < newFullWidth) {
            newFullHeight = newFullHeight xor newFullWidth
            newFullWidth = newFullWidth xor newFullHeight
            newFullHeight = newFullHeight xor newFullWidth
        }
        var virtualKeyHeight = fullHeight - newFullHeight
        if (virtualKeyHeight > 0) {
            return virtualKeyHeight
        }
        virtualKeyHeight = fullWidth - newFullWidth
        return virtualKeyHeight
    }

    protected fun getStatusBarHeight(): Int {
        // status bar height
        var statusBarHeight = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resourceId)
        }
        if (statusBarHeight == 0) {
            log.error("Can not get height of status bar")
        }
        return statusBarHeight
    }

    protected fun getActionBarHeight(): Int {
        // action bar height
        var actionBarHeight = 0
        val styledAttributes =
            this.theme.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
        actionBarHeight = styledAttributes.getDimension(0, 0f).toInt()
        styledAttributes.recycle()
        if (actionBarHeight == 0) {
            log.error("Can not get height of action bar")
        }
        return actionBarHeight
    }

    /**
     *
     * Starts/Stops the local video preview
     *
     * Before calling this method, you must:
     * Call the enableVideo method to enable the video.
     *
     * @param start Whether to start/stop the local preview
     * @param view The SurfaceView in which to render the preview
     * @param uid User ID.
     */
    protected fun preview(start: Boolean, view: SurfaceView?, uid: Int) {
        if (start) {
            rtcEngine().setupLocalVideo(VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, uid))
            rtcEngine().startPreview()
        } else {
            rtcEngine().stopPreview()
        }
    }

    /**
     * Allows a user to join a channel.
     *
     * Users in the same channel can talk to each other, and multiple users in the same channel can start a group chat. Users with different App IDs cannot call each other.
     *
     * You must call the leaveChannel method to exit the current call before joining another channel.
     *
     * A successful joinChannel method call triggers the following callbacks:
     *
     * The local client: onJoinChannelSuccess.
     * The remote client: onUserJoined, if the user joining the channel is in the Communication profile, or is a BROADCASTER in the Live Broadcast profile.
     *
     * When the connection between the client and Agora's server is interrupted due to poor
     * network conditions, the SDK tries reconnecting to the server. When the local client
     * successfully rejoins the channel, the SDK triggers the onRejoinChannelSuccess callback
     * on the local client.
     *
     * @param channel The unique channel name for the AgoraRTC session in the string format.
     * @param uid User ID.
     */
    fun joinChannel(channel: String, uid: Int) {
        var accessToken: String? = applicationContext.getString(R.string.agora_access_token)
        if (TextUtils.equals(accessToken, "") || TextUtils.equals(
                accessToken,
                "<#YOUR ACCESS TOKEN#>"
            )
        ) {
            accessToken = null // default, no token
        }
        rtcEngine().joinChannel(accessToken, channel, "OpenVCall", uid)
        config().mChannel = channel
        enablePreProcessor()
        log.debug("joinChannel $channel $uid")
    }

    /**
     * Allows a user to leave a channel.
     *
     * After joining a channel, the user must call the leaveChannel method to end the call before
     * joining another channel. This method returns 0 if the user leaves the channel and releases
     * all resources related to the call. This method call is asynchronous, and the user has not
     * exited the channel when the method call returns. Once the user leaves the channel,
     * the SDK triggers the onLeaveChannel callback.
     *
     * A successful leaveChannel method call triggers the following callbacks:
     *
     * The local client: onLeaveChannel.
     * The remote client: onUserOffline, if the user leaving the channel is in the
     * Communication channel, or is a BROADCASTER in the Live Broadcast profile.
     *
     * @param channel Channel Name
     */
    fun leaveChannel(channel: String) {
        log.debug("leaveChannel $channel")
        config().mChannel = null
        disablePreProcessor()
        rtcEngine().leaveChannel()
        config().reset()
    }

    /**
     * Enables image enhancement and sets the options.
     */
    protected fun enablePreProcessor() {
        if (Constant.BEAUTY_EFFECT_ENABLED) {
            rtcEngine().setBeautyEffectOptions(true, Constant.BEAUTY_OPTIONS)
        }
    }

    fun setBeautyEffectParameters(lightness: Float, smoothness: Float, redness: Float) {
        Constant.BEAUTY_OPTIONS.lighteningLevel = lightness
        Constant.BEAUTY_OPTIONS.smoothnessLevel = smoothness
        Constant.BEAUTY_OPTIONS.rednessLevel = redness
    }


    /**
     * Disables image enhancement.
     */
    protected fun disablePreProcessor() {
        // do not support null when setBeautyEffectOptions to false
        rtcEngine().setBeautyEffectOptions(false, Constant.BEAUTY_OPTIONS)
    }

    protected fun configEngine(
        videoDimension: VideoDimensions,
        fps: FRAME_RATE,
        encryptionKey: String?,
        encryptionMode: String
    ) {
        val config = EncryptionConfig()
        if (!TextUtils.isEmpty(encryptionKey)) {
            config.encryptionKey = encryptionKey
            if (TextUtils.equals(encryptionMode, "AES-128-XTS")) {
                config.encryptionMode = EncryptionConfig.EncryptionMode.AES_128_XTS
            } else if (TextUtils.equals(encryptionMode, "AES-256-XTS")) {
                config.encryptionMode = EncryptionConfig.EncryptionMode.AES_256_XTS
            }
            rtcEngine().enableEncryption(true, config)
        } else {
            rtcEngine().enableEncryption(false, config)
        }
        log.debug("configEngine $videoDimension $fps $encryptionMode")
        // Set the Resolution, FPS. Bitrate and Orientation of the video
        rtcEngine().setVideoEncoderConfiguration(
            VideoEncoderConfiguration(
                videoDimension,
                fps,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
            )
        )
    }

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


}