package com.haallo.ui.home.chat.view

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.haallo.ui.home.chat.archived.ArchivedFragment
import com.haallo.ui.home.chat.broadcast.BroadCastFragment
import com.haallo.ui.home.chat.starred.StarredMessageFragment

class ChatViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                BroadCastFragment.newInstance()
            }
            1 -> {
                StarredMessageFragment.newInstance()
            }
            2 -> {
                ArchivedFragment.newInstance()
            }
            else -> {
                BroadCastFragment.newInstance()
            }
        }
    }
}