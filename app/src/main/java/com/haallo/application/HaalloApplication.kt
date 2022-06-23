package com.haallo.application

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import com.androidnetworking.AndroidNetworking
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.haallo.BuildConfig
import com.haallo.base.ActivityManager
import com.haallo.di.BaseAppComponent
import com.haallo.di.BaseUiApp
import timber.log.Timber

@SuppressLint("Registered")
open class HaalloApplication : BaseUiApp() {

    companion object {
        lateinit var component: BaseAppComponent

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = this

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        ActivityManager.getInstance().init(this)
        AndroidNetworking.initialize(applicationContext)
        setupLog()
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