package com.haallo.ui.privacysetting.blockedcontact

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.haallo.base.BaseActivity
import com.haallo.base.extension.subscribeAndObserveOnMainThread
import com.haallo.base.extension.throttleClicks
import com.haallo.databinding.ActivitySettingBlockedContactBinding

class BlockedContactSettingActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, BlockedContactSettingActivity::class.java)
        }
    }

    private lateinit var binding: ActivitySettingBlockedContactBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingBlockedContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listenToViewEvent()
    }

    private fun listenToViewEvent() {
        binding.ivBack.throttleClicks().subscribeAndObserveOnMainThread {
            onBackPressed()
        }.autoDispose()
    }
}