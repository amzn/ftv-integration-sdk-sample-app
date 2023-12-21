// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.firetv.integrationsdk.video.player

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.amazon.firetv.integrationsdk.R
import com.amazon.firetv.integrationsdk.account.AccountRepository
import com.amazon.firetv.integrationsdk.sdk.FireTvPlaybackReporter
import com.amazon.firetv.integrationsdk.sdk.hashProfileId
import com.amazon.firetv.integrationsdk.util.VIDEO_TO_PLAY_BUNDLE_KEY
import com.amazon.firetv.integrationsdk.util.extractParcelable
import com.amazon.firetv.integrationsdk.video.model.Video
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView

/**
 * This Fragment plays the video which was selected in the VideoBrowserFragment.
 * */
class VideoPlayerFragment : Fragment() {

    private var player: ExoPlayer? = null
    private var periodicPlaybackStateReporter: PeriodicPlaybackStateReporter? = null
    private var playbackStateListener: PlaybackStateListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_video_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize(view)
    }

    override fun onStart() {
        Log.i(TAG, "onStart")
        super.onStart()
        periodicPlaybackStateReporter?.start()
    }

    override fun onPause() {
        Log.i(TAG, "onPause")
        super.onPause()
        player?.pause()
        playbackStateListener?.onFragmentPause()
        periodicPlaybackStateReporter?.stop()
    }

    override fun onDestroy() {
        Log.i(TAG, "onDestroy")
        super.onDestroy()
        releasePlayer()
    }

    private fun initialize(view: View) {
        val currentVideo = requireArguments().extractParcelable<Video>(VIDEO_TO_PLAY_BUNDLE_KEY)
        val currentProfile = AccountRepository.getInstance().getActiveProfile(view.context)
        val hashedProfileId = hashProfileId(currentProfile)

        Log.i(TAG, "Initializing player to play ${currentVideo!!.videoName} with current profile $currentProfile")

        view.findViewById<TextView>(R.id.video_text_view).text =
            String.format(
                requireContext().resources.getString(R.string.video_player_message),
                currentVideo.videoName
            )

        // Initialize the player
        player = ExoPlayer.Builder(requireContext())
            .build()
            .also { exoPlayer ->
                view.findViewById<StyledPlayerView>(R.id.video_view).player = exoPlayer
                exoPlayer.addMediaItem(MediaItem.fromUri(getString(currentVideo.videoResourceId)))
                exoPlayer.playWhenReady = true
                exoPlayer.prepare()
            }

        val fireTvPlaybackReporter = FireTvPlaybackReporter(requireContext())

        /* Create the periodic playback reporter to
           send playback events every 60 seconds
        */
        periodicPlaybackStateReporter = PeriodicPlaybackStateReporter(
            player = player!!,
            handler = Handler(player!!.applicationLooper),
            fireTvPlaybackReporter = fireTvPlaybackReporter,
            video = currentVideo,
            profileId = hashedProfileId
        )

        playbackStateListener = PlaybackStateListener(
            player = player!!,
            fireTvPlaybackReporter = fireTvPlaybackReporter,
            video = currentVideo,
            profileId = hashedProfileId,
            periodicPlaybackStateReporter = periodicPlaybackStateReporter!!,
            fragment = this
        )

        /* Attach listener to the Player so we can report
           playback events when player state changes occur
           ex: playing, paused, seek, exit
        */
        player!!.addListener(playbackStateListener!!)
    }

    private fun releasePlayer() {
        Log.i(TAG, "Releasing player")
        player?.release()
        player = null
    }

    companion object {
        private val TAG = VideoPlayerFragment::class.java.simpleName
    }
}
