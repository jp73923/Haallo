package com.haallo.ui.home.view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.haallo.ui.home.call.HomeCallFragmentOld
import com.haallo.ui.home.camera.HomeCameraFragmentOld
import com.haallo.ui.home.chat.HomeChatFragment
import com.haallo.ui.home.setting.HomeSettingFragment
import com.haallo.ui.home.status.HomeStatusFragmentOld

class HomePageViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount() = 5

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                HomeChatFragment()
            }
            1 -> {
                HomeStatusFragmentOld()
            }
            2 -> {
                HomeCameraFragmentOld()
            }
            3 -> {
                HomeCallFragmentOld()
            }
            4 -> {
                HomeSettingFragment()
            }
            else -> {
                HomeChatFragment()
            }
        }
    }
}