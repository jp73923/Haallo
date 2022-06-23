package com.haallo.ui.group

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.haallo.R
import com.haallo.base.OldBaseActivity
import com.haallo.databinding.ActivityGroupNameBinding
import com.haallo.ui.chat.model.GroupMsgModel
import com.haallo.ui.group.model.ContactPickModel
import com.haallo.ui.group.model.CreateGroupModel
import com.haallo.util.showToast
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*

class GroupNameActivityOld : OldBaseActivity() {

    companion object {
        var contactList: HashMap<Int, ContactPickModel> = HashMap()
        var arrayList: ArrayList<ContactPickModel> = ArrayList()
    }

    private lateinit var binding: ActivityGroupNameBinding

    private val REQUEST_ID_MULTIPLE_PERMISSIONS: Int = 105
    private lateinit var chatViewModel: com.haallo.ui.chat.ChatViewModelOld
    private lateinit var firebaseDatabaseReference: DatabaseReference
    var participantsList: ArrayList<CreateGroupModel.MemberData> = ArrayList()
    private var groupId: String = ""
    private var groupImage: String = ""
    private var groupName: String = ""
    private val groupModel: CreateGroupModel = CreateGroupModel()
    private val chatMsg: GroupMsgModel = GroupMsgModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGroupNameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initControl()
        observers()
    }

    override fun initView() {
        chatViewModel = ViewModelProvider(this).get(com.haallo.ui.chat.ChatViewModelOld::class.java)
        firebaseDatabaseReference = FirebaseDatabase.getInstance().reference
        binding.rvParticipantList.adapter = GroupParticipantAdapter(this, arrayList)
    }

    override fun initControl() {
        binding.ivGroupIcon.setOnClickListener {
            // chooseImageFromGallery()
            ImagePicker.with(this)
                .crop()                    //Crop image(Optional), Check Customization for more option
                //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    1080,
                    1080
                )    //Final image resolution will be less than 1080 x 1080(Optional)
                .start()

        }
        binding.ivGroupIconImage.setOnClickListener {
            ImagePicker.with(this)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    1080,
                    1080
                )    //Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }

        binding.tvParticipantCount.text = arrayList.size.toString()

        for (item in arrayList) {
            participantsList.add(
                CreateGroupModel.MemberData(
                    "u_" + item.id,
                    item.name,
                    item.number,
                    item.pic,
                    0
                )
            )
        }
        participantsList.add(
            CreateGroupModel.MemberData(
                "u_" + sharedPreference.userId,
                sharedPreference.userName,
                sharedPreference.mobileNumber,
                sharedPreference.profilePic,
                0
            )
        )

        binding.tvCreate.setOnClickListener {
            if (binding.etGroupName.text.toString().isEmpty()) {
                showToast(getString(R.string.please_add_group_name))
            } else {
                val instance = Calendar.getInstance()
                groupModel.timeStamp = instance.timeInMillis.toString()
                groupModel.admin_id = "u_" + sharedPreference.userId
                groupModel.admin_name = sharedPreference.name
                groupModel.group_name = binding.etGroupName.text.toString()
                groupModel.group_image = groupImage
                groupModel.members = participantsList
                firebaseDbHandler.createGroup(
                    groupModel,
                    sharedPreference.userId,
                    participantsList, this@GroupNameActivityOld,
                    chatMsg,
                    sharedPreference.userName + " created the group"
                )
            }
        }
    }

    fun observers() {
        chatViewModel.getFileToUrlResponse.observe(this, androidx.lifecycle.Observer {
            progressDialog.dismiss()
            groupImage = it.result
            Glide.with(this).load(groupImage).placeholder(R.drawable.logo).into(binding.ivGroupIconImage)
            binding.ivGroupIcon.visibility = View.GONE
            binding.ivGroupIconImage.visibility = View.VISIBLE
        })
        chatViewModel.error.observe(this) {
            hideLoading()
            progressDialog.hide()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ImagePicker.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val fileUri = data!!.data
            val currentImage: File?
            currentImage = File(fileUri?.path)
            val image = MultipartBody.Part.createFormData("image", currentImage!!.name, currentImage.asRequestBody("image/*".toMediaTypeOrNull()))
            progressDialog.show()
            chatViewModel.getUrlApi(sharedPreference.accessToken, image)
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }
}