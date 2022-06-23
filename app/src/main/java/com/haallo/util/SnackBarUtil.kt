package com.haallo.util

import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import com.haallo.R
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException

object SnackBarUtil {
    @Suppress("unused")
    val TAG = SnackBarUtil::class.simpleName

    fun somethingWentWrong(view: View?) {
        if (view == null) return
        //displaySnackBar(view, view.context.getString(R.string.error_something_went_wrong_please_retry))
        Toast.makeText(
            view.context,
            view.context.getString(R.string.error_something_went_wrong_please_retry),
            Toast.LENGTH_SHORT
        ).show()
    }

    fun displayError(view: View?, socketTimeoutException: SocketTimeoutException) {
        if (view == null) return
        Toast.makeText(
            view.context,
            view.context.getString(R.string.no_internet_connection),
            Toast.LENGTH_SHORT
        ).show()
    }

    fun displayError(view: View?, connectionException: ConnectException) {
        if (view == null) return
        //displaySnackBar(view, view.context.getString(R.string.error_connection_please_check_internet))
        Toast.makeText(
            view.context,
            view.context.getString(R.string.no_internet_connection),
            Toast.LENGTH_SHORT
        ).show()
    }

    fun displayError(view: View?, exception: HttpException) {
        Log.i(TAG, "displayError()")
        try {
            val errorBody =
                Gson().fromJson(
                    exception.response()?.errorBody()?.charStream(),
                    ErrorBean::class.java
                )
            //displaySnackBar(view, errorBody.message)
            Toast.makeText(view!!.context, errorBody.message, Toast.LENGTH_SHORT).show()
            Log.e("ErrorMessage", errorBody.message)
        } catch (e: Exception) {
            somethingWentWrong(view)
        }
    }
}