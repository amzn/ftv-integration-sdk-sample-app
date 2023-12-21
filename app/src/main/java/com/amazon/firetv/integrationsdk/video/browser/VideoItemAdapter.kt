// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.firetv.integrationsdk.video.browser

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.amazon.firetv.integrationsdk.FragmentReplacementListener
import com.amazon.firetv.integrationsdk.R
import com.amazon.firetv.integrationsdk.account.AccountRepository
import com.amazon.firetv.integrationsdk.util.VIDEO_TO_PLAY_BUNDLE_KEY
import com.amazon.firetv.integrationsdk.video.model.Video
import com.amazon.firetv.integrationsdk.video.player.VideoPlayerFragment
import com.amazon.firetv.integrationsdk.video.watchlist.WatchlistRepository
import com.bumptech.glide.Glide

/**
 * Adapter class which handles providing Video tiles to ViewHolder to display
 * */
class VideoItemAdapter(
    private val context: Context,
    private val videos: List<Video>
) : RecyclerView.Adapter<VideoItemAdapter.VideoItemViewHolder>() {

    inner class VideoItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val textView: TextView = view.findViewById(R.id.item_video_text)
        val imageButton: ImageButton = view.findViewById(R.id.item_video_image_button)

        init {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.row_item_unfocused_color))
            view.setOnClickListener(this)
            view.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    v.setBackgroundColor(ContextCompat.getColor(context, R.color.row_item_focused_color))
                } else {
                    v.setBackgroundColor(ContextCompat.getColor(context, R.color.row_item_unfocused_color))
                }
            }
        }

        override fun onClick(v: View?) {
            showVideoOptions(videos[layoutPosition])
        }

        private fun showVideoOptions(video: Video) {
            val watchlistRepository = WatchlistRepository.getInstance()
            val currentProfile = AccountRepository.getInstance().getActiveProfile(context)
            val isAdd = !watchlistRepository.isVideoInWatchlist(video, currentProfile)
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                .apply {
                    setTitle(video.videoName)
                    setItems(arrayOf("Watch", if (isAdd) "Add to Watchlist" else "Remove from Watchlist")
                    ) { _, selection ->
                        when (selection) {
                            0 -> ingressToWatch(video)
                            1 -> {
                                if (isAdd) {
                                    watchlistRepository.addToWatchlist(video, currentProfile, context)
                                } else {
                                    watchlistRepository.removeFromWatchlist(video, currentProfile, context)
                                }
                            }
                        }
                    }
                }
            builder.show()
        }

        private fun ingressToWatch(video: Video) {
            Log.i(TAG, "Video ${video.videoName} selected")
            val fragmentReplacementListener = context as FragmentReplacementListener
            val bundle = Bundle().apply {
                putParcelable(VIDEO_TO_PLAY_BUNDLE_KEY, video)
            }
            val fragment = VideoPlayerFragment().apply {
                arguments = bundle
            }
            fragmentReplacementListener.replaceFragment(fragment)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.video_item, parent, false)
        return VideoItemViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int = videos.size

    override fun onBindViewHolder(holder: VideoItemViewHolder, position: Int) {
        val video = videos[position]

        Glide.with(context)
            .load(video.videoImageUrl)
            .fitCenter()
            .into(holder.imageButton)

        holder.textView.text = String.format(
            context.resources.getString(R.string.video_item_name),
            video.videoName
        )
    }

    companion object {
        private val TAG = VideoItemAdapter::class.java.simpleName
    }
}
