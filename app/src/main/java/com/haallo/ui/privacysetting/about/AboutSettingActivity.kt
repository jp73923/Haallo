package com.haallo.ui.privacysetting.about

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.haallo.base.BaseActivity
import com.haallo.base.extension.subscribeAndObserveOnMainThread
import com.haallo.base.extension.throttleClicks
import com.haallo.databinding.ActivitySettingAboutBinding

class AboutSettingActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, AboutSettingActivity::class.java)
        }
    }

    private lateinit var binding: ActivitySettingAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listenToViewEvent()
    }

    private fun listenToViewEvent() {
        binding.ivBack.throttleClicks().subscribeAndObserveOnMainThread {
            onBackPressed()
        }.autoDispose()
    }
}