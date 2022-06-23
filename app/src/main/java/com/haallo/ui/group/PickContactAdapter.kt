package com.haallo.ui.group

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.haallo.R
import com.haallo.ui.group.model.ContactPickModel
import com.haallo.util.hide
import com.haallo.util.show
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PickContactAdapter(
    private val mContext: Context,
    private val user: ArrayList<ContactPickModel>,
    private var allContactListener: AllContactPickerListener
) : RecyclerView.Adapter<PickContactAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.list_item_contact, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return user.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        /* holder.itemView.remove_participant.setOnClickListener {
             if (!user[position].isSelected) {
                 (holder.itemView.context as PickContactsActivity).positionArrayList.add(position)
                 user[position].isSelected = true
                 holder.itemView.remove_participant.setImageDrawable(
                     holder.itemView.context.getDrawable(
                         R.drawable.group_tick
                     )
                 )
             } else {
                 (holder.itemView.context as PickContactsActivity).positionArrayList.remove(position)
                 holder.itemView.remove_participant.setImageDrawable(
                     holder.itemView.context.getDrawable(
                         R.drawable.radiobutton_off_dark
                     )
                 )
                 user[position].isSelected = false
             }
             notifyDataSetChanged()
         }*/
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
                    /* if (!(context as PickContactsActivity).hashmap.containsKey(position)) {
                         (context as PickContactsActivity).hashmap.put(position, user[position])
                         holder.itemView.remove_participant.setImageDrawable(
                             holder.itemView.context.getDrawable(
                                 R.drawable.group_tick
                             )
                         )
                     } else {
                         (context as PickContactsActivity).hashmap.remove(position)
                         holder.itemView.remove_participant.setImageDrawable(
                             holder.itemView.context.getDrawable(
                                 R.drawable.radiobutton_off_dark
                             )
                         )
                     }*/
                    if (!(mContext as PickContactsActivityOld).arrayList.contains(user[position])) {
                        mContext.arrayList.add(user[position])
                        remove_participant.setImageDrawable(
                            ContextCompat.getDrawable(mContext, R.drawable.group_tick)
                        )
                    } else {
                        mContext.arrayList.remove(user[position])
                        remove_participant.setImageDrawable(
                            ContextCompat.getDrawable(mContext, R.drawable.radiobutton_off_dark)
                        )
                    }
                    notifyDataSetChanged()

                }


                /* rootContact.setOnClickListener {
                     if (!user[position].isSelected) {
                         (holder.itemView.context as PickContactsActivity).positionArrayList.add(
                             position
                         )

                         user[position].isSelected = true
                         (holder.itemView.context as PickContactsActivity).updatedPickContactList.add(
                             user[position]
                         )

                         holder.itemView.remove_participant.setImageDrawable(
                             holder.itemView.context.getDrawable(
                                 R.drawable.group_tick
                             )
                         )
                     } else {
                         user[position].isSelected = false
                         (holder.itemView.context as PickContactsActivity).positionArrayList.remove(
                             position
                         )
                         (holder.itemView.context as PickContactsActivity).updatedPickContactList.removeAt(
                             position
                         )

                         holder.itemView.remove_participant.setImageDrawable(
                             holder.itemView.context.getDrawable(
                                 R.drawable.radiobutton_off_dark
                             )
                         )

                     }
                     notifyDataSetChanged()
                 }*/
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
        val remove_participant = view.findViewById(R.id.remove_participant) as ImageView
    }

    interface AllContactPickerListener {
        fun itemClick(otherUserId: String, otherUserName: String, otherUserPic: String, number: String)
    }
}