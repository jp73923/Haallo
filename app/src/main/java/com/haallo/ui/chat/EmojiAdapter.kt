package com.haallo.ui.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.haallo.R
import com.haallo.util.getResource

class EmojiAdapter(
    val context: Context,
    private val icons: ArrayList<String>, val listener: OnEmojiSelectedListener
) : RecyclerView.Adapter<EmojiAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.emoji_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return icons.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.ivEmojiIcon.setImageDrawable(context.getResource(icons[position]))
        holder.ivEmojiIcon.setOnClickListener {
            listener.onEmojiSelected(position, icons[position])
        }

    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivEmojiIcon: ImageView = view.findViewById(R.id.emojicon_icon)
    }

    interface OnEmojiSelectedListener {
        fun onEmojiSelected(position: Int, emoji: String)
    }
}

