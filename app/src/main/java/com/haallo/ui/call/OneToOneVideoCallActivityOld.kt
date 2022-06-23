package com.haallo.ui.call

import android.app.KeyguardManager
import android.content.*
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.database.*
import com.haallo.R
import com.haallo.base.OldBaseActivity
import com.haallo.constant.IntentConstant
import com.haallo.databinding.ActivityVideoCallBinding
import com.haallo.util.checkVideoCallPermission
import com.haallo.util.showLog
import com.haallo.util.showToast
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import io.agora.rtc.video.VideoEncoderConfiguration

class OneToOneVideoCallActivityOld : OldBaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityVideoCallBinding

    private var otherUserId: String? = ""
    private var otherUserName: String? = ""
    private var channelName: String? = ""
    private var callStatus: String? = ""
    private var isRearCamera: Boolean = false
    private var isMuteVideo: Boolean = false
    private var isPauseVideo: Boolean = false
    private var mRtcEngine: RtcEngine? = null
    private var videoCallFirBase: DatabaseReference? = null
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

        binding = ActivityVideoCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initControl()
    }

    //All UI Changes From Here
    override fun initView() {
        getDataFromIntent()
        setPlayer()
        firebaseCallStatusListener()
    }

    //Get Data From Intent
    private fun getDataFromIntent() {
        otherUserId = intent.getStringExtra(IntentConstant.OTHER_USER_ID)
        otherUserName = intent.getStringExtra(IntentConstant.OTHER_USER_NAME)
        callStatus = intent.getStringExtra(IntentConstant.CALL_STATUS)
        channelName = intent.getStringExtra(IntentConstant.CHANEL_NAME)
        isRearCamera = intent.getBooleanExtra(IntentConstant.IS_REAR_CAMERA, false)
        isMuteVideo = intent.getBooleanExtra(IntentConstant.IS_MUTE_VIDEO, false)
        isPauseVideo = intent.getBooleanExtra(IntentConstant.IS_PAUSED_VIDEO, false)
        videoCallFirBase =
            FirebaseDatabase.getInstance().reference.child("Call").child(channelName!!)
                .child("call_status")

        if (checkVideoCallPermission(this)) {
            setUpChannel()
        } else {
            showToast("Please provide permission for video call")
        }
    }

    //Set Player
    private fun setPlayer() {
        Intent(this, AudioService::class.java).also { intent ->
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
        }
        mp.start()

        mp = MediaPlayer.create(this, R.raw.samsung_original)

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

    //SetUp Channel
    private fun setUpChannel() {
        sharedPreference.isOnCall = true
        initializeAgoraEngine()
        mRtcEngine?.setEnableSpeakerphone(true)
        setupRemoteUserVideo()
        setupLocalUserVideo()
        joinChannel()
        if (callStatus == EnumUtils.CallStatus.INCOMING.value) {
            if (isRearCamera) {
                binding.ivRotateBackCamera.visibility = View.GONE
                binding.ivRotateFrontCamera.visibility = View.VISIBLE
                rotateCamera()
            }

            if (isMuteVideo) {
                binding.ivMute.visibility = View.GONE
                binding.ivUnMute.visibility = View.VISIBLE
                muteUnMuteVideo(true)
            }

            if (isPauseVideo) {
                binding.ivPauseVideo.visibility = View.GONE
                binding.ivResumeVideo.visibility = View.VISIBLE
                pauseAndResumeVideo(true)
            }
        }
    }

    //Initialize Agora Engine
    private fun initializeAgoraEngine() {
        try {
            mRtcEngine =
                RtcEngine.create(baseContext, getString(R.string.agora_app_id), rtcEventHandler)
        } catch (e: Exception) {
            showLog(Log.getStackTraceString(e))
            finish()
        }
    }

    //Set Up Remote User Profile
    private fun setupRemoteUserVideo() {
        mRtcEngine?.enableVideo()
        mRtcEngine?.setVideoEncoderConfiguration(
            VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_1280x720,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
            )
        )
    }

    //Set Up Local User Profile
    private fun setupLocalUserVideo() {
        mRtcEngine?.enableLocalVideo(true)
        val surfaceView = RtcEngine.CreateRendererView(baseContext)
        surfaceView.setZOrderMediaOverlay(true)
        binding.flLocalVideoViewContainer.addView(surfaceView)
        mRtcEngine?.setupLocalVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0))
    }

    //Join Channel
    private fun joinChannel() {
        mRtcEngine?.joinChannel(
            null,
            channelName,
            "Extra Optional Data",
            0
        )
    }

    //OnDestroy
    override fun onDestroy() {
        super.onDestroy()
        showLog("Destroying activity & leaving channel...")
        mRtcEngine?.leaveChannel()
        RtcEngine.destroy()
        finish()
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            broadcastReceiver,
            IntentFilter(EnumUtils.NotificationType.VIDEO_CALL_ACCEPT.value)
        )

        LocalBroadcastManager.getInstance(this).registerReceiver(
            broadcastReceiver,
            IntentFilter(EnumUtils.NotificationType.VIDEO_CALL_REJECT.value)
        )

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(
                broadcastReceiver,
                IntentFilter(EnumUtils.NotificationType.VIDEO_CALL_DISCONNECT.value)
            )
    }

    //onPause
    override fun onPause() {
        super.onPause()
        sharedPreference.isOnCall = false
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            mp.stop()
            if (mp.isPlaying) {
                mp.stop()
            }
            mp.release()

            when (intent.action) {
                EnumUtils.NotificationType.VIDEO_CALL_ACCEPT.value -> {

                }

                EnumUtils.NotificationType.VIDEO_CALL_REJECT.value -> {

                }

                EnumUtils.NotificationType.VIDEO_CALL_DISCONNECT.value -> {

                }
            }
        }
    }

    private val rtcEventHandler = object : IRtcEngineEventHandler() {
        /**
         * callback is triggered upon receiving and successfully decoding the first frame of the remote video
         */
        override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {
            runOnUiThread {
                mp.stop()
                if (mp.isPlaying) {
                    mp.stop()
                }
                mp.release()
                setupRemoteVideo(uid)
            }
        }

        /**
         * notifies the application that another (remote) user with [uid] has joined the channel
         */
        override fun onUserJoined(uid: Int, elapsed: Int) {
            super.onUserJoined(uid, elapsed)
            setCallState(EnumUtils.CallState.CONNECTED.value)
            showLog("onUserJoined() >> uid: $uid \n elapsed : $elapsed")
            runOnUiThread {
                binding.chronometerCall.visibility = View.VISIBLE
                binding.chronometerCall.base = SystemClock.elapsedRealtime()
                binding.chronometerCall.start()
            }
        }

        /**
         * @param channel Channel Name for the current session
         * @param uid Unique identifier assigned to current (self) user
         * @param elapsed Elapsed Time i guess.
         *
         * Callback function to notify that current (self) user has joined the [channel]
         * with unique identifier [uid]
         */
        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            super.onJoinChannelSuccess(channel, uid, elapsed)
            showLog("onJoinChannelSuccess() channel: $channel")
            showLog("onJoinChannelSuccess() uid: $uid")

        }

        /**
         * callback to notify the application that the user (self) has successfully left the channel.
         */
        override fun onLeaveChannel(stats: RtcStats?) {
            super.onLeaveChannel(stats)
            setCallState(EnumUtils.CallState.DISCONNECTED.value)
            showLog("onLeaveChannel()")
        }

        /**
         * notifies the application that a user (remote) has left the channel or is offline.
         */
        override fun onUserOffline(uid: Int, reason: Int) {
            showLog("onUserOffline()")
            runOnUiThread {
                onRemoteUserOffline()
            }
        }

        override fun onUserMuteAudio(uid: Int, muted: Boolean) {
            super.onUserMuteAudio(uid, muted)
            runOnUiThread { onRemoteUserAudioMuted(muted) }
        }

        /**
         * callback indicates that some other user (remote) has paused/resumed his/her video stream.
         */
        override fun onUserMuteVideo(uid: Int, muted: Boolean) {
            runOnUiThread { onRemoteUserPausedVideo(uid, muted) }
        }

        /**
         * callback indicates that some other user has enabled/disabled the video function.
         * Disabling the video function means that the user can only use voice call,
         * can neither show/send their own videos nor receive or display videos from other people.
         */
        override fun onUserEnableVideo(uid: Int, enabled: Boolean) {
            super.onUserEnableVideo(uid, enabled)
            showLog("onUserEnableVideo => uid : $uid , enabled : $enabled")
        }

        /**
         * callback indicates that some other user has enabled/disabled the local video function.
         */
        override fun onUserEnableLocalVideo(uid: Int, enabled: Boolean) {
            super.onUserEnableLocalVideo(uid, enabled)
            showLog("onUserEnableLocalVideo => uid : $uid , enabled : $enabled")
        }

        override fun onConnectionLost() {
            super.onConnectionLost()
            showLog("onConnectionLost()")
        }

        override fun onRejoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            super.onRejoinChannelSuccess(channel, uid, elapsed)
            showLog("onRejoinChannelSuccess()")
        }
    }

    //Set Up Remote Video
    private fun setupRemoteVideo(uid: Int) {
        if (binding.flRemoteVideoViewContainer.childCount >= 1) {
            return
        }
        val surfaceView = RtcEngine.CreateRendererView(baseContext)
        binding.flRemoteVideoViewContainer.addView(surfaceView)
        mRtcEngine?.setupRemoteVideo(
            VideoCanvas(
                surfaceView,
                VideoCanvas.RENDER_MODE_FIT,
                uid
            )
        )
        binding.tvCallStatus.visibility = View.GONE
        surfaceView.tag = uid
    }

    //Remote User Left
    private fun onRemoteUserOffline() {
        finish()
    }

    //Remote User Audio Mute
    private fun onRemoteUserAudioMuted(muted: Boolean) {
        if (muted) {
            showToast("$otherUserName muted the call")
        } else {
            showToast("$otherUserName unMuted the call")
        }
    }

    //Remote User Video Mute
    private fun onRemoteUserPausedVideo(uid: Int, muted: Boolean) {
        if (muted) {
            showToast("$otherUserName paused the video")
        } else {
            showToast("$otherUserName resume the video")
        }

        val surfaceView = binding.flRemoteVideoViewContainer.getChildAt(0) as SurfaceView
        val tag = surfaceView.tag
        if (tag != null && tag as Int == uid) {
            surfaceView.visibility = if (muted) View.GONE else View.VISIBLE
        }
    }

    //All Controls Defines Here
    override fun initControl() {
        binding.ivRotateBackCamera.setOnClickListener(this)
        binding.ivRotateFrontCamera.setOnClickListener(this)
        binding.ivPauseVideo.setOnClickListener(this)
        binding.ivResumeVideo.setOnClickListener(this)
        binding.ivMute.setOnClickListener(this)
        binding.ivUnMute.setOnClickListener(this)
        binding.ivDisConnectCall.setOnClickListener(this)
    }

    //OnClick Listener
    override fun onClick(v: View) {
        when (v.id) {
            R.id.ivRotateBackCamera -> {
                binding.ivRotateBackCamera.visibility = View.GONE
                binding.ivRotateFrontCamera.visibility = View.VISIBLE
                rotateCamera()
            }

            R.id.ivRotateFrontCamera -> {
                binding.ivRotateBackCamera.visibility = View.VISIBLE
                binding.ivRotateFrontCamera.visibility = View.GONE
                rotateCamera()
            }

            R.id.ivPauseVideo -> {
                binding.ivPauseVideo.visibility = View.GONE
                binding.ivResumeVideo.visibility = View.VISIBLE
                pauseAndResumeVideo(false)
                binding.ivVideoPaused.visibility = View.VISIBLE
            }

            R.id.ivResumeVideo -> {
                binding.ivPauseVideo.visibility = View.VISIBLE
                binding.ivResumeVideo.visibility = View.GONE
                pauseAndResumeVideo(true)
                binding.ivVideoPaused.visibility = View.GONE
            }

            R.id.ivMute -> {
                binding.ivMute.visibility = View.GONE
                binding.ivUnMute.visibility = View.VISIBLE
                muteUnMuteVideo(true)
            }

            R.id.ivUnMute -> {
                binding.ivMute.visibility = View.VISIBLE
                binding.ivUnMute.visibility = View.GONE
                muteUnMuteVideo(false)
            }

            R.id.ivDisConnectCall -> {
                endCall()
            }
        }
    }

    //Rotate Camera
    private fun rotateCamera() {
        mRtcEngine?.switchCamera()
    }

    //Pause And Resume Video
    private fun pauseAndResumeVideo(isVideo: Boolean) {
        mRtcEngine?.enableLocalVideo(isVideo)
    }

    //Mute/ unMute Video
    private fun muteUnMuteVideo(isMute: Boolean) {
        mRtcEngine?.muteLocalAudioStream(isMute)
    }

    //End Call
    private fun endCall() {
        setCallState(EnumUtils.CallState.DISCONNECTED.value)
        finish()
    }

    //Set Call State on FireBase
    private fun setCallState(callState: String) {
        videoCallFirBase?.setValue(callState)
    }

    //Firebase Call State Handel
    private fun firebaseCallStatusListener() {
        videoCallFirBase?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                when (dataSnapshot.hasChild("call_status").toString()) {
                    EnumUtils.CallState.CONNECTING.value -> {
                        binding.tvCallStatus.text = getString(R.string.connecting)
                    }

                    EnumUtils.CallState.RINGING.value -> {
                        binding.tvCallStatus.text = getString(R.string.ringing)
                    }

                    EnumUtils.CallState.CONNECTED.value -> {
                        mp.stop()
                        unbindService(mConnection)
                        binding.tvCallStatus.text = getString(R.string.connected)
                        //setUpChannel()
                    }

                    EnumUtils.CallState.REJECTED.value -> {
                        mp.stop()
                        unbindService(mConnection)
                        showToast("$otherUserName rejecting the call")
                        finish()
                    }

                    EnumUtils.CallState.DISCONNECTED.value -> {
                        showToast("$otherUserName disconnected the call")
                        finish()
                    }

                    EnumUtils.CallState.NOT_ANSWERED.value -> {
                        mp.stop()
                        unbindService(mConnection)
                        showToast("$otherUserName not answering the call")
                        finish()
                    }

                    EnumUtils.CallState.BUSY.value -> {
                        mp.stop()
                        unbindService(mConnection)
                        showToast("$otherUserName is busy on other call")
                        finish()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
}