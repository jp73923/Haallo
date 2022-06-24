package com.haallo.ui.home.view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.haallo.ui.home.call.HomeCallFragment
import com.haallo.ui.home.camera.HomeCameraFragment
import com.haallo.ui.home.chat.HomeChatFragment
import com.haallo.ui.home.setting.HomeSettingFragment
import com.haallo.ui.home.status.HomeStatusFragment

class HomePageViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount() = 5

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                HomeChatFragment.newInstance()
            }
            1 -> {
                HomeStatusFragment.newInstance()
            }
            2 -> {
                HomeCameraFragment.newInstance()
            }
            3 -> {
                HomeCallFragment.newInstance()
            }
            4 -> {
                HomeSettingFragment.newInstance()
            }
            else -> {
                HomeChatFragment.newInstance()
            }
        }
    }
}