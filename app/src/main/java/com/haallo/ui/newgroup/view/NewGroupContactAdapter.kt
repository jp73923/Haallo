package com.haallo.ui.newgroup.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.haallo.R
import com.haallo.ui.chat.newChat.ContactModel
import de.hdodenhof.circleimageview.CircleImageView

class NewGroupContactAdapter(
    private val context: Context,
    private val contactModelArrayList: ArrayList<ContactModel>,
    private var newChatContactAdapterCallback: NewGroupContactAdapterCallback
) : RecyclerView.Adapter<NewGroupContactAdapter.AdapterVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_new_group_contact, parent, false)
        return AdapterVH(view)
    }

    override fun getItemCount(): Int {
        return contactModelArrayList.size
    }

    override fun onBindViewHolder(holder: AdapterVH, position: Int) {
        val contactModel = contactModelArrayList[position]
        holder.tvUserName.text = contactModel.name

        Glide.with(context)
            .load(contactModel.pic)
            .placeholder(R.drawable.logo_haallo)
            .error(R.drawable.logo_haallo)
            .into(holder.ivUserProfileImage)

        if (contactModel.isSelected) {
            holder.ivAddRemoveParticipant.setImageResource(R.drawable.ic_select_contact_check)
        } else {
            holder.ivAddRemoveParticipant.setImageResource(R.drawable.ic_select_contact_uncheck)
        }
    }

    inner class AdapterVH(view: View) : RecyclerView.ViewHolder(view) {

        val ivUserProfileImage = view.findViewById(R.id.ivUserProfileImage) as CircleImageView
        val tvUserName = view.findViewById(R.id.tvUserName) as AppCompatTextView
        val ivAddRemoveParticipant = view.findViewById(R.id.ivAddRemoveParticipant) as AppCompatImageView

        init {
            ivAddRemoveParticipant.setOnClickListener {
                newChatContactAdapterCallback.adapterCallbackItemClick(contactModelArrayList[adapterPosition])
            }
        }
    }

    interface NewGroupContactAdapterCallback {
        fun adapterCallbackItemClick(contactModel: ContactModel)
    }
}