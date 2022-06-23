package com.haallo.ui.editprofile

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.haallo.R
import com.haallo.base.BaseBottomSheetDialogFragment
import com.haallo.base.extension.subscribeAndObserveOnMainThread
import com.haallo.base.extension.throttleClicks
import com.haallo.databinding.BottomSheetEditAboutBinding
import com.haallo.util.showToast
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class EditAboutBottomSheet(
    private val about: String
) : BaseBottomSheetDialogFragment() {

    private val saveAboutClickSubject: PublishSubject<String> = PublishSubject.create()
    val saveAboutClick: Observable<String> = saveAboutClickSubject.hide()

    private var _binding: BottomSheetEditAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_edit_about, container, false)
        _binding = BottomSheetEditAboutBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listenToEvent()

        dialog?.apply {
            val bottomSheetDialog = this as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            if (bottomSheet != null) {
                val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight = 0
                behavior.addBottomSheetCallback(mBottomSheetBehaviorCallback)
            }
        }
    }

    private val mBottomSheetBehaviorCallback: BottomSheetBehavior.BottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val bottomSheet = (view?.parent as View)
        bottomSheet.backgroundTintMode = PorterDuff.Mode.CLEAR
        bottomSheet.setBackgroundColor(Color.TRANSPARENT)
    }

    private fun listenToEvent() {
        binding.etAbout.setText(about)
        binding.etAbout.textChanges().subscribeAndObserveOnMainThread {
            binding.tvCount.text = (getString(R.string.max_length_about).toInt() - it.length).toString()
        }.autoDispose()

        binding.btnSave.throttleClicks().subscribeAndObserveOnMainThread {
            if (binding.etAbout.text.toString().isNotEmpty()) {
                saveAboutClickSubject.onNext(binding.etAbout.text.toString())
            } else {
                showToast(getString(R.string.msg_about_can_not_empty))
            }
        }.autoDispose()

        binding.btnCancel.throttleClicks().subscribeAndObserveOnMainThread {
            dismissBottomSheet()
        }.autoDispose()
    }

    fun dismissBottomSheet() {
        dismiss()
    }
}