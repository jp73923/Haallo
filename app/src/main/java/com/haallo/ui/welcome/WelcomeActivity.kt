package com.haallo.ui.welcome

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.haallo.base.BaseActivity
import com.haallo.base.extension.startActivityWithDefaultAnimation
import com.haallo.base.extension.subscribeAndObserveOnMainThread
import com.haallo.base.extension.throttleClicks
import com.haallo.databinding.ActivityWelcomeBinding
import com.haallo.ui.chooselanguage.ChooseLanguageActivity
import com.haallo.ui.termsandconditions.TermsAndConditionActivity

class WelcomeActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, WelcomeActivity::class.java)
        }
    }

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listenToViewEvent()
    }

    private fun listenToViewEvent() {
        binding.tvTermsCondition.throttleClicks().subscribeAndObserveOnMainThread {
            startActivityWithDefaultAnimation(TermsAndConditionActivity.getIntent(this))
        }.autoDispose()

        binding.btnContinue.throttleClicks().subscribeAndObserveOnMainThread {
            startActivityWithDefaultAnimation(ChooseLanguageActivity.getIntent(this))
            finish()
        }.autoDispose()
    }
}