package com.haallo.ui.newchat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.haallo.api.fbrtdb.model.FirebaseUser
import com.haallo.application.HaalloApplication
import com.haallo.base.BaseActivity
import com.haallo.base.ViewModelFactory
import com.haallo.base.extension.*
import com.haallo.constant.IntentConstant
import com.haallo.database.entity.ContactEntity
import com.haallo.databinding.ActivityNewChatContactBinding
import com.haallo.ui.chat.activity.ChatActivity
import com.haallo.ui.newchat.view.NewChatContactAdapter
import com.haallo.ui.chat.newChat.ContactModel
import com.haallo.ui.newgroup.NewGroupContactActivity
import com.haallo.ui.newchat.viewmodel.NewChatViewModel
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NewChatContactActivity : BaseActivity(), NewChatContactAdapter.NewChatContactAdapterCallback {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, NewChatContactActivity::class.java)
        }
    }

    private lateinit var binding: ActivityNewChatContactBinding

    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory<NewChatViewModel>
    private lateinit var newChatViewModel: NewChatViewModel

    private lateinit var newChatContactAdapter: NewChatContactAdapter

    private var contactModelArrayList = ArrayList<ContactModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        HaalloApplication.component.inject(this)
        newChatViewModel = getViewModelFromFactory(viewModelFactory)

        binding = ActivityNewChatContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listenToViewEvent()
        listenToViewModel()
    }

    private fun listenToViewEvent() {
        newChatContactAdapter = NewChatContactAdapter(this, contactModelArrayList, this)
        binding.rvContactList.adapter = newChatContactAdapter

        binding.ivBack.throttleClicks().subscribeAndObserveOnMainThread {
            onBackPressed()
        }.autoDispose()

        binding.llNewGroup.throttleClicks().subscribeAndObserveOnMainThread {
            startActivityWithDefaultAnimation(NewGroupContactActivity.getIntent(this))
        }.autoDispose()

        binding.ivSearch.throttleClicks().subscribeAndObserveOnMainThread {
            binding.rlHeader.visibility = View.GONE
            binding.rlSearch.visibility = View.VISIBLE
            binding.etSearch.requestFocus()
            binding.etSearch.openKeyboard(this)
        }.autoDispose()

        binding.ivClear.throttleClicks().subscribeAndObserveOnMainThread {
            binding.rlHeader.visibility = View.VISIBLE
            binding.rlSearch.visibility = View.GONE
            binding.etSearch.setText("")
            binding.etSearch.closeKeyboard(this)
        }.autoDispose()

        binding.etSearch.textChanges()
            .skipInitialValue()
            .debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                searchContactList(this, it.trim().toString())
            }.autoDispose()
    }

    private fun listenToViewModel() {
        newChatViewModel.newChatState.subscribeAndObserveOnMainThread {
            when (it) {
                is NewChatViewModel.NewChatViewState.GetAllPhoneContactList -> {
                    getAllFirebaseUserList(it.phoneContactArrayList)
                }
                is NewChatViewModel.NewChatViewState.ErrorMessage -> {
                    showLongToast(it.errorMessage)
                }
                is NewChatViewModel.NewChatViewState.LoadingState -> {
                    if (it.isLoading) {
                        showLoading()
                    } else {
                        hideLoading()
                    }
                }
            }
        }.autoDispose()

//        showLoading()
        newChatViewModel.getAllPhoneContacts()
    }

    private fun getAllFirebaseUserList(phoneContactArrayList: List<ContactEntity>) {
        val firebaseUserLiveData: MutableLiveData<ArrayList<FirebaseUser>> = MutableLiveData()
        firebaseUserLiveData.observe(this) {
            prepareMatchingContactList(phoneContactArrayList, it)
        }
        firebaseDbHandler.getAllContactFromFirebase(firebaseUserLiveData)
    }

    private fun prepareMatchingContactList(phoneContactArrayList: List<ContactEntity>, firebaseUserArrayList: ArrayList<FirebaseUser>) {
        contactModelArrayList = ArrayList()

        val myUserId = "u_${sharedPreference.userId}"

        for (phoneContact in phoneContactArrayList) {
            for (firebaseUser in firebaseUserArrayList) {

                if (firebaseUser.uid.toString() == myUserId) {
                    continue
                }

                var contactModel: ContactModel? = null

                if (phoneContact.phoneNo.contains("+")) {
                    if (firebaseUser.phone.contains("+")) {
                        if (phoneContact.phoneNo == firebaseUser.phone) {
                            contactModel = ContactModel(
                                id = firebaseUser.uid.toString(),
                                number = firebaseUser.phone,
                                name = phoneContact.name,
                                pic = firebaseUser.photo,
                                isSelected = false
                            )
                        }
                    } else {
                        if (phoneContact.phoneNo == (firebaseUser.countryCode + firebaseUser.phone)) {
                            contactModel = ContactModel(
                                id = firebaseUser.uid.toString(),
                                number = firebaseUser.phone,
                                name = phoneContact.name,
                                pic = firebaseUser.photo,
                                isSelected = false
                            )
                        }
                    }
                } else {
                    if (firebaseUser.phone.contains("+")) {
                        if (phoneContact.phoneNo == (firebaseUser.countryCode + firebaseUser.phone)) {
                            contactModel = ContactModel(
                                id = firebaseUser.uid.toString(),
                                number = firebaseUser.phone,
                                name = phoneContact.name,
                                pic = firebaseUser.photo,
                                isSelected = false
                            )
                        }
                    } else {
                        if (phoneContact.phoneNo == firebaseUser.phone) {
                            contactModel = ContactModel(
                                id = firebaseUser.uid.toString(),
                                number = firebaseUser.phone,
                                name = phoneContact.name,
                                pic = firebaseUser.photo,
                                isSelected = false
                            )
                        }
                    }
                }

                if (contactModel != null) {
                    contactModelArrayList.add(contactModel)
                }
            }
        }

        hideLoading()

        contactModelArrayList = removeDuplicates(contactModelArrayList)

        newChatContactAdapter = NewChatContactAdapter(this, contactModelArrayList, this)
        binding.rvContactList.adapter = newChatContactAdapter
    }

    private fun removeDuplicates(currentContactArrayList: ArrayList<ContactModel>): ArrayList<ContactModel> {
        val idCheckArrayList = ArrayList<String>()
        val newContactArrayList = ArrayList<ContactModel>()
        for (element in currentContactArrayList) {
            if (!idCheckArrayList.contains(element.id)) {
                idCheckArrayList.add(element.id)
                newContactArrayList.add(element)
            }
        }
        return newContactArrayList
    }

    private fun searchContactList(context: Context, keyword: String) {
        if (keyword.isEmpty()) {
            newChatContactAdapter = NewChatContactAdapter(context, contactModelArrayList, this)
            binding.rvContactList.adapter = newChatContactAdapter
        } else {
            val newContactArrayList = ArrayList<ContactModel>()
            for (contactModel in contactModelArrayList) {
                if (contactModel.name.contains(keyword, true)) {
                    newContactArrayList.add(contactModel)
                }
            }
            newChatContactAdapter = NewChatContactAdapter(context, newContactArrayList, this)
            binding.rvContactList.adapter = newChatContactAdapter
        }
    }

    override fun adapterCallbackItemClick(contactModel: ContactModel) {
        startActivityWithDefaultAnimation(
            Intent(this, ChatActivity::class.java)
                .putExtra(IntentConstant.OTHER_USER_ID, contactModel.id.removePrefix("u_"))
                .putExtra(IntentConstant.OTHER_USER_NAME, contactModel.name)
                .putExtra(IntentConstant.OTHER_USER_MOBILE, contactModel.number)
                .putExtra(IntentConstant.OTHER_USER_IMAGE, contactModel.pic)
        )
    }

    override fun onBackPressed() {
        if (binding.rlSearch.visibility == View.VISIBLE) {
            binding.rlHeader.visibility = View.VISIBLE
            binding.rlSearch.visibility = View.GONE
            binding.etSearch.setText("")
            binding.etSearch.closeKeyboard(this)
        } else {
            super.onBackPressed()
        }
    }
}