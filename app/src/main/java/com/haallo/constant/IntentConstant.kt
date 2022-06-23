package com.haallo.constant

interface IntentConstant {
    companion object {
        //Splash to Home
        const val IS_FROM = "isFrom"
        const val FORGOT_PASSWORD = "forgotPassword"
        const val REGISTRATION = "registration"
        const val SIGN_IN = "signIn"

        //Chat
        const val LEAVED = "leaved"
        const val OTHER_USER_ID = "otherUserId"
        const val OTHER_USER_NAME = "otherUserName"
        const val OTHER_USER_IMAGE = "otherUserImage"
        const val CHANEL_CALL_ID = "channelCallId"
        const val OTHER_USER_MOBILE = "otherUserMobile"
        const val BLOCK_USER = "blockUser"
        const val UN_BLOCK_USER = "unblockUser"
        const val DELETE_CHAT = "deleteChat"
        const val CHANEL_NAME = "chanelName"
        const val CALL_STATUS = "callStatus"
        const val IS_MUTE_VIDEO = "isMuteVideo"
        const val IS_MUTE_AUDIO = "isMuteAudio"
        const val IS_PAUSED_VIDEO = "isPausedVideo"
        const val GROUP_ID = "GROUPID"
        const val IS_REAR_CAMERA = "isRearCamera"
        const val PIC = "pic"
        const val VIDEO_URL = "videoUrl"
        const val DEACTIVATE_ACCOUNT = "deactivateAccount"
        const val LOGOUT = "logout"
    }
}