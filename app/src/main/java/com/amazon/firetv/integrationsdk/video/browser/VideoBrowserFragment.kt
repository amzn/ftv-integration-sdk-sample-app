// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.firetv.integrationsdk.video.browser

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amazon.firetv.integrationsdk.R
import com.amazon.firetv.integrationsdk.account.AccountRepository
import com.amazon.firetv.integrationsdk.video.model.Video
import com.amazon.firetv.integrationsdk.video.watchlist.WatchlistRepository

class VideoBrowserFragment : Fragment() {

    private var currentProfile: LiveData<String>? = null
    private var currentWatchlist: ArrayList<Video> = ArrayList()
    private var watchlistRecyclerView: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_video_browser, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentProfile = AccountRepository.getInstance().getActiveProfileLiveData(requireContext())

        // Set up row of videos to watch
        val videos = VideoDataSource().loadVideos()
        val videosRecyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_videos)
        videosRecyclerView.adapter = VideoItemAdapter(view.context, videos)
        videosRecyclerView.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)

        // Get watchlist data and observe changes
        val watchlistLiveData = WatchlistRepository.getInstance().getWatchlistLiveData(currentProfile!!.value ?: "none")
        watchlistLiveData.observe(viewLifecycleOwner
        ) { t ->
            if (t != null) {
                currentWatchlist.clear()
                currentWatchlist.addAll(t)
                watchlistRecyclerView!!.adapter!!.notifyDataSetChanged()
            }
        }

        // Setup watchlist row
        watchlistRecyclerView = view.findViewById(R.id.recycler_view_watchlist)
        watchlistRecyclerView!!.adapter = VideoItemAdapter(view.context, currentWatchlist);
        watchlistRecyclerView!!.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)

        // Update watchlist when profile changes
        currentProfile!!.observe(viewLifecycleOwner) { p -> WatchlistRepository.getInstance().updateProfile(p) }
    }
}
