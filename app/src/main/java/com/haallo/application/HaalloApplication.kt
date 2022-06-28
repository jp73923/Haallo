package com.haallo.application

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.multidex.MultiDex
import com.androidnetworking.AndroidNetworking
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.haallo.BuildConfig
import com.haallo.R
import com.haallo.base.ActivityManager
import com.haallo.database.db.HaalloDatabase
import com.haallo.di.BaseAppComponent
import com.haallo.di.BaseUiApp
import com.haallo.util.SharedPreferenceUtil
import timber.log.Timber

@SuppressLint("Registered")
open class HaalloApplication : BaseUiApp() {

    companion object {
        lateinit var component: BaseAppComponent

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        fun updateNightMode(context: Context) {
            val sharedPreference = SharedPreferenceUtil.getInstance(context)
            if (sharedPreference.nightTheme) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = this

        updateNightMode(this)

        ActivityManager.getInstance().init(this)
        AndroidNetworking.initialize(applicationContext)

        setupLog()

        HaalloDatabase.initDatabase(this)
    }

    private fun setupLog() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
    }

    override fun getAppComponent(): BaseAppComponent {
        return component
    }

    override fun setAppComponent(baseAppComponent: BaseAppComponent) {
        component = baseAppComponent
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}