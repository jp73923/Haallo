package com.haallo.ui.termsandconditions

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.haallo.base.BaseActivity
import com.haallo.base.extension.subscribeAndObserveOnMainThread
import com.haallo.base.extension.throttleClicks
import com.haallo.databinding.ActivityTermsAndConditionBinding
import com.haallo.util.isNetworkConnected

class TermsAndConditionActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, TermsAndConditionActivity::class.java)
        }
    }

    private lateinit var binding: ActivityTermsAndConditionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTermsAndConditionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listenToViewEvent()
    }

    //All UI Changes Done From Here
    private fun listenToViewEvent() {
        binding.ivBack.throttleClicks().subscribeAndObserveOnMainThread {
            onBackPressed()
        }.autoDispose()

        if (isNetworkConnected(this)) {
            binding.rlNoInternet.visibility = View.GONE
            binding.rlWebView.visibility = View.VISIBLE
            binding.wVHelp.loadUrl("http://haallo-app.fluper.in/term-condition/0")
        } else {
            binding.rlNoInternet.visibility = View.VISIBLE
            binding.rlWebView.visibility = View.GONE
        }
    }
}