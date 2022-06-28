package com.haallo.ui.chat.newChat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.haallo.R
import de.hdodenhof.circleimageview.CircleImageView

class AllContactAdapter(
    private val context: Context,
    private val contactModelArrayList: ArrayList<ContactModel>,
    private var allContactAdapterCallback: AllContactAdapterCallback
) : RecyclerView.Adapter<AllContactAdapter.AdapterVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_matching_chat_contact, parent, false)
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
    }

    inner class AdapterVH(view: View) : RecyclerView.ViewHolder(view) {

        val ivUserProfileImage = view.findViewById(R.id.ivUserProfileImage) as CircleImageView
        val tvUserName = view.findViewById(R.id.tvUserName) as AppCompatTextView

        init {
            view.setOnClickListener {
                allContactAdapterCallback.adapterCallbackItemClick(contactModelArrayList[adapterPosition])
            }
        }
    }

    interface AllContactAdapterCallback {
        fun adapterCallbackItemClick(contactModel: ContactModel)
    }
}