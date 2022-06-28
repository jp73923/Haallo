package com.haallo.api.viewmodelmodule

import com.haallo.api.phonecontact.PhoneContactRepository
import com.haallo.ui.newchat.viewmodel.NewChatViewModel
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class HaalloViewModelProvider {

    @Singleton
    @Provides
    fun provideNewChatViewModel(
        phoneContactRepository: PhoneContactRepository
    ): NewChatViewModel {
        return NewChatViewModel(
            phoneContactRepository
        )
    }
}