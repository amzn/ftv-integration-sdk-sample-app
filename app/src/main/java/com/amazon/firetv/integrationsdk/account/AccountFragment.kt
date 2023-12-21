// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.firetv.integrationsdk.account

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.amazon.firetv.integrationsdk.FragmentReplacementListener
import com.amazon.firetv.integrationsdk.R
import com.amazon.firetv.integrationsdk.client.MyCustomerDataApiClient
import com.amazon.firetv.integrationsdk.sdk.FireTvContentEntitlementReporter
import com.amazon.firetv.integrationsdk.sdk.FireTvSubscriptionEntitlementReporter
import com.amazon.firetv.integrationsdk.video.browser.VideoBrowserFragment
import com.amazon.firetv.integrationsdk.video.watchlist.WatchlistRepository

abstract class AccountFragment : Fragment() {

    lateinit var accountRepository: AccountRepository
    lateinit var fragmentReplacementListener: FragmentReplacementListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentReplacementListener = context as FragmentReplacementListener
        accountRepository = AccountRepository.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize buttons
        initializeNavigationButton(view)
        initializeProfileButtons(view, accountRepository.getActiveProfile(view.context))

        // Initialize click listeners
        view.findViewById<RadioGroup>(R.id.radioGroup_profile)
            .setOnCheckedChangeListener { group, checkedId ->
                // When the selection is cleared, checkedId is -1
                if (checkedId != -1) {
                    val profileName = group.findViewById<RadioButton>(checkedId).text.toString()
                    accountRepository.setActiveProfile(profileName, view.context)
                }
            }
    }

    abstract fun getNavigationButtonName(): String

    abstract fun getNavigationButtonClickListener(): (View) -> Unit

    private fun initializeNavigationButton(view: View) {
        val button = Button(context)
        button.text = getNavigationButtonName()
        button.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        button.setOnClickListener(getNavigationButtonClickListener())
        view.findViewById<LinearLayout>(R.id.account_fragment_layout)
            .addView(button)
    }

    private fun initializeProfileButtons(view: View, activeProfile: String?) {
        /* The selected profile will be sent as a hash of its profile ID when reporting playback events.
           If your app doesn't have a profile feature, you may instead report a different value.
           Please refer to the comments in
           app/src/main/java/com/amazon/firetv/integrationsdk/video/player/VideoPlayerFragment.kt
        */
        val profileButtons = listOf<RadioButton>(
            view.findViewById(R.id.radioButton_profile1),
            view.findViewById(R.id.radioButton_profile2)
        )

        activeProfile?.let {
            profileButtons.forEach { button ->
                button.isChecked = it == button.text.toString()
            }
        }
    }
}

class SignInFragment : AccountFragment() {
    override fun getNavigationButtonName(): String = "Sign In"
    override fun getNavigationButtonClickListener(): (View) -> Unit = {
        accountRepository.setLoginState(true, it.context)
        // Retrieve watchlist from server on login, WatchlistRepository will send the data to the FireTvIntegrationSDK
        val watchlistRepository = WatchlistRepository.getInstance()
        watchlistRepository.refreshWatchlistForAllProfiles(it.context)

        // Retrieve customer entitlements on login and report to FireTvIntegrationSDK
        val customerDataApiClient = MyCustomerDataApiClient()
        FireTvContentEntitlementReporter().refreshAllContentEntitlements(
            customerDataApiClient.retrieveCustomerPurchasedContent(),
            customerDataApiClient.retrieveCustomerRentedContent(),
            customerDataApiClient.retrieveCustomerRecordedContent(),
            it.context
        )
        FireTvSubscriptionEntitlementReporter().refreshAllSubscriptionEntitlements(
            customerDataApiClient.retrieveCustomerSubscriptions(),
            it.context
        )

        fragmentReplacementListener.replaceFragment(VideoBrowserFragment())
    }
}

class SignOutFragment : AccountFragment() {
    override fun getNavigationButtonName(): String = "Sign Out"
    override fun getNavigationButtonClickListener(): (View) -> Unit = {
        accountRepository.setLoginState(false, it.context)
        fragmentReplacementListener.replaceFragment(SignInFragment())
    }
}
