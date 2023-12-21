// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.firetv.integrationsdk.video.model

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

/**
 * Data class to represent metadata related to displaying and playing a video
 * */
@Parcelize
data class Video(
    val videoName: String,
    val amazonContentId: String,
    @StringRes val videoResourceId: Int,
    val videoImageUrl: String
) : Parcelable
