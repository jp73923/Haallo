package com.haallo.api.fbrtdb

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FBRTDBModule {

    @Provides
    @Singleton
    fun provideFBRTDBContactRepository(
    ): FBRTDBRepository {
        return FBRTDBRepository()
    }
}