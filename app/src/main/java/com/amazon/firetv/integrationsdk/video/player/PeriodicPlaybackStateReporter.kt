// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.firetv.integrationsdk.video.player

import android.os.Handler
import android.util.Log
import com.amazon.firetv.integrationsdk.sdk.FireTvPlaybackReporter
import com.amazon.firetv.integrationsdk.util.reportCurrentPlayerState
import com.amazon.firetv.integrationsdk.video.model.Video
import com.google.android.exoplayer2.Player
import java.util.Timer
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timerTask

/**
 * This class serves as an abstraction over java.util.Timer. It demonstrates sending an AmazonPlaybackEvent
 * every 60 seconds while in the player, regardless of playback state. The start() method is invoked during
 * initialization of the VideoPlayerFragment and the stop() method is invoked once the video finishes
 * within PlaybackStateListener
 * */
class PeriodicPlaybackStateReporter(
    private val player: Player,
    private val handler: Handler,
    private val fireTvPlaybackReporter: FireTvPlaybackReporter,
    private val video: Video,
    private val profileId: String
) {
    private var timer: Timer? = null;

    fun start() {
        // Do nothing if the timer is already running.
        if (timer != null) {
            return
        }

        Log.i(TAG, "Scheduling periodic playback reporting with interval $REPORTING_INTERVAL")

        timer = Timer()
        timer!!.scheduleAtFixedRate(
            timerTask {
                Log.i(TAG, "Executing periodic playback reporting task")

                /* ExoPlayer instance cannot be accessed from a separate thread. As a result we
                   are using a Handler configured to send runnables to the thread. tied to the Player.
                   See https://exoplayer.dev/hello-world.html#a-note-on-threading
                */
                handler.post {
                    player.reportCurrentPlayerState(fireTvPlaybackReporter, video, profileId)
                }
            },
            REPORTING_DELAY,
            REPORTING_INTERVAL
        )
    }

    fun stop() {
        Log.i(TAG, "Stopping periodic playback reporter")
        timer?.cancel()
        timer = null
    }

    companion object {
        private val TAG = PeriodicPlaybackStateReporter::class.java.simpleName
        private val REPORTING_INTERVAL = TimeUnit.MILLISECONDS.convert(60L, TimeUnit.SECONDS)
        private const val REPORTING_DELAY = 0L
    }
}
