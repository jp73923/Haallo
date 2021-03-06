package com.haallo.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import com.haallo.base.helper.FragmentStartListener
import com.haallo.ui.chat.dialog.CustomProgressDialog
import com.haallo.ui.chat.firebaseDb.FirebaseDbHandler
import com.haallo.util.ErrorUtil
import com.haallo.util.ProgressDialogUtil
import com.haallo.util.SharedPreferenceUtil
import com.haallo.util.UiUtils
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import timber.log.Timber

open class BaseFragment() : Fragment() {

    private val compositeDisposable = CompositeDisposable()

    lateinit var baseActivity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Not_MVVVM
        sharedPreferenceUtil = SharedPreferenceUtil.getInstance(requireContext())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.let {
            baseActivity = (context as Activity)
        }
    }

    fun Disposable.autoDispose() {
        compositeDisposable.add(this)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    @CallSuper
    open fun onBackPressed(): Boolean {
        Timber.i("[%s] onBackPressed", javaClass.simpleName)
        if (isAdded) {
            val view = baseActivity.currentFocus
            if (view != null && view is EditText) {
                UiUtils.hideKeyboard(baseActivity.window)
                return false
            }
        }
        return false
    }

    @SuppressLint("BinaryOperationInTimber")
    protected fun startFragment(fragment: Fragment) {
        val activity = ActivityManager.getInstance().foregroundActivity
        if (activity != null && activity is FragmentStartListener) {
            (activity as FragmentStartListener).onStartFragment(fragment)
        } else {
            Timber.e(
                "Try to start fragment but foreground activity ${activity?.javaClass?.simpleName} " +
                        "is either null or is not FragmentStartListener or FlutterBaseActivity"
            )
        }
    }

    //Not_MVVVM
    lateinit var sharedPreferenceUtil: SharedPreferenceUtil

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