package com.haallo.ui.privacysetting.lastseen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.haallo.base.BaseActivity
import com.haallo.base.extension.subscribeAndObserveOnMainThread
import com.haallo.base.extension.throttleClicks
import com.haallo.databinding.ActivitySettingLastSeenBinding

class LastSeenSettingActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, LastSeenSettingActivity::class.java)
        }
    }

    private lateinit var binding: ActivitySettingLastSeenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingLastSeenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listenToViewEvent()
    }

    private fun listenToViewEvent() {
        binding.ivBack.throttleClicks().subscribeAndObserveOnMainThread {
            onBackPressed()
        }.autoDispose()
    }
}