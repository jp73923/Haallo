package com.haallo.ui.chat

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.haallo.base.OldBaseAppViewModel
import com.haallo.ui.chat.newChat.MatchContactResponse
import com.haallo.ui.chat.response.ChatNotificationResponse
import com.haallo.ui.chat.response.GetFileToUrlResponse
import com.haallo.ui.chat.response.MuteUnMuteStatusResponse
import com.haallo.ui.chat.response.ReportUserResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody

class ChatViewModelOld(application: Application) : OldBaseAppViewModel(application) {

    var matchContactResponse = MutableLiveData<MatchContactResponse>()
    var getFileToUrlResponse = MutableLiveData<GetFileToUrlResponse>()
    var muteUnMuteStatusResponse = MutableLiveData<MuteUnMuteStatusResponse>()
    var reportUserResponse = MutableLiveData<ReportUserResponse>()
    var chatNotificationResponse = MutableLiveData<ChatNotificationResponse>()
    var videoCallResponse = MutableLiveData<ChatNotificationResponse>()
    var audiocallResponse = MutableLiveData<ChatNotificationResponse>()
    var error = MutableLiveData<Throwable>()


    //Match Contact Api
    fun matchContactApi(accessToken: String, mobile: List<String>) {

//        var contactListObservable = mDataBase.contactsDao().getAllContacts()

//        var contactListObservable =  Observable.fromCallable() {
//           return@fromCallable mDataBase.contactsDao().getAllContacts()
//        }.flatMap { contacts ->
//            val result = ArrayList<Contact>()
//            val matchContactResponse = MatchContactResponse("", result)
//            contacts.forEach() {
//                result.add(
//                    Contact(
//                        "",
//                        fullName = it.getFirstCharOfName(),
//                        id = it.id,
//                        mobile = "" + it.phone_number
//                    )
//                )
//            }
//
//            Observable.just(matchContactResponse)
//        }

//        val remoteApi = apiInterface.matchContact(
//            accessToken = accessToken,
//            mobile = mobile
//        )

        val disposable: Disposable = apiInterface.matchContact(
            accessToken = accessToken,
            mobile = mobile
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onMatchContactSuccess(it) }, { onError(it) })

//        val disposable  = Observable.zip(remoteApi, contactListObservable,{ matchContactResponse: MatchContactResponse,
//                                                          list: List<ContactEntity> -> {
//                                                              
//        }
//
//        })

//        val disposable: Disposable =  contactListObservable.mergeWith(remoteApi)
//
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ onMatchContactSuccess(it) }, { onError(it) })

    }

    //Match Contact Api Success
    private fun onMatchContactSuccess(it: MatchContactResponse) {
        matchContactResponse.value = it
    }

    //Get File To Url Api
    fun getUrlApi(accessToken: String, image: MultipartBody.Part?) {
        val disposable: Disposable = apiInterface.getFileUrlApi(
            accessToken = accessToken,
            image = image
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onGetFileToUrlSuccess(it) }, { onError(it) })
    }

    //Get File To Url Api Success
    private fun onGetFileToUrlSuccess(it: GetFileToUrlResponse) {
        getFileToUrlResponse.value = it
    }

    //Mute/UnMute Status Api
    fun muteUnMuteStatusApi(userId: String, muteUserId: String) {
        val disposable: Disposable = apiInterface.muteUnMuteStatusApi(
            userId = userId,
            mute_userId = muteUserId
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onMuteUnMuteStatusSuccess(it) }, { onError(it) })
    }

    //Mute/UnMute Status Api Success
    private fun onMuteUnMuteStatusSuccess(it: MuteUnMuteStatusResponse) {
        muteUnMuteStatusResponse.value = it
    }

    //Mute/UnMute Api
    fun muteUnMuteApi(accessToken: String, key: String, userId: String, muteUserId: String) {
        val disposable: Disposable = apiInterface.muteUnMuteApi(
            access_token = accessToken,
            key = key,
            userId = userId,
            mute_userId = muteUserId
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onMuteUnMuteStatusSuccess(it) }, { onError(it) })
    }

    //Report User Api
    fun reportUserApi(
        reportSenderId: String,
        reportedUserId: String,
        reason: String,
        comment: String?
    ) {
        val disposable: Disposable = apiInterface.reportUserApi(
            reportSenderId = reportSenderId,
            reportedUserId = reportedUserId,
            reason = reason,
            comment = comment
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onReportUserSuccess(it) }, { onError(it) })
    }

    //Report User Api Success
    private fun onReportUserSuccess(it: ReportUserResponse) {
        reportUserResponse.value = it
    }

    //Chat Notification Api
    fun chatNotificationApi(accessToken: String, receiverId: String, message: String) {
        val disposable: Disposable = apiInterface.chatNotificationApi(
            accessToken = accessToken,
            friend_id = receiverId
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onChatNotificationSuccess(it) }, { onError(it) })
    }

    //Chat Notification Api Success
    private fun onChatNotificationSuccess(it: ChatNotificationResponse) {
        chatNotificationResponse.value = it
    }


    fun audiocallNotification(
        accessToken: String,
        receiverId: String,
        title: String,
        message: String,
        notificationType: String,
        callId: String,
        fullName: String
    ) {
        val disposable: Disposable = apiInterface.audioCallNotification(
            accessToken = accessToken,
            receiverId = receiverId,
            title = title,
            message = message,
            notificationType = notificationType,
            callId = callId,
            fullName = fullName
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ audiocallResponse.value = it }, { onError(it) })
    }


    //Video Call Notification Api
    fun videoCallNotificationApi(
        accessToken: String,
        receiverId: String,
        title: String,
        message: String,
        notificationType: String,
        callId: String,
        fullName: String
    ) {
        val disposable: Disposable = apiInterface.videoCallNotificationApi(
            accessToken = accessToken,
            receiverId = receiverId,
            title = title,
            message = message,
            notificationType = notificationType,
            callId = callId,
            fullName = fullName
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ videoCallResponse.value = it }, { onError(it) })
    }

    //Video Call Notification Api Success
    private fun onVideoCallNotificationSuccess(it: ChatNotificationResponse) {
        videoCallResponse.value = it
    }

    //On Error
    private fun onError(it: Throwable) {
        error.value = it
    }
}