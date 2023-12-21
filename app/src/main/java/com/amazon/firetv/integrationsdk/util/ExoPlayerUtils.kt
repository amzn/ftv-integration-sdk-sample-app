// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.firetv.integrationsdk.util

import android.util.Log
import com.amazon.firetv.integrationsdk.sdk.FireTvPlaybackReporter
import com.amazon.firetv.integrationsdk.video.model.Video
import com.google.android.exoplayer2.Player

/**
 * Extension function on Player to report the current playback state.
 * */
fun Player.reportCurrentPlayerState(
    fireTvPlaybackReporter: FireTvPlaybackReporter,
    video: Video,
    profileId: String
) {
    if (isPlaying) {
        /* If your app supports playing ads then you should report the playback state as INTERSTITIAL rather than PLAYING
           when you detect an advertisement is playing instead of the actual content. The playback state should
           also be reported as INTERSTITIAL if any sort of preview or intro is playing instead of the actual content

           NOTE: The videos included in this app do not have any inserted advertisements. The code below is included to
           serve as an example of how to report the playback state as INTERSTITIAL. Implementation in your app may not follow
           this exactly
        */
        if (isPlayingAd) {
            fireTvPlaybackReporter.reportInterstitialEvent(
                playbackPositionMs = currentPosition,
                durationMs = duration,
                contentId = video.amazonContentId,
                profileId = profileId
            )
        } else {
            fireTvPlaybackReporter.reportPlayingEvent(
                playbackPositionMs = currentPosition,
                durationMs = duration,
                contentId = video.amazonContentId,
                profileId = profileId
            )
        }
    } else {
        when (playbackState) {
            Player.STATE_READY -> fireTvPlaybackReporter.reportPausedEvent(
                playbackPositionMs = currentPosition,
                durationMs = duration,
                contentId = video.amazonContentId,
                profileId = profileId
            )

            Player.STATE_ENDED -> this.reportExitedPlayback(fireTvPlaybackReporter, video, profileId)

            else -> Log.i(EXOPLAYER_UTILS_TAG, "Player state does not require reporting")
        }
    }
}

/**
 * Extension function on Player to report that the player has exited from playback (either because the
 * content ended or the user left the screen). This is broken out from `reportCurrentPlayerState` because
 * those two paths (content ended and user egress) are reported via two different paths.
 */
fun Player.reportExitedPlayback(
    fireTvPlaybackReporter: FireTvPlaybackReporter,
    video: Video,
    profileId: String
) {
    fireTvPlaybackReporter.reportExitEvent(
        playbackPositionMs = currentPosition,
        durationMs = duration,
        contentId = video.amazonContentId,
        profileId = profileId
    )
}
