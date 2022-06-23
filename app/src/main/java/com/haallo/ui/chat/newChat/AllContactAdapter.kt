package com.haallo.ui.chat.newChat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.haallo.R
import com.haallo.util.hide
import com.haallo.util.show
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class AllContactAdapter(
    private val user: ArrayList<ContactModel>,
    private var allContactListener: AllContactListener
) : RecyclerView.Adapter<AllContactAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.list_item_user, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return user.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            if (user[position].name != "") {
                tvUserName.text = user[position].name
            }
            tvInvite.hide()
            if (user[position].isOnHallo) {
                if (user[position].pic != "") {
                    Picasso.get().load(user[position].pic).into(ivUserPic)
                }
                rootContact.setOnClickListener {
                    allContactListener.itemClick(
                        user[position].id,
                        user[position].name,
                        user[position].pic,
                        user[position].number
                    )
                }
            } else {
                tvInvite.show()
                ivUserPic.setImageResource(R.drawable.logo_haallo)
            }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rootContact = view.findViewById(R.id.rootContact) as ConstraintLayout
        val ivUserPic = view.findViewById(R.id.ivUserPic) as CircleImageView
        val tvUserName = view.findViewById(R.id.tvUserName) as AppCompatTextView
        val tvInvite = view.findViewById(R.id.tvInvite) as AppCompatTextView
    }

    interface AllContactListener {
        fun itemClick(otherUserId: String, otherUserName: String, otherUserPic: String, number: String)
    }
}