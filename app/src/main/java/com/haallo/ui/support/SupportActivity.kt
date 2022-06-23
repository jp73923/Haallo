package com.haallo.ui.support

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.haallo.base.OldBaseActivity
import com.haallo.databinding.ActivitySupportBinding
import com.haallo.util.ErrorUtil
import com.haallo.util.showToast

class SupportActivity : OldBaseActivity() {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, SupportActivity::class.java)
        }
    }

    private lateinit var viewModel: SupportViewModelOld
    private lateinit var binding: ActivitySupportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySupportBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        initView()
        initControl()
        observer()
    }

    private fun observer() {
        viewModel.supportLiveData.observe(this, Observer {
            binding.edReport.setText("")
            showToast("Successfully sent.").also {
                finish()
            }
        })
        viewModel.mError.observe(this, Observer {
            ErrorUtil.handlerGeneralError(this, binding.root, it)
        })
    }

    override fun initView() {
        viewModel = ViewModelProvider(this).get(SupportViewModelOld::class.java)
    }

    override fun initControl() {
        binding.btnReport.setOnClickListener {
            if (isValid()) {
                viewModel.supportApi(
                    sharedPreference.accessToken,
                    sharedPreference.userId,
                    binding.edReport.text!!.toString()
                )
            }
        }
    }

    private fun isValid(): Boolean {
        if (binding.edReport.text.isNullOrBlank()) {
            binding.textInputLayout.error = "Please enter message."
            return false
        } else if (binding.edReport.text!!.length < 3) {
            binding.textInputLayout.error = "Please write more words."
            return false
        }
        return true
    }
}