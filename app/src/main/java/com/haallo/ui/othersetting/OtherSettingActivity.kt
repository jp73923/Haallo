package com.haallo.ui.othersetting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.haallo.base.BaseActivity
import com.haallo.base.extension.startActivityWithDefaultAnimation
import com.haallo.base.extension.subscribeAndObserveOnMainThread
import com.haallo.base.extension.throttleClicks
import com.haallo.databinding.ActivitySettingOtherBinding
import com.haallo.ui.othersetting.allcontact.AllContactSettingActivity
import com.haallo.ui.othersetting.help.HelpActivity
import com.haallo.ui.othersetting.notification.NotificationSettingActivity
import com.haallo.ui.othersetting.storyprivacy.StoryPrivacyActivity
import com.haallo.ui.othersetting.wallpapersetting.WallpaperSettingActivity

class OtherSettingActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, OtherSettingActivity::class.java)
        }
    }

    private lateinit var binding: ActivitySettingOtherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingOtherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listenToViewEvent()
    }

    private fun listenToViewEvent() {
        binding.ivBack.throttleClicks().subscribeAndObserveOnMainThread {
            onBackPressed()
        }.autoDispose()

        binding.tvStoryPrivacy.throttleClicks().subscribeAndObserveOnMainThread {
            startActivityWithDefaultAnimation(StoryPrivacyActivity.getIntent(this))
        }.autoDispose()

        binding.tvNotifications.throttleClicks().subscribeAndObserveOnMainThread {
            startActivityWithDefaultAnimation(NotificationSettingActivity.getIntent(this))
        }.autoDispose()

        binding.tvAllContacts.throttleClicks().subscribeAndObserveOnMainThread {
            startActivityWithDefaultAnimation(AllContactSettingActivity.getIntent(this))
        }.autoDispose()

        binding.tvWallpaper.throttleClicks().subscribeAndObserveOnMainThread {
            startActivityWithDefaultAnimation(WallpaperSettingActivity.getIntent(this))
        }.autoDispose()

        binding.tvHelp.throttleClicks().subscribeAndObserveOnMainThread {
            startActivityWithDefaultAnimation(HelpActivity.getIntent(this))
        }.autoDispose()
    }
}