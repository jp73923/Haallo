package com.haallo.ui.home.setting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.haallo.R
import com.haallo.base.OldBaseFragment
import com.haallo.databinding.FragmentHomeSettingBinding
import com.haallo.ui.splashToHome.splash.SplashActivityOld
import com.haallo.ui.support.SupportActivityOld
import com.haallo.util.SharedPreferenceUtil

class HomeSettingFragmentOld : OldBaseFragment(), View.OnClickListener {

    private var _binding: FragmentHomeSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViews()
        initControl()
    }

    override fun initViews() {
        binding.tvGroupName.setText(sharedPreferenceUtil.name?:"")
        Glide.with(this)
            .load(sharedPreferenceUtil.profilePic)
            .placeholder(R.drawable.profile_picture)
            .error(R.drawable.profile_picture)
            .into(binding.tvGroupIcon)
    }

    override fun initControl() {
        binding.rlAccount.setOnClickListener {
            startActivity(Intent(this.requireActivity(), AccountSettings::class.java))
        }
        binding.rlPrivacy.setOnClickListener {
            startActivity(Intent(this.requireActivity(), PrivacySettings::class.java))
        }
        binding.rlOthers.setOnClickListener {
            startActivity(Intent(this.requireActivity(), SupportActivityOld::class.java))
        }
        binding.rlLogout.setOnClickListener {
            SharedPreferenceUtil.getInstance(requireActivity()).deletePreferences()
            startActivity(Intent(this.requireActivity(), SplashActivityOld::class.java))
            requireActivity().finishAffinity()
        }
    }

    //OnClickListener
    override fun onClick(v: View) {

    }
}