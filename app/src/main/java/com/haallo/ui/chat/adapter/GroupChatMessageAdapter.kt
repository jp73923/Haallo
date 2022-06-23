package com.haallo.ui.chat.adapter

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.haallo.R
import com.haallo.constant.IntentConstant
import com.haallo.ui.chat.SetEmojiAdapter
import com.haallo.ui.chat.activity.FullScreenImageActivityOld
import com.haallo.ui.chat.customEnum.EnumUtils
import com.haallo.ui.chat.model.GroupMsgContactModel
import com.haallo.ui.chat.model.GroupMsgModel
import com.haallo.util.*
import com.wang.avi.AVLoadingIndicatorView
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView
import java.io.File
import kotlin.jvm.internal.Intrinsics

class GroupChatMessageAdapter(
    private val context: Context,
    private val activity: Activity,
    private val myUserId: String,
    private val chatDateTimeUtil: com.haallo.ui.chat.util.ChatDateTimeUtil,
    val audiohandle: onAudioPlayer,
    val onActionMessage: onMessageClick,
    val starAction: messageAction
) : ListAdapter<GroupMsgModel, RecyclerView.ViewHolder>(DiffUtilCallBack()) {

    var searchText = ""
    private val REQUEST_ID_PERMISSIONS: Int = 100
    val listPERMISSIONS: Array<String> = arrayOf(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
    )
    var audioPlayer = MediaPlayer()
    var progressDialog = ProgressDialog(context)

    class DiffUtilCallBack : DiffUtil.ItemCallback<GroupMsgModel>() {
        override fun areItemsTheSame(oldItem: GroupMsgModel, newItem: GroupMsgModel): Boolean {
            return Intrinsics.areEqual(oldItem as Any, newItem as Any)
        }

        override fun areContentsTheSame(oldItem: GroupMsgModel, newItem: GroupMsgModel): Boolean {
            return Intrinsics.areEqual(oldItem as Any, newItem as Any)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (Intrinsics.areEqual(getItem(position)?.senderid as Any, myUserId as Any)) {
            when {
                Intrinsics.areEqual(getItem(position).messageType as Any, "1" as Any) -> {
                    EnumUtils.EnumChatMessage.MessageSender.viewType
                }
                Intrinsics.areEqual(getItem(position).messageType as Any, "2" as Any) -> {
                    EnumUtils.EnumChatMessage.ImageSender.viewType
                }
                Intrinsics.areEqual(getItem(position).messageType as Any, "3" as Any) -> {
                    EnumUtils.EnumChatMessage.VideoSender.viewType
                }
                Intrinsics.areEqual(getItem(position).messageType as Any, "4" as Any) -> {
                    EnumUtils.EnumChatMessage.AudioSender.viewType
                }
                Intrinsics.areEqual(getItem(position).messageType as Any, "5" as Any) -> {
                    EnumUtils.EnumChatMessage.ScreenShot.viewType
                }
                Intrinsics.areEqual(getItem(position).messageType as Any, "7" as Any) -> {
                    EnumUtils.EnumChatMessage.PdfSender.viewType
                }
                Intrinsics.areEqual(getItem(position).messageType as Any, "6" as Any) -> {
                    EnumUtils.EnumChatMessage.ContactSender.viewType
                }
                Intrinsics.areEqual(getItem(position).messageType as Any, "11" as Any) -> {
                    EnumUtils.EnumChatMessage.ImageSender.viewType
                }
                Intrinsics.areEqual(getItem(position).messageType as Any, "9" as Any) -> {
                    EnumUtils.EnumChatMessage.EmojiSender.viewType
                }
                else -> EnumUtils.EnumChatMessage.MessageSender.viewType
            }
        } else {
            when {
                Intrinsics.areEqual(getItem(position).messageType as Any, "1" as Any) -> {
                    EnumUtils.EnumChatMessage.MessageReceiver.viewType
                }
                Intrinsics.areEqual(getItem(position).messageType as Any, "2" as Any) -> {
                    EnumUtils.EnumChatMessage.ImageReceiver.viewType
                }
                Intrinsics.areEqual(getItem(position).messageType as Any, "3" as Any) -> {
                    EnumUtils.EnumChatMessage.VideoReceiver.viewType
                }
                Intrinsics.areEqual(getItem(position).messageType as Any, "4" as Any) -> {
                    EnumUtils.EnumChatMessage.AudioReceiver.viewType
                }
                Intrinsics.areEqual(getItem(position).messageType as Any, "5" as Any) -> {
                    EnumUtils.EnumChatMessage.ScreenShot.viewType
                }
                Intrinsics.areEqual(getItem(position).messageType as Any, "7" as Any) -> {
                    EnumUtils.EnumChatMessage.PdfReceiver.viewType
                }
                Intrinsics.areEqual(getItem(position).messageType as Any, "6" as Any) -> {
                    EnumUtils.EnumChatMessage.ContactReceiver.viewType
                }
                Intrinsics.areEqual(getItem(position).messageType as Any, "11" as Any) -> {
                    EnumUtils.EnumChatMessage.ImageReceiver.viewType
                }
                Intrinsics.areEqual(getItem(position).messageType as Any, "9" as Any) -> {
                    EnumUtils.EnumChatMessage.EmojiReceiver.viewType
                }
                else -> EnumUtils.EnumChatMessage.MessageReceiver.viewType
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            EnumUtils.EnumChatMessage.ScreenShot.viewType -> {
                val screenshotvIEW: View = LayoutInflater.from(parent.context).inflate(R.layout.screenshot_item, parent, false)
                ScreenshotHolder(screenshotvIEW)
            }
            EnumUtils.EnumChatMessage.EmojiSender.viewType -> {
                val emojiSender: View = LayoutInflater.from(parent.context).inflate(R.layout.list_item_chat_send_emojis, parent, false)
                EmojiSenderHolder(emojiSender)
            }
            EnumUtils.EnumChatMessage.EmojiReceiver.viewType -> {
                val emojiReceiver: View = LayoutInflater.from(parent.context).inflate(R.layout.list_item_chat_receive_emojis, parent, false)
                EmojiReceiverHolder(emojiReceiver)
            }
            EnumUtils.EnumChatMessage.MessageSender.viewType -> {
                val messageSender: View = LayoutInflater.from(parent.context).inflate(R.layout.list_item_chat_send_message, parent, false)
                MessageSenderHolder(messageSender)
            }
            EnumUtils.EnumChatMessage.MessageReceiver.viewType -> {
                val messageReceiver: View = LayoutInflater.from(parent.context).inflate(R.layout.list_item_chat_receive_message, parent, false)
                MessageReceiverHolder(messageReceiver)
            }
            EnumUtils.EnumChatMessage.ImageSender.viewType -> {
                val imageSender: View = LayoutInflater.from(parent.context).inflate(R.layout.list_item_chat_send_image, parent, false)
                ImageSenderHolder(imageSender)
            }
            EnumUtils.EnumChatMessage.ImageReceiver.viewType -> {
                val imageReceiver: View = LayoutInflater.from(parent.context).inflate(R.layout.list_item_chat_receive_image, parent, false)
                ImageReceiverHolder(imageReceiver)
            }
            EnumUtils.EnumChatMessage.VideoSender.viewType -> {
                val videoSender: View = LayoutInflater.from(parent.context).inflate(R.layout.list_item_chat_send_video, parent, false)
                VideoSenderHolder(videoSender)
            }
            EnumUtils.EnumChatMessage.VideoReceiver.viewType -> {
                val videoReceiver: View = LayoutInflater.from(parent.context).inflate(R.layout.list_item_chat_receive_video, parent, false)
                VideoReceiverHolder(videoReceiver)
            }
            EnumUtils.EnumChatMessage.AudioSender.viewType -> {
                val audioSender: View = LayoutInflater.from(parent.context).inflate(R.layout.list_item_chat_send_audio, parent, false)
                AudioSenderHolder(audioSender)
            }
            EnumUtils.EnumChatMessage.AudioReceiver.viewType -> {
                val audioReceiver: View = LayoutInflater.from(parent.context).inflate(R.layout.list_item_chat_receive_audio, parent, false)
                AudioReceiverHolder(audioReceiver)
            }
            EnumUtils.EnumChatMessage.PdfSender.viewType -> {
                val pdfSender: View = LayoutInflater.from(parent.context).inflate(R.layout.list_item_chat_send_pdf, parent, false)
                PdfSenderHolder(pdfSender)
            }
            EnumUtils.EnumChatMessage.PdfReceiver.viewType -> {
                val pdfReceiver: View = LayoutInflater.from(parent.context).inflate(R.layout.list_item_chat_receive_pdf, parent, false)
                PdfReceiverHolder(pdfReceiver)
            }
            EnumUtils.EnumChatMessage.ContactSender.viewType -> {
                val contactSender: View = LayoutInflater.from(parent.context).inflate(R.layout.list_item_chat_send_contact, parent, false)
                ContactSenderHolder(contactSender)
            }
            EnumUtils.EnumChatMessage.ContactReceiver.viewType -> {
                val contactReceiver: View = LayoutInflater.from(parent.context).inflate(R.layout.list_item_chat_receive_contact, parent, false)
                ContactReceiverHolder(contactReceiver)
            }
            else -> {
                val inflate4: View = LayoutInflater.from(parent.context).inflate(R.layout.list_item_chat_send_message, parent, false)
                MessageSenderHolder(inflate4)
            }
        }
    }

    inner class ScreenshotHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val textMessage = view.findViewById(R.id.textMessage) as TextView

        fun bindScreenshotHolder(model: GroupMsgModel) {
            if (model.senderid == "u_" + SharedPreferenceUtil.getInstance(context).userId) {
                textMessage.text = "You captured screenshot"
            } else {
                textMessage.text = model.sendername + " captured screenshot"
            }
        }
    }

    inner class EmojiSenderHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val emojis_send = view.findViewById(R.id.emojis_send) as RecyclerView
        private val tvTimeSendMessage = view.findViewById(R.id.tvTimeSendMessage) as AppCompatTextView
        private val ivStarSendMessage = view.findViewById(R.id.ivStarSendMessage) as AppCompatImageView
        private val ivStatusSendMessage = view.findViewById(R.id.ivStatusSendMessage) as AppCompatImageView

        fun bindEmojiSenderView(model: GroupMsgModel) {
            // itemView.tvMessageSendMessage.text = model.message
            // itemView.tvMessageSendEmoji.setImageDrawable(context.getResource(model.message))
            val emojis: List<String> = model.message.split(",")
            emojis_send.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL, false
            )
            emojis_send.adapter = SetEmojiAdapter(context, emojis)
            //itemView.tvTimeSendMessage.text = ChatDateTimeUtil().get12HourTime(Date(model.timeStamp))
            tvTimeSendMessage.text = chatDateTimeUtil.getChatMessageTime(activity, model.timeStamp.toLong())

            ivStarSendMessage.setOnClickListener {
                starAction.onActionDone(model)
            }

            if (model.starmessage == "u_" + SharedPreferenceUtil.getInstance(context).userId)
                ivStarSendMessage.setImageDrawable(context.resources.getDrawable(R.drawable.star_active))
            else
                ivStarSendMessage.setImageDrawable(context.resources.getDrawable(R.drawable.star_inactive))


            /*  itemView.llSendMessage.setOnLongClickListener {
                  onActionMessage.handleClick(model, true, true)
                  return@setOnLongClickListener true
              }*/

            if (model.status == "seen") {
                ivStatusSendMessage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.last_seen))
            } else {
                ivStatusSendMessage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_done))
            }
        }
    }

    inner class EmojiReceiverHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val emojis_receive = view.findViewById(R.id.emojis_receive) as RecyclerView
        private val tvTimeReceiveEmoji = view.findViewById(R.id.tvTimeReceiveEmoji) as AppCompatTextView
        private val ivStarReceiveMessage = view.findViewById(R.id.ivStarReceiveMessage) as AppCompatImageView

        fun bindEmojiReceiverView(model: GroupMsgModel) {
            val emojis: List<String> = model.message.split(",")
            emojis_receive.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL, false
            )
            emojis_receive.adapter = SetEmojiAdapter(context, emojis)
            //  itemView.tvMessageReceiveEmoji.setImageDrawable(context.getResource(model.message))
            tvTimeReceiveEmoji.text =
                chatDateTimeUtil.getChatMessageTime(activity, model.timeStamp.toLong())

            /*itemView.llReceiveMessage.setOnLongClickListener {
                onActionMessage.handleClick(model, false, true)
                return@setOnLongClickListener true
            }*/
            ivStarReceiveMessage.setOnClickListener {
                starAction.onActionDone(model)
            }
            if (model.starmessage == "u_" + SharedPreferenceUtil.getInstance(context).userId)
                ivStarReceiveMessage.setImageDrawable(context.resources.getDrawable(R.drawable.star_active))
            else
                ivStarReceiveMessage.setImageDrawable(context.resources.getDrawable(R.drawable.star_inactive))
        }
    }

    inner class MessageSenderHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val llSendMessage = view.findViewById(R.id.llSendMessage) as LinearLayout
        private val tvMessageSendMessage = view.findViewById(R.id.tvMessageSendMessage) as EmojiconTextView
        private val tvTimeSendMessage = view.findViewById(R.id.tvTimeSendMessage) as AppCompatTextView
        private val ivStarSendMessage = view.findViewById(R.id.ivStarSendMessage) as AppCompatImageView
        private val ivStatusSendMessage = view.findViewById(R.id.ivStatusSendMessage) as AppCompatImageView

        fun bindMessageSenderView(model: GroupMsgModel) {
            //  itemView.tvMessageSendMessage.text = model.message
            //itemView.tvTimeSendMessage.text = ChatDateTimeUtil().get12HourTime(Date(model.timeStamp))
            tvTimeSendMessage.text = chatDateTimeUtil.getChatMessageTime(activity, model.timeStamp.toLong())

            if (searchText.isNotEmpty()) {
                //color your text here
                var index: Int = model.message.indexOf(searchText)

                val sb = SpannableStringBuilder(model.message)
                while (index > -1) {
                    val fcs = BackgroundColorSpan(Color.YELLOW) //specify color here
                    sb.setSpan(
                        fcs,
                        index,
                        index + searchText.length,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    )
                    index = model.message.indexOf(searchText, index + 1)
                }
                tvMessageSendMessage.text = sb
            } else {
                tvMessageSendMessage.text = model.message
            }
            llSendMessage.setOnLongClickListener {
                onActionMessage.handleClick(model, true, true)
                return@setOnLongClickListener true
            }

            ivStarSendMessage.setOnClickListener {
                starAction.onActionDone(model)
            }

            if (model.starmessage == "u_" + SharedPreferenceUtil.getInstance(context).userId)
                ivStarSendMessage.setImageDrawable(context.resources.getDrawable(R.drawable.star_active))
            else
                ivStarSendMessage.setImageDrawable(context.resources.getDrawable(R.drawable.star_inactive))

            if (model.status == "seen") {
                ivStatusSendMessage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.last_seen))
            } else {
                ivStatusSendMessage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_done))
            }
        }
    }

    inner class MessageReceiverHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val tvMessageReceiveMessage = view.findViewById(R.id.tvMessageReceiveMessage) as EmojiconTextView
        private val tvTimeReceiveMessage = view.findViewById(R.id.tvTimeReceiveMessage) as AppCompatTextView
        private val llReceiveMessage = view.findViewById(R.id.llReceiveMessage) as LinearLayout
        private val ivStarReceiveMessage = view.findViewById(R.id.ivStarReceiveMessage) as AppCompatImageView

        fun bindMessageReceiverView(model: GroupMsgModel) {
            // itemView.tvMessageReceiveMessage.text = model.message
            if (searchText.isNotEmpty()) {
                //color your text here
                var index: Int = model.message.indexOf(searchText)

                val sb = SpannableStringBuilder(model.message)
                while (index > -1) {
                    val fcs = BackgroundColorSpan(Color.YELLOW) //specify color here
                    sb.setSpan(
                        fcs,
                        index,
                        index + searchText.length,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    )
                    index = model.message.indexOf(searchText, index + 1)
                }
                tvMessageReceiveMessage.text = sb
            } else {
                tvMessageReceiveMessage.text = model.message
            }
            if (model.timeStamp != "") {
                tvTimeReceiveMessage.text = chatDateTimeUtil.getChatMessageTime(activity, model.timeStamp.toLong())
            }
            llReceiveMessage.setOnLongClickListener {
                onActionMessage.handleClick(model, false, true)
                return@setOnLongClickListener true
            }
            ivStarReceiveMessage.setOnClickListener {
                starAction.onActionDone(model)
            }
            if (model.starmessage == "u_" + SharedPreferenceUtil.getInstance(context).userId)
                ivStarReceiveMessage.setImageDrawable(context.resources.getDrawable(R.drawable.star_active))
            else
                ivStarReceiveMessage.setImageDrawable(context.resources.getDrawable(R.drawable.star_inactive))
        }
    }

    inner class ImageSenderHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val ivChatMsgSendImage = view.findViewById(R.id.ivChatMsgSendImage) as AppCompatImageView
        private val ivSeenStatusSendImage = view.findViewById(R.id.ivSeenStatusSendImage) as AppCompatImageView
        private val ivStarSendImage = view.findViewById(R.id.ivStarSendImage) as AppCompatImageView
        private val tvTimeSendImage = view.findViewById(R.id.tvTimeSendImage) as AppCompatTextView
        private val llSendImage = view.findViewById(R.id.llSendImage) as LinearLayout

        fun bindImageSenderView(model: GroupMsgModel) {
            ivChatMsgSendImage.setPic(model.mediaurl)
            tvTimeSendImage.text = chatDateTimeUtil.getChatMessageTime(activity, model.timeStamp.toLong())

            if (model.status == "seen") {
                ivSeenStatusSendImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.last_seen))
            } else {
                ivSeenStatusSendImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_done))
            }

            ivChatMsgSendImage.setOnClickListener {
                fullScreenImage(model.mediaurl)
            }

            ivStarSendImage.setOnClickListener {
                starAction.onActionDone(model)
            }

            if (model.starmessage == "u_" + SharedPreferenceUtil.getInstance(context).userId)
                ivStarSendImage.setImageDrawable(context.resources.getDrawable(R.drawable.star_active))
            else
                ivStarSendImage.setImageDrawable(context.resources.getDrawable(R.drawable.star_inactive))

            llSendImage.setOnLongClickListener {
                onActionMessage.handleClick(model, true, false)
                return@setOnLongClickListener true
            }
        }
    }

    inner class ImageReceiverHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val llMainReceiveImage = view.findViewById(R.id.llMainReceiveImage) as LinearLayout
        private val ivChatMsgReceiveImage = view.findViewById(R.id.ivChatMsgReceiveImage) as AppCompatImageView
        private val ivStarReceiveImage = view.findViewById(R.id.ivStarReceiveImage) as AppCompatImageView
        private val tvTimeReceiveImage = view.findViewById(R.id.tvTimeReceiveImage) as AppCompatTextView

        fun bindImageReceiverView(model: GroupMsgModel) {
            ivChatMsgReceiveImage.setPic(model.mediaurl)
            tvTimeReceiveImage.text = chatDateTimeUtil.getChatMessageTime(activity, model.timeStamp.toLong())

            ivChatMsgReceiveImage.setOnClickListener {
                fullScreenImage(model.mediaurl)
            }

            ivStarReceiveImage.setOnClickListener {
                starAction.onActionDone(model)
            }

            if (model.starmessage == "u_" + SharedPreferenceUtil.getInstance(context).userId)
                ivStarReceiveImage.setImageDrawable(context.resources.getDrawable(R.drawable.star_active))
            else
                ivStarReceiveImage.setImageDrawable(context.resources.getDrawable(R.drawable.star_inactive))

            llMainReceiveImage.setOnLongClickListener {
                onActionMessage.handleClick(model, false, false)
                return@setOnLongClickListener true
            }
        }
    }

    inner class VideoSenderHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val ivSendVideoThumbnail = view.findViewById(R.id.ivSendVideoThumbnail) as AppCompatImageView
        private val tvTimeSendVideo = view.findViewById(R.id.tvTimeSendVideo) as AppCompatTextView
        private val ivSeenStatusSendVideo = view.findViewById(R.id.ivSeenStatusSendVideo) as AppCompatImageView
        private val ivStarSendVideo = view.findViewById(R.id.ivStarSendVideo) as AppCompatImageView
        private val llSendVideo = view.findViewById(R.id.llSendVideo) as LinearLayout

        fun bindVideoSenderView(model: GroupMsgModel) {
            if (model.mediaurl != "") {
                Glide.with(context).load(model.mediaurl).into(ivSendVideoThumbnail)
            }

            tvTimeSendVideo.text = chatDateTimeUtil.getChatMessageTime(activity, model.timeStamp.toLong())

            ivSendVideoThumbnail.setOnClickListener {
                fullScreenVideo(model.mediaurl)
            }

            if (model.status == "seen") {
                ivSeenStatusSendVideo.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.last_seen))
            } else {
                ivSeenStatusSendVideo.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_done))
            }

            ivStarSendVideo.setOnClickListener {
                starAction.onActionDone(model)
            }

            if (model.starmessage == "u_" + SharedPreferenceUtil.getInstance(context).userId)
                ivStarSendVideo.setImageDrawable(context.resources.getDrawable(R.drawable.star_active))
            else
                ivStarSendVideo.setImageDrawable(context.resources.getDrawable(R.drawable.star_inactive))

            llSendVideo.setOnLongClickListener {
                onActionMessage.handleClick(model, true, false)
                return@setOnLongClickListener true
            }
        }
    }

    inner class VideoReceiverHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val tvTimeReceiveVideo = view.findViewById(R.id.tvTimeReceiveVideo) as AppCompatTextView
        private val ivThumbnailReceiveVideo = view.findViewById(R.id.ivThumbnailReceiveVideo) as AppCompatImageView
        private val ivStarReceiveVideo = view.findViewById(R.id.ivStarReceiveVideo) as AppCompatImageView
        private val llMainReceiveVideo = view.findViewById(R.id.llMainReceiveVideo) as LinearLayout

        fun bindVideoReceiverView(model: GroupMsgModel) {
            if (model.mediaurl != "") {
                val mediaMetadataRetriever = MediaMetadataRetriever()
                mediaMetadataRetriever.setDataSource(model.mediaurl, HashMap<String, String>())
                val bmFrame: Bitmap? =
                    mediaMetadataRetriever.getFrameAtTime(1000) //unit in microsecond
                if (bmFrame != null) {
                    ivThumbnailReceiveVideo.setImageBitmap(bmFrame)
                }
            }

            tvTimeReceiveVideo.text = chatDateTimeUtil.getChatMessageTime(activity, model.timeStamp.toLong())

            ivThumbnailReceiveVideo.setOnClickListener {
                fullScreenVideo(model.mediaurl)
            }

            ivStarReceiveVideo.setOnClickListener {
                starAction.onActionDone(model)
            }

            if (model.starmessage == "u_" + SharedPreferenceUtil.getInstance(context).userId)
                ivStarReceiveVideo.setImageDrawable(context.resources.getDrawable(R.drawable.star_active))
            else
                ivStarReceiveVideo.setImageDrawable(context.resources.getDrawable(R.drawable.star_inactive))

            llMainReceiveVideo.setOnLongClickListener {
                onActionMessage.handleClick(model, false, false)
                return@setOnLongClickListener true
            }

        }
    }

    inner class AudioSenderHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val tvTimeSendAudio = view.findViewById(R.id.tvTimeSendAudio) as AppCompatTextView
        private val ivSeenStatusSendAudio = view.findViewById(R.id.ivSeenStatusSendAudio) as AppCompatImageView
        private val ivStarSendAudio = view.findViewById(R.id.ivStarSendAudio) as AppCompatImageView
        private val llSendAudio = view.findViewById(R.id.llSendAudio) as RelativeLayout

        private val ivAudioDownloadSendAudio = view.findViewById(R.id.ivAudioDownloadSendAudio) as AppCompatImageView
        private val ivAudioPlaySendAudio = view.findViewById(R.id.ivAudioPlaySendAudio) as AppCompatImageView
        private val ivAudioPauseSendAudio = view.findViewById(R.id.ivAudioPauseSendAudio) as AppCompatImageView
        private val ivAudioBarSendAudio = view.findViewById(R.id.ivAudioBarSendAudio) as AppCompatImageView
        private val lineScalePulseOutIndicatorSendAudio = view.findViewById(R.id.lineScalePulseOutIndicatorSendAudio) as AVLoadingIndicatorView

        fun bindAudioSenderView(model: GroupMsgModel) {
            tvTimeSendAudio.text = chatDateTimeUtil.getChatMessageTime(activity, model.timeStamp.toLong())

            if (model.status == "seen") {
                ivSeenStatusSendAudio.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.last_seen))
            } else {
                ivSeenStatusSendAudio.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_done))
            }

            ivStarSendAudio.setOnClickListener {
                starAction.onActionDone(model)
            }

            if (model.starmessage == "u_" + SharedPreferenceUtil.getInstance(context).userId)
                ivStarSendAudio.setImageDrawable(context.resources.getDrawable(R.drawable.star_active))
            else
                ivStarSendAudio.setImageDrawable(context.resources.getDrawable(R.drawable.star_inactive))

            llSendAudio.setOnLongClickListener {
                onActionMessage.handleClick(model, true, false)
                return@setOnLongClickListener true
            }

            val filePath = Environment.getExternalStorageDirectory().toString() + "/" + Environment.DIRECTORY_MUSIC + "/" + model.message /*"${Environment.getExternalStorageDirectory()}/Esteto/Media/Audio/${model.content}"*/

            val file = File(filePath)
            if (file.exists()) {
                ivAudioDownloadSendAudio.visibility = View.GONE
                ivAudioPlaySendAudio.visibility = View.VISIBLE
                ivAudioPauseSendAudio.visibility = View.GONE
            } else {
                ivAudioDownloadSendAudio.visibility = View.VISIBLE
                ivAudioPlaySendAudio.visibility = View.GONE
                ivAudioPauseSendAudio.visibility = View.GONE
            }

            ivAudioDownloadSendAudio.setOnClickListener {
                /*    val storagePermission =
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    if (storagePermission == PackageManager.PERMISSION_GRANTED) {
                        downloadFile(
                            model.mediaurl,
                            model.message
                        )
                    }*/
                if (checkPermission()) {
                    downloadFile(model.mediaurl, model.message)
                } else {
                    //  showToast(checkPermission().toString(),context)
                    requestPermission()
                }
            }

            ivAudioPlaySendAudio.setOnClickListener {
                /*  val storagePermission =
                      ContextCompat.checkSelfPermission(
                          context,
                          Manifest.permission.WRITE_EXTERNAL_STORAGE
                      )*/
                if (checkPermission()) {
                    if (!audioPlayer.isPlaying) {
                        audiohandle.handleAudio(audioPlayer)
                        try {
                            audioPlayer.reset()
                            audioPlayer.setDataSource(filePath)
                            audioPlayer.prepare()
                            audioPlayer.start()
                            ivAudioPlaySendAudio.visibility = View.GONE
                            ivAudioPauseSendAudio.visibility = View.VISIBLE
                            ivAudioBarSendAudio.visibility = View.GONE
                            lineScalePulseOutIndicatorSendAudio.smoothToShow()
                            audioPlayer.setOnCompletionListener {
                                ivAudioPlaySendAudio.visibility = View.VISIBLE
                                ivAudioPauseSendAudio.visibility = View.GONE
                                lineScalePulseOutIndicatorSendAudio.hide()
                                ivAudioBarSendAudio.visibility = View.VISIBLE
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Can't play !", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            ivAudioPauseSendAudio.setOnClickListener {
                /*  val storagePermission =
                      ContextCompat.checkSelfPermission(
                          context,
                          Manifest.permission.WRITE_EXTERNAL_STORAGE
                      )*/
                if (checkPermission()) {
                    if (audioPlayer.isPlaying) {
                        ivAudioPlaySendAudio.visibility = View.VISIBLE
                        ivAudioPauseSendAudio.visibility = View.GONE
                        audioPlayer.pause()
                        lineScalePulseOutIndicatorSendAudio.hide()
                        ivAudioBarSendAudio.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    inner class AudioReceiverHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val tvTimeReceiveAudio = view.findViewById(R.id.tvTimeReceiveAudio) as AppCompatTextView
        private val ivStarReceiveStar = view.findViewById(R.id.ivStarReceiveStar) as AppCompatImageView
        private val rlMainReceiveAudio = view.findViewById(R.id.rlMainReceiveAudio) as RelativeLayout

        private val ivAudioDownloadReceiveAudio = view.findViewById(R.id.ivAudioDownloadReceiveAudio) as AppCompatImageView
        private val ivAudioPlayReceiveAudio = view.findViewById(R.id.ivAudioPlayReceiveAudio) as AppCompatImageView
        private val ivAudioPauseReceiveAudio = view.findViewById(R.id.ivAudioPauseReceiveAudio) as AppCompatImageView
        private val ivAudioBarReceiveAudio = view.findViewById(R.id.ivAudioBarReceiveAudio) as AppCompatImageView
        private val lineScalePulseOutIndicatorReceiveAudio = view.findViewById(R.id.lineScalePulseOutIndicatorReceiveAudio) as AVLoadingIndicatorView

        fun bindAudioReceiverView(GroupMsgModel: GroupMsgModel) {
            val messageBean = GroupMsgModel

            tvTimeReceiveAudio.text = chatDateTimeUtil.getChatMessageTime(activity, GroupMsgModel.timeStamp.toLong())

            /*   *//*  val sb = StringBuilder()
              sb.append(Environment.getExternalStorageDirectory())
              sb.append("/Esteto/Media/Audio/")
              sb.append(messageBean.content)
              val filePath = sb.toString()*//*
            val filePath =
                Environment.getExternalStorageDirectory()
                    .toString() + "/" + Environment.DIRECTORY_MUSIC + "/" + messageBean.message
            if (File(filePath).exists()) {
                itemView.ivAudioDownloadReceiveAudio.doGone()
                itemView.ivAudioPlayReceiveAudio.doVisible()
                itemView.ivAudioPauseReceiveAudio.doGone()
            } else {
                itemView.ivAudioDownloadReceiveAudio.doVisible()
                itemView.ivAudioPlayReceiveAudio.doGone()
                itemView.ivAudioPauseReceiveAudio.doGone()
            }*/
/*

            itemView.ivAudioDownloadReceiveAudio.setOnClickListener {
                downloadFile(
                    GroupMsgModel.mediaurl,
                    GroupMsgModel.message
                )
            }
*/

            ivStarReceiveStar.setOnClickListener {
                starAction.onActionDone(messageBean)
            }

            if (messageBean.starmessage == "u_" + SharedPreferenceUtil.getInstance(context).userId)
                ivStarReceiveStar.setImageDrawable(context.resources.getDrawable(R.drawable.star_active))
            else
                ivStarReceiveStar.setImageDrawable(context.resources.getDrawable(R.drawable.star_inactive))

            rlMainReceiveAudio.setOnLongClickListener {
                onActionMessage.handleClick(messageBean, false, false)
                return@setOnLongClickListener true
            }
            val filePath = Environment.getExternalStorageDirectory().toString() + "/" + Environment.DIRECTORY_MUSIC + "/" + messageBean.message
            val file = File(filePath)
            if (file.exists()) {
                ivAudioDownloadReceiveAudio.visibility = View.GONE
                ivAudioPlayReceiveAudio.visibility = View.VISIBLE
                ivAudioPauseReceiveAudio.visibility = View.GONE
            } else {
                ivAudioDownloadReceiveAudio.visibility = View.VISIBLE
                ivAudioPlayReceiveAudio.visibility = View.GONE
                ivAudioPauseReceiveAudio.visibility = View.GONE
            }

            ivAudioDownloadReceiveAudio.setOnClickListener {
                if (checkPermission()) {
                    downloadFile(messageBean.mediaurl, messageBean.message)
                } else {
                    //  showToast(checkPermission().toString(),context)
                    requestPermission()
                }
            }

            ivAudioPlayReceiveAudio.setOnClickListener {
                if (checkPermission()) {
                    if (!audioPlayer.isPlaying) {
                        try {
                            audiohandle.handleAudio(audioPlayer)
                            audioPlayer.reset()
                            audioPlayer.setDataSource(filePath)
                            audioPlayer.prepare()
                            audioPlayer.start()
                            ivAudioPlayReceiveAudio.visibility = View.GONE
                            ivAudioPauseReceiveAudio.visibility = View.VISIBLE
                            ivAudioBarReceiveAudio.visibility = View.GONE
                            lineScalePulseOutIndicatorReceiveAudio.smoothToShow()
                            audioPlayer.setOnCompletionListener {
                                ivAudioPlayReceiveAudio.visibility = View.VISIBLE
                                ivAudioPauseReceiveAudio.visibility = View.GONE
                                lineScalePulseOutIndicatorReceiveAudio.hide()
                                ivAudioBarReceiveAudio.visibility = View.VISIBLE
                            }
                        } catch (e: Exception) {
                            // Toast.makeText(context, "Can't play !", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            ivAudioPauseReceiveAudio.setOnClickListener {
                if (checkPermission()) {
                    if (audioPlayer.isPlaying) {
                        ivAudioPlayReceiveAudio.visibility = View.VISIBLE
                        ivAudioPauseReceiveAudio.visibility = View.GONE
                        audioPlayer.pause()
                        lineScalePulseOutIndicatorReceiveAudio.hide()
                        ivAudioBarReceiveAudio.visibility = View.VISIBLE
                    }
                } else {
                    showToast(context.getString(R.string.permission_not_given), context)
                }
            }
        }
    }

    inner class PdfSenderHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val tvTimeSendPdf = view.findViewById(R.id.tvTimeSendPdf) as AppCompatTextView
        private val ivChatMsgSendPdf = view.findViewById(R.id.ivChatMsgSendPdf) as AppCompatImageView
        private val ivSeenStatusSendPdf = view.findViewById(R.id.ivSeenStatusSendPdf) as AppCompatImageView
        private val ivStarSendPdf = view.findViewById(R.id.ivStarSendPdf) as AppCompatImageView
        private val llSendPdf = view.findViewById(R.id.llSendPdf) as LinearLayout

        fun bindPdfSenderView(model: GroupMsgModel) {
            tvTimeSendPdf.text = chatDateTimeUtil.getChatMessageTime(activity, model.timeStamp.toLong())
            ivChatMsgSendPdf.setOnClickListener {
                /*   context.startActivity(
                       Intent(context, WebViewPdfAct::class.java)
                           .putExtra("pdf", model.mediaurl)
                   )*/

                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(model.mediaurl)))
            }

            if (model.status == "seen") {
                ivSeenStatusSendPdf.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.last_seen))
            } else {
                ivSeenStatusSendPdf.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_done))
            }

            ivStarSendPdf.setOnClickListener {
                starAction.onActionDone(model)
            }

            if (model.starmessage == "u_" + SharedPreferenceUtil.getInstance(context).userId)
                ivStarSendPdf.setImageDrawable(context.resources.getDrawable(R.drawable.star_active))
            else
                ivStarSendPdf.setImageDrawable(context.resources.getDrawable(R.drawable.star_inactive))

            llSendPdf.setOnLongClickListener {
                onActionMessage.handleClick(model, true, false)
                return@setOnLongClickListener true
            }
        }
    }

    inner class PdfReceiverHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val llMainReceivePdf = view.findViewById(R.id.llMainReceivePdf) as LinearLayout
        private val tvTimeReceivePdf = view.findViewById(R.id.tvTimeReceivePdf) as AppCompatTextView

        private val ivChatMsgReceivePdf = view.findViewById(R.id.ivChatMsgReceivePdf) as AppCompatImageView
        private val ivStarReceivePdf = view.findViewById(R.id.ivStarReceivePdf) as AppCompatImageView

        fun bindPdfReceiverView(model: GroupMsgModel) {
            tvTimeReceivePdf.text = chatDateTimeUtil.getChatMessageTime(activity, model.timeStamp.toLong())
            ivChatMsgReceivePdf.setOnClickListener {
                /* context.startActivity(
                     Intent(context, WebViewPdfAct::class.java)
                         .putExtra("pdf", model.mediaurl)
                 )*/
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(model.mediaurl)))
            }
            ivStarReceivePdf.setOnClickListener {
                starAction.onActionDone(model)
            }

            if (model.starmessage == "u_" + SharedPreferenceUtil.getInstance(context).userId)
                ivStarReceivePdf.setImageDrawable(context.resources.getDrawable(R.drawable.star_active))
            else
                ivStarReceivePdf.setImageDrawable(context.resources.getDrawable(R.drawable.star_inactive))

            llMainReceivePdf.setOnLongClickListener {
                onActionMessage.handleClick(model, false, false)
                return@setOnLongClickListener true
            }
        }
    }

    inner class ContactSenderHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val llSendContact = view.findViewById(R.id.llSendContact) as LinearLayout

        private val tvNameSendContact = view.findViewById(R.id.tvNameSendContact) as AppCompatTextView
        private val tvNumberSendContact = view.findViewById(R.id.tvNumberSendContact) as AppCompatTextView
        private val tvTimeSendContact = view.findViewById(R.id.tvTimeSendContact) as AppCompatTextView

        private val ivSeenStatusSendContact = view.findViewById(R.id.ivSeenStatusSendContact) as AppCompatImageView
        private val ivStarSendContact = view.findViewById(R.id.ivStarSendContact) as AppCompatImageView

        fun bindContactSenderView(model: GroupMsgContactModel) {
            tvNameSendContact.text = model.contactName
            tvNumberSendContact.text = model.contactNumber
            tvTimeSendContact.text = chatDateTimeUtil.getChatMessageTime(activity, model.timeStamp.toLong())

            if (model.status == "seen") {
                ivSeenStatusSendContact.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.last_seen))
            } else {
                ivSeenStatusSendContact.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_done))
            }
            ivStarSendContact.setOnClickListener {
                starAction.onActionDone(model)
            }

            if (model.starmessage == "u_" + SharedPreferenceUtil.getInstance(context).userId)
                ivStarSendContact.setImageDrawable(context.resources.getDrawable(R.drawable.star_active))
            else
                ivStarSendContact.setImageDrawable(context.resources.getDrawable(R.drawable.star_inactive))

            llSendContact.setOnLongClickListener {
                onActionMessage.handleClick(model, true, false)
                return@setOnLongClickListener true
            }
        }
    }

    inner class ContactReceiverHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val llMainReceiveContact = view.findViewById(R.id.llMainReceiveContact) as LinearLayout
        private val tvTimeReceiveContact = view.findViewById(R.id.tvTimeReceiveContact) as AppCompatTextView
        private val tvNameReceiveContact = view.findViewById(R.id.tvNameReceiveContact) as AppCompatTextView
        private val tvNumberReceiveContact = view.findViewById(R.id.tvNumberReceiveContact) as AppCompatTextView
        private val tvCallReceiveContact = view.findViewById(R.id.tvCallReceiveContact) as AppCompatTextView
        private val ivStarReceiveContact = view.findViewById(R.id.ivStarReceiveContact) as AppCompatImageView

        fun bindContactReceiverView(model: GroupMsgContactModel) {
            tvNameReceiveContact.text = model.contactName
            tvNumberReceiveContact.text = model.contactNumber
            tvTimeReceiveContact.text = chatDateTimeUtil.getChatMessageTime(activity, model.timeStamp.toLong())

            tvCallReceiveContact.setOnClickListener {
                if (checkPhonePermission(context)) {
                    val contactNumber = model.contactNumber
                    context.startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel: $contactNumber")))
                }
            }

            ivStarReceiveContact.setOnClickListener {
                starAction.onActionDone(model)
            }

            if (model.starmessage == "u_" + SharedPreferenceUtil.getInstance(context).userId)
                ivStarReceiveContact.setImageDrawable(context.resources.getDrawable(R.drawable.star_active))
            else
                ivStarReceiveContact.setImageDrawable(context.resources.getDrawable(R.drawable.star_inactive))

            llMainReceiveContact.setOnLongClickListener {
                onActionMessage.handleClick(model, false, false)
                return@setOnLongClickListener true
            }
        }
    }

    fun getItemAtPos(pos: Int): GroupMsgModel? {
        return getItem(pos)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Intrinsics.checkParameterIsNotNull(holder, "holder")
        val model: GroupMsgModel = getItem(position) ?: return
        when (holder) {
            is MessageSenderHolder -> {
                holder.bindMessageSenderView(model)
            }
            is MessageReceiverHolder -> {
                holder.bindMessageReceiverView(model)
            }
            is ImageSenderHolder -> {
                holder.bindImageSenderView(model)
            }
            is ImageReceiverHolder -> {
                holder.bindImageReceiverView(model)
            }
            is VideoSenderHolder -> {
                holder.bindVideoSenderView(model)
            }
            is VideoReceiverHolder -> {
                holder.bindVideoReceiverView(model)
            }
            is AudioSenderHolder -> {
                holder.bindAudioSenderView(model)
            }
            is AudioReceiverHolder -> {
                holder.bindAudioReceiverView(model)
            }
            is PdfSenderHolder -> {
                holder.bindPdfSenderView(model)
            }
            is PdfReceiverHolder -> {
                holder.bindPdfReceiverView(model)
            }
            is EmojiSenderHolder -> {
                holder.bindEmojiSenderView(model)
            }
            is EmojiReceiverHolder -> {
                holder.bindEmojiReceiverView(model)
            }
            is ScreenshotHolder -> {
                holder.bindScreenshotHolder(model)
            }
//            is ContactSenderHolder -> {
//                holder.bindContactSenderView(model)
//            }
//            is ContactReceiverHolder -> {
//                holder.bindContactReceiverView(model)
//            }
        }
    }

    //Download File
    @SuppressLint("Range")
    private fun downloadFile(downloadUrl: String, fileName: String) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(downloadUrl)
        val request = DownloadManager.Request(uri)
        //Set whether this download may proceed over a roaming connection.
        request.setAllowedOverRoaming(true)
        //Set the title of this download, to be displayed in notifications (if enabled).
        request.setTitle(context.getString(R.string.downloading))
        //Set a description of this download, to be displayed in notifications (if enabled)
        request.setDescription(context.getString(R.string.downloading_file))
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        //Set the local destination for the downloaded file to a path within the application's external files directory
        /*  request.setDestinationInExternalPublicDir(
              "",
              "/Esteto/Media/EquesterAudio/" + fileName
          )*/
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, fileName)
        //Enqueue a new download and same the referenceId
        val downloadId = downloadManager.enqueue(request)
        Thread {
            val handler = Handler(context.mainLooper)
            handler.post {
                progressDialog.show()
            }
            do {
                val query = DownloadManager.Query()
                query.setFilterById(downloadId)
                val cursor = downloadManager.query(query)
                cursor.moveToFirst()
                val bytesDownloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                val bytesTotal = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

                val progress = ((bytesDownloaded * 100L) / bytesTotal)
                handler.post {
                    progressDialog.setMessage("Downloading $progress%")
                    progressDialog.setCancelable(false)
                }
                cursor.close()
            } while (status != DownloadManager.STATUS_SUCCESSFUL)
            handler.post {
                progressDialog.dismiss()
                notifyDataSetChanged()
            }
        }.start()
    }

    //Full Screen Image
    private fun fullScreenImage(image: String) {
        context.startActivity(
            Intent(context, FullScreenImageActivityOld::class.java)
                .putExtra(IntentConstant.PIC, image)
        )
    }

    //Full Screen Video
    private fun fullScreenVideo(video: String) {
        /* context.startActivity(
             Intent(context, ShowVideoActivity::class.java)
                 .putExtra(IntentConstant.VIDEO_URL, video)
         )*/
    }

    interface onAudioPlayer {
        fun handleAudio(audioPlayer: MediaPlayer)
    }

    interface messageAction {
        fun onActionDone(chatMsgModel: GroupMsgModel)
        fun onActionDone(chatMsgModel: GroupMsgContactModel)
    }

    interface onMessageClick {
        fun handleClick(groupmessageModel: GroupMsgModel, isLoginUser: Boolean, isCopy: Boolean)
        fun handleClick(
            groupmessageModel: GroupMsgContactModel,
            isLoginUser: Boolean,
            isCopy: Boolean
        )

    }

    fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager()
        } else {
            val readCheck =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            val writeCheck =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            return readCheck == PackageManager.PERMISSION_GRANTED && writeCheck == PackageManager.PERMISSION_GRANTED
        }

    }

    fun requestPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data =
                    Uri.parse(String.format("package:%s", context.packageName))
                context.startActivity(intent)
            } catch (e: java.lang.Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                context.startActivity(intent)
            }
        } else {
            ActivityCompat.requestPermissions(
                context as Activity,
                listPERMISSIONS,
                REQUEST_ID_PERMISSIONS
            )
        }
    }

    fun searchText(text: String) {
        this.searchText = text
    }
}