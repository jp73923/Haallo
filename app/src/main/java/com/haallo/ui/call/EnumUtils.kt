package com.haallo.ui.call

class EnumUtils {
    enum class NotificationType(val value: String) {
        VIDEO_CALL_INCOMING("VIDEO_CALL_INCOMING"),
        AUDIO_CALL_INCOMING("AUDIO_CALL_INCOMING"),
        GROUP_VIDEO_CALL_INCOMING("GROUP_VIDEO_CALL_INCOMING"),
        GROUP_AUDIO_CALL_INCOMING("GROUP_AUDIO_CALL_INCOMING"),
        VIDEO_CALL_DISCONNECT("VIDEO_CALL_DISCONNECT"),
        VIDEO_CALL_REJECT("VIDEO_CALL_REJECT"),
        VIDEO_CALL_ACCEPT("VIDEO_CALL_ACCEPT"),
        AUDIO_CALL_DISCONNECT("AUDIO_CALL_DISCONNECT"),
        AUDIO_CALL_REJECT("AUDIO_CALL_REJECT"),
        AUDIO_CALL_ACCEPT("AUDIO_CALL_ACCEPT")
    }

    enum class CallState(val value: String) {
        CONNECTING("0"),
        RINGING("1"),
        CONNECTED("2"),
        REJECTED("3"),
        DISCONNECTED("4"),
        NOT_ANSWERED("5"),
        BUSY("6")
    }

    enum class CallStatus(val value: String) {
        INCOMING("0"),
        OUTGOING("1")
    }

    enum class CallType(val value: String) {
        ONE_TO_ONE_VIDEO_CALL("1"),
        ONE_TO_ONE_AUDIO_CALL("0"),

    }
}