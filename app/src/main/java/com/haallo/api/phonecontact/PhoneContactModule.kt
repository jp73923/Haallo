package com.haallo.api.phonecontact

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PhoneContactModule {

    @Provides
    @Singleton
    fun providePhoneContactRepository(
    ): PhoneContactRepository {
        return PhoneContactRepository()
    }
}