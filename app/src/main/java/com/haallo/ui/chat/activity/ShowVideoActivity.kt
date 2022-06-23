//package com.haallo.ui.chat.activity
//
//import android.net.Uri
//import android.os.Bundle
//import android.view.View
//import com.google.android.exoplayer2.*
//import com.google.android.exoplayer2.source.ExtractorMediaSource
//import com.google.android.exoplayer2.source.TrackGroupArray
//import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
//import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
//import com.google.android.exoplayer2.trackselection.TrackSelectionArray
//import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
//import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
//import com.google.android.exoplayer2.util.Util
//import com.haallo.R
//import com.haallo.base.BaseActivity
//import com.haallo.constant.IntentConstant
//import com.haallo.util.fullScreenWindow
//import kotlinx.android.synthetic.main.activity_show_video.*
//
//class ShowVideoActivity : BaseActivity(), View.OnClickListener {
//
//    private var videoUrl: String? = null
//    private var player: SimpleExoPlayer? = null
//    private var time: Int? = 0
//    private lateinit var dataSourceFactory: DefaultDataSourceFactory
//    private var isFromShowVideo: Boolean = false
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_show_video)
//
//        initView()
//        initControl()
//    }
//
//    //All UI Changes From Here
//    override fun initView() {
//        fullScreenWindow(window)
//        getDataFromIntent()
//        exoPlayer()
//        playVideo()
//    }
//
//    //Get Data From Intent
//    private fun getDataFromIntent() {
//        videoUrl = intent.getStringExtra(IntentConstant.VIDEO_URL)
//    }
//
//    //Exo Player
//    private fun exoPlayer() {
//        if (player == null) {
//            val loadControl = DefaultLoadControl.Builder().createDefaultLoadControl()
//            player = ExoPlayerFactory.newSimpleInstance(
//                DefaultRenderersFactory(this),
//                DefaultTrackSelector(AdaptiveTrackSelection.Factory(DefaultBandwidthMeter()))
//            )
//            dataSourceFactory = DefaultDataSourceFactory(
//                this,
//                Util.getUserAgent(this, "mediaPlayerSample"),
//                DefaultBandwidthMeter()
//            )
//        }
//    }
//
//    //PlayVideo
//    private fun playVideo() {
//        if (videoUrl != null && videoUrl != "") {
//            try {
//                val mediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
//                    .createMediaSource(Uri.parse(videoUrl))
//                simpleExoPlayerView.player = player
//                showLoading()
//                player?.playWhenReady = true
//                player?.prepare(mediaSource)
//                if (isFromShowVideo && time != null) {
//                    time = time!! * 1000
//                    val seekTime: Long = time!!.toLong()
//                    player?.seekTo(seekTime)
//                }
//            } catch (e: Exception) {
//            }
//
//            player?.addListener(object : Player.EventListener {
//                override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
//
//                }
//
//                override fun onSeekProcessed() {
//
//                }
//
//                override fun onTracksChanged(
//                    trackGroups: TrackGroupArray?,
//                    trackSelections: TrackSelectionArray?
//                ) {
//
//                }
//
//                override fun onPlayerError(error: ExoPlaybackException?) {
//
//                }
//
//                override fun onLoadingChanged(isLoading: Boolean) {
//
//                }
//
//                override fun onPositionDiscontinuity(reason: Int) {
//
//                }
//
//                override fun onRepeatModeChanged(repeatMode: Int) {
//
//                }
//
//                override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
//
//                }
//
//                override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
//
//                }
//
//                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
//                    if (playbackState == ExoPlayer.STATE_BUFFERING) {
//                        showLoading()
//                    } else {
//                        hideLoading()
//                    }
//
//                    if (playWhenReady && playbackState == Player.STATE_READY) {
//                        ivBack.visibility = View.GONE
//                    } else {
//                        ivBack.visibility = View.VISIBLE
//                    }
//                }
//            })
//        }
//    }
//
//    //OnPause Method
//    override fun onPause() {
//        super.onPause()
//        player!!.playWhenReady = false
//        player!!.stop()
//    }
//
//    //All Controls Defines Here
//    override fun initControl() {
//        ivBack.setOnClickListener(this)
//    }
//
//    //OnClick Listener
//    override fun onClick(v: View) {
//        when (v.id) {
//            R.id.ivBack -> {
//                onBackPressed()
//            }
//        }
//
//    }
//}