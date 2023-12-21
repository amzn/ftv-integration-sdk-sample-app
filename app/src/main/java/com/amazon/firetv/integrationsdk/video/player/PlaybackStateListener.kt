// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.firetv.integrationsdk.video.player

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.amazon.firetv.integrationsdk.sdk.FireTvPlaybackReporter
import com.amazon.firetv.integrationsdk.util.reportCurrentPlayerState
import com.amazon.firetv.integrationsdk.util.reportExitedPlayback
import com.amazon.firetv.integrationsdk.video.model.Video
import com.google.android.exoplayer2.Player

/**
 * Implementation of the Player Listener interface.
 *
 * This class demonstrates the following scenarios for sending playback events:
 *   1. On playback state change (see onIsPlayingChanged). This handles the playing, paused and
 *   exit states.
 *
 *   2. On seek to a new playback position (see onPositionDiscontinuity)
 *
 *   3. On playing of an inserted advertisement (i.e. INTERSTITIAL) (see onPositionDiscontinuity)
 * */
class PlaybackStateListener(
    private val player: Player,
    private val fireTvPlaybackReporter: FireTvPlaybackReporter,
    private val video: Video,
    private val profileId: String,
    private val periodicPlaybackStateReporter: PeriodicPlaybackStateReporter,
    private val fragment: Fragment,
) : Player.Listener {

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        Log.i(TAG, "Playing state changed to $isPlaying")

        if (player.playbackState == Player.STATE_ENDED) {
            Log.i(TAG, "Video has ended, stopping periodic playback reporting")
            periodicPlaybackStateReporter.stop()
        }

        if (fragment.lifecycle.currentState != Lifecycle.State.RESUMED) {
            Log.i(TAG, "Player is offscreen or is leaving the screen, skip reporting the player state")
            return
        }

        player.reportCurrentPlayerState(fireTvPlaybackReporter, video, profileId)
    }

    override fun onPositionDiscontinuity(oldPosition: Player.PositionInfo, newPosition: Player.PositionInfo, reason: Int) {
        when (reason) {
            Player.DISCONTINUITY_REASON_SEEK -> {
                Log.i(TAG, "Seek detected from ${oldPosition.positionMs} to ${newPosition.positionMs} with reason $reason")
                fireTvPlaybackReporter.reportPlayingEvent(
                    playbackPositionMs = newPosition.positionMs,
                    durationMs = player.duration,
                    contentId = video.amazonContentId,
                    profileId = profileId
                )
            }

            /* If your app supports playing ads then you should report the playback state as INTERSTITIAL rather than PLAYING
               when you detect an advertisement is playing instead of the actual video content. The playback state should
               also be reported as INTERSTITIAL if any sort of preview or intro is playing instead of the actual content

               Since this app is using the ExoPlayer library for playing videos we are using the onPositionDiscontinuity()
               listener function to determine when an advertisement is playing. We only report the INTERSTITIAL state
               when the reason is DISCONTINUITY_REASON_AUTO_TRANSITION and isPlayingAd() evaluates to true
               as this indicates the content has transitioned to/from an inserted advertisement. See:
               https://developer.android.com/reference/androidx/media3/common/Player#DISCONTINUITY_REASON_AUTO_TRANSITION()

               NOTE: The videos included in this app do not have any inserted advertisements. The code below is included to
               serve as an example of how to report the playback state as INTERSTITIAL. Implementation in your app may not follow
               this exactly
            */
            Player.DISCONTINUITY_REASON_AUTO_TRANSITION -> {
                if (player.isPlayingAd) {
                    fireTvPlaybackReporter.reportInterstitialEvent(
                        playbackPositionMs = newPosition.positionMs,
                        durationMs = player.duration,
                        contentId = video.amazonContentId,
                        profileId = profileId
                    )
                } else {
                    fireTvPlaybackReporter.reportPlayingEvent(
                        playbackPositionMs = newPosition.positionMs,
                        durationMs = player.duration,
                        contentId = video.amazonContentId,
                        profileId = profileId
                    )
                }
            }

            else -> Log.i(TAG, "Seek was not due to user seeking forward/backward in the player")
        }
    }

    fun onFragmentPause() {
        player.reportExitedPlayback(fireTvPlaybackReporter, video, profileId)
    }

    companion object {
        private val TAG = PlaybackStateListener::class.java.simpleName
    }
}
