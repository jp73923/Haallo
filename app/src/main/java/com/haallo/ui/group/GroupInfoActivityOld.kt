package com.haallo.ui.group

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.haallo.R
import com.haallo.base.OldBaseActivity
import com.haallo.base.extension.startActivityWithDefaultAnimation
import com.haallo.constant.IntentConstant
import com.haallo.databinding.ActivityGroupInfoBinding
import com.haallo.ui.chat.model.GroupMsgModel
import com.haallo.ui.chat.model.GroupRecentMessageModel
import com.haallo.ui.group.model.CreateGroupModel
import com.haallo.ui.home.HomeActivity
import com.haallo.util.findGroupDateFromTimeStamp
import java.util.*

class GroupInfoActivityOld : OldBaseActivity() {

    private lateinit var binding: ActivityGroupInfoBinding

    var groupId = ""
    var groupImage = ""
    var membersData = ArrayList<CreateGroupModel.MemberData>()
    private val chatMsg: GroupMsgModel = GroupMsgModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGroupInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initControl()
    }

    override fun initView() {
        groupId = intent.getStringExtra(IntentConstant.GROUP_ID)!!
        firebaseDbHandler.groupRef.child(groupId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val group: CreateGroupModel = snapshot.getValue(CreateGroupModel::class.java)!!
                binding.tvHeading.text = group.group_name
                groupImage = group.group_image
                binding.tvCreatedDate.text = "Created at " + findGroupDateFromTimeStamp(group.timeStamp.toLong())
                membersData.clear()
                for (item in group.members) {
                    membersData.add(item)
                }
                binding.tvParticipantCount.text = membersData.size.toString()
                binding.rvParticipantList.adapter = ParticipantAdapter(this@GroupInfoActivityOld, membersData)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun initControl() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.ivEditGroupName.setOnClickListener {
            showGroupInfoPrompt(binding.tvHeading.text.toString().trim())
        }
        binding.tvLeaveGroup.setOnClickListener {
            sendMsgToChat(sharedPreference.name + " left the group !", binding.tvHeading.text.toString().trim())
            updatedRecentMessage(sharedPreference.name + " left the group !", "1", binding.tvHeading.text.toString().trim())

            startActivityWithDefaultAnimation(HomeActivity.getIntent(this))
            finish()
            /*  showToast("under development")*/
        }
    }

    private fun sendMsgToChat(msg: String, groupName: String) {
        chatMsg.messageType = "1"
        chatMsg.group_id = groupId
        chatMsg.group_name = groupName!!
        chatMsg.message = msg
        chatMsg.profile_image = groupImage!!
        chatMsg.isLeaved = "u_" + sharedPreference.userId!!
        chatMsg.senderid = "u_" + sharedPreference.userId!!
        chatMsg.status = "sent"

        chatMsg.sendername = sharedPreference.userName

        val instance = Calendar.getInstance()
        chatMsg.timeStamp = (instance.timeInMillis).toString()
        firebaseDbHandler.saveGroupChatMesssage(groupId, chatMsg)
    }

    private fun updatedRecentMessage(msg: String, mediaType: String, groupName: String) {
        val recentMsgModel = GroupRecentMessageModel()
        recentMsgModel.id = groupId
        recentMsgModel.group_id = groupId!!
        recentMsgModel.name = groupName
        recentMsgModel.isLeaved = "u_" + sharedPreference.userId!!
        recentMsgModel.profile_image = groupImage
        recentMsgModel.lastmessage = msg
        recentMsgModel.messageType = mediaType
        val instance = Calendar.getInstance()
        recentMsgModel.timeStamp = (instance.timeInMillis.toString())
        firebaseDbHandler.updateRecentMessageGroup(
            "u_" + sharedPreference.userId,
            groupId!!,
            recentMsgModel,
            membersData
        )
    }

    fun showGroupInfoPrompt(groupName: String) {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.group_name_prompt, null)

        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .create()

        mBuilder.window?.setBackgroundDrawableResource(R.drawable.margin_dialog)
        mBuilder.show()

        val etGroupName = mDialogView.findViewById(R.id.etGroupName) as AppCompatEditText
        val okBtn = mDialogView.findViewById(R.id.okBtn) as AppCompatTextView
        val cancelBtn = mDialogView.findViewById(R.id.cancelBtn) as AppCompatTextView

        etGroupName.setText(groupName)
        etGroupName.setSelection(groupName.length - 1)

        okBtn.setOnClickListener {
            firebaseDbHandler.groupRef.child(groupId).child("group_name").setValue(etGroupName.text.toString())
            for (item in membersData) {
                if (item.id.contains("u_")) {
                    val otherRecentMessage: DatabaseReference =
                        firebaseDbHandler.recentMsgRef.child(item.id)
                    otherRecentMessage.child(groupId).child("name")
                        .setValue(etGroupName.text.toString())
                } else {
                    val otherRecentMessage: DatabaseReference =
                        firebaseDbHandler.recentMsgRef.child("u_" + item.id)
                    otherRecentMessage.child(groupId).child("name")
                        .setValue(etGroupName.text.toString())
                }
            }
            mBuilder.dismiss()
        }
        cancelBtn.setOnClickListener {
            mBuilder.dismiss()
        }
    }
}