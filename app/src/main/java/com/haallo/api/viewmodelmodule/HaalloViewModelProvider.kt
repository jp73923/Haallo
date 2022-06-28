package com.haallo.api.viewmodelmodule

import com.haallo.api.contact.ContactRepository
import com.haallo.ui.home.viewmodel.HomeViewModel
import com.haallo.ui.newchat.viewmodel.NewChatViewModel
import com.haallo.ui.newgroup.viewmodel.NewGroupViewModel
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class HaalloViewModelProvider {

    @Singleton
    @Provides
    fun provideHomeViewModel(
        contactRepository: ContactRepository
    ): HomeViewModel {
        return HomeViewModel(
            contactRepository
        )
    }

    @Singleton
    @Provides
    fun provideNewChatViewModel(
        contactRepository: ContactRepository
    ): NewChatViewModel {
        return NewChatViewModel(
            contactRepository
        )
    }

    @Singleton
    @Provides
    fun provideNewGroupViewModel(
        contactRepository: ContactRepository
    ): NewGroupViewModel {
        return NewGroupViewModel(
            contactRepository
        )
    }
}