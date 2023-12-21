package com.amazon.firetv.integrationsdk.video.model

/**
 * Data class to encapsulate an item in a user's watchlist including the time added.
 */
data class WatchlistItem(val timeAdded: Long, val video: Video)