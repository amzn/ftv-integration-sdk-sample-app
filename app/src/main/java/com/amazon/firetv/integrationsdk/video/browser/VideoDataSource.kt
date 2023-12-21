// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.firetv.integrationsdk.video.browser

import com.amazon.firetv.integrationsdk.R
import com.amazon.firetv.integrationsdk.video.model.Video

/**
 * Data source class to handle loading list of videos used by the VideoItemAdapter
 * */
class VideoDataSource {
    fun loadVideos(): List<Video> {
        return listOf(
            Video(
                "Tears of Steel",
                "tt2285752",
                R.string.video1_url,
                "https://m.media-amazon.com/images/S/ims-msa-images/7/6/8/768fde86320f98d1ebf622f6e8931ef7.7a7a85f4._QL100_.jpg"
            ),
            Video(
                "Big Buck Bunny",
                "tt1254207",
                R.string.video2_url,
                "https://m.media-amazon.com/images/S/pv-target-images/79e7c97034a966bf199792d5c2a20bd51b3a87379fc4e2147aec5a29917579dc._QL100_.jpg"
            )
        )
    }
}
