package com.haallo.ui.starredchat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.haallo.base.BaseActivity
import com.haallo.base.extension.subscribeAndObserveOnMainThread
import com.haallo.base.extension.throttleClicks
import com.haallo.databinding.ActivityStarredChatBinding

class StarredChatActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, StarredChatActivity::class.java)
        }
    }

    private lateinit var binding: ActivityStarredChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStarredChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listenToViewEvent()
    }

    private fun listenToViewEvent() {
        binding.ivBack.throttleClicks().subscribeAndObserveOnMainThread {
            onBackPressed()
        }.autoDispose()

        binding.ivSearch.throttleClicks().subscribeAndObserveOnMainThread {

        }.autoDispose()

        binding.ivMore.throttleClicks().subscribeAndObserveOnMainThread {

        }.autoDispose()
    }

}