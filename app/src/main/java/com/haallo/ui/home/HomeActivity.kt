package com.haallo.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.haallo.R
import com.haallo.base.BaseActivity
import com.haallo.base.extension.subscribeAndObserveOnMainThread
import com.haallo.base.extension.throttleClicks
import com.haallo.databinding.ActivityHomeBinding
import com.haallo.ui.home.view.HomePageViewPagerAdapter
import com.haallo.util.getCurrentTimeStamp

class HomeActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context): Intent {
            val intent = Intent(context, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            return intent
        }
    }

    private lateinit var binding: ActivityHomeBinding
//    private lateinit var tabManager: HomeTabManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        tabManager = HomeTabManager(this, savedInstanceState)

        listenToViewEvent(this)
    }

    private fun listenToViewEvent(context: Context) {
        binding.llChat.throttleClicks().subscribeAndObserveOnMainThread {
            binding.viewPager.setCurrentItem(0, true)
        }.autoDispose()

        binding.llStory.throttleClicks().subscribeAndObserveOnMainThread {
            binding.viewPager.setCurrentItem(1, true)
        }.autoDispose()

        binding.llCamera.throttleClicks().subscribeAndObserveOnMainThread {
            binding.viewPager.setCurrentItem(2, true)
        }.autoDispose()

        binding.llCall.throttleClicks().subscribeAndObserveOnMainThread {
            binding.viewPager.setCurrentItem(3, true)
        }.autoDispose()

        binding.llSetting.throttleClicks().subscribeAndObserveOnMainThread {
            binding.viewPager.setCurrentItem(4, true)
        }.autoDispose()

        val homePageViewPagerAdapter = HomePageViewPagerAdapter(this)
        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.adapter = homePageViewPagerAdapter
        binding.viewPager.offscreenPageLimit = 1
        binding.viewPager.currentItem = 0
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setSelectedTab(context, position)
            }
        })
    }

    private fun setSelectedTab(context: Context, mPos: Int) {
        binding.apply {
            ivChat.setImageResource(R.drawable.chat_icon_white)
            tvChat.setTextColor(ContextCompat.getColor(context, R.color.color_737373))

            ivStory.setImageResource(R.drawable.story)
            tvStory.setTextColor(ContextCompat.getColor(context, R.color.color_737373))

            ivCamera.setImageResource(R.drawable.camera_disable)
            tvCamera.setTextColor(ContextCompat.getColor(context, R.color.color_737373))

            ivCall.setImageResource(R.drawable.call_disable)
            tvCall.setTextColor(ContextCompat.getColor(context, R.color.color_737373))

            ivSetting.setImageResource(R.drawable.settings)
            tvSetting.setTextColor(ContextCompat.getColor(context, R.color.color_737373))

            when (mPos) {
                0 -> {
                    ivChat.setImageResource(R.drawable.chats_icon_blue)
                    tvChat.setTextColor(ContextCompat.getColor(context, R.color.dark_sky_blue))
                }
                1 -> {
                    ivStory.setImageResource(R.drawable.story_blue)
                    tvStory.setTextColor(ContextCompat.getColor(context, R.color.dark_sky_blue))
                }
                2 -> {
                    ivCamera.setImageResource(R.drawable.camera_full_blue)
                    tvCamera.setTextColor(ContextCompat.getColor(context, R.color.dark_sky_blue))
                }
                3 -> {
                    ivCall.setImageResource(R.drawable.calls_blue)
                    tvCall.setTextColor(ContextCompat.getColor(context, R.color.dark_sky_blue))
                }
                4 -> {
                    ivSetting.setImageResource(R.drawable.settings_a)
                    tvSetting.setTextColor(ContextCompat.getColor(context, R.color.dark_sky_blue))
                }
                else -> {
                    ivChat.setImageResource(R.drawable.chats_icon_blue)
                    tvChat.setTextColor(ContextCompat.getColor(context, R.color.dark_sky_blue))
                }
            }
        }
    }

    //Window Focus Listener
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            setMyOnlineStatus(true)
        } else {
            setMyOnlineStatus(false)
        }
        super.onWindowFocusChanged(hasFocus)
    }

    //Set My OnLine Status
    private fun setMyOnlineStatus(onlineState: Boolean) {
        val myUserId = "u_${sharedPreference.userId}"
        if (onlineState) {
            firebaseDbHandler.setLastSeenStatus(myUserId, "Online")
        } else {
            val currentTime = getCurrentTimeStamp()
            firebaseDbHandler.setLastSeenStatus(myUserId, currentTime.toString())
        }
    }

//    override fun onBackPressed() {
//        if (!tabManager.onBackPressed()) {
//            if (tabManager.activatedTab != HaalloTabBarView.TAB_CHAT) {
//                tabManager.selectTab(HaalloTabBarView.TAB_CHAT, true)
//                return
//            } else {
//                finish()
//            }
//        }
//    }
}