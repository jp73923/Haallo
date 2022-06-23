package com.haallo.ui.home

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.haallo.ui.home.call.HomeCallFragmentOld
import com.haallo.ui.home.camera.HomeCameraFragmentOld
import com.haallo.ui.home.chat.HomeChatFragmentOld
import com.haallo.ui.home.setting.HomeSettingFragmentOld
import com.haallo.ui.home.status.HomeStatusFragmentOld

class HomeViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return when (position) {
            0 -> {
                HomeChatFragmentOld()
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
                HomeSettingFragmentOld()
            }
        }
    }

    override fun getCount(): Int {
        return 5
    }
}