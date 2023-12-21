package com.amazon.firetv.integrationsdk.sdk

import android.content.Context
import com.amazon.firetv.integrationsdk.client.model.CustomerSubscriptionEntitlement
import com.amazon.firetv.integrationsdk.client.model.toAmazonSubscriptionEntitlement
import com.amazon.tv.developer.sdk.content.model.AmazonSetListStatus
import com.amazon.tv.developer.sdk.content.entitlement.AmazonSubscriptionEntitlementReceiver

/**
 * Reports subscription entitlements to the Fire TV Integration SDK.
 */
class FireTvSubscriptionEntitlementReporter {

    fun refreshAllSubscriptionEntitlements(customerSubscriptions: List<CustomerSubscriptionEntitlement>,
                                           context: Context) {
        isFTVIntegrationSDKSupportedOnDevice(context) {
            val amznSubscriptionEntitlements = customerSubscriptions.map { it.toAmazonSubscriptionEntitlement() }

            AmazonSubscriptionEntitlementReceiver.getInstance(context).setSubscriptions(AmazonSetListStatus.COMPLETE,
                amznSubscriptionEntitlements)
        }
    }

    companion object {
        private val TAG = FireTvSubscriptionEntitlementReporter::class.java.simpleName
    }
}
