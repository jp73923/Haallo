package com.haallo.ui.home.chat.broadcast.view

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.haallo.R
import com.haallo.api.chat.model.RecentMessageModel
import com.haallo.base.OldBaseActivity
import com.haallo.ui.chat.firebaseDb.FirebaseDbHandler
import com.haallo.ui.chat.util.ChatDateTimeUtil
import com.haallo.util.hide
import com.haallo.util.show
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class RecentChatListAdapter(
    private val context: Context,
    private val myUserId: String,
    private val chatList: ArrayList<RecentMessageModel>,
    private val firebaseDbHandler: FirebaseDbHandler,
    private val recentChatListListener: RecentChatListListener,
    private val recentChatListListenerGroup: RecentChatListListenerGroup
) : RecyclerView.Adapter<RecentChatListAdapter.MyViewHolder>() {

    //Inflate view for recycler
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item_chat_list, p0, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.apply {
            val otherUserId: String?
            val otherUserName: String?
            val otherUserPic: String?
            if (chatList[position].senderid == myUserId) {
                otherUserId = chatList[position].receiverid
                otherUserName = chatList[position].name
                otherUserPic = chatList[position].profile_image
            } else {
                otherUserId = chatList[position].senderid
                otherUserName = chatList[position].name
                otherUserPic = chatList[position].profile_image
            }

            if (otherUserPic != null && otherUserPic != "") {
                Picasso.get().load(otherUserPic).into(ivUserProfile)
            }

            if (otherUserName != null && otherUserName != "") {
                tvUserName.text = otherUserName
            }

            val otherUserIdWithU = "u_$otherUserId"
            firebaseDbHandler.getUserLastSeen(otherUserIdWithU,
                object : FirebaseDbHandler.LastSeenListener {
                    override fun onLastSeenFound(lastSeen: String) {
                        if (lastSeen == "Online") {
                            ivOnline.show()
                        } else {
                            ivOnline.hide()
                        }
                    }
                })

            val lastMessageType: String? = chatList[position].messageType
            if (lastMessageType != null && lastMessageType != "") {
                val lastMessage: String? = chatList[position].lastmessage
                if (lastMessage != null && lastMessage != "") {
                    when (lastMessageType) {
                        "1" -> {
                            cvMediaType.visibility = View.GONE
                            tvLastMessage.text = lastMessage
                        }
                        "2" -> {
                            Picasso.get().load(lastMessage).into(ivMediaType)
                            tvLastMessage.text = context.getString(R.string.photo)
                        }
                        "3" -> {
                            ivMediaType.setImageResource(R.drawable.ic_video)
                            tvLastMessage.text = context.getString(R.string.video)
                        }
                        "4" -> {
                            ivMediaType.setImageResource(R.drawable.ic_mic)
                            tvLastMessage.text = context.getString(R.string.audio)
                        }
                        "7" -> {
                            ivMediaType.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pdf_icon))
                            tvLastMessage.text = context.getString(R.string.pdf)
                        }
                        "11" -> {
                            ivMediaType.setImageDrawable(ContextCompat.getDrawable(context, com.giphy.sdk.ui.R.drawable.gph_ic_gif)) //gph_ic_gif
                            ivMediaType.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.lightGray))
                            tvLastMessage.text = context.getString(R.string.gif)
                        }
                        "6" -> {
                            ivMediaType.setImageResource(R.drawable.ic_contact)
                            tvLastMessage.text = context.getString(R.string.contact)
                        }
                    }
                }
            }

            val timeStamp: Long? = chatList[position].timeStamp?.toLong()
            if (timeStamp != null) {
                val timeAgo: String = ChatDateTimeUtil.getTimeAgoForChatList(context, timeStamp)
                tvTime.text = timeAgo
            }

            //var unReadMessage = firebaseDbHandler.unReadChatCountMessages()

            /* rootChatList.setOnLongClickListener {
                 if (chatList[position].receiverid != "") {
                     val receiverId: String? = chatList[position].receiverid
                     val senderId: String? = chatList[position].senderid
                     val otherUserId: String
                     val otherName: String
                     val otherPic: String
                     if (senderId != null && receiverId != null) {
                         if (myUserId == receiverId) {
                             otherUserId = chatList[position].senderid!!
                             otherName = chatList[position].name!!
                             otherPic = chatList[position].profile_image!!
                         } else {
                             otherUserId = chatList[position].id!!
                             otherName = chatList[position].name!!
                             otherPic = chatList[position].profile_image!!
                         }
                         recentChatListListener.onChatSelect(
                             otherUserId,
                             otherName,
                             otherPic,
                             "longclick"
                         )
                     }
                 } else {
                     recentChatListListenergGroup.onChatSelect(
                         chatList[position].group_id!!,
                         chatList[position].name!!,
                         chatList[position].profile_image!!,
                         chatList[position].isLeaved, "longclick"
                     )

                 }
                 return@setOnLongClickListener true
             }*/

            rootChatList.setOnClickListener {
                if (chatList[position].receiverid != "") {
                    val receiverId: String? = chatList[position].receiverid
                    val senderId: String? = chatList[position].senderid
                    val otherUserId: String
                    val otherName: String
                    val otherPic: String
                    if (senderId != null && receiverId != null) {
                        if (myUserId == receiverId) {
                            otherUserId = chatList[position].senderid!!
                            otherName = chatList[position].name!!
                            otherPic = chatList[position].profile_image!!
                        } else {
                            otherUserId = chatList[position].id!!
                            otherName = chatList[position].name!!
                            otherPic = chatList[position].profile_image!!
                        }
                        recentChatListListener.onChatSelect(
                            otherUserId,
                            otherName,
                            otherPic,
                            "click"
                        )
                    }
                } else {
                    recentChatListListenerGroup.onChatSelect(
                        chatList[position].group_id!!,
                        chatList[position].name!!,
                        chatList[position].profile_image!!,
                        chatList[position].isLeaved, "click"
                    )
                }
            }
        }
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rootChatList = view.findViewById(R.id.rootChatList) as CardView

        val tvTime = view.findViewById(R.id.tvTime) as AppCompatTextView
        val tvLastMessage = view.findViewById(R.id.tvLastMessage) as AppCompatTextView
        val tvUserName = view.findViewById(R.id.tvUserName) as AppCompatTextView

        val ivMediaType = view.findViewById(R.id.ivMediaType) as AppCompatImageView
        val ivOnline = view.findViewById(R.id.ivOnline) as AppCompatImageView

        val cvMediaType = view.findViewById(R.id.cvMediaType) as CardView
        val ivUserProfile = view.findViewById(R.id.ivUserProfile) as CircleImageView
    }

    interface RecentChatListListener {
        fun onChatSelect(otherUserId: String, otherUserName: String, otherUserPic: String, type: String)
    }

    interface RecentChatListListenerGroup {
        fun onChatSelect(
            Group_id: String,
            otherUserName: String,
            groupPic: String,
            leaved: String?,
            type: String
        )
    }
}