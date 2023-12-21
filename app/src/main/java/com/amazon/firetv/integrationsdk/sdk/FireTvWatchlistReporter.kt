package com.amazon.firetv.integrationsdk.sdk

import android.content.Context
import com.amazon.firetv.integrationsdk.video.model.WatchlistItem
import com.amazon.tv.developer.sdk.content.personalization.AmazonCustomerListReceiver
import com.amazon.tv.developer.sdk.content.personalization.model.AmazonContentId
import com.amazon.tv.developer.sdk.content.personalization.model.AmazonCustomerListEntry
import com.amazon.tv.developer.sdk.content.personalization.model.AmazonCustomerListType
import com.amazon.tv.developer.sdk.content.personalization.model.AmazonProfileId

/**
 * Reports the customers watchlist and watchlist changes to the FireTv Integration SDK.
 */
class FireTvWatchlistReporter(private val context: Context) {

    fun addItemToWatchlist(
        contentId: String,
        profileId: String,
        addedTimestamp: Long
    ) {
        isFTVIntegrationSDKSupportedOnDevice(context) {
            val entry = AmazonCustomerListEntry.builder()
                .contentId(
                    AmazonContentId.builder()
                        .namespace(AmazonContentId.NAMESPACE_CDF_ID)
                        .id(contentId)
                        .build()
                )
                .profileId(AmazonProfileId.builder()
                    .namespace(AmazonProfileId.NAMESPACE_APP_INTERNAL)
                    .id(hashProfileId(profileId))
                    .build())
                .addedTimestampMs(addedTimestamp)
                .build()

            AmazonCustomerListReceiver.getInstance(context).addCustomerListEntry(AmazonCustomerListType.WATCHLIST, entry)
        }
    }

    fun removeItemFromWatchlist(
        contentId: String,
        profileId: String
    ) {
        isFTVIntegrationSDKSupportedOnDevice(context) {
            val entry = AmazonCustomerListEntry.builder()
                .contentId(
                    AmazonContentId.builder()
                        .namespace(AmazonContentId.NAMESPACE_CDF_ID)
                        .id(contentId)
                        .build()
                )
                .profileId(AmazonProfileId.builder()
                    .namespace(AmazonProfileId.NAMESPACE_APP_INTERNAL)
                    .id(hashProfileId(profileId))
                    .build())
                .build()

            AmazonCustomerListReceiver.getInstance(context).removeCustomerListEntry(AmazonCustomerListType.WATCHLIST, entry)
        }
    }

    fun setWatchlist(
        watchlist: Map<String, List<WatchlistItem>>,
    ) {
        isFTVIntegrationSDKSupportedOnDevice(context) {
            val amazonCustomerListEntries = watchlist.map { profileToWatchlistItems ->
                val hashedProfileId = hashProfileId(profileToWatchlistItems.key)
                profileToWatchlistItems.value.map { watchlistItem ->
                    AmazonCustomerListEntry.builder()
                        .contentId(AmazonContentId.builder()
                            .namespace(AmazonContentId.NAMESPACE_CDF_ID)
                            .id(watchlistItem.video.amazonContentId)
                            .build())
                        .profileId(AmazonProfileId.builder()
                            .namespace(AmazonProfileId.NAMESPACE_APP_INTERNAL)
                            .id(hashedProfileId)
                            .build())
                        .addedTimestampMs(watchlistItem.timeAdded)
                        .build()
                }
            }.flatten()



            AmazonCustomerListReceiver.getInstance(context).setCustomerList(AmazonCustomerListType.WATCHLIST,
                amazonCustomerListEntries)
        }
    }
}