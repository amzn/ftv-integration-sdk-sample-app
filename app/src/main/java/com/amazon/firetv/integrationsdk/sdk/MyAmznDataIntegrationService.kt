// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.firetv.integrationsdk.sdk

import com.amazon.firetv.integrationsdk.client.MyCustomerDataApiClient
import com.amazon.firetv.integrationsdk.client.model.toAmazonContentEntitlement
import com.amazon.firetv.integrationsdk.client.model.toAmazonCustomerListEntry
import com.amazon.firetv.integrationsdk.client.model.toAmazonPlaybackEvent
import com.amazon.firetv.integrationsdk.client.model.toAmazonSubscriptionEntitlement
import com.amazon.tv.developer.sdk.content.AmazonDataIntegrationService
import com.amazon.tv.developer.sdk.content.entitlement.AmazonSubscriptionEntitlementReceiver
import com.amazon.tv.developer.sdk.content.entitlement.model.AmazonSubscriptionEntitlement
import com.amazon.tv.developer.sdk.content.personalization.AmazonContentEntitlementReceiver
import com.amazon.tv.developer.sdk.content.personalization.AmazonCustomerListReceiver
import com.amazon.tv.developer.sdk.content.personalization.AmazonPlaybackReceiver
import com.amazon.tv.developer.sdk.content.personalization.model.AmazonContentEntitlement
import com.amazon.tv.developer.sdk.content.personalization.model.AmazonCustomerListEntry
import com.amazon.tv.developer.sdk.content.personalization.model.AmazonCustomerListType
import com.amazon.tv.developer.sdk.content.personalization.model.AmazonPlaybackEvent

/**
 * Implementation for the data integration service that allows Amazon to pull data
 * from your app.
 * */
class MyAmznDataIntegrationService : AmazonDataIntegrationService() {
    val apiClient: MyCustomerDataApiClient = MyCustomerDataApiClient()

    override fun getRecentPlaybackEventsSince(
        playbackReceiver: AmazonPlaybackReceiver,
        startingTimestampMs: Long
    ) {
        val amznPlaybackEvents: List<AmazonPlaybackEvent> =
            apiClient.retrieveCustomerWatchActivity(startingTimestampMs)
                .map { it.toAmazonPlaybackEvent() }
        playbackReceiver.addPlaybackEvents(amznPlaybackEvents)
    }

    override fun getAllContentEntitlements(entitlementReceiver: AmazonContentEntitlementReceiver) {
        // No-op, API not implemented
    }

    override fun getAllSubscriptionEntitlements(entitlementsReceiver: AmazonSubscriptionEntitlementReceiver) {
        // No-op, API not implemented
    }

    override fun getAllCustomerListEntries(customerListReceiver: AmazonCustomerListReceiver, type: Int) {
        val amznCustomerListEntries: List<AmazonCustomerListEntry> =
            when (type) {
                AmazonCustomerListType.WATCHLIST -> apiClient.retrieveCustomerWatchList()
                    .map { it.toAmazonCustomerListEntry() }
                else -> listOf()
            }
        customerListReceiver.addCustomerListEntries(type, amznCustomerListEntries)
    }
}
