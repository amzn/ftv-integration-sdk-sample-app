// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.firetv.integrationsdk.sdk

import android.content.Context
import android.util.Base64
import android.util.Log
import java.security.MessageDigest

/**
 * Higher order helper function to check if the integration SDK feature library
 * is available on the device before invoking the function passed in. This is required
 * otherwise you will receive a ClassNotFound exception when attempting to access any SDK class
 * where the feature is not on the device.
 * */
inline fun isFTVIntegrationSDKSupportedOnDevice(context: Context, function: () -> Unit) {
    if (context.packageManager.hasSystemFeature(FIRE_TV_INTEGRATION_SDK_FEATURE)) {
        Log.i(FTV_FEATURE_AVAILABILITY_TAG, "The Fire TV Integration SDK is supported on this device")
        function()
    } else {
        Log.i(FTV_FEATURE_AVAILABILITY_TAG, "The Fire TV Integration SDK is not supported on this device")
    }
}

fun hashProfileId(profileId: String): String {
    /* We recommend taking a hash value of your internal profile ID, which should be the same across all devices.
       DO NOT send us the profile name provided by the customer.
       If your app does not have a profiles feature, you should provide a consistent value when reporting all
       playback events. We recommend using a hash of the account ID in this case.
    */
    val hashedProfileIdByteArray = MessageDigest.getInstance(SHA_256).digest(profileId.toByteArray())
    return Base64.encodeToString(hashedProfileIdByteArray, Base64.DEFAULT)
}

const val SHA_256 = "SHA-256"

const val FIRE_TV_INTEGRATION_SDK_FEATURE = "com.amazon.tv.developer.sdk.content"
const val FTV_FEATURE_AVAILABILITY_TAG = "FTVFeatureAvailability"
