package com.haallo.ui.chat.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asynctaskcoffee.audiorecorder.uikit.VoiceSenderDialog
import com.asynctaskcoffee.audiorecorder.worker.AudioRecordListener
import com.asynctaskcoffee.audiorecorder.worker.MediaPlayListener
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.ui.GPHContentType
import com.giphy.sdk.ui.Giphy
import com.giphy.sdk.ui.views.GiphyDialogFragment
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.*
import com.haallo.R
import com.haallo.constant.Constants
import com.haallo.constant.IntentConstant
import com.haallo.databinding.ActivityChatBinding
import com.haallo.ui.call.CallModel
import com.haallo.ui.call.EnumUtils
import com.haallo.ui.call.OneToOneAudioCallActivityOld
import com.haallo.ui.call.OneToOneVideoCallActivityOld
import com.haallo.ui.chat.ChatViewModelOld
import com.haallo.ui.chat.EmojiAdapter
import com.haallo.ui.chat.EmojiAdapterSelected
import com.haallo.ui.chat.adapter.ChatMessageAdapter
import com.haallo.ui.chat.constant.AppConstant
import com.haallo.ui.chat.dialog.*
import com.haallo.ui.chat.firebaseDb.FirebaseDbHandler
import com.haallo.ui.chat.model.ChatMsgModel
import com.haallo.ui.chat.model.RecentMsgModel
import com.haallo.ui.chat.placespicker.Place
import com.haallo.ui.chat.placespicker.PlacesPickerActivity
import com.haallo.ui.chat.util.RealPathUtil
import com.haallo.ui.groupinfo.GroupInfoActivity
import com.haallo.util.*
import com.imagepicker.FilePickUtils
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.config.Configurations
import com.jaiselrahman.filepicker.model.MediaFile
import com.squareup.picasso.Picasso
import com.wafflecopter.multicontactpicker.ContactResult
import com.wafflecopter.multicontactpicker.LimitColumn
import com.wafflecopter.multicontactpicker.MultiContactPicker
import com.wafflecopter.multicontactpicker.RxContacts.PhoneNumber
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*

class ChatActivity : ScreenshotDetectionActivity(), View.OnClickListener, GiphyDialogFragment.GifSelectionListener, AudioRecordListener, MediaPlayListener {

    private lateinit var binding: ActivityChatBinding

    private var audioFilePath: String? = null
    private var cancelBound = 100f
    private val chatMsg: ChatMsgModel = ChatMsgModel()
    private var chatMsgAdapter: ChatMessageAdapter? = null
    private var chatMsgList: MutableLiveData<ArrayList<ChatMsgModel?>> = MutableLiveData()
    private val messageKey: MutableLiveData<String> = MutableLiveData()
    private var initialX = 0f
    private var isAudioDeleted = false
    private var recorder: MediaRecorder? = null
    private var mediaTypeGlobal: String = ""
    private lateinit var chatViewModel: ChatViewModelOld
    private var otherUserId: String? = "0"
    private var otherUserIdwithU: String = ""
    private var myUserIdwithU: String = ""
    private var otherUserName: String? = ""
    private var otherUserMobile: String? = ""
    private var otherUserPic: String? = ""
    private var myUserId: String = "0"
    private var myUserName: String = ""
    private var myUserPic: String? = ""
    private var chatNodeId: String = ""
    private var contact: ArrayList<ContactResult> = ArrayList()
    private var isAttachmentOpen: Boolean = false
    private var isEmojiOpen: Boolean = false
    private lateinit var readStatusFirebaseDatabase: DatabaseReference
    private lateinit var myUserBlockFirebaseDatabase: DatabaseReference
    private lateinit var otherUserBlockFirebaseDatabase: DatabaseReference
    private lateinit var firebaseDatabaseReference: DatabaseReference
    private var timer: CountDownTimer? = null
    private var muteUnMuteStatus: String = ""
    private var isYouBlockToOtherUser: Boolean = false
    private var isOtherUserBlockToYou: Boolean = false
    private val blockUser: ArrayList<String> = ArrayList()
    private var fileRequestCode = 19235
    private var otherUserOnline: Boolean = false
    private var channelName: String = ""
    private var callModel: CallModel = CallModel()
    private val pickLocationRequest = 7125
    private var emojIcon: EmojIconActions? = null
    private val REQUEST_ID_MULTIPLE_PERMISSIONS: Int = 105
    lateinit var mediaPlayer: MediaPlayer
    val PERMISSIONS: Array<String> = arrayOf(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
    )
    var key = "jAJnN6X08Rc1CU7Iob8H06iFEfQLovqV"

    var EmojiIcons: ArrayList<String> = ArrayList()
    var arrayListSelectedEmoji: ArrayList<String> = ArrayList()

    var selectedEmoji = ""
    var textToSearch = ""

    //Permission Result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (lifeCycleCallBackManager != null) {
            lifeCycleCallBackManager.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
            )
        }
    }

    //OnActivityResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val filePath: String?
        if (lifeCycleCallBackManager != null) {
            lifeCycleCallBackManager.onActivityResult(requestCode, resultCode, data)
        }

        if (requestCode == Constants.CAMERA) {
            if (data != null) {
                val thumbnail = data.extras!!.get("data") as Bitmap
                val fileUri = bitmapToFile(thumbnail, applicationContext)
                val currentImage: File?
                currentImage = File(fileUri.path)

                val image =
                    MultipartBody.Part.createFormData(
                        "image",
                        currentImage!!.name,
                        currentImage.asRequestBody("image/*".toMediaTypeOrNull())
                    )
                postFileToServer(image, "2")
            }
        } else if (requestCode == ImagePicker.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val fileUri = data!!.data
            val currentImage: File?
            currentImage = File(fileUri?.path)
            val image =
                MultipartBody.Part.createFormData(
                    "image",
                    currentImage.name,
                    currentImage.asRequestBody("image/*".toMediaTypeOrNull())
                )


            postFileToServer(image, "2")

        } else if (data != null) {
            when (requestCode) {
                AppConstant.REQUEST_CODE_SELECT_AUDIO -> {
                    if (data.data != null) {
                        filePath = RealPathUtil.getRealPath(this, data.data!!)
                        if (filePath != null && File(filePath).exists() && resultCode == Activity.RESULT_OK) {
                            storeAudioToLocal(filePath)
                        }
                    }
                }

                AppConstant.REQUEST_CODE_SELECT_DOCUMENTS -> {
                    if (data.data != null) {
                        onSelectedPdf(data)
                    }
                }

                AppConstant.REQUEST_CODE_SELECT_CONTACT -> {
                    contact = MultiContactPicker.obtainResult(data)
                    val phoneNumber: ArrayList<PhoneNumber> =
                        contact[0].phoneNumbers as ArrayList<PhoneNumber>
                    if (phoneNumber.isNotEmpty()) {
                        if (phoneNumber.size > 1) {
                            val chooseContactDialog = ChooseContactDialog(this, phoneNumber, object : ChooseContactDialog.ChooseContactDialogListener {
                                override fun onSelectContact(position: Int) {
                                    sendContactToChat(
                                        phoneNumber[position].number,
                                        contact[0].displayName
                                    )
                                }
                            })
                            chooseContactDialog.show()
                        } else {
                            sendContactToChat(phoneNumber[0].number, contact[0].displayName)
                        }
                    }
                }

                fileRequestCode -> {
                    val videoFile: MediaFile?
                    val fileList: ArrayList<MediaFile>? =
                        data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES)
                    if (fileList != null && fileList.isNotEmpty()) {
                        videoFile = fileList[0]
                        filePath = RealPathUtil.getRealPath(this, videoFile.uri)
                        storeVideoToLocal(filePath!!)


                    }
                }

                pickLocationRequest -> {
                    val place: Place? = data.getParcelableExtra(Place.EXTRA_PLACE)
                    sendLocation(place)
                }
            }
        }
    }

    private val onFileChoose = FilePickUtils.OnFileChoose { fileUri, requestCode ->
        val currentImage: File?
        currentImage = File(fileUri)
//        storeImageToLocal(currentImage.path)
        val bitmap = BitmapFactory.decodeFile(fileUri)


        val image =
            MultipartBody.Part.createFormData(
                "image",
                currentImage.name,
                currentImage.asRequestBody("image/*".toMediaTypeOrNull())
            )


        postFileToServer(image, "2")
    }

    private var filePickUtils = FilePickUtils(this, onFileChoose)
    private var lifeCycleCallBackManager = filePickUtils.callBackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO),
            1
        )
        initView()
        initControl()
    }

    //All UI Changes From Here
    override fun initView() {
        mediaPlayer = MediaPlayer()
        chatViewModel = ViewModelProvider(this).get(ChatViewModelOld::class.java)
        Giphy.configure(this, key)
        getMyData()
        getDataFromIntent()
        createChatNode()
        setData()
        observer()
        mTextChangeListener()
        chatMessageRecycler()
        mFirebaseDbObserver()
        //voiceClickListener()
        lastSeenListener()
        findBlockUser()
        attachmentViewClick()
        emojIcon = EmojIconActions(
            applicationContext,
            binding.parentView,
            binding.etMessage,
            binding.ivSmily,
            "#495C66",
            "#DCE1E2",
            "#E6EBEF"
        )
// add emojis for People
        for (i in 0 until 194) {
            EmojiIcons.add("huminity_" + (i + 1))
        }

        binding.rvEmojiSetList.adapter = EmojiAdapterSelected(this@ChatActivity, arrayListSelectedEmoji)

        binding.rvEmojiSetList.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.rvEmojiList.adapter = EmojiAdapter(this, EmojiIcons, object : EmojiAdapter.OnEmojiSelectedListener {
            override fun onEmojiSelected(position: Int, emoji: String) {
                arrayListSelectedEmoji.add(emoji)
                binding.cross.visibility = View.VISIBLE
                binding.ivVoice.visibility = View.GONE
                binding.ivSend.visibility = View.VISIBLE
                (binding.rvEmojiSetList.adapter as EmojiAdapterSelected).notifyDataSetChanged()
            }
        })
        prefixEmojicon()
    }

    //Get My Data
    private fun getMyData() {
        myUserId = sharedPreference.userId
        myUserName = sharedPreference.name
        myUserPic = sharedPreference.profilePic
    }

    //Get Data From Intent
    private fun getDataFromIntent() {
        otherUserId = intent.getStringExtra(IntentConstant.OTHER_USER_ID)
        otherUserName = intent.getStringExtra(IntentConstant.OTHER_USER_NAME)
        otherUserMobile = intent.getStringExtra(IntentConstant.OTHER_USER_MOBILE)
        otherUserPic = intent.getStringExtra(IntentConstant.OTHER_USER_IMAGE)

        if (otherUserId != null) {
            //showLoading()
            //chatViewModel.muteUnMuteStatusApi(sharedPreference.userId.toString(), otherUserId!!)
        } else {
            finish()
        }

        myUserId = SharedPreferenceUtil.getInstance(this).userId
        if (myUserId.toString().contains("u_")) {
            myUserIdwithU = "$myUserId"
        } else {
            myUserIdwithU = "u_$myUserId"
        }
        if (otherUserId.toString().contains("u_")) {
            otherUserIdwithU = "$otherUserId"
        } else {
            otherUserIdwithU = "u_$otherUserId"
        }

    }

    //Create Chat Node
    private fun createChatNode() {
        //  chatNodeId = otherUserIdwithU + "_" + myUserIdwithU
        if (myUserIdwithU.removePrefix("u_").toInt() < otherUserIdwithU?.removePrefix("u_")!!.toInt()) {
            chatNodeId = myUserIdwithU + "_" + otherUserIdwithU
        } else {
            chatNodeId = otherUserIdwithU + "_" + myUserIdwithU
        }

        firebaseDatabaseReference = FirebaseDatabase.getInstance().reference
    }

    //Set Data
    private fun setData() {
        /* firebaseDbHandler.getUserProfilePic(otherUserIdwithU,
             object : FirebaseDbHandler.ProfilePicListener {
                 override fun onProfilePicFound(profilePic: String) {
                     if (profilePic != "") {
                         otherUserPic = profilePic
                         Picasso.get().load(profilePic).into(ivOtherUserPic)
                     }
                 }
             })*/
        if (otherUserPic != null && otherUserPic != "") {

            Picasso.get().load(otherUserPic).placeholder(R.drawable.logo).into(binding.ivOtherUserPic)
        }

        binding.tvHeading.text = otherUserName
        chatMsg.senderid = myUserIdwithU
        chatMsg.sendername = myUserName
        chatMsg.receiverid = otherUserIdwithU
        chatMsg.receivername = otherUserName!!
        chatMsg.status = "sent"
        //  chatMsg.se d = myUserIdwithU
    }

    //Observer
    private fun observer() {
        chatViewModel.getFileToUrlResponse.observe(this) {
            progressDialog.hide()
            sendFileToChat(it.result, mediaTypeGlobal)
            var message = ""
            when (mediaTypeGlobal) {
                "2" -> {
                    message = "Sent an image"
                }

                "3" -> {
                    message = "Sent an video"
                }

                "4" -> {
                    message = "Sent an audio"
                }

                "7" -> {
                    message = "Sent an document"
                }
            }
            chatNotificationApi(message)
        }

        chatViewModel.reportUserResponse.observe(this) {
            hideLoading()
            showToast(it.message)
        }

        chatViewModel.audiocallResponse.observe(this) {
            hideLoading()
            showToast("Audio Call")
            startActivity(
                Intent(this, OneToOneAudioCallActivityOld::class.java)
                    .putExtra(IntentConstant.OTHER_USER_ID, otherUserId!!.replace("u_", "")!!)
                    .putExtra(IntentConstant.OTHER_USER_NAME, otherUserName)
                    .putExtra(IntentConstant.CALL_STATUS, EnumUtils.CallStatus.OUTGOING.value)
                    .putExtra(IntentConstant.CHANEL_NAME, channelName)
                    .putExtra(IntentConstant.IS_REAR_CAMERA, false)
                    .putExtra(IntentConstant.IS_MUTE_VIDEO, false)
                    .putExtra(IntentConstant.IS_PAUSED_VIDEO, false)
            )
        }
        chatViewModel.videoCallResponse.observe(this, androidx.lifecycle.Observer {
            hideLoading()
            showToast("Video Call")
            startActivity(
                Intent(this, OneToOneVideoCallActivityOld::class.java)
                    .putExtra(IntentConstant.OTHER_USER_ID, otherUserId!!.replace("u_", "")!!)
                    .putExtra(IntentConstant.OTHER_USER_NAME, otherUserName)
                    .putExtra(IntentConstant.CALL_STATUS, EnumUtils.CallStatus.OUTGOING.value)
                    .putExtra(IntentConstant.CHANEL_NAME, channelName)
                    .putExtra(IntentConstant.IS_REAR_CAMERA, false)
                    .putExtra(IntentConstant.IS_MUTE_VIDEO, false)
                    .putExtra(IntentConstant.IS_PAUSED_VIDEO, false)
            )


        })

        chatViewModel.error.observe(this) {
            hideLoading()
            progressDialog.hide()
            // showError(this, parentView, it)
        }
    }

    //Send File Url to FirBase with Media Type
    private fun sendFileToChat(fileUrl: String, mediaType: String) {
        chatMsg.messageType = mediaType
        chatMsg.status = "sent"
        chatMsg.message = fileUrl.substring(
            fileUrl.lastIndexOf("/") + 1
        ).trim()
        chatMsg.mediaurl = fileUrl

        chatMsg.chat_id = chatNodeId
        val instance = Calendar.getInstance()
        chatMsg.timeStamp = instance.timeInMillis.toString()


        firebaseDbHandler.saveChatMessage(chatNodeId, chatMsg)
        updatedRecentMessage(fileUrl, mediaType)
    }

    //Update Recent Message on Firebase
    private fun updatedRecentMessage(msg: String, mediaType: String) {
        val recentMsgModel = RecentMsgModel()
        recentMsgModel.id = otherUserIdwithU
        recentMsgModel.name = otherUserName
        recentMsgModel.profile_image = otherUserPic
        recentMsgModel.senderid = myUserIdwithU
        recentMsgModel.mobile = otherUserMobile
        recentMsgModel.receiverid = otherUserIdwithU
        recentMsgModel.lastmessage = msg
        recentMsgModel.messageType = mediaType
        val instance = Calendar.getInstance()
        recentMsgModel.timeStamp = (instance.timeInMillis.toString())
        firebaseDbHandler.updateRecentMessage(myUserIdwithU, otherUserIdwithU, recentMsgModel)
    }

    //Update Recent Message on Firebase
    private fun updatedRecentCall(callModel: CallModel) {
        firebaseDbHandler.updateRecentCall(myUserIdwithU, otherUserIdwithU, callModel)
        firebaseDbHandler.updateRecentCall(otherUserIdwithU, myUserIdwithU, callModel)
    }

    //Message Box Listener
    private fun mTextChangeListener() {
        binding.etMessage.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s == null || s.isBlank()) {
                    binding.ivVoice.doVisible()
                    binding.ivSend.doInVisible()
                    return
                }
                binding.ivVoice.doInVisible()
                binding.ivSend.doVisible()
                if (isAttachmentOpen) {
                    closeAttachment()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    //Set Chat Message Recycler
    private fun chatMessageRecycler() {
        chatMsgAdapter = ChatMessageAdapter(
            this,
            this,
            myUserIdwithU,
            com.haallo.ui.chat.util.ChatDateTimeUtil(),
            object : ChatMessageAdapter.onAudioPlayer {
                override fun handleAudio(audioPlayer: MediaPlayer) {
                    mediaPlayer = audioPlayer
                }

            },
            object : ChatMessageAdapter.onMessageClick {
                override fun handleClick(
                    chatMsgModel: ChatMsgModel,
                    isLoginUser: Boolean,
                    isCopy: Boolean
                ) {
                    showActionPrompt(chatMsgModel, isLoginUser, isCopy)
                }
            },
            object : ChatMessageAdapter.messageAction {
                override fun onActionDone(chatMsgModel: ChatMsgModel) {
                    updateMessage(chatMsgModel)
                }

            }
        )
        binding.rvChatMessage.layoutManager = LinearLayoutManager(this)
        binding.rvChatMessage.itemAnimator = null
        binding.rvChatMessage.adapter = chatMsgAdapter

        chatMsgAdapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                binding.rvChatMessage.scrollToPosition(itemCount - 1)
            }
        })

        binding.rvChatMessage.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                var tempChatMsg: ChatMsgModel? = null
                val pos: Int = (binding.rvChatMessage.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                if (pos > -1) {
                    tempChatMsg = chatMsgAdapter?.getItemAtPos(pos)
                    val dayMessage =
                        com.haallo.ui.chat.util.ChatDateTimeUtil.getDayForChat(
                            this@ChatActivity,
                            tempChatMsg?.timeStamp?.toLong()!!
                        )
                    binding.tvDayOfMsg.text = dayMessage
                }
            }
        })
    }

    //Firebase Db Observer
    private fun mFirebaseDbObserver() {
        myUserBlockFirebaseDatabase = FirebaseDatabase.getInstance().getReference("userState").child(myUserIdwithU)
            .child("blockUsers")

        otherUserBlockFirebaseDatabase = FirebaseDatabase.getInstance().getReference("userState")
            .child(otherUserIdwithU)
            .child("blockUsers")

        readStatusFirebaseDatabase = FirebaseDatabase.getInstance().getReference("messages").child(chatNodeId)

        chatMsgList.value?.clear()
        firebaseDbHandler.getChatMessages(
            chatNodeId,
            chatMsgList,
            messageKey,
            sharedPreference.userId
        )
        chatMsgList.observe(this) {
            chatMsgAdapter?.submitList(null)
            chatMsgAdapter?.submitList(it)
        }

        messageKey.observe(this) {
            readStatusFirebaseDatabase.child(it).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.hasChild("receiverid")) {
                        val receiverId = dataSnapshot.child("receiverid").value.toString()
                        val readStatus = dataSnapshot.child("readStatus").value.toString()
                        if (receiverId == sharedPreference.userId.toString() && readStatus != "seen") {
                            readStatusFirebaseDatabase.child(it).child("readStatus")
                                .setValue("seen")
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }

    //Voice Click Listener
    @SuppressLint("ClickableViewAccessibility")
    /*  private fun voiceClickListener() {
          ivVoice.setOnTouchListener { v, event ->
              if (isAttachmentOpen) {
                  closeAttachment()
              }
              checkAndOpenPicAudioFile(event)
              true
          }
      }*/

    private fun checkAndOpenPicAudioFile(event: MotionEvent?) {
        if (checkStoragePermission(this) && checkAudioPermission(this)) {
            if (event == null) {
                //Intrinsics.throwNpe()
            }
            when (event!!.action) {
                MotionEvent.ACTION_DOWN -> {
                    isAudioDeleted = false
                    binding.ivAttachment.hide()
                    binding.cvMessage.hide()
                    binding.ivSmily.hide()
                    binding.ivCustomSmiley.hide()
                    binding.ivCamera.hide()
                    binding.recordingLayout.doVisible()

                    binding.recTimer.base = SystemClock.elapsedRealtime()
                    binding.recTimer.start()
                    startRecording()
                }
                MotionEvent.ACTION_UP -> {
                    if (!isAudioDeleted) {
                        actionUp(true)
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (binding.ivVoice.x != 0.0f) {
                        val x = binding.ivVoice.x
                        if (x <= binding.slideToCancel.x + cancelBound) {
                            isAudioDeleted = true
                            actionUp(false)
                        }
                    }
                    if (event.rawX < initialX && isAudioDeleted) {
                        binding.ivVoice.animate().x(event.rawX).setDuration(0).start()
                    }
                }
            }
        } else {
            showToast("Please provide audio permission")
        }
    }

    @SuppressLint("InlinedApi")
    private fun actionUp(isRecorded: Boolean) {
        stopRecording()
        binding.recTimer.stop()
        binding.ivAttachment.show()
        binding.cvMessage.show()
        binding.ivCustomSmiley.show()
//      ivSmily.show()
        binding.ivCamera.show()
        binding.recordingLayout.hide()
        val audioDuration = getAudioDuration(this, audioFilePath!!)
        if (audioDuration > 512 && isRecorded) {
            progressDialog.show()
            progressDialog.setMessage("Uploading Audio")
            val file = File(audioFilePath!!)
            val audio = file.let {
                MultipartBody.Part.createFormData(
                    "image",
                    file.name,
                    file.asRequestBody("audio/*".toMediaTypeOrNull())
                )
            }
            postFileToServer(audio, "4")
        } else {
            deleteAudioFile(audioFilePath!!)
        }
    }

    //Start Recording
    private fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            val file = File("${Environment.getExternalStorageDirectory()}/Esteto/Media/Audio/")
            if (!file.exists()) {
                file.mkdirs()
            }
            audioFilePath = "${file.absolutePath}/${System.currentTimeMillis()}.mp3"
            setOutputFile(audioFilePath)
            try {
                prepare()
                start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //Stop Recording
    private fun stopRecording() {
        binding.ivAttachment.show()
        binding.cvMessage.show()
        binding.ivCustomSmiley.show()
        //  ivSmily.show()
        binding.ivCamera.show()
        recorder?.apply {
            try {
                stop()
                release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        recorder = null
    }

    //Delete Audio File
    private fun deleteAudioFile(filePath: String) {
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
        }
    }

    //Last Seen Listener
    private fun lastSeenListener() {
        firebaseDbHandler.getUserLastSeen(otherUserIdwithU,
            object : FirebaseDbHandler.LastSeenListener {
                override fun onLastSeenFound(lastSeen: String) {
                    if (lastSeen == "Online") {
                        binding.tvLastSeen.text = getString(R.string.online)
                    } else {
                        val time = com.haallo.ui.chat.util.ChatDateTimeUtil.getTimeAgo(
                            this@ChatActivity,
                            lastSeen.toLong()
                        )
                        val lastSeenMessage = "Last Seen: $time"
                        binding.tvLastSeen.text = lastSeenMessage
                    }
                }
            })
    }

    //Window Focus Listener
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            setMyOnlineStatus(true)
        } else {
            setMyOnlineStatus(false)
        }
        super.onWindowFocusChanged(hasFocus)
    }

    //Set My OnLine Status
    private fun setMyOnlineStatus(onlineState: Boolean) {
        val myUserId = "u_$myUserId"
        if (onlineState) {
            firebaseDbHandler.setLastSeenStatus(myUserId, "Online")
        } else {
            val currentTime = getCurrentTimeStamp()
            firebaseDbHandler.setLastSeenStatus(myUserId, currentTime.toString())
        }
    }

    //OnBlock User
    private fun findBlockUser() {
        myUserBlockFirebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value != null) {
                    blockUser.clear()
                    blockUser.addAll(dataSnapshot.value as ArrayList<String>)
                    for (i in 0 until blockUser.size) {
                        if (!isYouBlockToOtherUser) {
                            isYouBlockToOtherUser = blockUser[i] == otherUserId.toString()
                        }
                    }
                    if (isYouBlockToOtherUser) {
                        binding.cvMessageLayout.hide()
                        binding.tvBlock.show()
                        val blockText = "$otherUserName is blocked by you"
                        binding.tvBlock.text = blockText
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        otherUserBlockFirebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value != null) {
                    val otherBlockUser: ArrayList<String> = ArrayList()
                    otherBlockUser.addAll(dataSnapshot.value as ArrayList<String>)
                    for (i in 0 until otherBlockUser.size) {
                        if (!isOtherUserBlockToYou) {
                            isOtherUserBlockToYou = otherBlockUser[i] == myUserId.toString()
                        }
                    }
                    if (isOtherUserBlockToYou) {
                        /*llChat.hide()
                        tvBlock.show()
                        val blockText = "You are blocked by $otherUserName"
                        tvBlock.text = blockText*/
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    //Set Emoji
    private fun prefixEmojicon() {
        binding.ivSmily.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                //free up resources!
                binding.ivSmily.viewTreeObserver.removeOnGlobalLayoutListener(this)
                //Prefix for Bug! in Library
                emojIcon!!.ShowEmojIcon()
            }
        })
    }

    //All Controls Defines Here
    override fun initControl() {
        binding.searchMessage.setOnClickListener(this)
        binding.ivClearSearch.setOnClickListener(this)
        binding.rlSendRecordButton.setOnClickListener(this)
        binding.ivBack.setOnClickListener(this)
        binding.ivOtherUserPic.setOnClickListener(this)
        binding.ivVideoCall.setOnClickListener(this)
        binding.ivAudioCall.setOnClickListener(this)
        binding.ivMore.setOnClickListener(this)
        binding.cross.setOnClickListener(this)
        binding.ivCamera.setOnClickListener(this)
        binding.ivSmily.setOnClickListener(this)
        binding.ivCustomSmiley.setOnClickListener(this)
        binding.ivAttachment.setOnClickListener(this)
        binding.ivSend.setOnClickListener(this)
        binding.ivSmileyPeople.setOnClickListener(this)
        binding.animal.setOnClickListener(this)
        binding.food.setOnClickListener(this)
        binding.technolooy.setOnClickListener(this)
        binding.symbols.setOnClickListener(this)

        binding.etSearchMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                textToSearch = p0.toString()
                setFilter(chatMsgList, p0.toString())
                chatMsgAdapter?.searchText(textToSearch)

            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    //OnClick Listener
    override fun onClick(v: View) {
        when (v.id) {
            R.id.searchMessage -> {
                binding.rlSearchLayout.visibility = View.VISIBLE
                binding.searchMessage.visibility = View.GONE
            }
            R.id.ivClearSearch -> {
                textToSearch = ""
                binding.rlSearchLayout.visibility = View.GONE
                binding.searchMessage.visibility = View.VISIBLE
                chatMsgList.value?.clear()
                chatMsgAdapter?.searchText(textToSearch)
                firebaseDbHandler.getChatMessages(
                    chatNodeId,
                    chatMsgList,
                    messageKey,
                    sharedPreference.userId
                )
            }
            R.id.rlSendRecordButton -> {
                val dialog = VoiceSenderDialog(this)
                dialog.setBeepEnabled(true)
                dialog.show(supportFragmentManager, "VOICE")
            }
            R.id.ivSmileyPeople -> {
                binding.tvEmojiType.text = "Smileys & people"
                // add emojis for People
                EmojiIcons.clear()
                for (i in 0 until 194) {
                    EmojiIcons.add("huminity_" + (i + 1))
                }
                binding.rvEmojiList.adapter?.notifyDataSetChanged()
            }
            R.id.animal -> {
                binding.tvEmojiType.text = "Nature"
                // add emojis for nature
                EmojiIcons.clear()
                for (i in 0 until 64) {
                    EmojiIcons.add("nature_" + (i + 1))
                }
                binding.rvEmojiList.adapter?.notifyDataSetChanged()
            }
            R.id.food -> {
                binding.tvEmojiType.text = "Food"
                // add emojis for food
                EmojiIcons.clear()
                for (i in 0 until 39) {
                    EmojiIcons.add("food_" + (i + 1))
                }
                binding.rvEmojiList.adapter?.notifyDataSetChanged()
            }
            R.id.technolooy -> {
                binding.tvEmojiType.text = "Technology"
                // add emojis for food
                EmojiIcons.clear()
                for (i in 0 until 44) {
                    EmojiIcons.add("travel_" + (i + 1))
                }
                binding.rvEmojiList.adapter?.notifyDataSetChanged()
            }

            R.id.symbols -> {
                binding.tvEmojiType.text = "Symbols"
                EmojiIcons.clear()
                for (i in 0 until 54) {
                    EmojiIcons.add("symbol_" + (i + 1))
                }
                binding.rvEmojiList.adapter?.notifyDataSetChanged()
            }

            R.id.ivBack -> {
                onBackClick()
            }
            R.id.cross -> {
                if (arrayListSelectedEmoji.size == 1) {
                    arrayListSelectedEmoji.removeAt((arrayListSelectedEmoji.size) - 1)
                    binding.cross.visibility = View.GONE
                    (binding.rvEmojiSetList.adapter as EmojiAdapterSelected).notifyDataSetChanged()

                } else if (arrayListSelectedEmoji.size > 1) {
                    arrayListSelectedEmoji.removeAt((arrayListSelectedEmoji.size) - 1)
                    binding.cross.visibility = View.VISIBLE
                    (binding.rvEmojiSetList.adapter as EmojiAdapterSelected).notifyDataSetChanged()
                }
            }

            R.id.ivCustomSmiley -> {
                if (isEmojiOpen) {
                    closeEmojiPopup()
                    binding.etMessage.visibility = View.VISIBLE
                    binding.ivVoice.visibility = View.VISIBLE
                    binding.rlEmojiSetLayout.visibility = View.GONE
                } else {
                    binding.rlEmojiListLayout.visibility = View.VISIBLE
                    isEmojiOpen = true
                    binding.etMessage.visibility = View.GONE
                    binding.rlEmojiSetLayout.visibility = View.VISIBLE
                }
            }

            R.id.ivVideoCall -> {
                if (isAttachmentOpen) {
                    isAttachmentOpen = false
                    binding.ivAttachment.setImageResource(R.drawable.plus)
                    closeAttachment()
                } else if (isOtherUserBlockToYou) {
                    showToast("You are blocked by $otherUserName")
                } else if (isYouBlockToOtherUser) {
                    showToast("$otherUserName is blocked by you")
                } else {
                    if (checkVideoCallPermission(this)) {
                        callApi(EnumUtils.CallType.ONE_TO_ONE_VIDEO_CALL.value)
                    }
                }
            }

            R.id.ivAudioCall -> {
                if (isAttachmentOpen) {
                    isAttachmentOpen = false
                    binding.ivAttachment.setImageResource(R.drawable.plus)
                    closeAttachment()
                } else if (isOtherUserBlockToYou) {
                    showToast("You are blocked by $otherUserName")
                } else if (isYouBlockToOtherUser) {
                    showToast("$otherUserName is blocked by you")
                } else {
                    if (checkAudioCallPermission(this)) {
                        callApi(EnumUtils.CallType.ONE_TO_ONE_AUDIO_CALL.value)
                    }
                }
            }

            R.id.ivMore -> {
                if (isAttachmentOpen) {
                    isAttachmentOpen = false
                    binding.ivAttachment.setImageResource(R.drawable.plus)
                    closeAttachment()
                } else if (isOtherUserBlockToYou) {
                    showToast("You are Blocked by $otherUserName")
                } else {
                    val chatMoreOptionDialog =
                        ChatMoreOptionDialog(this, muteUnMuteStatus, isYouBlockToOtherUser, object : ChatMoreOptionDialog.ChatMoreOptionDialogListener {
                            override fun onBlockUser() {
                                blockUser()
                            }

                            override fun onUnBlockUser() {
                                unBlockUser()
                            }

                            override fun onMuteNotifications(muteUnMuteStatus: String) {
                                //    muteUnMuteNotification(muteUnMuteStatus)
                                showToast("Under development")
                            }

                            override fun onReportUser() {
                                //  reportUser()
                                showToast("Under development")

                            }

                            override fun onDeleteChat() {

                                //  showToast("Under development")
                                otherUserId?.let { showActionPrompt(it) }

                            }

                            override fun clearChat() {
                                deleteChat()
                            }
                        })
                    chatMoreOptionDialog.show()
                }
            }

            R.id.ivAttachment -> {
                if (isAttachmentOpen) {
                    closeAttachment()
                } else {
                    openAttachment()
                }
            }

            /*R.id.ivOtherUserPic -> {
                closeAttachment()
                if (otherUserPic != null && otherUserPic != "") {
                    startActivity(
                        Intent(this, FullScreenImageActivity::class.java)
                            .putExtra(IntentConstant.PIC, otherUserPic)
                    )
                }
            }*/

            R.id.ivCamera -> {
                if (isAttachmentOpen) {
                    closeAttachment()
                } else {
                    /* if (checkCameraAndStoragePermission(this)) {
                         takePhotoFromCamera()
                     }*/
                    checkPermissionCamera()
                }
            }

            R.id.ivSmily -> {
                if (isEmojiOpen) {
                    closeEmojiPopup()
                } else {
                    binding.rlEmojiListLayout.visibility = View.VISIBLE
                }
            }

            R.id.ivSend -> {
                if (isAttachmentOpen) {
                    closeAttachment()
                }
                if (isEmojiOpen) {
                    closeEmojiPopup()
                    for (item in arrayListSelectedEmoji) {
                        if (selectedEmoji == "") {
                            selectedEmoji = item
                        } else {
                            selectedEmoji = "$selectedEmoji,$item"
                        }
                    }
                    arrayListSelectedEmoji.clear()
                    sendEmojiToChat(selectedEmoji)

                } else {
                    val obj = binding.etMessage.text.toString()
                    if (obj.trim().isNotEmpty()) {
                        sendMsgToChat(obj)
                        binding.etMessage.text!!.clear()
                    }
                }
            }
        }
    }

    //Call Api
    private fun callApi(type: String) {
        showLoading()
        channelName = "${FirebaseDatabase.getInstance().reference.push().key}"
        callModel.call_id = channelName
        callModel.call_status = EnumUtils.CallState.CONNECTING.value
        callModel.call_type = type
        callModel.isGroup = "0"
        callModel.message = "Ringing"
        callModel.receiverImage = otherUserPic
        callModel.receiverid = otherUserIdwithU
        callModel.receivername = otherUserName!!
        callModel.senderImage = myUserPic!!
        callModel.senderid = myUserIdwithU
        callModel.sendername = myUserName
        callModel.timeStamp = getCurrentTimeStamp()

        FirebaseDatabase.getInstance().getReference("Call").child(channelName)
            .setValue(callModel)
            .addOnSuccessListener {
                updatedRecentCall(callModel)
                if (type == "1") {
                    chatViewModel.videoCallNotificationApi(
                        sharedPreference.accessToken, otherUserId!!.replace("u_", "")!!,
                        "Video call",
                        "${sharedPreference.userName} is calling",
                        EnumUtils.NotificationType.VIDEO_CALL_INCOMING.value,
                        channelName,
                        sharedPreference.userName!!
                    )

                } else {
                    chatViewModel.audiocallNotification(
                        sharedPreference.accessToken, otherUserId!!.replace("u_", "")!!,
                        "Voice call",
                        "${sharedPreference.userName} is calling",
                        EnumUtils.NotificationType.AUDIO_CALL_INCOMING.value,
                        channelName,
                        sharedPreference.userName!!
                    )
                }
            }
    }

    /*  //Select Camera
      private fun takePhotoFromCamera() {
          filePickUtils.requestImageCamera(FilePickUtils.CAMERA_PERMISSION, false, false)
      }
  */
    //onBackPressed
    override fun onBackPressed() {
        onBackClick()
    }

    //Block User
    private fun blockUser() {
        val blockUnBlockDialog = BlockUnBlockDialog(this, IntentConstant.BLOCK_USER, object : BlockUnBlockDialog.BlockUnBlockDialogListener {
            override fun onClick() {
                blockUser.add(otherUserId!!.toString())
                val dummy: ArrayList<String> = ArrayList()
                myUserBlockFirebaseDatabase.setValue(dummy)
                myUserBlockFirebaseDatabase.setValue(blockUser)
                showToast("Block Successfully")
                binding.cvMessageLayout.hide()
                binding.tvBlock.show()
                val blockText = "$otherUserName is blocked by you"
                binding.tvBlock.text = blockText
            }
        })
        blockUnBlockDialog.show()
    }

    //UnBlock User
    private fun unBlockUser() {
        /*val blockUnBlockDialog = BlockUnBlockDialog(
            this,
            IntentConstant.UN_BLOCK_USER,
            object : BlockUnBlockDialogListener {
                override fun onClick() {
                    var blockPosition: Int = -1
                    for (i in 0 until blockUser.size) {
                        if (blockUser[i] == otherUserId) {
                            blockPosition = i
                        }
                        if (blockPosition != -1) {
                            blockUser.removeAt(blockPosition)
                            blockPosition = -1
                        }
                    }
                    val dummy: ArrayList<String> = ArrayList()
                    myUserBlockFirebaseDatabase.setValue(dummy)
                    myUserBlockFirebaseDatabase.setValue(blockUser)
                    isYouBlockToOtherUser = false
                    showToast("UnBlock Successfully")
                    llChat.show()
                    tvBlock.hide()
                }
            })
        blockUnBlockDialog.show()*/

        var blockPosition: Int = -1
        for (i in 0 until blockUser.size) {
            if (blockUser[i] == otherUserId.toString()) {
                blockPosition = i
            }
            if (blockPosition != -1) {
                blockUser.removeAt(blockPosition)
                blockPosition = -1
            }
        }
        val dummy: ArrayList<String> = ArrayList()
        myUserBlockFirebaseDatabase.setValue(dummy)
        myUserBlockFirebaseDatabase.setValue(blockUser)
        isYouBlockToOtherUser = false
        showToast("UnBlock Successfully")
        binding.cvMessageLayout.show()
        binding.tvBlock.hide()
    }

    //Mute UnMute Notification
    private fun muteUnMuteNotification(key: String) {
        showLoading()
        chatViewModel.muteUnMuteApi(
            sharedPreference.accessToken,
            key,
            sharedPreference.userId.toString(),
            otherUserId!!.toString()
        )
    }

    //Report User
    private fun reportUser() {
        val reportUserDialog = ReportUserDialog(this, object : ReportUserDialog.ReportUserDialogListener {
            override fun onReportClick(reason: String, comment: String?) {
                showLoading()
                chatViewModel.reportUserApi(
                    sharedPreference.userId.toString(),
                    otherUserId!!.toString(),
                    reason,
                    comment
                )
            }
        })
        reportUserDialog.show()
    }


    //onBackClick
    private fun onBackClick() {
        if (isAttachmentOpen) {
            closeAttachment()
        } else {
            super.onBackPressed()
        }
    }

    //Open Attachment
    private fun openAttachment() {
        isAttachmentOpen = true
        emojIcon?.closeEmojIcon()
        binding.attachmentView.show()
        binding.ivAttachment.setImageResource(R.drawable.cross_blue)
    }

    //Close Attachment
    private fun closeAttachment() {
        isAttachmentOpen = false
        emojIcon?.closeEmojIcon()
        binding.attachmentView.hide()
        binding.ivAttachment.setImageResource(R.drawable.plus)
    }

    private fun closeEmojiPopup() {
        isEmojiOpen = false
        binding.rlEmojiListLayout.visibility = View.GONE
        binding.etMessage.visibility = View.VISIBLE
        binding.rlEmojiSetLayout.visibility = View.GONE
        binding.ivVoice.visibility = View.VISIBLE
        binding.ivSend.visibility = View.GONE
    }

    //Attachment View Click
    private fun attachmentViewClick() {
        binding.attachmentView.setOnAttachmentClick { id ->
            when (id) {
                R.id.attachment_gallery -> {
                    binding.attachmentView.hide()
                    binding.ivAttachment.setImageResource(R.drawable.plus)
                    selectImageAndVideo()
                }

                R.id.attachment_document -> {
                    binding.attachmentView.hide()
                    binding.ivAttachment.setImageResource(R.drawable.plus)
                    /*pickDocFile()*/
                    if (checkPermission()) {
                        pdfIntent()
                    } else {
                        requestPermission()
                    }
                }

                R.id.attachment_audio -> {
                    binding.attachmentView.hide()
                    binding.ivAttachment.setImageResource(R.drawable.plus)
                    pickAudioFile()
                }

                R.id.attachment_contact -> {
                    binding.attachmentView.hide()
                    binding.ivAttachment.setImageResource(R.drawable.plus)
                    picContact()
                }
                R.id.attachment_gif -> {
                    binding.attachmentView.hide()
                    binding.ivAttachment.setImageResource(R.drawable.plus)
                    GiphyDialogFragment.newInstance().show(supportFragmentManager, "giphy_dialog")
                }

                R.id.attachment_location -> {
                    binding.attachmentView.hide()
                    binding.ivAttachment.setImageResource(R.drawable.plus)
                    picLocation()
                }
            }
        }
    }

    //Choose Image/ Video Dialog
    private fun selectImageAndVideo() {
        if (checkPermission()) {
            val items = arrayOf(
                getString(R.string.pick_image),
                getString(R.string.pick_video)
            )
            val builder = AlertDialog.Builder(this)
            builder.setTitle(Html.fromHtml(getString(R.string.complete_action_using)))
            builder.setItems(items) { _, position ->
                when (position) {
                    0 -> chooseImageFromGallery()
                    1 -> chooseVideoFromGallery()
                    else -> return@setItems
                }
            }.show()
        }
    }

    //Choose Image From Gallery
    private fun chooseImageFromGallery() {
        /*   filePickUtils.requestImageGallery(
               FilePickUtils.STORAGE_PERMISSION_IMAGE,
               true,
               false
           )*/
        ImagePicker.with(this)
            .crop()                    //Crop image(Optional), Check Customization for more option
            //Final image size will be less than 1 MB(Optional)
            .maxResultSize(
                1080,
                1080
            )    //Final image resolution will be less than 1080 x 1080(Optional)
            .galleryOnly()
            .start()
    }

    //Store Image To Local
    private fun storeImageToLocal(imagePath: String) {
        val sourceFile = File(imagePath)
        val sb = StringBuilder()
        sb.append(Environment.getExternalStorageDirectory())
        sb.append("/Esteto/Media/HaalloImage/")
        sb.append(sourceFile.name)
        val file = File(sb.toString())
        sourceFile.copyTo(file, true)
        val image = file.let {
            MultipartBody.Part.createFormData(
                "image",
                file.name,
                file.asRequestBody("image/*".toMediaTypeOrNull())
            )
        }
        //postFileToServer(image, "2")
    }

    //Choose Video From Gallery
    private fun chooseVideoFromGallery() {
        if (checkCameraAndStoragePermission(this)) {
            val intent = Intent(this, FilePickerActivity::class.java)
            intent.putExtra(
                FilePickerActivity.CONFIGS, object : Configurations.Builder() {}
                    .setCheckPermission(true)
                    .setShowImages(false)
                    .setShowVideos(true)
                    .enableImageCapture(false)
                    .enableVideoCapture(true)
                    .setMaxSelection(1)
                    .setSkipZeroSizeFiles(true)
                    .build()
            )
            startActivityForResult(intent, fileRequestCode)
        } else {
            showToast(getString(R.string.permission_required_to_open_camera))
        }
    }

    //Store Video To Local
    private fun storeVideoToLocal(videoFilePath: String) {
        val sourceFile = File(videoFilePath)
        /*      val sb = StringBuilder()
              sb.append(Environment.getExternalStorageDirectory())
              sb.append("/Esteto/Media/HaalloVideo/")
              sb.append(sourceFile.name)
              val file = File(sb.toString())
              sourceFile.copyTo(file, true)*/
        val video = MultipartBody.Part.createFormData(
            "image",
            sourceFile.name,
            sourceFile.asRequestBody("video/*".toMediaTypeOrNull())
        )
        /* val video = file.let {

         }*/

        postFileToServer(video, "3")
    }

    //Pick Doc File
    private fun pickDocFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "application/*"
        val mimeTypes = arrayOf(
            "application/pdf" /*{for .pdf}*/,
            "application/msword" /*{for .doc}*/,
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document" /*{for .docx}*/
        )
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(intent, AppConstant.REQUEST_CODE_SELECT_DOCUMENTS)
    }

    private fun onSelectedPdf(data: Intent?) {
        if (data != null) {
            val filePath = RealPathUtil.getRealPath(this@ChatActivity, data.data!!)
            val pdfFile = File(filePath)
            val pdf = MultipartBody.Part.createFormData(
                "image",
                pdfFile.name,
                pdfFile.asRequestBody("pdf/*".toMediaTypeOrNull())
            )
            postFileToServer(pdf, "7")
        }
    }

    //Pick Audio File
    private fun pickAudioFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "audio/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, AppConstant.REQUEST_CODE_SELECT_AUDIO)
    }

    //Store Audio To Local
    private fun storeAudioToLocal(audioFilePath2: String) {
        val sourceFile = File(audioFilePath2)
        val sb = StringBuilder()
        sb.append(Environment.getExternalStorageDirectory())
        sb.append("/Esteto/Media/HaalloAudio/")
        sb.append(sourceFile.name)
        val file = File(sb.toString())
        sourceFile.copyTo(file, true)
        val audio = file.let {
            MultipartBody.Part.createFormData(
                "image",
                file.name,
                file.asRequestBody("audio/*".toMediaTypeOrNull())
            )
        }
        postFileToServer(audio, "4")
    }

    //Pic Contact
    private fun picContact() {
        if (checkContactPermission(this)) {
            MultiContactPicker.Builder(this)
                .theme(R.style.AppTheme) //Optional - default: MultiContactPicker.Azure
                .hideScrollbar(false) //Optional - default: false
                .showTrack(true) //Optional - default: true
                .searchIconColor(
                    ContextCompat.getColor(
                        this,
                        R.color.white
                    )
                ) //Option - default: White
                .setChoiceMode(MultiContactPicker.CHOICE_MODE_SINGLE) //Optional - default: CHOICE_MODE_MULTIPLE
                .handleColor(
                    ContextCompat.getColor(
                        this,
                        R.color.white
                    )
                ) //Optional - default: Azure Blue
                .bubbleColor(
                    ContextCompat.getColor(this, R.color.colorPrimary)
                ) //Optional - default: Azure Blue
                .bubbleTextColor(Color.WHITE) //Optional - default: White
                .setTitleText("Select Contacts") //Optional - default: Select Contacts
                .setLoadingType(MultiContactPicker.LOAD_ASYNC) //Optional - default LOAD_ASYNC (wait till all loaded vs stream results)
                .limitToColumn(LimitColumn.NONE) //Optional - default NONE (Include phone + email, limiting to one can improve loading time)
                .setActivityAnimations(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                ) //Optional - default: No animation overrides
                .showPickerForResult(AppConstant.REQUEST_CODE_SELECT_CONTACT)
        }
    }

    //Pick Location
    private fun picLocation() {
        startActivityForResult(
            Intent(this@ChatActivity, PlacesPickerActivity::class.java),
            pickLocationRequest
        )
    }

    //Send Location To Chat
    private fun sendLocation(place: Place?) {

    }

    //Post File to Server and Retrieve Url Api
    private fun postFileToServer(image: MultipartBody.Part, mediaType: String) {
        mediaTypeGlobal = mediaType
        progressDialog.show()
        chatViewModel.getUrlApi(sharedPreference.accessToken, image)
    }

    //Send Text Message to FirBase
    private fun sendMsgToChat(msg: String) {
        chatMsg.messageType = "1"
        chatMsg.status = "sent"
        chatMsg.message = msg

        chatMsg.chat_id = chatNodeId
        val instance = Calendar.getInstance()
        chatMsg.timeStamp = (instance.timeInMillis).toString()
        firebaseDbHandler.saveChatMessage(chatNodeId, chatMsg)
        updatedRecentMessage(msg, "1")
        //chatNotificationApi(etMessage.getString())
    }

    private fun sendEmojiToChat(msg: String) {
        chatMsg.messageType = "9"
        chatMsg.status = "sent"
        chatMsg.message = msg

        chatMsg.chat_id = chatNodeId
        val instance = Calendar.getInstance()
        chatMsg.timeStamp = (instance.timeInMillis).toString()
        firebaseDbHandler.saveChatMessage(chatNodeId, chatMsg)
        updatedRecentMessage(msg, "9")
        //chatNotificationApi(etMessage.getString())
    }

    //Send Contact To Chat
    private fun sendContactToChat(number: String, name: String) {
        chatMsg.messageType = "6"
        chatMsg.status = "sent"
        chatMsg.message = "Contact"
        chatMsg.chat_id = chatNodeId
        chatMsg.contactName = name
        chatMsg.contactNumber = number
        val instance = Calendar.getInstance()
        chatMsg.timeStamp = (instance.timeInMillis).toString()
        firebaseDbHandler.saveChatMessage(chatNodeId, chatMsg)

        updatedRecentMessage("Contact", "6")
        //chatNotificationApi("Sent an contact")
    }

    //OnResume
    override fun onResume() {
        super.onResume()
        setMyOnlineStatus(true)
    }

    //onPause
    override fun onPause() {
        super.onPause()
        stopRecording()
        setMyOnlineStatus(false)
        timer?.cancel()
    }

    //Chat Notification Api
    private fun chatNotificationApi(message: String) {
        chatViewModel.chatNotificationApi(sharedPreference.accessToken, otherUserId ?: "", message)
    }

    private fun checkPermissionCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), FilePickUtils.CAMERA_PERMISSION)
        } else {
            takePhotoFromCamera()
        }
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, Constants.CAMERA)
    }

    fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager()
        } else {
            val readCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            val writeCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            return readCheck == PackageManager.PERMISSION_GRANTED && writeCheck == PackageManager.PERMISSION_GRANTED
        }
    }

    fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(String.format("package:%s", applicationContext.packageName))
                startActivity(intent)
            } catch (e: java.lang.Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivity(intent)
            }
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_ID_MULTIPLE_PERMISSIONS)
        }
    }

    fun checkAndRequestPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager()
        } else {
            val readpermission = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE)
            val writepermission = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val listPermissionsNeeded = ArrayList<String>()
            if (writepermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            if (readpermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            if (listPermissionsNeeded.isNotEmpty()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    try {
                        val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                        intent.addCategory("android.intent.category.DEFAULT")
                        intent.data = Uri.parse(String.format("package:%s", applicationContext.packageName))
                        startActivity(intent)
                    } catch (e: java.lang.Exception) {
                        val intent = Intent()
                        intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                        startActivity(intent)
                    }
                } else {
                    ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), REQUEST_ID_MULTIPLE_PERMISSIONS)
                }
                return false
            }
            return true
        }
    }

    private fun pdfIntent() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "application/*"
        val mimeTypes = arrayOf(
            "application/pdf" /*{for .pdf}*/
            /*,
            "application/msword" *//*{for .doc}*//*,
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document" *//*{for .docx}*/
        )
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        // startActivityForResult(intent, Constants.DOCUMENT_PICKER)
        startActivityForResult(intent, AppConstant.REQUEST_CODE_SELECT_DOCUMENTS)
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer.stop()
    }

    fun showActionPrompt(chatMsgModel: ChatMsgModel, isLoginUser: Boolean, isCopy: Boolean) {
        val view = layoutInflater.inflate(R.layout.action_prompt, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(view)

        val delete = dialog.findViewById<TextView>(R.id.delete)
        val copy = dialog.findViewById<TextView>(R.id.copy)
        val starred = dialog.findViewById<TextView>(R.id.starred)
        val forward = dialog.findViewById<TextView>(R.id.forward)
        if (isLoginUser) {
            delete!!.visibility = View.VISIBLE
        } else {
            delete!!.visibility = View.GONE

        }

        if (isCopy) {
            copy!!.visibility = View.VISIBLE
        } else {
            copy!!.visibility = View.GONE
        }

        if (chatMsgModel.starmessage == "") {
            starred!!.text = "Star"
        } else {
            starred!!.text = "Unstar"
        }
        delete?.setOnClickListener {
            showToast("delete")
            firebaseDbHandler.deleteMeesage(
                chatMsgModel.chat_id,
                chatMsgModel.message_id
            )
            chatMsgList.value?.clear()
            firebaseDbHandler.getChatMessages(
                chatNodeId,
                chatMsgList,
                messageKey,
                sharedPreference.userId.toString()
            )
            dialog.dismiss()
        }
        copy?.setOnClickListener {
            copyText(chatMsgModel.content)
            dialog.dismiss()
        }
        starred?.setOnClickListener {
            if (chatMsgModel.starmessage == "") {
                chatMsgModel.starmessage = "u_" + sharedPreference.userId
                firebaseDbHandler.updateMessage(chatMsgModel)
            } else {
                chatMsgModel.starmessage = ""
                firebaseDbHandler.updateMessage(chatMsgModel)
            }
            dialog.dismiss()
        }

        forward?.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun copyText(textToCopy: String) {
        val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", textToCopy)
        clipboard.setPrimaryClip(clip)
        showToast("text copied")
    }

    fun updateMessage(chatMsgModel: ChatMsgModel) {
        if (chatMsgModel.starmessage == "") {
            chatMsgModel.starmessage = "u_" + sharedPreference.userId
            firebaseDbHandler.updateMessage(chatMsgModel)
        } else {
            chatMsgModel.starmessage = ""
            firebaseDbHandler.updateMessage(chatMsgModel)
        }
        chatMsgList.value?.clear()
        firebaseDbHandler.getChatMessages(
            chatNodeId,
            chatMsgList,
            messageKey,
            sharedPreference.userId
        )
    }

    override fun didSearchTerm(term: String) {

    }

    override fun onDismissed(selectedContentType: GPHContentType) {

    }

    override fun onGifSelected(media: Media, searchTerm: String?, selectedContentType: GPHContentType) {
        closeAttachment()
        mediaTypeGlobal = "11"
        chatNotificationApi("sent a gif")
        sendFileToChat("http://media2.giphy.com/media/" + media.id + "/giphy.gif", mediaTypeGlobal)
    }

    //Delete Chat
    private fun deleteChat() {
        val confirmationDialog = ConfirmationDialog(this, IntentConstant.DELETE_CHAT, object : ConfirmationDialog.ConfirmationDialogListener {
            override fun onYesClick() {
                firebaseDbHandler.deleteChatMessages(
                    firebaseDbHandler.messageRef,
                    chatNodeId,
                    sharedPreference.userId,
                    otherUserId!!.toString()
                )
                updatedRecentMessage("", "1")
                chatMsgList.value?.clear()
                firebaseDbHandler.getChatMessages(
                    chatNodeId,
                    chatMsgList,
                    messageKey,
                    sharedPreference.userId
                )
            }
        })
        confirmationDialog.show()
    }

    fun showActionPrompt(receiverId: String) {
        val view = layoutInflater.inflate(R.layout.action_prompt_chat_list, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(view)

        val clear = dialog.findViewById<TextView>(R.id.clear)
        val info = dialog.findViewById<TextView>(R.id.info)

        clear?.text = "Delete Chat"
        clear?.setOnClickListener {
            firebaseDbHandler.deleteChat("u_" + sharedPreference.userId, receiverId)
            showToast("Chat Deleted !", this)
            dialog.dismiss()
            finish()
        }
        info?.setOnClickListener {
            startActivity(Intent(this, GroupInfoActivity::class.java).putExtra(IntentConstant.GROUP_ID, receiverId))
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onAudioReady(audioUri: String?) {
        val currentImage: File?
        currentImage = File(audioUri)
        val image = MultipartBody.Part.createFormData("image", currentImage.name, currentImage.asRequestBody("image/*".toMediaTypeOrNull()))
        postFileToServer(image, "4")
    }

    override fun onReadyForRecord() {

    }

    override fun onRecordFailed(errorMessage: String?) {

    }

    override fun onStartMedia() {

    }

    override fun onStopMedia() {

    }

    override fun onScreenCaptured(path: String) {
        // showToast(path)
        chatMsg.messageType = "5"
        chatMsg.status = "sent"
        chatMsg.message = "screenshot captured"
        chatMsg.chat_id = chatNodeId
        val instance = Calendar.getInstance()
        chatMsg.timeStamp = (instance.timeInMillis).toString()
        firebaseDbHandler.saveChatMessage(chatNodeId, chatMsg)
        // Do something when screen was captured
    }

    override fun onScreenCapturedWithDeniedPermission() {

    }

    fun setFilter(chatMsgModelList: MutableLiveData<ArrayList<ChatMsgModel?>>, searchText: String) {
        var arrayList: ArrayList<ChatMsgModel?>? = ArrayList()
        for (item in chatMsgModelList.value!!) {
            if (item!!.message.toLowerCase().contains(searchText.toLowerCase())) {
                arrayList!!.add(item)
            }
        }
        chatMsgAdapter?.submitList(arrayList)
        chatMsgAdapter?.notifyDataSetChanged()
    }
}