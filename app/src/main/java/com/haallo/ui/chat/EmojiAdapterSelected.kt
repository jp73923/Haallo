package com.haallo.ui.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.haallo.R
import com.haallo.util.getResource

class EmojiAdapterSelected(
    val context: Context,
    private val icons: ArrayList<String>
) : RecyclerView.Adapter<EmojiAdapterSelected.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.emoji_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return icons.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.ivEmojiIcon.setImageDrawable(context.getResource(icons[position]))
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivEmojiIcon: ImageView = view.findViewById(R.id.emojicon_icon)
    }
}