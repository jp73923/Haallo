package com.haallo.ui.home

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.firebase.FirebaseApp
import com.haallo.R
import com.haallo.base.OldBaseActivity
import com.haallo.databinding.ActivityHomeBinding
import com.haallo.ui.home.call.HomeCallFragmentOld
import com.haallo.ui.home.camera.HomeCameraFragmentOld
import com.haallo.ui.home.chat.HomeChatFragmentOld
import com.haallo.ui.home.setting.HomeSettingFragmentOld
import com.haallo.ui.home.status.HomeStatusFragmentOld
import com.haallo.util.getCurrentTimeStamp
import com.haallo.util.showLog

class HomeActivityOld : OldBaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityHomeBinding

    private val fragment: ArrayList<Fragment> = ArrayList()
    private var homeViewPagerAdapter: HomeViewPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initControl()
    }

    //All Ui Changes From Here
    override fun initView() {
        showLog(sharedPreference.accessToken)
        FirebaseApp.initializeApp(this)
        addFragment()
        homeViewPagerAdapter(this)
        chatSelect(this)
    }

    //Add Fragment
    private fun addFragment() {
        fragment.add(HomeChatFragmentOld())
        fragment.add(HomeStatusFragmentOld())
        fragment.add(HomeCameraFragmentOld())
        fragment.add(HomeCallFragmentOld())
        fragment.add(HomeSettingFragmentOld())
    }

    //Home ViewPager Adapter
    private fun homeViewPagerAdapter(context: Context) {
        homeViewPagerAdapter = HomeViewPagerAdapter(supportFragmentManager)
        binding.homeViewPager.adapter = homeViewPagerAdapter
        binding.homeViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        chatSelect(context)
                    }
                    1 -> {
                        storySelect(context)
                    }
                    2 -> {
                        cameraSelect(context)
                    }
                    3 -> {
                        callSelect(context)
                    }
                    4 -> {
                        settingSelect(context)
                    }
                }
            }
        })
    }

    //Chat Select
    private fun chatSelect(context: Context) {
        binding.apply {
            ivChat.setImageResource(R.drawable.chats_icon_blue)
            ivStory.setImageResource(R.drawable.story)
            ivCamera.setImageResource(R.drawable.camera_disable)
            ivCall.setImageResource(R.drawable.call_disable)
            ivSetting.setImageResource(R.drawable.settings)
            tvChat.setTextColor(ContextCompat.getColor(context, R.color.dark_sky_blue))
            tvStory.setTextColor(ContextCompat.getColor(context, R.color.color_737373))
            tvCamera.setTextColor(ContextCompat.getColor(context, R.color.color_737373))
            tvCall.setTextColor(ContextCompat.getColor(context, R.color.color_737373))
            tvSetting.setTextColor(ContextCompat.getColor(context, R.color.color_737373))
        }
    }

    //Story Select
    private fun storySelect(context: Context) {
        binding.apply {
            ivChat.setImageResource(R.drawable.chat_icon_white)
            ivStory.setImageResource(R.drawable.story_blue)
            ivCamera.setImageResource(R.drawable.camera_disable)
            ivCall.setImageResource(R.drawable.call_disable)
            ivSetting.setImageResource(R.drawable.settings)
            tvChat.setTextColor(ContextCompat.getColor(context, R.color.color_737373))
            tvStory.setTextColor(ContextCompat.getColor(context, R.color.dark_sky_blue))
            tvCamera.setTextColor(ContextCompat.getColor(context, R.color.color_737373))
            tvCall.setTextColor(ContextCompat.getColor(context, R.color.color_737373))
            tvSetting.setTextColor(ContextCompat.getColor(context, R.color.color_737373))
        }
    }

    //Camera Select
    private fun cameraSelect(context: Context) {
        binding.apply {
            ivChat.setImageResource(R.drawable.chat_icon_white)
            ivStory.setImageResource(R.drawable.story)
            ivCamera.setImageResource(R.drawable.camera_full_blue)
            ivCall.setImageResource(R.drawable.call_disable)
            ivSetting.setImageResource(R.drawable.settings)
            tvChat.setTextColor(ContextCompat.getColor(context, R.color.color_737373))
            tvStory.setTextColor(ContextCompat.getColor(context, R.color.color_737373))
            tvCamera.setTextColor(ContextCompat.getColor(context, R.color.dark_sky_blue))
            tvCall.setTextColor(ContextCompat.getColor(context, R.color.color_737373))
            tvSetting.setTextColor(ContextCompat.getColor(context, R.color.color_737373))
        }
    }

    //Call Select
    private fun callSelect(context: Context) {
        binding.apply {
            ivChat.setImageResource(R.drawable.chat_icon_white)
            ivStory.setImageResource(R.drawable.story)
            ivCamera.setImageResource(R.drawable.camera_disable)
            ivCall.setImageResource(R.drawable.calls_blue)
            ivSetting.setImageResource(R.drawable.settings)
            tvChat.setTextColor(ContextCompat.getColor(context, R.color.color_737373))
            tvStory.setTextColor(ContextCompat.getColor(context, R.color.color_737373))
            tvCamera.setTextColor(ContextCompat.getColor(context, R.color.color_737373))
            tvCall.setTextColor(ContextCompat.getColor(context, R.color.dark_sky_blue))
            tvSetting.setTextColor(ContextCompat.getColor(context, R.color.color_737373))
        }
    }

    //Setting Select
    private fun settingSelect(context: Context) {
        binding.apply {
            ivChat.setImageResource(R.drawable.chat_icon_white)
            ivStory.setImageResource(R.drawable.story)
            ivCamera.setImageResource(R.drawable.camera_disable)
            ivCall.setImageResource(R.drawable.call_disable)
            ivSetting.setImageResource(R.drawable.settings_a)
            tvChat.setTextColor(ContextCompat.getColor(context, R.color.color_737373))
            tvStory.setTextColor(ContextCompat.getColor(context, R.color.color_737373))
            tvCamera.setTextColor(ContextCompat.getColor(context, R.color.color_737373))
            tvCall.setTextColor(ContextCompat.getColor(context, R.color.color_737373))
            tvSetting.setTextColor(ContextCompat.getColor(context, R.color.dark_sky_blue))
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

    //All Controls Defines Here
    override fun initControl() {
        binding.llChat.setOnClickListener(this)
        binding.llStory.setOnClickListener(this)
        binding.llCamera.setOnClickListener(this)
        binding.llCall.setOnClickListener(this)
        binding.llSetting.setOnClickListener(this)
    }

    //OnClick Listener
    override fun onClick(v: View) {
        when (v.id) {
            R.id.llChat -> {
                binding. homeViewPager.setCurrentItem(0, true)
            }
            R.id.llStory -> {
                binding.homeViewPager.setCurrentItem(1, true)
            }
            R.id.llCamera -> {
                binding. homeViewPager.setCurrentItem(2, true)
            }
            R.id.llCall -> {
                binding.homeViewPager.setCurrentItem(3, true)
            }
            R.id.llSetting -> {
                binding.homeViewPager.setCurrentItem(4, true)
            }
        }
    }
}