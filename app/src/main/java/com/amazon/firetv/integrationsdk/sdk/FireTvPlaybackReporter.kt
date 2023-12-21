// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.firetv.integrationsdk.sdk

import android.content.Context
import android.util.Log
import com.amazon.tv.developer.sdk.content.personalization.AmazonPlaybackReceiver
import com.amazon.tv.developer.sdk.content.personalization.model.AmazonContentId
import com.amazon.tv.developer.sdk.content.personalization.model.AmazonPlaybackEvent
import com.amazon.tv.developer.sdk.content.personalization.model.AmazonPlaybackState
import com.amazon.tv.developer.sdk.content.personalization.model.AmazonProfileId

/**
 * Reports AmazonPlaybackEvent objects to the AmazonPlaybackReceiver in the Fire TV Integration SDK.
 * */
class FireTvPlaybackReporter(private val context: Context) {

    fun reportPlayingEvent(
        playbackPositionMs: Long,
        durationMs: Long,
        contentId: String,
        profileId: String
    ) {
        isFTVIntegrationSDKSupportedOnDevice(context) {
            Log.i(TAG, "Reporting playback event with state: PLAYING, content id: $contentId, playback position: $playbackPositionMs, profile id: $profileId")
            reportEvent(
                AmazonPlaybackState.PLAYING,
                playbackPositionMs,
                durationMs,
                contentId,
                profileId
            )
        }
    }

    fun reportPausedEvent(
        playbackPositionMs: Long,
        durationMs: Long,
        contentId: String,
        profileId: String
    ) {
        isFTVIntegrationSDKSupportedOnDevice(context) {
            Log.i(TAG, "Reporting playback event with state: PAUSED, content id: $contentId, playback position: $playbackPositionMs, profile id: $profileId")
            reportEvent(
                AmazonPlaybackState.PAUSED,
                playbackPositionMs,
                durationMs,
                contentId,
                profileId
            )
        }
    }

    fun reportExitEvent(
        playbackPositionMs: Long,
        durationMs: Long,
        contentId: String,
        profileId: String
    ) {
        isFTVIntegrationSDKSupportedOnDevice(context) {
            Log.i(TAG, "Reporting playback event with state: EXIT, content id: $contentId, playback position: $playbackPositionMs, profile id: $profileId")
            reportEvent(
                AmazonPlaybackState.EXIT,
                playbackPositionMs,
                durationMs,
                contentId,
                profileId
            )
        }
    }

    fun reportInterstitialEvent(
        playbackPositionMs: Long,
        durationMs: Long,
        contentId: String,
        profileId: String
    ) {
        isFTVIntegrationSDKSupportedOnDevice(context) {
            Log.i(
                TAG,
                "Reporting playback event with state: INTERSTITIAL, content id: $contentId, playback position: $playbackPositionMs, profile id: $profileId"
            )
            reportEvent(
                AmazonPlaybackState.INTERSTITIAL,
                playbackPositionMs,
                durationMs,
                contentId,
                profileId
            )
        }
    }

    private fun reportEvent(
        playbackState: Int,
        playbackPositionMs: Long,
        durationMs: Long,
        contentId: String,
        profileId: String
    ) {
        val event = AmazonPlaybackEvent.builder()
            .state(playbackState)
            .playbackPositionMs(playbackPositionMs)
            .durationMs(durationMs)
            .contentId(
                AmazonContentId.builder()
                    .namespace(AmazonContentId.NAMESPACE_CDF_ID)
                    .id(contentId)
                    .build()
            )
            .profileId(
                /* Refer to app/src/main/java/com/amazon/firetv/integrationsdk/video/player/VideoPlayerFragment.kt
                   for information on sending the profile ID.
                */
                AmazonProfileId.builder()
                    .namespace(AmazonProfileId.NAMESPACE_APP_INTERNAL)
                    .id(profileId)
                    .build()
            )
            .buildActiveEvent()

        AmazonPlaybackReceiver.getInstance(context).addPlaybackEvent(event)
    }

    companion object {
        private val TAG = FireTvPlaybackReporter::class.java.simpleName
    }
}
