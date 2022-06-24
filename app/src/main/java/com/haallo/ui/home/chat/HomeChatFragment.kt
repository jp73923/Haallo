package com.haallo.ui.home.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.haallo.R
import com.haallo.base.BaseFragment
import com.haallo.base.extension.subscribeAndObserveOnMainThread
import com.haallo.base.extension.throttleClicks
import com.haallo.databinding.FragmentHomeChatBinding
import com.haallo.ui.home.chat.view.ChatViewPagerAdapter

class HomeChatFragment : BaseFragment() {

    companion object {
        @JvmStatic
        fun newInstance() = HomeChatFragment()
    }

    private var _binding: FragmentHomeChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenToViewEvent()
    }

    private fun listenToViewEvent() {
        binding.ivSearch.throttleClicks().subscribeAndObserveOnMainThread {

        }.autoDispose()

        val followViewPagerAdapter = ChatViewPagerAdapter(this)
        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.offscreenPageLimit = 1
        binding.viewPager.adapter = followViewPagerAdapter
        binding.viewPager.currentItem = 0
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.label_broadcast)
                }
                1 -> {
                    tab.text = getString(R.string.label_starred)
                }
                2 -> {
                    tab.text = getString(R.string.label_archived)
                }
            }
        }.attach()
    }
}