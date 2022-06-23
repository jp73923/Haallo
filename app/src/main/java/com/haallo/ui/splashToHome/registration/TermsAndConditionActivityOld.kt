package com.haallo.ui.splashToHome.registration

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.haallo.R
import com.haallo.base.OldBaseActivity
import com.haallo.databinding.ActivityTermsAndConditionBinding
import com.haallo.util.isNetworkConnected

class TermsAndConditionActivityOld : OldBaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityTermsAndConditionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTermsAndConditionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initControl()
    }

    //All UI Changes Done From Here
    override fun initView() {
        binding.tvHeading.text = getString(R.string.terms_and_conditions_text)
        getAppTheme()
    }

    private fun getAppTheme() {
        if (sharedPreference.nightTheme) {
            binding.rootLayoutTermsCondition.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
            binding.tvNoInternet.setTextColor(ContextCompat.getColor(this, R.color.white))
            if (isNetworkConnected(this)) {
                binding.rlNoInternet.visibility = View.GONE
                binding.rlWebView.visibility = View.VISIBLE
                binding.wVHelp.loadUrl("http://haallo-app.fluper.in/term-condition/1")
            } else {
                binding.rlNoInternet.visibility = View.VISIBLE
                binding.rlWebView.visibility = View.GONE
            }
        } else {
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

    //All Control Defines Here
    override fun initControl() {
        binding.btnBack.setOnClickListener(this)
    }

    //OnClick Listener Function
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnBack -> {
                onBackPressed()
            }
        }
    }
}