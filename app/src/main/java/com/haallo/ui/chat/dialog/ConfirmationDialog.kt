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
import com.haallo.databinding.PopUpConfirmationDialogBinding

class ConfirmationDialog(
    context: Context,
    private val isFrom: String,
    private val confirmationDialogListener: ConfirmationDialogListener
) : Dialog(context), View.OnClickListener {

    private var _binding: PopUpConfirmationDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        val view = View.inflate(context, R.layout.pop_up_confirmation_dialog, null)
        _binding = PopUpConfirmationDialogBinding.bind(view)


        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window!!.setGravity(Gravity.CENTER)

        initViews()
        initControls()
    }

    //All UI Changes From Here
    private fun initViews() {
        when (isFrom) {
            IntentConstant.DEACTIVATE_ACCOUNT -> {
                binding.tvAlertFor.text = context.getString(R.string.alert_for_deactivate)
                binding.tvWarning.text = context.getString(R.string.account_deactivate_warning)
            }
            IntentConstant.LOGOUT -> {
                binding.tvAlertFor.text = context.getString(R.string.alert_for_logout)
                binding.tvWarning.text = context.getString(R.string.logout_warning)
            }
            IntentConstant.BLOCK_USER -> {
                binding.tvAlertFor.text = context.getString(R.string.alert_for_block)
                binding.tvWarning.text = context.getString(R.string.block_warning)
            }
            IntentConstant.UN_BLOCK_USER -> {
                binding.tvAlertFor.text = context.getString(R.string.alert_for_un_block)
                binding.tvWarning.text = context.getString(R.string.un_block_warning)
            }
            IntentConstant.DELETE_CHAT -> {
                binding.tvAlertFor.text = context.getString(R.string.alert_for_delete_chat)
                binding.tvWarning.text = context.getString(R.string.delete_chat_warning)
            }
        }
    }

    //All Controls Defines Here
    private fun initControls() {
        binding.btnTakeMeBack.setOnClickListener(this)
        binding.btnConfirm.setOnClickListener(this)
    }

    //Onclick Listener
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnTakeMeBack -> {
                dismiss()
            }

            R.id.btnConfirm -> {
                dismiss()
                confirmationDialogListener.onYesClick()
            }
        }
    }

    interface ConfirmationDialogListener {
        fun onYesClick()
    }
}