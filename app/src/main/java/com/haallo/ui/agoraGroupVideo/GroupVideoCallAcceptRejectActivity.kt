package com.haallo.ui.agoraGroupVideo

import android.app.KeyguardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.*
import android.view.View
import android.view.WindowManager
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.haallo.R
import com.haallo.constant.IntentConstant
import com.haallo.databinding.ActivityVideoCallAcceptRejectBinding
import com.haallo.ui.agoraGroupVideo.openvcall.model.ConstantApp
import com.haallo.ui.agoraGroupVideo.openvcall.ui.CallActivity
import com.haallo.ui.call.AudioService
import com.haallo.ui.call.EnumUtils
import com.haallo.util.checkVideoCallPermission
import com.haallo.util.showToast
import com.squareup.picasso.Picasso

class GroupVideoCallAcceptRejectActivity : com.haallo.ui.agoraGroupVideo.openvcall.ui.BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityVideoCallAcceptRejectBinding

    private var isRearCamera: Boolean = false
    private var isPauseVideo: Boolean = false
    private var isMuteVideo: Boolean = false
    private var otherUserId: String? = ""
    private var otherUserName: String? = ""
    private var otherUserPic: String? = ""
    private var channelName: String? = ""
    private var handler: Handler = Handler(Looper.getMainLooper())
    private var videoCallDataBase: DatabaseReference? = null
    private lateinit var mService: AudioService
    private var mBound: Boolean = false
    private var mp: MediaPlayer = MediaPlayer()

    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            val binder = service as AudioService.LocalBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVideoCallAcceptRejectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUIandEvent()
        initControl()
    }


    //Get Data From Intent
    private fun getDataFromIntent() {
        otherUserId = intent.getStringExtra(IntentConstant.OTHER_USER_ID)
        otherUserName = intent.getStringExtra(IntentConstant.OTHER_USER_NAME)
        otherUserPic = intent.getStringExtra(IntentConstant.OTHER_USER_IMAGE)
        channelName = intent.getStringExtra(IntentConstant.CHANEL_CALL_ID)
        videoCallDataBase =
            FirebaseDatabase.getInstance().reference.child("Call").child(channelName!!)
                .child("call_status")
        setCallStatus(EnumUtils.CallState.RINGING.value)
    }

    //Set data
    private fun setData() {
        if (otherUserPic != null && otherUserPic != "") {
            Picasso.get().load(otherUserPic).into(binding.ivUserPic)
        }

        if (otherUserName != null && otherUserName != "") {
            binding.tvUserName.text = otherUserName
        }
    }

    //Set Player
    private fun setPlayer() {
        Intent(this, AudioService::class.java).also { intent ->
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
        }

        mp = MediaPlayer.create(this, R.raw.samsung_original)
        mp.start()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            this.window.addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
    }

    //All Controls Defines Here
    fun initControl() {
        binding.ivRotateBackCamera.setOnClickListener(this)
        binding.ivRotateFrontCamera.setOnClickListener(this)
        binding.ivPauseVideo.setOnClickListener(this)
        binding.ivResumeVideo.setOnClickListener(this)
        binding.ivMute.setOnClickListener(this)
        binding.ivUnMute.setOnClickListener(this)
        binding.ivReceiveCall.setOnClickListener(this)
        binding.ivDisConnectCall.setOnClickListener(this)
    }

    //OnClick Listener
    override fun onClick(v: View) {
        when (v.id) {
            R.id.ivRotateBackCamera -> {
                binding.ivRotateBackCamera.visibility = View.GONE
                binding.ivRotateFrontCamera.visibility = View.VISIBLE
                isRearCamera = true
            }

            R.id.ivRotateFrontCamera -> {
                binding.ivRotateBackCamera.visibility = View.VISIBLE
                binding.ivRotateFrontCamera.visibility = View.GONE
                isRearCamera = false
            }

            R.id.ivPauseVideo -> {
                binding.ivPauseVideo.visibility = View.GONE
                binding.ivResumeVideo.visibility = View.VISIBLE
                isPauseVideo = true
            }

            R.id.ivResumeVideo -> {
                binding.ivPauseVideo.visibility = View.VISIBLE
                binding.ivResumeVideo.visibility = View.GONE
                isPauseVideo = false
            }

            R.id.ivMute -> {
                binding.ivMute.visibility = View.GONE
                binding.ivUnMute.visibility = View.VISIBLE
                isMuteVideo = false
            }

            R.id.ivUnMute -> {
                binding.ivMute.visibility = View.VISIBLE
                binding.ivUnMute.visibility = View.GONE
                isMuteVideo = true
            }

            R.id.ivReceiveCall -> {
                if (checkVideoCallPermission(this)) {
                    showToast("gROUP VIDEO")
                    forwardToRoom()
                    finish()
                    /*  setCallStatus(EnumUtils.CallState.CONNECTED.value)
                      startActivity(
                          Intent(this, OneToOneVideoCallActivity::class.java)
                              .putExtra(IntentConstant.OTHER_USER_ID, otherUserId)
                              .putExtra(IntentConstant.OTHER_USER_NAME, otherUserName)
                              .putExtra(
                                  IntentConstant.CALL_STATUS,
                                  EnumUtils.CallStatus.INCOMING.value
                              )
                              .putExtra(IntentConstant.CHANEL_NAME, channelName)
                              .putExtra(IntentConstant.IS_REAR_CAMERA, isRearCamera)
                              .putExtra(IntentConstant.IS_MUTE_VIDEO, isMuteVideo)
                              .putExtra(IntentConstant.IS_PAUSED_VIDEO, isPauseVideo)
                      )
                      finish()*/
                }
            }

            R.id.ivDisConnectCall -> {
                setCallStatus(EnumUtils.CallState.REJECTED.value)
                finish()
            }
        }
    }

    //onPause
    override fun onPause() {
        super.onPause()
        if (mp.isPlaying) {
            mp.stop()
        }
        mp.release()
        handler.removeCallbacksAndMessages(null)
    }

    override fun initUIandEvent() {
        getDataFromIntent()
        setData()
        setPlayer()

        //To Check Call Pic Or Not
        handler.postDelayed({
            setCallStatus(EnumUtils.CallState.NOT_ANSWERED.value)
            finish()
        }, 30000)
    }

    override fun deInitUIandEvent() {

    }

    //Set Call Status
    private fun setCallStatus(value: String) {
        videoCallDataBase?.setValue(value)
    }

    private fun forwardToRoom() {
        mp.stop()
        if (mp.isPlaying) {
            mp.stop()
        }
        mp.release()
        vSettings().mChannelName = otherUserName
        vSettings().mEncryptionKey = channelName
        val i = Intent(this, CallActivity::class.java)
        i.putExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME, otherUserName)
        i.putExtra(ConstantApp.ACTION_KEY_ENCRYPTION_KEY, channelName)
        i.putExtra(
            ConstantApp.ACTION_KEY_ENCRYPTION_MODE,
            resources.getStringArray(R.array.encryption_mode_values)[1]
        )
        startActivity(i)
    }

}