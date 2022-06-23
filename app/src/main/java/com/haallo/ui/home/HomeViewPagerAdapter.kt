package com.haallo.ui.home

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.haallo.ui.home.call.HomeCallFragmentOld
import com.haallo.ui.home.camera.HomeCameraFragmentOld
import com.haallo.ui.home.chat.HomeChatFragment
import com.haallo.ui.home.setting.HomeSettingFragment
import com.haallo.ui.home.status.HomeStatusFragmentOld

class HomeViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): androidx.fragment.app.Fragment {
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

            else -> {
                HomeSettingFragment()
            }
        }
    }

    override fun getCount(): Int {
        return 5
    }
}