package com.haallo.ui.support

import androidx.lifecycle.MutableLiveData
import com.haallo.base.OldBaseViewModel
import com.haallo.ui.chat.response.ChatNotificationResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SupportViewModelOld : OldBaseViewModel() {
    var supportLiveData = MutableLiveData<ChatNotificationResponse>()
    var onError = MutableLiveData<Throwable>()

    //Registration Api
    fun supportApi(
        accessToken: String,
        user_id: String,
        report: String,
    ) {
        val disposable: Disposable = apiInterface.support(
            accessToken = accessToken,
            user_id = user_id,
            report = report,
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onRegistrationSuccess(it) },
                { onError(it) })
    }

    //Registration Success
    private fun onRegistrationSuccess(it: ChatNotificationResponse) {
        supportLiveData.value = it
    }

    //On Error
    private fun onError(it: Throwable) {
        onError.value = it
    }
}