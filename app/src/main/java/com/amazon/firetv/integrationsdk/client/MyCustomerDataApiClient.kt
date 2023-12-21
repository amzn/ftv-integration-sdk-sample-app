// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.firetv.integrationsdk.client

import com.amazon.firetv.integrationsdk.client.model.CustomerListEntry
import com.amazon.firetv.integrationsdk.client.model.CustomerPurchasedContent
import com.amazon.firetv.integrationsdk.client.model.CustomerRecordedContent
import com.amazon.firetv.integrationsdk.client.model.CustomerRentedContent
import com.amazon.firetv.integrationsdk.client.model.CustomerSubscriptionEntitlement
import com.amazon.firetv.integrationsdk.client.model.CustomerTitleWatched
import com.amazon.firetv.integrationsdk.client.model.MyPlaybackState

/* *
 * Client class for accessing your API to get customer data such as watch activity and entitlements.
 * */
public class MyCustomerDataApiClient {
    /* *
     * Function where you'd call your API to get customer watch activity
     * */
    public fun retrieveCustomerWatchActivity(startingTimestampMs: Long): List<CustomerTitleWatched> =
        // Replace this example event with your logic to retrieve customer watch activity from your API
        listOf(
            CustomerTitleWatched(
                amznCatalogId = "amznCatalogId1",
                profileId = "App profile 1",
                playbackState = MyPlaybackState.PAUSED,
                currentPlaybackPositionMs = 123456L,
                durationMs = 3600000L,
                creditPositionMs = 3500000L,
                lastWatchTimeMs = 1690316225L
            )
        )

    /* *
     * Function where you'd call your API to get content items the customer purchased
     * */
    public fun retrieveCustomerPurchasedContent(): List<CustomerPurchasedContent> =
        // Replace this example event with your logic to retrieve customer data
        listOf(
            CustomerPurchasedContent(
                amznCatalogId = "amznCatalogId2",
                purchaseTimestampMs = 1690316225L,
            )
        )

    /* *
     * Function where you'd call your API to get content items the customer rented
     * */
    public fun retrieveCustomerRentedContent(): List<CustomerRentedContent> =
        // Replace this example event with your logic to retrieve customer data
        listOf(
            CustomerRentedContent(
                amznCatalogId = "amznCatalogId2",
                expirationTimestampMs = 1721939083L,
                rentedTimestampMs = 1690316225L,
            )
        )

    /* *
     * Function where you'd call your API to get content items the customer has recorded
     * */
    public fun retrieveCustomerRecordedContent(): List<CustomerRecordedContent> =
        // Replace this example event with your logic to retrieve customer data
        listOf(
            CustomerRecordedContent(
                amznCatalogId = "amznCatalogId2",
                recordedTimestampMs = 1690316225L,
            )
        )

    /* *
     * Function where you'd call your API to get customer subscription entitlements
     * */
    public fun retrieveCustomerSubscriptions(): List<CustomerSubscriptionEntitlement> =
        // Replace this example event with your logic to retrieve customer data
        listOf(
            CustomerSubscriptionEntitlement(
                subscriptionId = "subscriptionId1",
                expirationTimestampMs = 1721939083L,
                acquisitionTimestampMs = 1690316225L
            )
        )

    /* *
     * Function where you'd call your API to get customer watch list
     * */
    public fun retrieveCustomerWatchList(): List<CustomerListEntry> =
        // Replace this example event with your logic to retrieve customer data
        listOf(
            CustomerListEntry(
                amznCatalogId = "tt2285752",
                profileId = "App profile 2",
                addedTimestampMs = 1690316225L
            )
        )
}
