package com.haallo.api.contact

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ContactModule {

    @Provides
    @Singleton
    fun provideContactRepository(): ContactRepository {
        return ContactRepository()
    }
}