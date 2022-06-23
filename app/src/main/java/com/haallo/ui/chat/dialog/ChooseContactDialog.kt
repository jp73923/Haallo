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
import com.haallo.databinding.PopUpChooseContactBinding
import com.haallo.ui.chat.adapter.PhoneNumberAdapter
import com.wafflecopter.multicontactpicker.RxContacts.PhoneNumber

class ChooseContactDialog(
    context: Context,
    private val phoneNumber: ArrayList<PhoneNumber>,
    private val chooseContactDialogListener: ChooseContactDialogListener
) : Dialog(context) {

    private var _binding: PopUpChooseContactBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        val view = View.inflate(context, R.layout.pop_up_choose_contact, null)
        _binding = PopUpChooseContactBinding.bind(view)

        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window!!.setGravity(Gravity.CENTER)

        initViews()
        initControls()
    }

    //All UI Changes From Here
    private fun initViews() {
        val phoneNumberAdapter = PhoneNumberAdapter(context, phoneNumber, object : PhoneNumberAdapter.PhoneNumberListener {
            override fun phoneSelect(position: Int) {
                chooseContactDialogListener.onSelectContact(position)
            }
        })
        binding.rvSelectPhone.adapter = phoneNumberAdapter
    }

    //All Controls Defines Here
    private fun initControls() {

    }

    interface ChooseContactDialogListener {
        fun onSelectContact(position: Int)
    }
}