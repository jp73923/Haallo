package com.haallo.di

import android.app.Application
import android.content.Context
import com.haallo.api.fbrtdb.FBRTDBModule
import com.haallo.api.phonecontact.PhoneContactModule
import com.haallo.api.viewmodelmodule.HaalloViewModelProvider
import com.haallo.application.Haallo
import com.haallo.base.network.NetworkModule
import com.haallo.base.prefs.PrefsModule
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class HaalloAppModule(val app: Application) {
    @Provides
    @Singleton
    fun provideApplication(): Application {
        return app
    }

    @Provides
    @Singleton
    fun provideContext(): Context {
        return app
    }
}

@Singleton
@Component(
    modules = [
        HaalloAppModule::class,
        NetworkModule::class,
        PrefsModule::class,
        HaalloViewModelProvider::class,
        PhoneContactModule::class,
        FBRTDBModule::class,
    ]
)

interface HaalloAppComponent : BaseAppComponent {
    fun inject(app: Haallo)
}