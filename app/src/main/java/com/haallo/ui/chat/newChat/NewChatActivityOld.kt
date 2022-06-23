package com.haallo.ui.chat.newChat

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.*
import com.haallo.base.OldBaseActivity
import com.haallo.constant.IntentConstant
import com.haallo.database.db.MyRoomDatabase
import com.haallo.databinding.ActivityNewChatBinding
import com.haallo.service.FetchContactsService
import com.haallo.ui.chat.ChatViewModelOld
import com.haallo.ui.chat.activity.ChatActivity
import com.haallo.ui.chat.model.UserModel
import com.haallo.ui.group.PickContactsActivityOld
import com.haallo.util.hide
import com.haallo.util.show
import com.haallo.util.showLog

class NewChatActivityOld : OldBaseActivity() {

    private lateinit var binding: ActivityNewChatBinding

    private lateinit var chatViewModel: ChatViewModelOld
    private var allContactAdapter: AllContactAdapter? = null
    private val firebaseUser: MutableLiveData<ArrayList<UserModel?>> = MutableLiveData()
    private var allContact: List<ContactModel> = ArrayList()
    private var isSearchBoxOpen: Boolean = false
    private var allUser: ArrayList<ContactModel> = ArrayList()
    private val dummyAllUser: ArrayList<ContactModel> = ArrayList()
    private var mobileList: List<String> = arrayListOf<String>()

    private val rootDb: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val userDb: DatabaseReference = rootDb.child("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initControl()
    }

    //All UI Changes From Here
    override fun initView() {
        chatViewModel = ViewModelProvider(this).get(com.haallo.ui.chat.ChatViewModelOld::class.java)
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
        var contactModel: ContactModel?
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

                allContact =
                    it.map { contact -> ContactModel(contact.phone_number, contact.name, "") }
                        .toList()

                firebaseDbHandler.getAllContactFromFirebase(firebaseUser)

                this.firebaseUser.observe(this) { bean ->
                    val userListOnHallo: ArrayList<ContactModel> = ArrayList()
                    val userListNotOnHallo: ArrayList<ContactModel> = ArrayList()
                    for (i in allContact.indices) {
                        var isOnHallo = false
                        for (j in 0 until bean.size) {
                            val myUserId = "u_${sharedPreference.userId}"
                            showLog(allContact[i].number)
                            if (bean[j]!!.uid != myUserId)
                                if (allContact[i].number != sharedPreference.countryCode + sharedPreference.mobileNumber) {
                                    if (bean[j]!!.phone.contains("+")) {
                                        if (allContact[i].number == bean[j]!!.phone) {
                                            isOnHallo = true
                                            contactModel = ContactModel(
                                                bean[j]?.phone ?: "",
                                                allContact[i].name ?: "",
                                                bean[j]?.photo ?: "",
                                                bean[j]?.uid ?: "",
                                                true
                                            )
                                            dummyAllUser.add(contactModel!!)
                                            //dummyAllUser.addAll(userListOnHallo)
                                            /*break*/
                                        }
                                    } else {
                                        if (allContact[i].number == bean[j]!!.countryCode + bean[j]!!.phone) {
                                            isOnHallo = true
                                            contactModel = ContactModel(
                                                bean[j]?.phone ?: "",
                                                allContact[i].name ?: "",
                                                bean[j]?.photo ?: "",
                                                bean[j]?.uid ?: "",
                                                true
                                            )
                                            dummyAllUser.add(contactModel!!)
                                            //dummyAllUser.addAll(userListOnHallo)
                                            /*break*/
                                        }
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

        allContactAdapter = AllContactAdapter(removeDuplicates(dummyAllUser), object : AllContactAdapter.AllContactListener {
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
        binding.rlNewGroup.setOnClickListener {
            startActivity(Intent(this, PickContactsActivityOld::class.java))
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
    fun removeDuplicates(list: ArrayList<ContactModel>): ArrayList<ContactModel> {

        // Create a new ArrayList
        val newList = ArrayList<String>()
        val newListContact = ArrayList<ContactModel>()

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