package com.virtucam.media

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Surface
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory

/**
 * ExoPlayer wrapper for broadcasting live RTSP/RTMP streams from OBS.
 * Hardware-decodes the stream directly onto our hijacked OpenGL Surface.
 */
class StreamPlayer(
    private val context: Context,
    private val streamUrl: String,
    private val outputSurface: Surface,
    private val onFrameAvailable: () -> Unit
) {

    companion object {
        private const val TAG = "StreamPlayer"
    }

    private var exoPlayer: ExoPlayer? = null
    private val handler = Handler(Looper.getMainLooper())

    /**
     * Start connecting to the live stream
     */
    fun start() {
        handler.post {
            try {
                initializePlayer()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize StreamPlayer", e)
            }
        }
    }

    private fun initializePlayer() {
        if (exoPlayer != null) return

        // 1. Build ExoPlayer instance
        exoPlayer = ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(context))
            .build()
            
        // 2. Set the target OpenGL-backed Surface for rendering
        exoPlayer?.setVideoSurface(outputSurface)

        // 3. Configure the stream
        val uri = Uri.parse(streamUrl)
        
        val mediaSource = if (streamUrl.startsWith("rtsp", ignoreCase = true)) {
            // Use RtspMediaSource for explicit RTSP support
            RtspMediaSource.Factory()
                .setForceUseRtpTcp(true) // TCP often better for local networks
                .createMediaSource(MediaItem.fromUri(uri))
        } else {
            // Default factory for RTMP/HTTP
            DefaultMediaSourceFactory(context).createMediaSource(MediaItem.fromUri(uri))
        }

        exoPlayer?.setMediaSource(mediaSource)

        // 4. Configure listener to trigger rendering frame updates
        exoPlayer?.addListener(object : Player.Listener {
            override fun onVideoSizeChanged(videoSize: VideoSize) {
                super.onVideoSizeChanged(videoSize)
                Log.d(TAG, "Stream Video Size: ${videoSize.width}x${videoSize.height}")
            }

            override fun onRenderedFirstFrame() {
                super.onRenderedFirstFrame()
                Log.d(TAG, "First stream frame rendered!")
                // Tell the virtual render thread a new frame is available for OpenGL swapping
                onFrameAvailable()
            }
            
            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                super.onPlayerError(error)
                Log.e(TAG, "Stream error: ${error.message}")
            }
        })
        
        // 5. Start playback immediately
        exoPlayer?.playWhenReady = true
        exoPlayer?.prepare()
    }

    /**
     * Stop and release the player
     */
    fun stop() {
        handler.post {
            try {
                exoPlayer?.stop()
                exoPlayer?.release()
                exoPlayer = null
            } catch (e: Exception) {
                Log.e(TAG, "Error releasing StreamPlayer", e)
            }
        }
    }
}
