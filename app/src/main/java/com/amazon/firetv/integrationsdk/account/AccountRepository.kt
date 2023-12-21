// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.firetv.integrationsdk.account

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData

/**
 * Handles maintaining account level state such as login status and active profile
 * via Android SharedPreferences
 * */
class AccountRepository private constructor() {
    private val profileLiveData = MutableLiveData<String>()

    fun setLoginState(loginState: Boolean, context: Context) = getSharedPreferences(context)
        .edit(commit = true) {
            putBoolean(LOGIN_STATE_KEY, loginState)
        }

    fun getLoginState(context: Context): Boolean = getSharedPreferences(context)
        .getBoolean(LOGIN_STATE_KEY, false)

    fun setActiveProfile(profileName: String, context: Context) {
        getSharedPreferences(context)
            .edit(commit = true) {
                putString(ACTIVE_PROFILE_KEY, profileName)
            }
        profileLiveData.postValue(profileName)
    }

    fun getActiveProfile(context: Context): String = getSharedPreferences(context)
            .getString(ACTIVE_PROFILE_KEY, NO_ACTIVE_PROFILE_SET) ?: NO_ACTIVE_PROFILE_SET

    fun getActiveProfileLiveData(context: Context): MutableLiveData<String> {
        profileLiveData.postValue(getActiveProfile(context))
        return profileLiveData
    }

    private fun getSharedPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences(LOGIN_SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val LOGIN_STATE_KEY = "is_logged_in"
        private const val ACTIVE_PROFILE_KEY = "active_profile"
        private const val LOGIN_SHARED_PREF_FILE_NAME = "account_prefs"
        private const val NO_ACTIVE_PROFILE_SET = "none"

        @Volatile
        private var instance: AccountRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: AccountRepository().also { instance = it }
            }
    }
}
