package com.haallo.ui.home.setting

import android.content.Intent
import android.os.Bundle
import com.haallo.base.OldBaseActivity
import com.haallo.databinding.ActivityPrivacySettingsBinding

class PrivacySettings : OldBaseActivity() {

    private lateinit var binding: ActivityPrivacySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPrivacySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initControl()
    }

    override fun initView() {

    }

    override fun initControl() {
        binding.rlNotification.setOnClickListener {
            startActivity(Intent(this, NotificationSettingsActivityOld::class.java))
        }

    }
}