@file:JvmName("FragmentExtension")

package com.haallo.base.extension

import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.haallo.R

fun Fragment.startActivityWithDefaultAnimation(intent: Intent) {
    activity?.startActivityWithDefaultAnimation(intent)
}

fun Fragment.startActivityForResultWithDefaultAnimation(intent: Intent, requestCode: Int) {
    startActivityForResult(intent, requestCode)
    activity?.overridePendingTransition(R.anim.activity_move_in_from_right, R.anim.activity_move_out_to_left)
}

fun Fragment.startActivityWithBottomInAnimation(intent: Intent) {
    activity?.startActivityWithBottomInAnimation(intent)
}

fun Fragment.startActivityForResultWithBottomInAnimation(intent: Intent, requestCode: Int) {
    startActivityForResult(intent, requestCode)
    activity?.overridePendingTransition(R.anim.activity_move_in_from_bottom, R.anim.default_exit)
}

fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun Fragment.showLongToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
}

inline fun <reified T : ViewModel> Fragment.getViewModelFromFactory(vmFactory: ViewModelProvider.Factory): T {
    return ViewModelProvider(this, vmFactory)[T::class.java]
}