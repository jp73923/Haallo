package com.haallo.ui.newchat.viewmodel

import android.content.Context
import com.haallo.api.phonecontact.PhoneContactRepository
import com.haallo.api.phonecontact.model.PhoneContact
import com.haallo.base.BaseViewModel
import com.haallo.base.extension.subscribeOnIoAndObserveOnMainThread
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class NewChatViewModel(
    private val phoneContactRepository: PhoneContactRepository
) : BaseViewModel() {

    private val newChatStateSubject: PublishSubject<NewChatViewState> = PublishSubject.create()
    val newChatState: Observable<NewChatViewState> = newChatStateSubject.hide()

    fun getAllPhoneContacts(context: Context) {
        phoneContactRepository.getAllPhoneContacts(context)
            .doOnSubscribe {
                newChatStateSubject.onNext(NewChatViewState.LoadingState(true))
            }
            .subscribeOnIoAndObserveOnMainThread({ phoneContactArrayList ->
                newChatStateSubject.onNext(NewChatViewState.GetAllPhoneContactList(phoneContactArrayList))
            }, { throwable ->
                newChatStateSubject.onNext(NewChatViewState.LoadingState(false))
                throwable.localizedMessage?.let {
                    newChatStateSubject.onNext(NewChatViewState.ErrorMessage(it))
                }
            }).autoDispose()
    }

    sealed class NewChatViewState {
        data class ErrorMessage(val errorMessage: String) : NewChatViewState()
        data class LoadingState(val isLoading: Boolean) : NewChatViewState()
        data class GetAllPhoneContactList(val phoneContactArrayList: ArrayList<PhoneContact>) : NewChatViewState()
    }
}