package com.haallo.ui.privacysetting.profilephoto

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.haallo.base.BaseActivity
import com.haallo.base.extension.subscribeAndObserveOnMainThread
import com.haallo.base.extension.throttleClicks
import com.haallo.databinding.ActivitySettingProfilePhotoBinding

class ProfilePhotoSettingActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, ProfilePhotoSettingActivity::class.java)
        }
    }

    private lateinit var binding: ActivitySettingProfilePhotoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingProfilePhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listenToViewEvent()
    }

    private fun listenToViewEvent() {
        binding.ivBack.throttleClicks().subscribeAndObserveOnMainThread {
            onBackPressed()
        }.autoDispose()
    }
}