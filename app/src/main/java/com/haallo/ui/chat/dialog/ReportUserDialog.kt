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
import android.widget.RadioButton
import com.haallo.R
import com.haallo.databinding.PopUpReportUserBinding
import com.haallo.util.getString
import com.haallo.util.showToast

class ReportUserDialog(
    context: Context,
    private val reportUserDialogListener: ReportUserDialogListener
) : Dialog(context), View.OnClickListener {

    private var _binding: PopUpReportUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        val view = View.inflate(context, R.layout.pop_up_report_user, null)
        _binding = PopUpReportUserBinding.bind(view)

        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window!!.setGravity(Gravity.CENTER)

        initViews()
        initControls()
    }

    //All UI Changes From Here
    private fun initViews() {

    }

    //All Controls Defines Here
    private fun initControls() {
        binding.btnReport.setOnClickListener(this)
    }

    //Onclick Listener
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnReport -> {
                if (isInputValid()) {
                    dismiss()
                    val selectedId = binding.rgReason.checkedRadioButtonId
                    val radioButton: View = binding.rgReason.findViewById(selectedId)
                    val idx: Int = binding.rgReason.indexOfChild(radioButton)
                    val r: RadioButton = binding.rgReason.getChildAt(idx) as RadioButton
                    reportUserDialogListener.onReportClick(
                        r.text.toString(),
                        binding.etComment.getString()
                    )
                }
            }
        }
    }

    //Input Validation
    private fun isInputValid(): Boolean {
        if (!binding.rb1.isChecked && !binding.rb2.isChecked && !binding.rb3.isChecked && !binding.rb4.isChecked) {
            showToast("Please select reason for report", context)
            return false
        }
        return true
    }

    interface ReportUserDialogListener {
        fun onReportClick(reason: String, comment: String?)
    }
}