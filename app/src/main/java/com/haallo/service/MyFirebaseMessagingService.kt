package com.haallo.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.haallo.R
import com.haallo.ui.agoraGroupVideo.GroupAudioCallAcceptRejectActivity
import com.haallo.ui.agoraGroupVideo.GroupVideoCallAcceptRejectActivity
import com.haallo.ui.call.AudioCallAcceptRejectActivity
import com.haallo.ui.call.EnumUtils
import com.haallo.ui.call.VideoCallAcceptRejectActivityOld
import com.haallo.ui.chat.activity.ChatActivity
import com.haallo.constant.IntentConstant
import java.lang.ref.WeakReference

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val contextRef: WeakReference<Context> = WeakReference(this)

    override fun onNewToken(token: String) {

    }

    override fun onMessageReceived(p0: RemoteMessage) {

        if (p0 != null) {


            if (p0.notification != null) {
                val title = p0.notification!!.title
                val body = p0.notification!!.body
                sendNotificationData(contextRef.get(), body, title, p0)


            } else {
                sendNotificationData(contextRef.get(), "body", "title")
            }

        }
    }

    private fun sendNotificationData(
        context: Context?,
        body: String?,
        title: String?,
        p0: RemoteMessage? = null
    ) {
        var intent: Intent? = null
        if (p0 == null) {
            intent = Intent(context, ChatActivity::class.java)
        } else if (p0.data["notification_type"] == EnumUtils.NotificationType.AUDIO_CALL_INCOMING.value) {


            intent = Intent(this, AudioCallAcceptRejectActivity::class.java)
                .putExtra(IntentConstant.OTHER_USER_NAME, p0.data["full_name"])
                .putExtra(IntentConstant.CHANEL_CALL_ID, p0.data["call_id"])
                .putExtra(IntentConstant.IS_REAR_CAMERA, false)
                .putExtra(IntentConstant.IS_MUTE_VIDEO, false)
                .putExtra(IntentConstant.IS_PAUSED_VIDEO, false)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

        } else if (p0.data["notification_type"] == EnumUtils.NotificationType.VIDEO_CALL_INCOMING.value) {
            intent = Intent(this, VideoCallAcceptRejectActivityOld::class.java)
                .putExtra(IntentConstant.OTHER_USER_NAME, p0.data["full_name"])
                .putExtra(IntentConstant.CHANEL_CALL_ID, p0.data["call_id"])
                .putExtra(IntentConstant.IS_REAR_CAMERA, false)
                .putExtra(IntentConstant.IS_MUTE_VIDEO, false)
                .putExtra(IntentConstant.IS_PAUSED_VIDEO, false)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

        } else if (p0.data["notification_type"] == EnumUtils.NotificationType.GROUP_AUDIO_CALL_INCOMING.value) {


            intent = Intent(this, GroupAudioCallAcceptRejectActivity::class.java)
                .putExtra(IntentConstant.OTHER_USER_NAME, p0.data["full_name"])
                .putExtra(IntentConstant.CHANEL_CALL_ID, p0.data["call_id"])
                .putExtra(IntentConstant.IS_REAR_CAMERA, false)
                .putExtra(IntentConstant.IS_MUTE_VIDEO, false)
                .putExtra(IntentConstant.IS_PAUSED_VIDEO, false)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

        } else if (p0.data["notification_type"] == EnumUtils.NotificationType.GROUP_VIDEO_CALL_INCOMING.value) {


            intent = Intent(this, GroupVideoCallAcceptRejectActivity::class.java)
                .putExtra(IntentConstant.OTHER_USER_NAME, p0.data["full_name"])
                .putExtra(IntentConstant.CHANEL_CALL_ID, p0.data["call_id"])
                .putExtra(IntentConstant.IS_REAR_CAMERA, false)
                .putExtra(IntentConstant.IS_MUTE_VIDEO, false)
                .putExtra(IntentConstant.IS_PAUSED_VIDEO, false)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

        }


        /*else {
            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT
            )
        }

        val channelId = "notification"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(context!!, channelId)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())*/
        val intentMove = Intent(this, AudioCallAcceptRejectActivity::class.java)

        intentMove?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intentMove,
            PendingIntent.FLAG_ONE_SHOT
        )
        val mNotification = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val chanelId = System.currentTimeMillis().toInt()
        val sound = Uri.parse("android.resource://" + packageName + "/" + R.raw.samsung_original)
        val notificationBuilder = NotificationCompat.Builder(this, chanelId.toString())
        notificationBuilder
            .setContentTitle(getString(R.string.app_name))
            .setSmallIcon(R.drawable.logo) // TOdO fixed
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_VIBRATE or Notification.DEFAULT_LIGHTS)
            .setSound(sound)
            .setContentIntent(pendingIntent)
            .setColor(ContextCompat.getColor(applicationContext, R.color.colorAccent))
            .setStyle(
                NotificationCompat.BigTextStyle().setBigContentTitle(title).bigText(body)
            ).priority = Notification.PRIORITY_MAX

        val mChannel: NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val imp = NotificationManager.IMPORTANCE_HIGH
            mChannel = NotificationChannel(
                chanelId.toString(), getString(R.string.app_name), imp
            )
            mChannel.description = body
            mChannel.importance = NotificationManager.IMPORTANCE_HIGH
            mChannel.lightColor = Color.CYAN
            mChannel.canShowBadge()
            mChannel.setShowBadge(true)
            mNotification.createNotificationChannel(mChannel)
        }
        mNotification.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())

    }


}