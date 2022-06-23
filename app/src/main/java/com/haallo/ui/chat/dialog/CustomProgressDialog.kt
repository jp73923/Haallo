package com.haallo.ui.chat.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatTextView
import com.haallo.R

class CustomProgressDialog(
    private val mContext: Context,
    private val message: String
) : Dialog(mContext) {

    private lateinit var tvMessage: AppCompatTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = View.inflate(context, R.layout.custom_progress_dialog, null)

        tvMessage = view.findViewById(R.id.tvMessage)
        tvMessage.text = message

        setContentView(view)
        setCancelable(false)

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(window!!.attributes)
        layoutParams.width = (mContext.resources.displayMetrics.widthPixels * 0.90).toInt()
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.dimAmount = 0.7f
        window!!.attributes = layoutParams
    }

    fun setMessage(message: String) {
        tvMessage.text = message
    }
}
