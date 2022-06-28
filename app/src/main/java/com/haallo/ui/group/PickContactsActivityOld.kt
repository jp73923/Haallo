package com.haallo.ui.group

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.haallo.api.fbrtdb.model.FirebaseUser
import com.haallo.base.OldBaseActivity
import com.haallo.constant.IntentConstant
import com.haallo.database.db.MyRoomDatabase
import com.haallo.databinding.ActivityPickContactsBinding
import com.haallo.service.FetchContactsService
import com.haallo.ui.chat.ChatViewModelOld
import com.haallo.ui.chat.activity.ChatActivity
import com.haallo.ui.group.model.ContactPickModel
import com.haallo.util.hide
import com.haallo.util.show
import com.haallo.util.showLog
import com.haallo.util.showToast

class PickContactsActivityOld : OldBaseActivity() {

    private lateinit var binding: ActivityPickContactsBinding

    private lateinit var chatViewModel: ChatViewModelOld
    private var allContactAdapter: PickContactAdapter? = null
    private val firebaseUser: MutableLiveData<ArrayList<FirebaseUser>> = MutableLiveData()
    private var allContact: List<ContactPickModel> = ArrayList()
    private var isSearchBoxOpen: Boolean = false
    private var allUser: ArrayList<ContactPickModel> = ArrayList()
    private val dummyAllUser: ArrayList<ContactPickModel> = ArrayList()
    private var mobileList: List<String> = arrayListOf()

    var hashmap: HashMap<Int, ContactPickModel> = HashMap()
    var arrayList: ArrayList<ContactPickModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPickContactsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initControl()
    }

    //All UI Changes From Here
    override fun initView() {
        chatViewModel = ViewModelProvider(this).get(ChatViewModelOld::class.java)
//        userDb.addValueEventListener(object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                for (snap in snapshot.children){
//
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//
//        })
        FetchContactsService.enqueueWork(
            this, Intent(
                this, FetchContactsService::class.java
            ), false
        )
        observer()
        getContactList()
        searchBoxListener()
    }

    //Observer
    private fun observer() {
        chatViewModel.matchContactResponse.observe(this) {
            hideLoading()
            showLog(it.result.toString())
        }

        chatViewModel.error.observe(this) {
            hideLoading()
            showError(this, binding.rootNewchat, it)
        }
    }

    //Get Contact List
    private fun getContactList() {
        var contactModel: ContactPickModel?
        /*val cr = contentResolver
        val cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        if (cur?.count ?: 0 > 0) {
            while (cur != null && cur.moveToNext()) {
                val id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID))
                val name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    val pCur = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null
                    )
                    while (pCur!!.moveToNext()) {
                        val phoneNo =
                            pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        contactModel = ContactModel(phoneNo, name, "")
                        showLog(name)
                        showLog(phoneNo)
                        allContact.add(contactModel)
                        mobile.add(phoneNo)
                    }
                    pCur.close()
                }
            }
        }
        cur?.close()*/

        MyRoomDatabase.getDatabase(this).contactsDao().getAllContactsLiveData()
            .observe(this, Observer {
                mobileList = it.map { it.phone_number }.toList()
                chatViewModel.matchContactApi(sharedPreference.accessToken, mobileList)

                allContact = it.map { contact -> ContactPickModel(contact.phone_number, contact.name, "") }.toList()
                firebaseDbHandler.getAllContactFromFirebase(firebaseUser)

                this.firebaseUser.observe(this) { bean ->
                    val userListOnHallo: ArrayList<ContactPickModel> = ArrayList()
                    val userListNotOnHallo: ArrayList<ContactPickModel> = ArrayList()
                    for (i in allContact.indices) {
                        var isOnHallo = false
                        for (j in 0 until bean.size) {
                            val myUserId = "u_${sharedPreference.userId}"
                            showLog(allContact[i].number)
                            if (bean[j]!!.uid.toString() != myUserId)
                                if (allContact[i].number != sharedPreference.countryCode + sharedPreference.mobileNumber) {

                                    if (allContact[i].number == bean[j]!!.countryCode + bean[j]!!.phone) {
                                        isOnHallo = true
                                        contactModel = ContactPickModel(
                                            bean[j]?.phone ?: "",
                                            allContact[i].name ?: "",
                                            bean[j]?.photo ?: "",
                                            bean[j]?.uid.toString(),
                                            false,
                                            true
                                        )
                                        dummyAllUser.add(contactModel!!)
                                        //dummyAllUser.addAll(userListOnHallo)
                                        /*break*/
                                    }
                                }
                        }

                        /*if (!isOnHallo) {
                            showLog("${allContact[i].name} NotOnHaallo")
                            contactModel = ContactModel("", allContact[i].name, "", "", false)
                            userListNotOnHallo.add(contactModel!!)
                        }*/
                    }

                    /*  allUser.addAll(userListOnHallo)
                      // allUser.addAll(userListNotOnHallo)*/

                    if (dummyAllUser.isEmpty()) {
                        hideLoading()
                    } else {
                        hideLoading()
                        setContactAdapter()
                    }
                }
            })


    }

    //Set Contact Adapter
    private fun setContactAdapter() {
        hideLoading()

        allContactAdapter = PickContactAdapter(this, removeDuplicates(dummyAllUser), object : PickContactAdapter.AllContactPickerListener {
            override fun itemClick(
                otherUserId: String,
                otherUserName: String,
                otherUserPic: String,
                number: String
            ) {
                chatActivity(otherUserId, otherUserName, otherUserPic, number)
            }

        })
        binding.rvContactList.adapter = allContactAdapter
    }

    //Chat Activity
    private fun chatActivity(
        otherUserId: String,
        otherUserName: String,
        otherUserPic: String,
        number: String
    ) {
        val id = otherUserId.removePrefix("u_")
        startActivity(
            Intent(this, ChatActivity::class.java)
                .putExtra(IntentConstant.OTHER_USER_ID, id.toString())
                .putExtra(IntentConstant.OTHER_USER_NAME, otherUserName)
                .putExtra(IntentConstant.OTHER_USER_MOBILE, number)
                .putExtra(IntentConstant.OTHER_USER_IMAGE, otherUserPic)
        )
    }

    //Search Box Listener
    private fun searchBoxListener() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    dummyAllUser.clear()
                    for (i in 0 until allUser.size) {
                        val name: String = allUser[i].name
                        if (name.contains(s.toString())) {
                            dummyAllUser.add(allUser[i])
                        }
                    }
                    setContactAdapter()
                } else {
                    dummyAllUser.addAll(allUser)
                    setContactAdapter()
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    //All Controls Defines Here
    override fun initControl() {
        binding.ivBack.setOnClickListener {
            backClick()
        }
        binding.btnNext.setOnClickListener {

            if (arrayList.size != 0) {
                GroupNameActivityOld.arrayList = arrayList
                startActivity(Intent(this, GroupNameActivityOld::class.java))
                Log.e("fdsfds", arrayList.toString())
            } else {
                showToast("Please Select atleast one participant", this)
            }
        }

        binding.ivSearch.setOnClickListener {
            isSearchBoxOpen = true
            binding.tvHeading.hide()
            binding.ivSearch.hide()
            binding.etSearch.show()
        }
    }

    //OnBack Pressed
    override fun onBackPressed() {
        backClick()
    }

    //Back Click
    private fun backClick() {
        if (isSearchBoxOpen) {
            isSearchBoxOpen = false
            binding.etSearch.hide()
            binding.ivSearch.show()
            binding.tvHeading.show()
        } else {
            super.onBackPressed()
        }
    }


    // Function to remove duplicates from an ArrayList
    private fun removeDuplicates(list: ArrayList<ContactPickModel>): ArrayList<ContactPickModel> {

        // Create a new ArrayList
        val newList = ArrayList<String>()
        val newListContact = ArrayList<ContactPickModel>()

        // Traverse through the first list
        for (element in list) {

            // If this element is not present in newList
            // then add it
            if (!newList.contains(element.id)) {
                newList.add(element.id)
                newListContact.add(element)
            }
        }

        // return the new list
        return newListContact
    }

}