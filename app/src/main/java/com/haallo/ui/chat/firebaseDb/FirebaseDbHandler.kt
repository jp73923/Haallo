package com.haallo.ui.chat.firebaseDb

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.haallo.api.chat.model.RecentMessageModel
import com.haallo.api.fbrtdb.model.FirebaseUser
import com.haallo.constant.IntentConstant
import com.haallo.ui.call.CallModel
import com.haallo.ui.chat.activity.GroupChatActivity
import com.haallo.ui.chat.model.*
import com.haallo.ui.group.model.CreateGroupModel
import com.haallo.util.SharedPreferenceUtil
import timber.log.Timber
import java.util.*

class FirebaseDbHandler(val context: Context) {

    val messageRef by lazy {
        FirebaseDatabase.getInstance().getReference("Messages").child("PrivateMessages")
    }

    val recentMsgRef by lazy {
        FirebaseDatabase.getInstance().getReference("RecentMessage")
    }

    private val firebaseUserRef by lazy {
        FirebaseDatabase.getInstance().getReference("users")
    }
    val groupRef by lazy {
        FirebaseDatabase.getInstance().getReference("Groups")
    }
    val storyRef by lazy {
        FirebaseDatabase.getInstance().getReference("status")
    }

    val callRef by lazy {
        FirebaseDatabase.getInstance().getReference("Call")
    }

    val callRecentRef by lazy {
        FirebaseDatabase.getInstance().getReference("RecentCall")
    }

    private val groupMsgRef by lazy {
        FirebaseDatabase.getInstance().getReference("Messages").child("GroupMessages")
    }

    private val presenceRef by lazy {
        FirebaseDatabase.getInstance().getReference("presence")
    }

    fun setLastSeenStatus(userId: String, timeStamp: String) {
        presenceRef.child(userId).setValue(timeStamp)
    }

    fun getUserLastSeen(userId: String, lastSeenListener: LastSeenListener) {
        presenceRef.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChild(userId)) {
                    lastSeenListener.onLastSeenFound(dataSnapshot.child(userId).value.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    interface LastSeenListener {
        fun onLastSeenFound(lastSeen: String)
    }

    fun getUserName(userId: String, nameListener: NameListener) {
        firebaseUserRef.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChild("firstName") && dataSnapshot.hasChild("lastName")) {
                    val firstName: String = dataSnapshot.child("firstName").value.toString()
                    val lastName: String = dataSnapshot.child("lastName").value.toString()
                    nameListener.onNameFound("$firstName $lastName")
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    interface NameListener {
        fun onNameFound(name: String)
    }

    fun getUserProfilePic(userId: String, profilePicListener: ProfilePicListener) {
        firebaseUserRef.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChild("profileImage")) {
                    profilePicListener.onProfilePicFound(dataSnapshot.child("profileImage").value.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    interface ProfilePicListener {
        fun onProfilePicFound(profilePic: String)
    }

    fun getRecentMessages(userId: String, recentMsgList: MutableLiveData<ArrayList<RecentMessageModel>>) {
        val recentMsg = recentMsgList.value ?: ArrayList()
        FirebaseDatabase.getInstance().getReference("RecentMessage").child(userId).addChildEventListener(object : ChildEventListener {

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
                val msg = dataSnapshot.getValue(RecentMessageModel::class.java)
                for (i in 0 until recentMsg.size) {
                    if (recentMsg[i].id == msg?.id) {
                        recentMsg[i] = msg!!
                    }
                }
                recentMsgList.value = recentMsg
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                try {
                    val value: RecentMessageModel? = dataSnapshot.getValue(RecentMessageModel::class.java)
                    if (value != null) {
                        recentMsg.add(value)
                    }
                    recentMsgList.value = recentMsg
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val msg = dataSnapshot.getValue(RecentMessageModel::class.java)
                try {
                    for (i in 0 until recentMsg.size) {
                        if (recentMsg[i].id == msg?.id) {
                            recentMsg.remove(msg)
                        }
                    }
                    recentMsgList.value = recentMsg
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    fun getRecentCall(
        userId: String?,
        recentMsgList: MutableLiveData<ArrayList<CallModel>>
    ) {
        val recentMsg = recentMsgList.value ?: ArrayList()
        FirebaseDatabase.getInstance().getReference("RecentCall").child(userId!!).addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
                val msg = dataSnapshot.getValue(CallModel::class.java)
                for (i in 0 until recentMsg.size) {
                    if (recentMsg[i].call_id == msg?.call_id) {
                        recentMsg[i] = msg!!
                    }
                }
                recentMsgList.value = recentMsg
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                try {
                    val value: CallModel? =
                        dataSnapshot.getValue(CallModel::class.java)
                    if (value != null) {
                        recentMsg.add(value)
                    }
                    recentMsgList.value = recentMsg
                } catch (e: Exception) {

                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val msg = dataSnapshot.getValue(CallModel::class.java)
                try {
                    for (i in 0 until recentMsg.size) {
                        if (recentMsg[i].call_id == msg?.call_id) {
                            recentMsg.remove(msg)
                        }
                    }
                    recentMsgList.value = recentMsg
                } catch (e: Exception) {

                }
            }
        }
        )
    }

    fun getAllContactFromFirebase(firebaseUserLiveData: MutableLiveData<ArrayList<FirebaseUser>>) {
        val firebaseUserArrayList = ArrayList<FirebaseUser>()
        firebaseUserRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach { childDataSnapshot ->
                    childDataSnapshot.getValue(FirebaseUser::class.java)?.let {
                        firebaseUserArrayList.add(it)
                    }
                }
                firebaseUserLiveData.value = firebaseUserArrayList
            }

            override fun onCancelled(p0: DatabaseError) {
                Timber.tag("<><><>").e(p0.message)
                firebaseUserLiveData.value = firebaseUserArrayList
            }
        })
    }

    fun getAllStories(userId: String, statusData: MutableLiveData<ArrayList<StatusModel?>>) {
        val statusList: ArrayList<StatusModel?> = ArrayList()
        storyRef.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    statusList.add(it.getValue(StatusModel::class.java))
                }
                statusData.value = statusList
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun getChatMessages(
        chatNodeId: String,
        chatMsgList: MutableLiveData<ArrayList<ChatMsgModel?>>?,
        key: MutableLiveData<String>,
        userId: String
    ) {
        val recentMsg: ArrayList<ChatMsgModel?> = chatMsgList?.value ?: ArrayList()
        messageRef.child(chatNodeId).addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}

            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
                val msg = dataSnapshot.getValue(ChatMsgModel::class.java)
                for (i in 0 until recentMsg.size) {
                    if (recentMsg[i]?.senderid == msg?.senderid) {
                        recentMsg[i] = msg
                    }
                }
                /* if (!recentMsg[0]!!.deleted.contains(userId)) {
                     Log.e("fdsfsd Child changed",recentMsg.toString())
                    chatMsgList?.value = recentMsg
                     key.value = dataSnapshot.key
                 }*/
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val chatMsgModel: ChatMsgModel? =
                    dataSnapshot.getValue(ChatMsgModel::class.java)
                if (!chatMsgModel!!.deleted.contains(userId)) {
                    recentMsg.add(dataSnapshot.getValue(ChatMsgModel::class.java))
                    chatMsgList?.value = recentMsg
                    key.value = dataSnapshot.key
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val msg = dataSnapshot.getValue(ChatMsgModel::class.java)
                for (i in 0 until recentMsg.size - 1) {
                    if (recentMsg[i]?.senderid == msg?.senderid) {
                        recentMsg.remove(msg)
                    }
                }
                chatMsgList?.value = recentMsg
            }
        }
        )
    }

    fun getGroupChatMessages(
        chatNodeId: String,
        chatMsgList: MutableLiveData<ArrayList<GroupMsgModel?>>?,
        key: MutableLiveData<String>,
        userId: String
    ) {
        val recentMsg: ArrayList<GroupMsgModel?> = chatMsgList?.value ?: ArrayList()
        groupMsgRef.child(chatNodeId).addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}

            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
                val msg = dataSnapshot.getValue(GroupMsgModel::class.java)
                for (i in 0 until recentMsg.size) {
                    if (recentMsg[i]?.senderid == msg?.senderid) {
                        recentMsg[i] = msg
                    }
                }
                if (!recentMsg[0]!!.deleted.contains(userId)) {
                    chatMsgList?.value = recentMsg
                    key.value = dataSnapshot.key
                }
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val chatMsgModel: GroupMsgModel? =
                    dataSnapshot.getValue(GroupMsgModel::class.java)
                if (!chatMsgModel!!.deleted.contains(userId)) {
                    recentMsg.add(dataSnapshot.getValue(GroupMsgModel::class.java))
                    chatMsgList?.value = recentMsg
                    key.value = dataSnapshot.key
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val msg = dataSnapshot.getValue(GroupMsgModel::class.java)
                for (i in 0 until recentMsg.size - 1) {
                    if (recentMsg[i]?.senderid == msg?.senderid) {
                        recentMsg.remove(msg)
                    }
                }
                chatMsgList?.value = recentMsg
            }
        }
        )
    }

    fun saveChatMessage(chatNodeId: String, chatMsg: ChatMsgModel) {
        var uniquekey = messageRef.push().key.toString()
        chatMsg.message_id = uniquekey
        messageRef.child(chatNodeId).child(uniquekey).setValue(chatMsg)
    }

    fun saveGroupChatMesssage(chatNodeId: String, chatMsg: GroupMsgModel) {
        var uniquekey = messageRef.push().key.toString()
        chatMsg.message_id = uniquekey
        groupMsgRef.child(chatNodeId).child(uniquekey).setValue(chatMsg)
    }

    fun saveGroupChatContactMesssage(chatNodeId: String, chatMsg: GroupMsgContactModel) {
        var uniquekey = messageRef.push().key.toString()
        chatMsg.message_id = uniquekey
        groupMsgRef.child(chatNodeId).child(uniquekey).setValue(chatMsg)
    }

    fun deleteGroupMessage(groupId: String, messageId: String) {
        groupMsgRef.child(groupId).child(messageId).removeValue()
    }

    fun deleteMeesage(chatId: String, messageId: String) {
        messageRef.child(chatId).child(messageId).removeValue()
    }

    fun deleteChat(userId: String, receiverId: String) {
        recentMsgRef.child(userId).child(receiverId).removeValue().addOnCompleteListener {

        }
    }

    fun saveUser(userId: String, firebaseUser: FirebaseUser) {
        firebaseUserRef.child(userId).setValue(firebaseUser)
    }

    fun updateMessage(msg: ChatMsgModel) {
        messageRef.child(msg.chat_id).child(msg.message_id).setValue(msg)
    }

    fun updateGroupMessage(msg: GroupMsgModel) {
        groupMsgRef.child(msg.group_id).child(msg.message_id).setValue(msg)
    }

    fun updateGroupMessage(msg: GroupMsgContactModel) {
        groupMsgRef.child(msg.group_id).child(msg.message_id).setValue(msg)
    }

    fun updateRecentMessage(myUserId: String, otherUserId: String, msg: RecentMsgModel) {
        val myRecentMessage: DatabaseReference = recentMsgRef.child(myUserId)
        myRecentMessage.child(otherUserId).setValue(msg)

        msg.name = SharedPreferenceUtil.getInstance(context).name
        msg.profile_image = SharedPreferenceUtil.getInstance(context).profilePic

        val otherRecentMessage: DatabaseReference = recentMsgRef.child(otherUserId)
        otherRecentMessage.child(myUserId).setValue(msg)
    }

    fun updateRecentCall(myUserId: String, otherUserId: String, msg: CallModel) {
        val myRecentCall: DatabaseReference = callRecentRef.child(myUserId)
        myRecentCall.child(otherUserId).setValue(msg)

        msg.sendername = SharedPreferenceUtil.getInstance(context).name
        msg.senderImage = SharedPreferenceUtil.getInstance(context).profilePic

        val otherRecentMessage: DatabaseReference = callRecentRef.child(otherUserId)
        otherRecentMessage.child(myUserId).setValue(msg)
    }

    fun updateRecentMessageGroup(
        myUserId: String,
        otherUserId: String,
        msg: GroupRecentMessageModel,
        membersData: ArrayList<CreateGroupModel.MemberData>
    ) {
        for (item in membersData) {
            if (item.id.contains("u_")) {
                val otherRecentMessage: DatabaseReference = recentMsgRef.child(item.id)
                otherRecentMessage.child(otherUserId).setValue(msg)
            } else {
                val otherRecentMessage: DatabaseReference = recentMsgRef.child("u_" + item.id)
                otherRecentMessage.child(otherUserId).setValue(msg)
            }
        }
        /*  val otherRecentMessage: DatabaseReference = recentMsgRef.child(otherUserId)
          otherRecentMessage.child(myUserId).setValue(msg)*/
    }

    fun deleteChatMessages(
        deleteChatRef: DatabaseReference,
        chatNodeId: String,
        myUserId: String,
        otherUserId: String
    ) {
        deleteChatRef.child(chatNodeId).removeValue()
        /* deleteChatRef.addChildEventListener(object : ChildEventListener {
             override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                 val isDeletedId: String? =
                     (dataSnapshot.getValue(ChatMsgModel::class.java))?.deleted
                 if (isDeletedId != null) {
                     val messageKey = dataSnapshot.key
                     if (isDeletedId.contains(otherUserId)) {
                         deleteChatRef.child(messageKey!!).child("deleted").setValue(chatNodeId)
                     } else {
                         deleteChatRef.child(messageKey!!).child("deleted").setValue(myUserId)
                     }
                 }
             }

             override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
             }

             override fun onChildRemoved(snapshot: DataSnapshot) {
             }

             override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
             }

             override fun onCancelled(error: DatabaseError) {
             }
         })*/
    }

    fun createStatus(
        statusModel: StatusModel,
        userId: String
    ) {
        var generateGroupId = storyRef.push().key.toString()
        storyRef.child(userId).child(generateGroupId).setValue(statusModel)
    }

    fun createGroup(
        createGroup: CreateGroupModel,
        userId: String,
        participantsList: ArrayList<CreateGroupModel.MemberData>,
        context: Context,
        chatMsg: GroupMsgModel,
        msg: String
    ) {
        var generateGroupId = groupRef.push().key.toString()
        createGroup.group_id = generateGroupId
        groupRef.child(generateGroupId).setValue(createGroup)

        chatMsg.messageType = "1"
        chatMsg.group_id = generateGroupId
        chatMsg.group_name = createGroup.group_name
        chatMsg.message = msg
        chatMsg.profile_image = createGroup.group_image!!
        chatMsg.senderid = "u_" + SharedPreferenceUtil.getInstance(context).userId!!
        chatMsg.status = "sent"

        chatMsg.sendername = SharedPreferenceUtil.getInstance(context).userName
        val instance = Calendar.getInstance()
        chatMsg.timeStamp = (instance.timeInMillis).toString()
        saveGroupChatMesssage(generateGroupId, chatMsg)

        val recentMsgModel = GroupRecentMessageModel()
        recentMsgModel.id = generateGroupId!!
        recentMsgModel.group_id = generateGroupId!!
        recentMsgModel.name = createGroup.group_name
        recentMsgModel.profile_image = createGroup.group_image!!
        recentMsgModel.lastmessage = msg
        recentMsgModel.messageType = "1"

        recentMsgModel.timeStamp = (instance.timeInMillis.toString())
        updateRecentMessageGroup(
            "u_" + SharedPreferenceUtil.getInstance(context).userId!!,
            generateGroupId!!,
            recentMsgModel,
            participantsList
        )

        context.startActivity(
            Intent(context, GroupChatActivity::class.java)
                .putExtra(IntentConstant.OTHER_USER_ID, generateGroupId)
                .putExtra(IntentConstant.OTHER_USER_NAME, createGroup.group_name)
                .putExtra(IntentConstant.OTHER_USER_IMAGE, createGroup.group_image)
        )
    }

    fun unReadChatCountMessages(chatNodeId: String, myUserId: String): Int {
        var unReadMessage: Int = 0
        val unReadChatCountRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("messages").child(chatNodeId)
        unReadChatCountRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val readStatus: String? = (dataSnapshot.getValue(ChatMsgModel::class.java))?.status
                val receiverId: String? = (dataSnapshot.getValue(ChatMsgModel::class.java))?.receiverid
                if (readStatus != null) {
                    if (receiverId == myUserId && readStatus == "sent") {
                        unReadMessage += 1
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        return unReadMessage
    }
}