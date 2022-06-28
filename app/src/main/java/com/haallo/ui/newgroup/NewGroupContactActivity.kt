package com.haallo.ui.newgroup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.haallo.R
import com.haallo.api.fbrtdb.model.FirebaseUser
import com.haallo.application.HaalloApplication
import com.haallo.base.BaseActivity
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.haallo.base.ViewModelFactory
import com.haallo.base.extension.*
import com.haallo.database.entity.ContactEntity
import com.haallo.databinding.ActivityNewGroupContactBinding
import com.haallo.ui.chat.newChat.ContactModel
import com.haallo.ui.newgroup.view.NewGroupContactAdapter
import com.haallo.ui.newgroup.viewmodel.NewGroupViewModel
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NewGroupContactActivity : BaseActivity(), NewGroupContactAdapter.NewGroupContactAdapterCallback {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, NewGroupContactActivity::class.java)
        }
    }

    private lateinit var binding: ActivityNewGroupContactBinding

    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory<NewGroupViewModel>
    private lateinit var newGroupViewModel: NewGroupViewModel

    private lateinit var newGroupContactAdapter: NewGroupContactAdapter

    private var allContactModelArrayList = ArrayList<ContactModel>()
    private var searchContactModelArrayList = ArrayList<ContactModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        HaalloApplication.component.inject(this)
        newGroupViewModel = getViewModelFromFactory(viewModelFactory)

        binding = ActivityNewGroupContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listenToViewEvent()
        listenToViewModel()
    }

    private fun listenToViewEvent() {
        newGroupContactAdapter = NewGroupContactAdapter(this, allContactModelArrayList, this)
        binding.rvContactList.adapter = newGroupContactAdapter

        binding.ivBack.throttleClicks().subscribeAndObserveOnMainThread {
            onBackPressed()
        }.autoDispose()

        binding.btnNext.throttleClicks().subscribeAndObserveOnMainThread {
            checkIsParticipantSelected()
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
        newGroupViewModel.newGroupState.subscribeAndObserveOnMainThread {
            when (it) {
                is NewGroupViewModel.NewGroupViewState.GetAllPhoneContactList -> {
                    getAllFirebaseUserList(it.phoneContactArrayList)
                }
                is NewGroupViewModel.NewGroupViewState.ErrorMessage -> {
                    showLongToast(it.errorMessage)
                }
                is NewGroupViewModel.NewGroupViewState.LoadingState -> {
                    if (it.isLoading) {
                        showLoading()
                    } else {
                        hideLoading()
                    }
                }
            }
        }.autoDispose()

//        showLoading()
        newGroupViewModel.getAllPhoneContacts()
    }

    private fun getAllFirebaseUserList(phoneContactArrayList: List<ContactEntity>) {
        val firebaseUserLiveData: MutableLiveData<ArrayList<FirebaseUser>> = MutableLiveData()
        firebaseUserLiveData.observe(this) {
            prepareMatchingContactList(phoneContactArrayList, it)
        }
        firebaseDbHandler.getAllContactFromFirebase(firebaseUserLiveData)
    }

    private fun prepareMatchingContactList(phoneContactArrayList: List<ContactEntity>, firebaseUserArrayList: ArrayList<FirebaseUser>) {
        allContactModelArrayList = ArrayList()

        val myUserId = "u_${sharedPreference.userId}"
        val mobileNumber = sharedPreference.mobileNumber

        for (phoneContact in phoneContactArrayList) {
            for (firebaseUser in firebaseUserArrayList) {

                if (firebaseUser.uid.toString() == myUserId) {
                    continue
                }

                try {
                    val devicePhoneNo = if (phoneContact.phoneNo.contains("+")) {
                        PhoneNumberUtil.getInstance().parse(phoneContact.phoneNo, "").nationalNumber.toString()
                    } else {
                        phoneContact.phoneNo
                    }
                    val firebasePhoneNo = if (firebaseUser.phone.contains("+")) {
                        PhoneNumberUtil.getInstance().parse(firebaseUser.phone, "").nationalNumber.toString()
                    } else {
                        firebaseUser.phone
                    }
                    if (devicePhoneNo == mobileNumber) {
                        continue
                    }
                    if (firebasePhoneNo == mobileNumber) {
                        continue
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                var contactModel: ContactModel? = null
                if (phoneContact.phoneNo.contains("+")) {
                    if (firebaseUser.phone.contains("+")) {
                        try {
                            val devicePhoneNo = PhoneNumberUtil.getInstance().parse(phoneContact.phoneNo, "").nationalNumber.toString()
                            val firebasePhoneNo = PhoneNumberUtil.getInstance().parse(firebaseUser.phone, "").nationalNumber.toString()
                            if (devicePhoneNo == firebasePhoneNo) {
                                contactModel = ContactModel(
                                    id = firebaseUser.uid.toString(),
                                    number = firebaseUser.phone,
                                    name = phoneContact.name,
                                    pic = firebaseUser.photo,
                                    isSelected = false
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        try {
                            val devicePhoneNo = PhoneNumberUtil.getInstance().parse(phoneContact.phoneNo, "").nationalNumber.toString()
                            if (devicePhoneNo == firebaseUser.phone) {
                                contactModel = ContactModel(
                                    id = firebaseUser.uid.toString(),
                                    number = firebaseUser.phone,
                                    name = phoneContact.name,
                                    pic = firebaseUser.photo,
                                    isSelected = false
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    if (firebaseUser.phone.contains("+")) {
                        try {
                            val firebasePhoneNo = PhoneNumberUtil.getInstance().parse(firebaseUser.phone, "").nationalNumber.toString()
                            if (firebasePhoneNo == phoneContact.phoneNo) {
                                contactModel = ContactModel(
                                    id = firebaseUser.uid.toString(),
                                    number = firebaseUser.phone,
                                    name = phoneContact.name,
                                    pic = firebaseUser.photo,
                                    isSelected = false
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
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
                    allContactModelArrayList.add(contactModel)
                }
            }
        }

        runOnUiThread {
            hideLoading()

            allContactModelArrayList = removeDuplicates(allContactModelArrayList)

            newGroupContactAdapter = NewGroupContactAdapter(this, allContactModelArrayList, this)
            binding.rvContactList.adapter = newGroupContactAdapter
        }
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
        searchContactModelArrayList = ArrayList()
        if (keyword.isEmpty()) {
            newGroupContactAdapter = NewGroupContactAdapter(context, allContactModelArrayList, this)
            binding.rvContactList.adapter = newGroupContactAdapter
        } else {
            for (contactModel in allContactModelArrayList) {
                if (contactModel.name.contains(keyword, true)) {
                    searchContactModelArrayList.add(contactModel)
                }
            }
            newGroupContactAdapter = NewGroupContactAdapter(context, searchContactModelArrayList, this)
            binding.rvContactList.adapter = newGroupContactAdapter
        }
    }

    override fun adapterCallbackItemClick(contactModel: ContactModel) {
        if (searchContactModelArrayList.isNotEmpty()) {
            val mPos = searchContactModelArrayList.indexOfFirst { it.id == contactModel.id }
            if (mPos != -1) {
                searchContactModelArrayList[mPos].isSelected = !searchContactModelArrayList[mPos].isSelected
                newGroupContactAdapter.notifyItemChanged(mPos)
            }
        } else {
            val mPos = allContactModelArrayList.indexOfFirst { it.id == contactModel.id }
            allContactModelArrayList[mPos].isSelected = !allContactModelArrayList[mPos].isSelected
            newGroupContactAdapter.notifyItemChanged(mPos)
        }
    }

    private fun checkIsParticipantSelected() {
        val contactModelArrayList = ArrayList<ContactModel>()
        var isAnySelected = false
        for (contactModel in allContactModelArrayList) {
            if (contactModel.isSelected) {
                isAnySelected = true
                contactModelArrayList.add(contactModel)
            }
        }
        if (isAnySelected) {
            GroupNameActivity.contactModelArrayList = contactModelArrayList
            startActivityWithDefaultAnimation(GroupNameActivity.getIntent(this))
        } else {
            showToast(getString(R.string.msg_select_at_least_one_participant))
        }
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