package com.haallo.ui.call

import android.app.KeyguardManager
import android.content.*
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.haallo.R
import com.haallo.base.OldBaseActivity
import com.haallo.constant.IntentConstant
import com.haallo.databinding.ActivityAudioCallBinding
import com.haallo.util.checkAudioCallPermission
import com.haallo.util.showLog
import com.haallo.util.showToast
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine

class OneToOneAudioCallActivityOld : OldBaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityAudioCallBinding

    private var otherUserId: String? = ""
    private var otherUserName: String? = ""
    private var channelName: String? = ""
    private var callStatus: String? = ""
    private var isMuteAudio: Boolean = false
    private var mRtcEngine: RtcEngine? = null
    private var audioCallFirBase: DatabaseReference? = null
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

        FirebaseApp.initializeApp(this)

        binding = ActivityAudioCallBinding.inflate(layoutInflater)
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
        isMuteAudio = intent.getBooleanExtra(IntentConstant.IS_MUTE_AUDIO, false)
        audioCallFirBase =
            FirebaseDatabase.getInstance().reference.child("Call").child(channelName!!)
                .child("call_status")

        binding.tvUserName.text = otherUserName
        if (checkAudioCallPermission(this)) {
            setUpChannel()
        } else {
            showToast("Please provide permission for audio call")
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

        joinChannel()
        if (callStatus == EnumUtils.CallStatus.INCOMING.value) {

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
            IntentFilter(EnumUtils.NotificationType.AUDIO_CALL_ACCEPT.value)
        )

        LocalBroadcastManager.getInstance(this).registerReceiver(
            broadcastReceiver,
            IntentFilter(EnumUtils.NotificationType.AUDIO_CALL_REJECT.value)
        )

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(
                broadcastReceiver,
                IntentFilter(EnumUtils.NotificationType.AUDIO_CALL_DISCONNECT.value)
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
                EnumUtils.NotificationType.AUDIO_CALL_ACCEPT.value -> {

                }

                EnumUtils.NotificationType.AUDIO_CALL_REJECT.value -> {

                }

                EnumUtils.NotificationType.AUDIO_CALL_DISCONNECT.value -> {

                }
            }
        }
    }

    private val rtcEventHandler = object : IRtcEngineEventHandler() {
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
         * callback indicates that some other user (remote) has paused/resumed his/her audio stream.
         */

        override fun onConnectionLost() {
            super.onConnectionLost()
            showLog("onConnectionLost()")
        }

        override fun onRejoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            super.onRejoinChannelSuccess(channel, uid, elapsed)
            showLog("onRejoinChannelSuccess()")
        }
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

    //All Controls Defines Here
    override fun initControl() {
        binding.ivMute.setOnClickListener(this)
        binding.ivUnMute.setOnClickListener(this)
        binding.ivDisConnectCall.setOnClickListener(this)
    }

    //OnClick Listener
    override fun onClick(v: View) {
        when (v.id) {
            R.id.ivMute -> {
                binding.ivMute.visibility = View.GONE
                binding.ivUnMute.visibility = View.VISIBLE
                muteUnMuteAudio(true)
            }
            R.id.ivUnMute -> {
                binding.ivMute.visibility = View.VISIBLE
                binding.ivUnMute.visibility = View.GONE
                muteUnMuteAudio(false)
            }
            R.id.ivDisConnectCall -> {
                endCall()
            }
        }
    }

    //Mute/ unMute Audio
    private fun muteUnMuteAudio(isMute: Boolean) {
        mRtcEngine?.muteLocalAudioStream(isMute)
    }

    //End Call
    private fun endCall() {
        setCallState(EnumUtils.CallState.DISCONNECTED.value)
        finish()
    }

    //Set Call State on FireBase
    private fun setCallState(callState: String) {
        audioCallFirBase?.setValue(callState)
    }

    //Firebase Call State Handel
    private fun firebaseCallStatusListener() {
        audioCallFirBase?.addValueEventListener(object : ValueEventListener {
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