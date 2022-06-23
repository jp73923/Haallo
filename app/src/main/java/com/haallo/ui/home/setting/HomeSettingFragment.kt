package com.haallo.ui.home.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.haallo.R
import com.haallo.base.BaseFragment
import com.haallo.base.extension.startActivityWithDefaultAnimation
import com.haallo.base.extension.subscribeAndObserveOnMainThread
import com.haallo.base.extension.throttleClicks
import com.haallo.databinding.FragmentHomeSettingBinding
import com.haallo.ui.accountsetting.AccountSettingActivity
import com.haallo.ui.editprofile.EditProfileActivity
import com.haallo.ui.privacysetting.PrivacySettingActivity
import com.haallo.ui.splashToHome.splash.SplashActivity
import com.haallo.ui.support.SupportActivity
import com.haallo.util.SharedPreferenceUtil

class HomeSettingFragment : BaseFragment() {

    private var _binding: FragmentHomeSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenToViewEvent()
    }

    private fun listenToViewEvent() {
        binding.ivEdit.throttleClicks().subscribeAndObserveOnMainThread {
            startActivityWithDefaultAnimation(EditProfileActivity.getIntent(requireContext()))
        }.autoDispose()

        binding.tvAccount.throttleClicks().subscribeAndObserveOnMainThread {
            startActivityWithDefaultAnimation(AccountSettingActivity.getIntent(requireContext()))
        }.autoDispose()

        binding.tvPrivacy.throttleClicks().subscribeAndObserveOnMainThread {
            startActivityWithDefaultAnimation(PrivacySettingActivity.getIntent(requireContext()))
        }.autoDispose()

        binding.tvNightMode.throttleClicks().subscribeAndObserveOnMainThread {

        }.autoDispose()

        binding.tvOthers.throttleClicks().subscribeAndObserveOnMainThread {
            startActivityWithDefaultAnimation(SupportActivity.getIntent(requireContext()))
        }.autoDispose()

        binding.tvLogout.throttleClicks().subscribeAndObserveOnMainThread {
            SharedPreferenceUtil.getInstance(requireActivity()).deletePreferences()
            startActivityWithDefaultAnimation(SplashActivity.getIntent(requireContext()))
        }.autoDispose()
    }

    override fun onResume() {
        super.onResume()
        loadProfile()
    }

    private fun loadProfile() {
        binding.tvName.text = sharedPreferenceUtil.name
        binding.tvAbout.text = sharedPreferenceUtil.about

        Glide.with(this)
            .load(sharedPreferenceUtil.profilePic)
            .circleCrop()
            .placeholder(R.drawable.outline_account_circle_24_profile)
            .into(binding.ivUserProfile)
    }
}