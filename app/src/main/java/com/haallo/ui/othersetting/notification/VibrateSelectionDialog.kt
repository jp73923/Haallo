package com.haallo.ui.othersetting.notification

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.haallo.R
import com.haallo.api.notification.VibrateSelectState
import com.haallo.base.BaseDialogFragment
import com.haallo.base.extension.subscribeAndObserveOnMainThread
import com.haallo.databinding.DialogVibrateSelectionBinding
import com.jakewharton.rxbinding3.widget.checkedChanges
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class VibrateSelectionDialog(
    private var vibrateType: String = ""
) : BaseDialogFragment() {

    private val vibrateSelectClickSubject: PublishSubject<VibrateSelectState> = PublishSubject.create()
    val vibrateSelectClick: Observable<VibrateSelectState> = vibrateSelectClickSubject.hide()

    private var _binding: DialogVibrateSelectionBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AlertDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_vibrate_selection, container, false)
        _binding = DialogVibrateSelectionBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listenToEvent()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

//        val bottomSheet = (view?.parent as View)
//        bottomSheet.backgroundTintMode = PorterDuff.Mode.CLEAR
//        bottomSheet.setBackgroundColor(Color.TRANSPARENT)
    }

    private fun listenToEvent() {
        if (vibrateType.isNotEmpty()) {
            vibrateType = getString(R.string.label_off)
        }

        when (vibrateType) {
            getString(R.string.label_off) -> {
                binding.rbOff.isChecked = true
            }
            getString(R.string.label_short) -> {
                binding.rbShort.isChecked = true
            }
            getString(R.string.label_long) -> {
                binding.rbLong.isChecked = true
            }
        }

        binding.rgVibrate.checkedChanges().skipInitialValue().subscribeAndObserveOnMainThread {
            when (it) {
                R.id.rbOff -> {
                    vibrateSelectClickSubject.onNext(VibrateSelectState.VibrateOff)
                }
                R.id.rbShort -> {
                    vibrateSelectClickSubject.onNext(VibrateSelectState.ShortVibrate)
                }
                R.id.rbLong -> {
                    vibrateSelectClickSubject.onNext(VibrateSelectState.LongVibrate)
                }
            }
        }.autoDispose()
    }

    fun dismissBottomSheet() {
        dismiss()
    }
}