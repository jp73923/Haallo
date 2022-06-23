package com.haallo.application

import android.app.Activity
import android.app.Application
import android.text.TextUtils
import android.util.Log
import com.google.firebase.FirebaseApp
import com.haallo.R
import com.haallo.di.DaggerHaalloAppComponent
import com.haallo.di.HaalloAppComponent
import com.haallo.di.HaalloAppModule
import com.haallo.ui.agoraGroupVideo.openvcall.model.AGEventHandler
import com.haallo.ui.agoraGroupVideo.openvcall.model.CurrentUserSettings
import com.haallo.ui.agoraGroupVideo.openvcall.model.EngineConfig
import com.haallo.ui.agoraGroupVideo.openvcall.model.MyEngineEventHandler
import com.haallo.ui.groupAudio.WorkerThread
import io.agora.rtc.Constants
import io.agora.rtc.RtcEngine
import org.slf4j.LoggerFactory

class Haallo : HaalloApplication() {

    companion object {

        operator fun get(app: Application): Haallo {
            return app as Haallo
        }

        operator fun get(activity: Activity): Haallo {
            return activity.application as Haallo
        }

        lateinit var component: HaalloAppComponent
            private set
    }

    override fun onCreate() {
        super.onCreate()
        try {
            component = DaggerHaalloAppComponent.builder()
                .haalloAppModule(HaalloAppModule(this))
                .build()
            component.inject(this)
            super.setAppComponent(component)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //=============================AGApplication=============================
        FirebaseApp.initializeApp(this)
        createRtcEngine();
    }

    //=============================AGApplication=============================
    private val mVideoSettings = CurrentUserSettings()

    private val log = LoggerFactory.getLogger(this.javaClass)
    private var mRtcEngine: RtcEngine? = null
    private var mRtcEngineAudio: RtcEngine? = null
    private var mConfig: EngineConfig? = null
    private var mEventHandler: MyEngineEventHandler? = null
    private var mEventHandlerAudio: MyEngineEventHandler? = null

    fun rtcEngine(): RtcEngine? {
        return mRtcEngine
    }

    fun rtcEngineAudio(): RtcEngine? {
        return mRtcEngineAudio
    }

    fun config(): EngineConfig? {
        return mConfig
    }

    fun userSettings(): CurrentUserSettings? {
        return mVideoSettings
    }

    val mAudioSettings = CurrentUserSettings()

    fun addEventHandler(handler: AGEventHandler?) {
        mEventHandler!!.addEventHandler(handler)
    }

    fun remoteEventHandler(handler: AGEventHandler?) {
        mEventHandler!!.removeEventHandler(handler)
    }

    private fun createRtcEngine() {
        val context = applicationContext
        val appId = context.getString(R.string.agora_app_id)
        if (TextUtils.isEmpty(appId)) {
            throw RuntimeException("NEED TO use your App ID, get your own ID at https://dashboard.agora.io/")
        }
        mEventHandler = MyEngineEventHandler()
        mEventHandlerAudio = MyEngineEventHandler()
        mRtcEngine = try {
            // Creates an RtcEngine instance
            RtcEngine.create(context, appId, mEventHandler)
        } catch (e: java.lang.Exception) {
            log.error(Log.getStackTraceString(e))
            throw RuntimeException(
                """
                    NEED TO check rtc sdk init fatal error
                    ${Log.getStackTraceString(e)}
                    """.trimIndent()
            )
        }
        mRtcEngineAudio = try {
            // Creates an RtcEngine instance
            RtcEngine.create(context, appId, mEventHandlerAudio)
        } catch (e: java.lang.Exception) {
            log.error(Log.getStackTraceString(e))
            throw RuntimeException(
                """
                    NEED TO check rtc sdk init fatal error
                    ${Log.getStackTraceString(e)}
                    """.trimIndent()
            )
        }

        /*
          Sets the channel profile of the Agora RtcEngine.
          The Agora RtcEngine differentiates channel profiles and applies different optimization
          algorithms accordingly. For example, it prioritizes smoothness and low latency for a
          video call, and prioritizes video quality for a video broadcast.
         */mRtcEngine?.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION)
        // Enables the video module.
        mRtcEngine?.enableVideo()
        /*
          Enables the onAudioVolumeIndication callback at a set time interval to report on which
          users are speaking and the speakers' volume.
          Once this method is enabled, the SDK returns the volume indication in the
          onAudioVolumeIndication callback at the set time interval, regardless of whether any user
          is speaking in the channel.
         */mRtcEngine?.enableAudioVolumeIndication(200, 3, false)
        mRtcEngineAudio?.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION)
        // Enables the video module.
        mRtcEngineAudio?.enableAudio()
        /*
          Enables the onAudioVolumeIndication callback at a set time interval to report on which
          users are speaking and the speakers' volume.
          Once this method is enabled, the SDK returns the volume indication in the
          onAudioVolumeIndication callback at the set time interval, regardless of whether any user
          is speaking in the channel.
         */mRtcEngineAudio?.enableAudioVolumeIndication(200, 3, false)
        mConfig = EngineConfig()
    }

    private var mWorkerThread: WorkerThread? = null

    @Synchronized
    fun initWorkerThread() {
        if (mWorkerThread == null) {
            mWorkerThread = WorkerThread(applicationContext)
            mWorkerThread?.start()
            mWorkerThread?.waitForReady()
        }
    }

    @Synchronized
    fun getWorkerThread(): WorkerThread? {
        return mWorkerThread
    }

    @Synchronized
    fun deInitWorkerThread() {
        mWorkerThread?.exit()
        try {
            mWorkerThread?.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        mWorkerThread = null
    }
}