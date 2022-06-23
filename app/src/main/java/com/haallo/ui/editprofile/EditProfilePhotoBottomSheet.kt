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
import com.haallo.api.profile.model.EditProfilePhotoState
import com.haallo.base.BaseBottomSheetDialogFragment
import com.haallo.base.extension.subscribeAndObserveOnMainThread
import com.haallo.base.extension.throttleClicks
import com.haallo.databinding.BottomSheetEditProfilePhotoBinding
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class EditProfilePhotoBottomSheet : BaseBottomSheetDialogFragment() {

    private val editProfilePhotoClickSubject: PublishSubject<EditProfilePhotoState> = PublishSubject.create()
    val editProfilePhotoClick: Observable<EditProfilePhotoState> = editProfilePhotoClickSubject.hide()

    private var _binding: BottomSheetEditProfilePhotoBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_edit_profile_photo, container, false)
        _binding = BottomSheetEditProfilePhotoBinding.bind(view)
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
        binding.llCamera.throttleClicks().subscribeAndObserveOnMainThread {
            editProfilePhotoClickSubject.onNext(EditProfilePhotoState.OpenCamera)
        }.autoDispose()

        binding.llGallery.throttleClicks().subscribeAndObserveOnMainThread {
            editProfilePhotoClickSubject.onNext(EditProfilePhotoState.OpenGallery)
        }.autoDispose()

        binding.ivDelete.throttleClicks().subscribeAndObserveOnMainThread {
            editProfilePhotoClickSubject.onNext(EditProfilePhotoState.DeletePhoto)
        }.autoDispose()
    }

    fun dismissBottomSheet() {
        dismiss()
    }
}