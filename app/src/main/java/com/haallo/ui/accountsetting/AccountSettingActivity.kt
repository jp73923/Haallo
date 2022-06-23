package com.haallo.ui.accountsetting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.haallo.api.coroutine.APIHelper
import com.haallo.api.coroutine.AppViewModelFactory
import com.haallo.api.coroutine.RetrofitBuilder.GATEWAY_SERVICE
import com.haallo.api.coroutine.Status
import com.haallo.base.OldBaseActivity
import com.haallo.databinding.ActivitySettingAccountBinding
import com.haallo.ui.home.setting.SettingViewModel
import com.haallo.ui.splashToHome.signIn.SignInActivityOld
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

        initView()
        initControl()
    }

    override fun initView() {
        viewModel = ViewModelProvider(this, AppViewModelFactory(APIHelper(GATEWAY_SERVICE))).get(SettingViewModel::class.java)
    }

    override fun initControl() {
        binding.rlRemoveAccount.setOnClickListener {
            showDialogConfirmationsDialog()
        }
    }

    private fun showDialogConfirmationsDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Are you sure you want to remove you account")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, _ ->
                viewModel?.removeAccount(sharedPreference.accessToken, sharedPreference.mobileNumber)
                    ?.observe(this, Observer {
                        it?.let { resource ->
                            when (resource.status) {
                                Status.SUCCESS -> {
                                    val data = resource.data
                                    if (data?.isSuccessful == true) {
                                        showToast("Successfully account deleted")
                                        SharedPreferenceUtil.getInstance(this).deletePreferences()
                                        startActivity(Intent(this, SignInActivityOld::class.java))
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
            .setNegativeButton("No") { dialog, id ->
                dialog.dismiss()
            }
        val alert = dialogBuilder.create()
        alert.setTitle("Confirmation")
        alert.show()
    }
}