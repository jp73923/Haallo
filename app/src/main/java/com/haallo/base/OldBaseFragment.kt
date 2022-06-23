package com.haallo.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.haallo.R
import com.haallo.ui.chat.dialog.CustomProgressDialog
import com.haallo.ui.chat.firebaseDb.FirebaseDbHandler
import com.haallo.util.ErrorUtil
import com.haallo.util.ProgressDialogUtil
import com.haallo.util.SharedPreferenceUtil

abstract class OldBaseFragment : Fragment() {

    lateinit var sharedPreferenceUtil: SharedPreferenceUtil

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return TextView(activity).apply {
            setText(R.string.hello_blank_fragment)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sharedPreferenceUtil = SharedPreferenceUtil.getInstance(requireActivity())
    }

    abstract fun initViews()
    abstract fun initControl()

    fun hideLoading() {
        ProgressDialogUtil.getInstance().hideProgress()
    }

    fun showLoading() {
        hideLoading()
        ProgressDialogUtil.getInstance().showProgress(requireContext(), false)
    }

    fun showError(context: Context?, view: View?, throwable: Throwable) {
        ErrorUtil.handlerGeneralError(context, view, throwable)
    }

    val firebaseDbHandler by lazy {
        FirebaseDbHandler(requireContext())
    }

    val progressDialog: CustomProgressDialog by lazy {
        CustomProgressDialog(requireContext(), "Please wait...")
    }
}