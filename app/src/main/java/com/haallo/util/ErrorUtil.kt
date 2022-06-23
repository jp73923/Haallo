package com.haallo.util

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import com.haallo.base.OldBaseActivity
import com.haallo.ui.splashToHome.signIn.SignInActivityOld
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException

object ErrorUtil {
    private val TAG = ErrorUtil::class.simpleName

    fun handlerGeneralError(context: Context?, view: View?, throwable: Throwable) {
        Log.e(TAG, "Error: ${throwable.message}")
        throwable.printStackTrace()

        if (context == null) return

        when (throwable) {
            is HttpException -> {
                try {
                    when (throwable.code()) {
                        401 -> {
                            context as OldBaseActivity
                            val sharedPreferenceUtil = SharedPreferenceUtil.getInstance(context)
                            sharedPreferenceUtil.screenWidth = 0
                            sharedPreferenceUtil.homeLogin = 0
                            sharedPreferenceUtil.mobileNumber = ""
                            sharedPreferenceUtil.countryCode = ""
                            sharedPreferenceUtil.accessToken = ""
                            sharedPreferenceUtil.chatWallpaper = ""
                            sharedPreferenceUtil.profilePic = ""
                            sharedPreferenceUtil.name = ""
                            sharedPreferenceUtil.about = ""
                            sharedPreferenceUtil.userName = ""
                            sharedPreferenceUtil.userGender = ""
                            sharedPreferenceUtil.splashProgress = 0
                            sharedPreferenceUtil.userId = "0"
                            context.startActivity(Intent(context, SignInActivityOld::class.java))
                            context.finishAffinity()
                        }
                        else -> {
                            SnackBarUtil.displayError(view, throwable)
                        }
                    }
                } catch (exception: Exception) {
                    SnackBarUtil.somethingWentWrong(view)
                    exception.printStackTrace()
                }
            }
            is ConnectException -> SnackBarUtil.displayError(view, throwable)
            is SocketTimeoutException -> SnackBarUtil.displayError(view, throwable)
            else -> SnackBarUtil.somethingWentWrong(view)
        }
    }
}