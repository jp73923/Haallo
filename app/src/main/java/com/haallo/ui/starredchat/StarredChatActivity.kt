package com.haallo.ui.starredchat

import android.os.Bundle
import com.haallo.base.BaseActivity
import com.haallo.base.extension.subscribeAndObserveOnMainThread
import com.haallo.base.extension.throttleClicks
import com.haallo.databinding.ActivityStarredChatBinding

class StarredChatActivity : BaseActivity() {

    private lateinit var binding: ActivityStarredChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStarredChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listenToViewEvent()
    }

    private fun listenToViewEvent() {
        binding.ivBack.throttleClicks().subscribeAndObserveOnMainThread {

        }.autoDispose()

        binding.ivSearch.throttleClicks().subscribeAndObserveOnMainThread {

        }.autoDispose()

        binding.ivMore.throttleClicks().subscribeAndObserveOnMainThread {

        }.autoDispose()
    }

}