package com.haallo.ui.chat.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.haallo.R
import com.haallo.databinding.PopUpChatMoreOptionBinding

class ChatMoreOptionDialog(
    context: Context,
    private val muteUnMuteStatus: String,
    private val isBlock: Boolean,
    private val chatMoreOptionDialogListener: ChatMoreOptionDialogListener
) : Dialog(context), View.OnClickListener {

    private var _binding: PopUpChatMoreOptionBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        val view = View.inflate(context, R.layout.pop_up_chat_more_option, null)
        _binding = PopUpChatMoreOptionBinding.bind(view)

        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window!!.setGravity(Gravity.TOP or Gravity.END)

        initViews()
        initControls()
    }

    //All UI Changes From Here
    private fun initViews() {
        if (muteUnMuteStatus == "mute") {
            binding.tvMuteNotifications.visibility = View.GONE
            binding.tvUnMuteNotifications.visibility = View.VISIBLE
        } else if (muteUnMuteStatus == "unmute") {
            binding.tvMuteNotifications.visibility = View.VISIBLE
            binding.tvUnMuteNotifications.visibility = View.GONE
        }

        if (isBlock) {
            binding.tvBlockUser.visibility = View.GONE
            binding.tvUnBlockUser.visibility = View.VISIBLE
        } else {
            binding.tvBlockUser.visibility = View.VISIBLE
            binding.tvUnBlockUser.visibility = View.GONE
        }
    }

    //All Controls Defines Here
    private fun initControls() {
        binding.tvBlockUser.setOnClickListener(this)
        binding.tvClearChat.setOnClickListener(this)
        binding.tvUnBlockUser.setOnClickListener(this)
        binding.tvMuteNotifications.setOnClickListener(this)
        binding.tvUnMuteNotifications.setOnClickListener(this)
        binding.tvReportUser.setOnClickListener(this)
        binding.tvDeleteChat.setOnClickListener(this)
    }

    //Onclick Listener
    override fun onClick(v: View) {
        when (v.id) {
            R.id.tvBlockUser -> {
                dismiss()
                chatMoreOptionDialogListener.onBlockUser()
            }
            R.id.tvUnBlockUser -> {
                dismiss()
                chatMoreOptionDialogListener.onUnBlockUser()
            }
            R.id.tvMuteNotifications -> {
                dismiss()
                chatMoreOptionDialogListener.onMuteNotifications("mute")
            }
            R.id.tvUnMuteNotifications -> {
                dismiss()
                chatMoreOptionDialogListener.onMuteNotifications("unmute")
            }
            R.id.tvReportUser -> {
                dismiss()
                chatMoreOptionDialogListener.onReportUser()
            }
            R.id.tvDeleteChat -> {
                dismiss()
                chatMoreOptionDialogListener.onDeleteChat()
            }
            R.id.tvClearChat -> {
                dismiss()
                chatMoreOptionDialogListener.clearChat()
            }
        }
    }

    interface ChatMoreOptionDialogListener {
        fun onBlockUser()
        fun onUnBlockUser()
        fun onMuteNotifications(muteUnMuteStatus: String)
        fun onReportUser()
        fun onDeleteChat()
        fun clearChat()
    }
}