package com.haallo.ui.home.call

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.haallo.R
import com.haallo.ui.call.CallModel
import com.haallo.databinding.ListItemCallListBinding
import com.haallo.util.findDateAndTime

class CallHistoryAdapter(val chatList:ArrayList<CallModel>, val listener:OnCallInterface) : RecyclerView.Adapter<CallHistoryAdapter.CallHistoryViewHolder>() {

    interface OnCallInterface {
        fun onVideoCall(view: View,item: CallModel)
        fun onAudioCall(view: View,item: CallModel)
    }

    class CallHistoryViewHolder(val binding: ListItemCallListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(call: CallModel){
            binding.tvUserName.setText(call.sendername)
            binding.tvUserTimeStamp.setText(findDateAndTime(call.timeStamp))
            Glide.with(binding.root).load(call.senderImage).into(binding.ivUserProfile)

            if (call.call_type == "0") {
                binding.ivImageType.setImageResource(R.drawable.calls_blue)
            } else {
                binding.ivImageType.setImageResource(R.drawable.video_blue)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallHistoryViewHolder {
        return CallHistoryViewHolder(ListItemCallListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CallHistoryViewHolder, position: Int) {
        holder.bind(chatList[position])
        holder.binding.ivImageType.setOnClickListener {
            if (chatList[position].call_type == "0"){
                listener.onAudioCall(it, chatList[position])
            } else {
                listener.onVideoCall(it, chatList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }
}