// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.firetv.integrationsdk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.amazon.firetv.integrationsdk.account.AccountRepository
import com.amazon.firetv.integrationsdk.account.SignInFragment
import com.amazon.firetv.integrationsdk.video.browser.VideoBrowserFragment

interface FragmentReplacementListener {
    fun replaceFragment(fragment: Fragment)
}

class MainActivity : AppCompatActivity(), FragmentReplacementListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loggedIn = AccountRepository.getInstance().getLoginState(this)
        supportFragmentManager.commit {
            val initialFragment = if (!loggedIn) {
                SignInFragment()
            } else {
                VideoBrowserFragment()
            }

            setReorderingAllowed(true)
            add(R.id.fragment_container_view, initialFragment)
        }
    }

    override fun replaceFragment(fragment: Fragment) = supportFragmentManager.commit {
        setReorderingAllowed(true)
        replace(R.id.fragment_container_view, fragment)
    }
}
