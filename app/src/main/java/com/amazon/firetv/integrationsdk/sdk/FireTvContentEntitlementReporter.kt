package com.amazon.firetv.integrationsdk.sdk

import android.content.Context
import com.amazon.firetv.integrationsdk.client.model.CustomerPurchasedContent
import com.amazon.firetv.integrationsdk.client.model.CustomerRecordedContent
import com.amazon.firetv.integrationsdk.client.model.CustomerRentedContent
import com.amazon.firetv.integrationsdk.client.model.toAmazonContentEntitlement
import com.amazon.tv.developer.sdk.content.model.AmazonSetListStatus
import com.amazon.tv.developer.sdk.content.personalization.AmazonContentEntitlementReceiver
import com.amazon.tv.developer.sdk.content.personalization.model.AmazonContentEntitlement

class FireTvContentEntitlementReporter {

    fun refreshAllContentEntitlements(purchasedItems: List<CustomerPurchasedContent>,
                                      rentedItems: List<CustomerRentedContent>,
                                      recordedItems: List<CustomerRecordedContent>,
                                      context: Context) {
        isFTVIntegrationSDKSupportedOnDevice(context) {
            val amznContentEntitlements = ArrayList<AmazonContentEntitlement>()
            amznContentEntitlements.addAll(purchasedItems.map { it.toAmazonContentEntitlement() })
            amznContentEntitlements.addAll(rentedItems.map { it.toAmazonContentEntitlement() })
            amznContentEntitlements.addAll(recordedItems.map { it.toAmazonContentEntitlement() })

            AmazonContentEntitlementReceiver.getInstance(context).setContentEntitlements(AmazonSetListStatus.COMPLETE,
                amznContentEntitlements)
        }
    }

    companion object {
        private val TAG = FireTvContentEntitlementReporter::class.java.simpleName
    }
}
