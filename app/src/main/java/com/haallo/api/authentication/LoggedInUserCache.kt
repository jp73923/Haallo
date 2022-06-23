package com.haallo.api.authentication

import com.google.gson.Gson
import com.haallo.api.authentication.model.LoggedInUser
import com.haallo.api.authentication.model.HaalloUser
import com.haallo.base.prefs.LocalPrefs
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

/**
 *
 * This class is responsible for caching the logged in user so that the user can
 * be accessed without having to contact the server meaning it's faster and can
 * be accessed offline
 */
class LoggedInUserCache(
    private val localPrefs: LocalPrefs,
) {
    private var haalloUser: HaalloUser? = null

    private val loggedInUserCacheUpdatesSubject = PublishSubject.create<Unit>()
    val loggedInUserCacheUpdates: Observable<Unit> = loggedInUserCacheUpdatesSubject.hide()

    private val userAuthenticationFailSubject = PublishSubject.create<Unit>()
    val userAuthenticationFail: Observable<Unit> = userAuthenticationFailSubject.hide()

    private var userUnauthorized: Boolean = false

    enum class PreferenceKey(val identifier: String) {
        LOGGED_IN_USER_JSON_KEY("loggedInUser"),
        LOGGED_IN_USER_TOKEN("token")
    }

    init {
        userUnauthorized = false
        loadLoggedInUserFromLocalPrefs()
    }

    private var loggedInUserTokenLocalPref: String?
        get() {
            return localPrefs.getString(PreferenceKey.LOGGED_IN_USER_TOKEN.identifier, null)
        }
        set(value) {
            localPrefs.putString(PreferenceKey.LOGGED_IN_USER_TOKEN.identifier, value)
        }


    private fun loadLoggedInUserFromLocalPrefs() {
        val userJsonString =
            localPrefs.getString(PreferenceKey.LOGGED_IN_USER_JSON_KEY.identifier, null)
        var haalloUser: HaalloUser? = null

        if (userJsonString != null) {
            try {
                haalloUser = Gson().fromJson(userJsonString, HaalloUser::class.java)
            } catch (e: Exception) {
                Timber.e(e, "Failed to parse logged in user from json string")
            }
        }
        this.haalloUser = haalloUser
    }

    fun setLoggedInUserToken(token: String?) {
        userUnauthorized = false
        loggedInUserTokenLocalPref = token
        loadLoggedInUserFromLocalPrefs()
    }

    fun getLoginUserToken(): String? {
        return loggedInUserTokenLocalPref
    }

    fun setLoggedInUser(haalloUser: HaalloUser?) {
        localPrefs.putString(
            PreferenceKey.LOGGED_IN_USER_JSON_KEY.identifier,
            Gson().toJson(haalloUser)
        )
        loadLoggedInUserFromLocalPrefs()
        loggedInUserCacheUpdatesSubject.onNext(Unit)
    }

    fun clearLoggedInUserLocalPrefs() {
        // These preferences are currently only saved locally so we only want to wipe
        // them when a new user logs in, otherwise if the user logs out and logs back
        // in the settings will be gone
        clearUserPreferences()
    }

    fun getLoggedInUser(): LoggedInUser? {
        val loggedInUser = haalloUser ?: return null
        return LoggedInUser(loggedInUser, loggedInUserTokenLocalPref)
    }

    fun getUserId(): Int? {
        return getLoggedInUser()?.loggedInUser?.id
    }

    /**
     * Clear previous user preferences, if the current logged in user is different
     */
    private fun clearUserPreferences() {
        try {
            haalloUser = null
            for (preferenceKey in PreferenceKey.values()) {
                localPrefs.removeValue(preferenceKey.identifier)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun userUnauthorized() {
        if (!userUnauthorized) {
            userAuthenticationFailSubject.onNext(Unit)
        }
        userUnauthorized = true
    }
}