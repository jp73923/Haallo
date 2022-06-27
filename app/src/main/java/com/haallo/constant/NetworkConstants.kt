package com.haallo.constant

class NetworkConstants {
    companion object {
        const val BASE_URL = "http://3.131.36.176/"

        const val SIGN_IN = "api/login"
        const val SIGN_UP = "api/register"

        const val VERIFY_OTP = "api/verifyOtp"
        const val RESEND_OTP = "api/reSendOtp"

        const val CREATE_PROFILE = "api/createProfile"
        const val UPDATE_PROFILE = "api/updateProfile"

        const val FORGOT_PASSWORD = "api/forgotPassword"
        const val RESET_PASSWORD = "api/resetPassword"

        const val LOGOUT = "api/logout"

        //Chat
        const val GET_FILE_URL = "api/fileUpload"
        const val GET_MUTE_UN_MUTE_STATUS = ""
        const val MUTE_UN_MUTE = ""
        const val REPORT_USER = ""
        const val MATCH_CONTACT = "api/checkContact"
        const val CHAT_NOTIFICATION = "sendMessage"
        const val VIDEO_CALL_NOTIFICATION = "api/callingRequest"

        const val REMOVE_ACCOUNT = "api/removeAccount"
        const val update_backgound_image = "api/update_backgound_image"
        const val muteNotification = "api/muteNotification"
        const val NOTIFICATION_LIST = "user/notification-list"
        const val API_SUPPORT = "api/support"
    }
}