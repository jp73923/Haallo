package com.haallo.database.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.haallo.database.repositories.ContactsRepository
import com.haallo.database.db.MyRoomDatabase
import com.haallo.database.entity.ContactEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ContactsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: ContactsRepository
    val allContacts: LiveData<List<ContactEntity>>


    private val _insertContact = MutableLiveData<Boolean>()
    val insertContact: LiveData<Boolean> = _insertContact

    private val _updateContact = MutableLiveData<Boolean>()
    val updateContact: LiveData<Boolean> = _updateContact

    init {
        val contactsDao = MyRoomDatabase.getDatabase(application).contactsDao()
        repository = ContactsRepository(contactsDao)
        allContacts = repository.getAllContacts()
    }

    fun insertSingleContact(contactEntity: ContactEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertSingleContact(contactEntity)
        launch(Dispatchers.Main) {
            _insertContact.value = true
        }
    }

    fun updateContact(contactEntity: ContactEntity) = viewModelScope.launch(Dispatchers.IO){
        repository.updateContact(contactEntity)
    }

    fun insertMultipleContacts(contactEntity: ArrayList<ContactEntity>) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertMultipleContacts(contactEntity)
    }

    fun deleteAllMessages() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAllContacts()
    }
}