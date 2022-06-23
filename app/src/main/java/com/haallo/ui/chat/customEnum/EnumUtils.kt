package com.haallo.ui.chat.customEnum

object EnumUtils {
    enum class EnumChatMessage(val viewType: Int) {
        MessageSender(1),
        MessageReceiver(2),
        ImageSender(3),
        ImageReceiver(4),
        VideoSender(5),
        VideoReceiver(6),
        AudioSender(7),
        AudioReceiver(8),
        PdfSender(9),
        PdfReceiver(10),
        ContactSender(11),
        ContactReceiver(12),
        EmojiSender(13),
        EmojiReceiver(14),
        ScreenShot(15)
    }
}