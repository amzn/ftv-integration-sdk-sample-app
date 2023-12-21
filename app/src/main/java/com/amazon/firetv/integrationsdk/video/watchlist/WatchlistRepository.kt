package com.amazon.firetv.integrationsdk.video.watchlist

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.amazon.firetv.integrationsdk.client.MyCustomerDataApiClient
import com.amazon.firetv.integrationsdk.sdk.FireTvWatchlistReporter
import com.amazon.firetv.integrationsdk.video.browser.VideoDataSource
import com.amazon.firetv.integrationsdk.video.model.Video
import com.amazon.firetv.integrationsdk.video.model.WatchlistItem

/**
 * Manages watchlist state for users. Also sends watchlist updates to the Fire TV Integration SDK.
 */
class WatchlistRepository private constructor() {

    private var profileWatchlistMap: HashMap<String, ArrayList<Video>> = HashMap()
    private val watchlistLiveData: MutableLiveData<List<Video>> = MutableLiveData()

    fun getWatchlistLiveData(profileId: String): MutableLiveData<List<Video>> {
        if (!profileWatchlistMap.containsKey(profileId)) {
            profileWatchlistMap[profileId] = ArrayList()
        }

        watchlistLiveData.value = profileWatchlistMap[profileId]
        return watchlistLiveData
    }

    fun addToWatchlist(video: Video, profileId: String, context: Context) {
        changeAndUpdateWatchlist(profileId) { list ->
            list.add(video)
            FireTvWatchlistReporter(context).addItemToWatchlist(video.amazonContentId, profileId, System.currentTimeMillis())
        }
    }

    fun removeFromWatchlist(video: Video, profileId: String, context: Context) {
        changeAndUpdateWatchlist(profileId) { list ->
            list.remove(video)
            FireTvWatchlistReporter(context).removeItemFromWatchlist(video.amazonContentId, profileId)
        }
    }

    fun updateProfile(profileId: String) {
        changeAndUpdateWatchlist(profileId) {}
    }

    fun isVideoInWatchlist(video: Video, profileId: String): Boolean {
        if (!profileWatchlistMap.containsKey(profileId)) {
            return false
        }
        return profileWatchlistMap[profileId]!!.contains(video)
    }

    private fun changeAndUpdateWatchlist(profileId: String, changeFn: (ArrayList<Video>) -> Unit) {
        if (!profileWatchlistMap.containsKey(profileId)) {
            profileWatchlistMap[profileId] = ArrayList()
        }

        changeFn(profileWatchlistMap[profileId]!!)
        watchlistLiveData.value = profileWatchlistMap[profileId]!!
    }

    /**
     * Refreshes the watchlist from the server.
     */
    fun refreshWatchlistForAllProfiles(context: Context) {
        val watchList = MyCustomerDataApiClient().retrieveCustomerWatchList()

        // reset watchlist with server data
        profileWatchlistMap = HashMap()

        // collect items to report to FTVIntegrationSDK
        val profileToWatchedItems = HashMap<String, ArrayList<WatchlistItem>>()

        watchList.forEach { customerListEntry ->
            if (!profileWatchlistMap.containsKey(customerListEntry.profileId)) {
                profileWatchlistMap[customerListEntry.profileId] = ArrayList()
            }
            if (!profileToWatchedItems.containsKey(customerListEntry.profileId)) {
                profileToWatchedItems[customerListEntry.profileId] = ArrayList()
            }

            val video = VideoDataSource().loadVideos().find {
                it.amazonContentId == customerListEntry.amznCatalogId
            }
            if (video != null) {
                profileWatchlistMap[customerListEntry.profileId]!!.add(video)
                profileToWatchedItems[customerListEntry.profileId]!!.add(WatchlistItem(customerListEntry.addedTimestampMs, video))

            }
        }

        FireTvWatchlistReporter(context).setWatchlist(profileToWatchedItems)
    }

    companion object {
        @Volatile
        private var instance: WatchlistRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: WatchlistRepository().also { instance = it }
            }
    }
}