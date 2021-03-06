package com.haallo.ui.agoraGroupVideo.openvcall.model;

public interface AGEventHandler {
    void onUserOffline(int uid, int reason);
    void onJoinChannelSuccess(String channel, int uid, int elapsed);
    void onExtraCallback(int type, Object... data);

    int EVENT_TYPE_ON_DATA_CHANNEL_MSG = 3;

    int EVENT_TYPE_ON_USER_VIDEO_MUTED = 6;

    int EVENT_TYPE_ON_USER_AUDIO_MUTED = 7;

    int EVENT_TYPE_ON_SPEAKER_STATS = 8;

    int EVENT_TYPE_ON_AGORA_MEDIA_ERROR = 9;

    int EVENT_TYPE_ON_USER_VIDEO_STATS = 10;

    int EVENT_TYPE_ON_APP_ERROR = 13;

    int EVENT_TYPE_ON_AUDIO_QUALITY = 11;

    int EVENT_TYPE_ON_AUDIO_ROUTE_CHANGED = 18;





}
