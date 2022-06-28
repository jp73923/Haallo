package com.haallo.ui.home.viewmodel

import android.content.Context
import com.haallo.api.contact.ContactRepository
import com.haallo.base.BaseViewModel

class HomeViewModel(
    private val contactRepository: ContactRepository
) : BaseViewModel() {

    fun fetchPhoneContacts(context: Context) {
        contactRepository.fetchPhoneContacts(context)
    }
}