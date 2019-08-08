package com.example.drmdemo

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.drmdemo.helper.Utils
import com.example.drmdemo.model.BasicModel
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.Player.STATE_BUFFERING
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto
import com.google.android.exoplayer2.drm.FrameworkMediaDrm
import com.google.android.exoplayer2.drm.HttpMediaDrmCallback
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import kotlinx.android.synthetic.main.activity_play_video.*


class PlayVideoActivity : AppCompatActivity() {


    private val USER_AGENT = "user-agent"


    private var DRM_DASH_URL = "https://storage.googleapis.com/wvmedia/cenc/h264/tears/tears.mpd"

    private var DRM_LICENSE_URL = "https://proxy.staging.widevine.com/proxy"

    enum class StreamingType {
        DASH
    }

    private val handler = Handler()
    var player: SimpleExoPlayer? = null
    private val bandwidthMeter = DefaultBandwidthMeter()
    private val selector = DefaultTrackSelector()
    private val loadControl = DefaultLoadControl()
    private lateinit var drmSessionManager: DefaultDrmSessionManager<FrameworkMediaCrypto>
    private lateinit var drmCallback: HttpMediaDrmCallback


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_video)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        DRM_DASH_URL = (intent?.getSerializableExtra("data") as BasicModel).uri
        DRM_LICENSE_URL = (intent?.getSerializableExtra("data") as BasicModel).drm_license_url


        drmCallback = HttpMediaDrmCallback(
            DRM_LICENSE_URL,
            DefaultHttpDataSourceFactory(USER_AGENT)
        )
        drmSessionManager = DefaultDrmSessionManager(
            C.WIDEVINE_UUID,
            FrameworkMediaDrm.newInstance(C.WIDEVINE_UUID), drmCallback, null, handler, null
        )

        player = initPlayer(isDrm = true, type = StreamingType.DASH)
        player?.playWhenReady = true


        player?.addListener(object : Player.EventListener {
            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun onSeekProcessed() {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun onPlayerError(error: ExoPlaybackException?) {
                //To change body of created functions use File | Settings | File Templates.
                releasePlayer()
                setResult(Activity.RESULT_CANCELED)
                onBackPressed()
            }

            override fun onLoadingChanged(isLoading: Boolean) {

            }

            override fun onPositionDiscontinuity(reason: Int) {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == STATE_BUFFERING) {
                    prgrsBar.visibility = View.VISIBLE
                } else {
                    prgrsBar.visibility = View.GONE
                }
            }
        })

    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)


    }

    private fun initPlayer(isDrm: Boolean, type: StreamingType): SimpleExoPlayer {
        val renderersFactory = if (isDrm)
            DefaultRenderersFactory(this, drmSessionManager) else DefaultRenderersFactory(this)
        val player = ExoPlayerFactory.newSimpleInstance(renderersFactory, selector, loadControl)

        val playerView = findViewById<PlayerView>(R.id.player_view)
        playerView.player = player

        val dataSourceFactory = DefaultDataSourceFactory(
            this, bandwidthMeter,
            DefaultHttpDataSourceFactory(USER_AGENT, bandwidthMeter)
        )

        val mediaSource = createDashSource(DRM_DASH_URL, dataSourceFactory)



        player.prepare(mediaSource)

        return player
    }

    private fun createDashSource(
        url: String,
        dataSourceFactory: DefaultDataSourceFactory
    ): DashMediaSource {
        return DashMediaSource.Factory(
            DefaultDashChunkSource.Factory(dataSourceFactory),
            dataSourceFactory
        ).createMediaSource(Uri.parse(url))
    }


    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            if (!Utils.isConnected(this@PlayVideoActivity)) {
                setResult(Activity.RESULT_OK)
                onBackPressed()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        releasePlayer()
    }

    private fun releasePlayer() {
        if (player != null) {
            player?.stop();
            player?.release();
            player = null
        }
    }
}