package com.haallo.ui.newgroup.viewmodel

import com.haallo.api.contact.ContactRepository
import com.haallo.base.BaseViewModel
import com.haallo.base.extension.subscribeOnIoAndObserveOnMainThread
import com.haallo.database.entity.ContactEntity
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class NewGroupViewModel(
    private val contactRepository: ContactRepository
) : BaseViewModel() {

    private val newGroupStateSubject: PublishSubject<NewGroupViewState> = PublishSubject.create()
    val newGroupState: Observable<NewGroupViewState> = newGroupStateSubject.hide()

    fun getAllPhoneContacts() {
        contactRepository.getAllPhoneContacts()
            .doOnSubscribe {
                newGroupStateSubject.onNext(NewGroupViewState.LoadingState(true))
            }
            .subscribeOnIoAndObserveOnMainThread({ phoneContactArrayList ->
                newGroupStateSubject.onNext(NewGroupViewState.GetAllPhoneContactList(phoneContactArrayList))
            }, { throwable ->
                newGroupStateSubject.onNext(NewGroupViewState.LoadingState(false))
                throwable.localizedMessage?.let {
                    newGroupStateSubject.onNext(NewGroupViewState.ErrorMessage(it))
                }
            }).autoDispose()
    }

    sealed class NewGroupViewState {
        data class ErrorMessage(val errorMessage: String) : NewGroupViewState()
        data class LoadingState(val isLoading: Boolean) : NewGroupViewState()
        data class GetAllPhoneContactList(val phoneContactArrayList: List<ContactEntity>) : NewGroupViewState()
    }
}