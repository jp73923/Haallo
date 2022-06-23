package com.haallo.api.coroutine

import com.haallo.ui.chat.newChat.MatchContactResponse
import com.haallo.ui.chat.response.ChatNotificationResponse
import com.haallo.ui.chat.response.GetFileToUrlResponse
import com.haallo.ui.chat.response.MuteUnMuteStatusResponse
import com.haallo.ui.chat.response.ReportUserResponse
import com.haallo.constant.NetworkConstants
import com.haallo.ui.home.setting.LogoutResponse
import com.haallo.ui.splashToHome.forgotAndResetPassword.ForgotPasswordResponse
import com.haallo.ui.splashToHome.forgotAndResetPassword.ResetPasswordResponse
import com.haallo.ui.splashToHome.otp.ResendOtpResponse
import com.haallo.ui.splashToHome.otp.OtpVerifyResponse
import com.haallo.ui.splashToHome.profile.CreateProfileResponse
import com.haallo.ui.splashToHome.registration.RegistrationResponse
import com.haallo.ui.splashToHome.signIn.SignInResponse
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiInterface {

    //Registration Api
    @FormUrlEncoded
    @POST(NetworkConstants.REGISTER)
    fun registration(
        @Field("country_code") country_code: String,
        @Field("mobile") mobile: String,
        @Field("password") password: String,
        @Field("device_token") device_token: String
    ): Observable<RegistrationResponse>

    //Resend Api
    @FormUrlEncoded
    @POST(NetworkConstants.RESEND_OTP)
    fun resendOtp(
        @Header("accessToken") accessToken: String,
        @Field("mobile") mobile: String
    ): Observable<ResendOtpResponse>

    //Verify OTP
    @FormUrlEncoded
    @POST(NetworkConstants.VERIFY_OTP)
    fun verifyOtp(
        @Header("accessToken") accessToken: String,
        @Field("mobile") mobile: String,
        @Field("otp") otp: String
    ): Observable<OtpVerifyResponse>

    //Create Profile
    @Multipart
    @POST(NetworkConstants.PROFILE_CREATION)
    fun createProfile(
        @Header("accessToken") accessToken: String,
        @Part("user_name") user_name: RequestBody,
        @Part("mobile") mobile: RequestBody,
        @Part("address") address: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part image: MultipartBody.Part? = null
    ): Observable<CreateProfileResponse>

    //Login Api
    @FormUrlEncoded
    @POST(NetworkConstants.LOGIN)
    fun signIn(
        @Field("mobile") mobile: String,
        @Field("password") password: String,
        @Field("device_token") device_token: String,
        @Field("device_type") device_type: String = "1"
    ): Observable<SignInResponse>

    //Forgot Password Api
    @FormUrlEncoded
    @POST(NetworkConstants.FORGOT_PASSWORD)
    fun forgotPassword(
        @Field("mobile") mobile: String
    ): Observable<ForgotPasswordResponse>

    //Login Api
    @FormUrlEncoded
    @POST(NetworkConstants.RESET_PASSWORD)
    fun resetPassword(
        @Field("mobile") mobile: String,
        @Field("password") password: String
    ): Observable<ResetPasswordResponse>

    //Logout Api
    @GET(NetworkConstants.LOGOUT)
    fun logout(
        @Header("accessToken") accessToken: String
    ): Observable<LogoutResponse>

    //Match Contact Api
    @FormUrlEncoded
    @POST(NetworkConstants.MATCH_CONTACT)
    fun matchContact(
        @Header("accessToken") accessToken: String,
        @Field("mobile[]") mobile: List<String>
    ): Observable<MatchContactResponse>

    //Get File To Url
    @Multipart
    @POST(NetworkConstants.GET_FILE_URL)
    fun getFileUrlApi(
        @Header("accessToken") accessToken: String,
        @Part image: MultipartBody.Part? = null
    ): Observable<GetFileToUrlResponse>

    //Chat Notification
    @FormUrlEncoded
    @POST(NetworkConstants.CHAT_NOTIFICATION)
    fun chatNotificationApi(
        @Header("accessToken") accessToken: String,
        @Field("friend_id") friend_id: String
    ): Observable<ChatNotificationResponse>

    //Mute/UnMute Status Api
    @FormUrlEncoded
    @PUT(NetworkConstants.GET_MUTE_UN_MUTE_STATUS)
    fun muteUnMuteStatusApi(
        @Field("userId") userId: String,
        @Field("mute_userId") mute_userId: String
    ): Observable<MuteUnMuteStatusResponse>

    //Mute UnMute
    @FormUrlEncoded
    @POST(NetworkConstants.MUTE_UN_MUTE)
    fun muteUnMuteApi(
        @Header("access_token") access_token: String,
        @Field("key") key: String,
        @Field("userId") userId: String,
        @Field("mute_userId") mute_userId: String
    ): Observable<MuteUnMuteStatusResponse>

    //Report User
    @FormUrlEncoded
    @POST(NetworkConstants.REPORT_USER)
    fun reportUserApi(
        @Field("reportSenderId") reportSenderId: String,
        @Field("reportedUserId") reportedUserId: String,
        @Field("reason") reason: String,
        @Field("comment") comment: String?
    ): Observable<ReportUserResponse>

    //Video Call Notification
    @FormUrlEncoded
    @POST(NetworkConstants.VIDEO_CALL_NOTIFICATION)
    fun videoCallNotificationApi(
        @Header("accessToken") accessToken: String,
        @Field("send_to_id") receiverId: String,
        @Field("title") title: String,
        @Field("message") message: String,
        @Field("notification_type") notificationType: String,
        @Field("call_id") callId: String,
        @Field("full_name") fullName: String,
    ): Observable<ChatNotificationResponse>



    @FormUrlEncoded
    @POST(NetworkConstants.VIDEO_CALL_NOTIFICATION)
    fun audioCallNotification(
        @Header("accessToken") accessToken: String,
        @Field("send_to_id") receiverId: String,
        @Field("title") title: String,
        @Field("message") message: String,
        @Field("notification_type") notificationType: String,
        @Field("call_id") callId: String,
        @Field("full_name") fullName: String,
    ): Observable<ChatNotificationResponse>

    @FormUrlEncoded
    @POST(NetworkConstants.API_SUPPORT)
    fun support(
        @Header("accessToken") accessToken: String,
        @Field("user_id") user_id: String,
        @Field("report") report: String,
    ): Observable<ChatNotificationResponse>




    /*//Get Group List Response
    @GET(NetworkConstants.GET_GROUP_LIST)
    fun getGroupList(
        @Header("accessToken") accessToken: String
    ): Observable<GroupListResponse>

    //Create Group
    @Multipart
    @POST(NetworkConstants.CREATE_GROUP)
    fun createGroup(
        @Header("accessToken") accessToken: String,
        @Part("mobile") mobile: RequestBody,
        @Part("group_name") group_name: RequestBody,
        @Part("member[]") member: ArrayList<Long>,
        @Part image: MultipartBody.Part? = null
    ): Observable<CreateGroupResponse>

    //Match Contact List
    @FormUrlEncoded
    @POST(NetworkConstants.SEND_CALL_NOTIFICATION)
    fun sendCallNotification(
        @Header("accessToken") accessToken: String,
        @Field("send_to_id") send_to_id: String,
        @Field("title") title: String,
        @Field("notification_type") notification_type: String,
        @Field("call_id") call_id: String,
        @Field("message") message: String,
        @Field("full_name") full_name: String,
        @Field("profile_pic") profile_pic: String?
    ): Observable<NotificationRes>

    @FormUrlEncoded
    @POST(NetworkConstants.SEND_NOTIFICATION_TOPIC)
    fun sendNotificationTopic(
        @Header("accessToken") accessToken: String,
        @Field("send_to_id") send_to_id: String,
        @Field("title") title: String,
        @Field("notification_type") notification_type: String,
        @Field("call_id") call_id: String,
        @Field("message") message: String,
        @Field("full_name") full_name: String,
        @Field("profile_pic") profile_pic: String,
        @Field("user_id[]") user_id: List<String>
    ): Observable<RegistrationRes>

    //Update Profile
    @Multipart
    @POST(NetworkConstants.EDIT_PROFILE)
    fun editProfile(
        @Header("accessToken") accessToken: String,
        @Part("name") name: RequestBody? = null,
        @Part("about") about: RequestBody? = null,
        @Part image: MultipartBody.Part? = null
    ): Observable<EditProfileResponse>

    //Remove Account Api
    @FormUrlEncoded
    @POST(NetworkConstants.REMOVE_ACCOUNT)
    fun removeAccount(
        @Header("accessToken") accessToken: String,
        @Field("user_id") user_id: Int,
        @Field("group_id[]") group_id: List<String>?
    ): Observable<RemoveAccountResponse>

    //Get Help Data
    @GET(NetworkConstants.HELP)
    fun getHelp(
        @Header("accessToken") accessToken: String
    ): Observable<HelpResponse>

    //Save Wallpaper
    @Multipart
    @POST(NetworkConstants.SAVE_WALLPAPER)
    fun saveWallpaper(
        @Header("accessToken") accessToken: String,
        @Part image: MultipartBody.Part? = null
    ): Observable<WallpaperResponse>*/
}