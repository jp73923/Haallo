package com.haallo.ui.createprofile

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
import com.haallo.databinding.BottomSheetSelectGenderBinding
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.*

class SelectGenderBottomSheet(
    private val selectedGender: String
) : BaseBottomSheetDialogFragment() {

    private val selectGenderClickSubject: PublishSubject<String> = PublishSubject.create()
    val editProfilePhotoClick: Observable<String> = selectGenderClickSubject.hide()

    private var _binding: BottomSheetSelectGenderBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_select_gender, container, false)
        _binding = BottomSheetSelectGenderBinding.bind(view)
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
        if (selectedGender.isNotEmpty()) {
            when {
                selectedGender.lowercase(Locale.getDefault()).equals(getString(R.string.male), true) -> {
                    binding.rbMale.isChecked = true
                }
                selectedGender.lowercase(Locale.getDefault()).equals(getString(R.string.female), true) -> {
                    binding.rbFemale.isChecked = true
                }
                else -> {
                    binding.rbMale.isChecked = true
                }
            }
        } else {
            binding.rbMale.isChecked = true
        }

        binding.ivClose.throttleClicks().subscribeAndObserveOnMainThread {
            dismissBottomSheet()
        }.autoDispose()

        binding.rbMale.throttleClicks().subscribeAndObserveOnMainThread {
            binding.rbMale.isChecked = true
            selectGenderClickSubject.onNext(getString(R.string.male))
        }.autoDispose()

        binding.rbFemale.throttleClicks().subscribeAndObserveOnMainThread {
            binding.rbFemale.isChecked = true
            selectGenderClickSubject.onNext(getString(R.string.female))
        }.autoDispose()
    }

    fun dismissBottomSheet() {
        dismiss()
    }
}