package com.haallo.ui.splashToHome.welcome

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.haallo.R
import com.haallo.base.OldBaseActivity
import com.haallo.databinding.ActivityWelcomeBinding
import com.haallo.ui.splashToHome.chooseLanguage.ChooseLanguageActivityOld
import com.haallo.ui.splashToHome.registration.TermsAndConditionActivityOld

class WelcomeActivityOld : OldBaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initControl()
    }

    override fun initView() {

    }

    //All Control Defines Here
    override fun initControl() {
        binding.tvTermsCondition.setOnClickListener(this)
        binding.btnContinue.setOnClickListener(this)
    }

    //OnClick Listener Function
    override fun onClick(v: View) {
        when (v.id) {
            R.id.tvTermsCondition -> {
                startActivity(Intent(this, TermsAndConditionActivityOld::class.java))
            }

            R.id.btnContinue -> {
                startActivity(Intent(this, ChooseLanguageActivityOld::class.java))
                finish()
            }
        }
    }
}
