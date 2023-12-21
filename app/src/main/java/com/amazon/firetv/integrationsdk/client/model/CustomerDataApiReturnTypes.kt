// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.firetv.integrationsdk.client.model

import com.amazon.firetv.integrationsdk.sdk.hashProfileId
import com.amazon.tv.developer.sdk.content.entitlement.model.AmazonSubscriptionEntitlement
import com.amazon.tv.developer.sdk.content.personalization.model.AmazonContentEntitlement
import com.amazon.tv.developer.sdk.content.personalization.model.AmazonContentId
import com.amazon.tv.developer.sdk.content.personalization.model.AmazonCustomerListEntry
import com.amazon.tv.developer.sdk.content.personalization.model.AmazonEntitlementType
import com.amazon.tv.developer.sdk.content.personalization.model.AmazonPlaybackEvent
import com.amazon.tv.developer.sdk.content.personalization.model.AmazonPlaybackState
import com.amazon.tv.developer.sdk.content.personalization.model.AmazonProfileId

const val ONE_YEAR_MS: Long = (365L * 86400 * 1000)

/**
 * Data class returned by your internal API to get customer purchased content entitlements
 * */
data class CustomerPurchasedContent(
    val amznCatalogId: String,
    val purchaseTimestampMs: Long,
)

fun CustomerPurchasedContent.toAmazonContentEntitlement(): AmazonContentEntitlement =
    AmazonContentEntitlement.builder()
        .contentId(
            AmazonContentId.builder()
                .id(amznCatalogId)
                .namespace(AmazonContentId.NAMESPACE_CDF_ID)
                .build()
        )
        .acquisitionTimestampMs(purchaseTimestampMs)
        .type(AmazonEntitlementType.PURCHASE)
        .build()

/**
 * Data class returned by your internal API to get customer rented content entitlements
 * */
data class CustomerRentedContent(
    val amznCatalogId: String,
    val expirationTimestampMs: Long,
    val rentedTimestampMs: Long,
)

fun CustomerRentedContent.toAmazonContentEntitlement(): AmazonContentEntitlement =
    AmazonContentEntitlement.builder()
        .contentId(
            AmazonContentId.builder()
                .id(amznCatalogId)
                .namespace(AmazonContentId.NAMESPACE_CDF_ID)
                .build()
        )
        .acquisitionTimestampMs(rentedTimestampMs)
        .expirationTimestampMs(expirationTimestampMs)
        .type(AmazonEntitlementType.RENTAL)
        .build()

/**
 * Data class returned by your internal API to get customer recorded content entitlements
 * */
data class CustomerRecordedContent(
    val amznCatalogId: String,
    val recordedTimestampMs: Long,
)

fun CustomerRecordedContent.toAmazonContentEntitlement(): AmazonContentEntitlement =
    AmazonContentEntitlement.builder()
        .contentId(
            AmazonContentId.builder()
                .id(amznCatalogId)
                .namespace(AmazonContentId.NAMESPACE_CDF_ID)
                .build()
        )
        .acquisitionTimestampMs(recordedTimestampMs)
        .expirationTimestampMs(recordedTimestampMs + ONE_YEAR_MS) // For example, if your recordings have a 1 year retention time
        .type(AmazonEntitlementType.RECORDING)
        .build()

/**
 * Data class returned by your internal API to get customer watch list
 * */
data class CustomerListEntry(
    val amznCatalogId: String,
    val profileId: String,
    val addedTimestampMs: Long
)

fun CustomerListEntry.toAmazonCustomerListEntry(): AmazonCustomerListEntry =
    AmazonCustomerListEntry.builder()
        .contentId(
            AmazonContentId.builder()
                .id(amznCatalogId)
                .namespace(AmazonContentId.NAMESPACE_CDF_ID)
                .build()
        )
        .profileId(
            AmazonProfileId.builder()
                .id(hashProfileId(profileId))
                .namespace(AmazonProfileId.NAMESPACE_APP_INTERNAL)
                .build()
        )
        .addedTimestampMs(addedTimestampMs)
        .build()

/**
 * Data class returned by your internal API to get customer subscriptions
 * */
data class CustomerSubscriptionEntitlement(
    val subscriptionId: String,
    val expirationTimestampMs: Long,
    val acquisitionTimestampMs: Long
)

fun CustomerSubscriptionEntitlement.toAmazonSubscriptionEntitlement(): AmazonSubscriptionEntitlement =
    AmazonSubscriptionEntitlement.builder()
        .subscriptionId(subscriptionId)
        .acquisitionTimestampMs(acquisitionTimestampMs)
        .expirationTimestampMs(expirationTimestampMs)
        .build()

/**
 * Data class returned by your internal API to get customer watch activity
 * */
data class CustomerTitleWatched(
    val amznCatalogId: String,
    val profileId: String,
    val playbackState: MyPlaybackState,
    val currentPlaybackPositionMs: Long,
    val durationMs: Long,
    val creditPositionMs: Long,
    val lastWatchTimeMs: Long
)

fun CustomerTitleWatched.toAmazonPlaybackEvent(): AmazonPlaybackEvent =
    AmazonPlaybackEvent.builder()
        .contentId(
            AmazonContentId.builder()
                .id(amznCatalogId)
                .namespace(AmazonContentId.NAMESPACE_CDF_ID)
                .build()
        )
        .playbackPositionMs(currentPlaybackPositionMs)
        .durationMs(durationMs)
        .creditsPositionMs(creditPositionMs)
        .state(playbackState.toAmazonPlaybackState())
        .profileId(
            AmazonProfileId.builder()
                .id(hashProfileId(profileId))
                .namespace(AmazonProfileId.NAMESPACE_APP_INTERNAL)
                .build()
        )
        .eventTimestampMs(lastWatchTimeMs)
        .buildOffDeviceEvent()

interface IAmazonEntitlementType {
    fun toAmazonEntitlementType(): Int
}

/**
 * Represents the type of entitlement for the content
 * */
enum class MyEntitlementType : IAmazonEntitlementType {
    RECORDING {
        override fun toAmazonEntitlementType() = AmazonEntitlementType.RECORDING
    },
    PURCHASE {
        override fun toAmazonEntitlementType() = AmazonEntitlementType.PURCHASE
    },
    RENTAL {
        override fun toAmazonEntitlementType() = AmazonEntitlementType.RENTAL
    }
}

interface IAmazonPlaybackState {
    fun toAmazonPlaybackState(): Int
}

/**
 * Represents the current playback state for customer watch activity
 * */
enum class MyPlaybackState : IAmazonPlaybackState {
    PLAYING {
        override fun toAmazonPlaybackState() = AmazonPlaybackState.PLAYING
    },
    PAUSED {
        override fun toAmazonPlaybackState() = AmazonPlaybackState.PAUSED
    },
    EXIT {
        override fun toAmazonPlaybackState() = AmazonPlaybackState.EXIT
    },
    INTERSTITIAL {
        override fun toAmazonPlaybackState() = AmazonPlaybackState.INTERSTITIAL
    }
}
