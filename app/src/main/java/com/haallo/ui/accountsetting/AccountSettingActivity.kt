package com.haallo.ui.accountsetting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.haallo.R
import com.haallo.api.coroutine.APIHelper
import com.haallo.api.coroutine.AppViewModelFactory
import com.haallo.api.coroutine.RetrofitBuilder.GATEWAY_SERVICE
import com.haallo.api.coroutine.Status
import com.haallo.base.OldBaseActivity
import com.haallo.databinding.ActivitySettingAccountBinding
import com.haallo.ui.home.setting.SettingViewModel
import com.haallo.ui.signin.SignInActivity
import com.haallo.util.SharedPreferenceUtil
import com.haallo.util.showToast

class AccountSettingActivity : OldBaseActivity() {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, AccountSettingActivity::class.java)
        }
    }

    private lateinit var binding: ActivitySettingAccountBinding

    var viewModel: SettingViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listenToViewEvent()

        initView()
        initControl()
    }

    private fun listenToViewEvent() {

    }

    override fun initView() {
        viewModel = ViewModelProvider(this, AppViewModelFactory(APIHelper(GATEWAY_SERVICE))).get(SettingViewModel::class.java)
    }

    override fun initControl() {
        binding.tvRemoveAccount.setOnClickListener {
            showDialogConfirmationsDialog()
        }
    }

    private fun showDialogConfirmationsDialog() {
        val dialogBuilder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        dialogBuilder.setMessage(getString(R.string.msg_are_you_sure_do_you_want_remove_your_account))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.label_yes)) { dialog, _ ->
                viewModel?.removeAccount(sharedPreference.accessToken, sharedPreference.mobileNumber)
                    ?.observe(this, Observer {
                        it?.let { resource ->
                            when (resource.status) {
                                Status.SUCCESS -> {
                                    val data = resource.data
                                    if (data?.isSuccessful == true) {
                                        showToast(getString(R.string.msg_your_account_deleted))

                                        SharedPreferenceUtil.getInstance(this).deletePreferences()

                                        startActivity(Intent(this, SignInActivity::class.java))
                                        finish()
                                    }
                                }
                                Status.ERROR -> {
                                    showToast("")
                                }
                                Status.LOADING -> {

                                }
                            }
                        }
                    })
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.label_no)) { dialog, id ->
                dialog.dismiss()
            }
        val alert = dialogBuilder.create()
        alert.setTitle(getString(R.string.label_confirmation))
        alert.show()
    }
}