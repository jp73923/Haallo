package com.haallo.ui.home.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.haallo.base.BaseActivity
import com.haallo.databinding.ActivitySettingNotificationBinding

class NotificationSettingActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, NotificationSettingActivity::class.java)
        }
    }

    private lateinit var binding: ActivitySettingNotificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}