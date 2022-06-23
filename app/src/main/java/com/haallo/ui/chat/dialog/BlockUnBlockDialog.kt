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
import com.haallo.constant.IntentConstant
import com.haallo.databinding.PopUpBlockUnblockUserBinding

class BlockUnBlockDialog(
    context: Context,
    private val isFrom: String,
    private val blockUnBlockDialogListener: BlockUnBlockDialogListener
) : Dialog(context), View.OnClickListener {

    private var _binding: PopUpBlockUnblockUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        val view = View.inflate(context, R.layout.pop_up_block_unblock_user, null)
        _binding = PopUpBlockUnblockUserBinding.bind(view)

        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window!!.setGravity(Gravity.CENTER)

        initViews()
        initControls()
    }

    //All UI Changes From Here
    private fun initViews() {
        when (isFrom) {
            IntentConstant.BLOCK_USER -> {
                binding.tvHeading.text = context.getString(R.string.alert_for_block)
                binding.tvWarning.text = context.getString(R.string.block_warning)
                binding.btnBlockUnBlock.text = context.getString(R.string.block)
            }
            IntentConstant.UN_BLOCK_USER -> {
                binding.tvHeading.text = context.getString(R.string.alert_for_un_block)
                binding.tvWarning.text = context.getString(R.string.un_block_warning)
                binding.btnBlockUnBlock.text = context.getString(R.string.un_block)
            }
        }
    }

    //All Controls Defines Here
    private fun initControls() {
        binding.btnBlockUnBlock.setOnClickListener(this)
    }

    //Onclick Listener
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnBlockUnBlock -> {
                dismiss()
                blockUnBlockDialogListener.onClick()
            }
        }
    }

    interface BlockUnBlockDialogListener {
        fun onClick()
    }
}