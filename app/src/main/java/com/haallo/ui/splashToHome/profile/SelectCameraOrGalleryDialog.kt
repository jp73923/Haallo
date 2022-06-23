package com.haallo.ui.splashToHome.profile

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import com.haallo.R
import com.haallo.databinding.PopUpSelectCameraOrGalleryBinding

class SelectCameraOrGalleryDialog(
    context: Context,
    private val nightTheme: Boolean,
    private val selectCameraOrGalleryDialogListener: SelectCameraOrGalleryDialogListener
) : Dialog(context), View.OnClickListener {

    private var _binding: PopUpSelectCameraOrGalleryBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        val view = View.inflate(context, R.layout.pop_up_select_camera_or_gallery,null)
        _binding = PopUpSelectCameraOrGalleryBinding.bind(view)

//        setContentView(R.layout.pop_up_select_camera_or_gallery)

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setGravity(Gravity.BOTTOM)
        initViews()
        initContRol()
    }

    private fun initViews() {
        checkAppTheme()
        binding.ivCamera.visibility = View.VISIBLE
    }

    //Check App Theme
    private fun checkAppTheme() {
        if (nightTheme) {
            binding.llSelectOption.setBackgroundColor(ContextCompat.getColor(context, R.color.black))
            binding.tvChooseAction.setTextColor(ContextCompat.getColor(context, R.color.white))
            binding.tvGallery.setTextColor(ContextCompat.getColor(context, R.color.white))
            binding.tvCamera.setTextColor(ContextCompat.getColor(context, R.color.white))
        }
    }

    //All Control Defines Here
    private fun initContRol() {
        binding.ivGallery.setOnClickListener(this)
        binding.ivCamera.setOnClickListener(this)
        binding.ivDismiss.setOnClickListener(this)
    }

    //OnClick Listener Function
    override fun onClick(v: View) {
        when (v.id) {
            R.id.ivGallery -> {
                dismiss()
                selectCameraOrGalleryDialogListener.onGalleryClick()
            }

            R.id.ivCamera -> {
                dismiss()
                selectCameraOrGalleryDialogListener.onCameraClick()
            }

            R.id.ivDismiss -> {
                dismiss()
            }
        }
    }

    interface SelectCameraOrGalleryDialogListener {
        fun onGalleryClick()
        fun onCameraClick()
    }
}