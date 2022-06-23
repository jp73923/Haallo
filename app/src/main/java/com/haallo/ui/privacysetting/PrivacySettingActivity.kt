package com.haallo.ui.privacysetting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.haallo.base.BaseActivity
import com.haallo.base.extension.startActivityWithDefaultAnimation
import com.haallo.base.extension.subscribeAndObserveOnMainThread
import com.haallo.base.extension.throttleClicks
import com.haallo.databinding.ActivitySettingPrivacyBinding
import com.haallo.ui.privacysetting.about.AboutSettingActivity
import com.haallo.ui.privacysetting.blockedcontact.BlockedContactSettingActivity
import com.haallo.ui.privacysetting.lastseen.LastSeenSettingActivity
import com.haallo.ui.privacysetting.profilephoto.ProfilePhotoSettingActivity

class PrivacySettingActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, PrivacySettingActivity::class.java)
        }
    }

    private lateinit var binding: ActivitySettingPrivacyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingPrivacyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listenToViewEvent()
    }

    private fun listenToViewEvent() {
        binding.ivBack.throttleClicks().subscribeAndObserveOnMainThread {
            onBackPressed()
        }.autoDispose()

        binding.tvLastSeen.throttleClicks().subscribeAndObserveOnMainThread {
            startActivityWithDefaultAnimation(LastSeenSettingActivity.getIntent(this))
        }.autoDispose()

        binding.tvProfilePhoto.throttleClicks().subscribeAndObserveOnMainThread {
            startActivityWithDefaultAnimation(ProfilePhotoSettingActivity.getIntent(this))
        }.autoDispose()

        binding.tvBlockedContacts.throttleClicks().subscribeAndObserveOnMainThread {
            startActivityWithDefaultAnimation(BlockedContactSettingActivity.getIntent(this))
        }.autoDispose()

        binding.tvAbout.throttleClicks().subscribeAndObserveOnMainThread {
            startActivityWithDefaultAnimation(AboutSettingActivity.getIntent(this))
        }.autoDispose()
    }
}