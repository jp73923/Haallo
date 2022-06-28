package com.haallo.ui.newgroup.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.haallo.R
import com.haallo.ui.chat.newChat.ContactModel
import de.hdodenhof.circleimageview.CircleImageView

class NewGroupParticipantAdapter(
    private val mContext: Context,
    private val user: ArrayList<ContactModel>
) : RecyclerView.Adapter<NewGroupParticipantAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.list_item_user, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return user.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            tvUserName.text = user[position].name
            Glide.with(mContext).load(user[position].pic).placeholder(R.drawable.logo)
                .error(R.drawable.logo).into(ivUserPic)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivUserPic = view.findViewById(R.id.ivUserPic) as CircleImageView
        val tvUserName = view.findViewById(R.id.tvUserName) as AppCompatTextView
    }
}