package com.haallo.ui.forgotpasswordotpverify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.haallo.R
import com.haallo.base.BaseActivity
import com.haallo.base.extension.startActivityWithDefaultAnimation
import com.haallo.base.extension.subscribeAndObserveOnMainThread
import com.haallo.base.extension.throttleClicks
import com.haallo.databinding.ActivityForgotPasswordOtpVerifyBinding
import com.haallo.ui.forgotpasswordotpverify.viewmodel.ForgotPasswordOTPVerifyViewModel
import com.haallo.ui.resetpassword.ResetPasswordActivity
import com.haallo.util.dismissKeyboard
import com.haallo.util.getString
import com.haallo.util.openKeyboard
import com.haallo.util.showToast
import timber.log.Timber

class ForgotPasswordOtpVerifyActivity : BaseActivity(), TextWatcher, View.OnKeyListener {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, ForgotPasswordOtpVerifyActivity::class.java)
        }
    }

    private lateinit var binding: ActivityForgotPasswordOtpVerifyBinding
    private lateinit var forgotPasswordOTPVerifyViewModel: ForgotPasswordOTPVerifyViewModel
    private var accessToken: String = ""
    private var mobile: String = ""
    private var mBroadcastReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityForgotPasswordOtpVerifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listenToViewEvent()
    }

    private fun listenToViewEvent() {
        forgotPasswordOTPVerifyViewModel = ViewModelProvider(this).get(ForgotPasswordOTPVerifyViewModel::class.java)

        accessToken = sharedPreference.accessToken
        mobile = sharedPreference.mobileNumber

        binding.ivBack.throttleClicks().subscribeAndObserveOnMainThread {
            onBackPressed()
        }.autoDispose()

        binding.tvResend.throttleClicks().subscribeAndObserveOnMainThread {
            resendOtpApi()
            binding.etOtp1.setText("")
            binding.etOtp2.setText("")
            binding.etOtp3.setText("")
            binding.etOtp4.setText("")
            binding.etOtp1.requestFocus()
            openKeyboard(this)
        }.autoDispose()

        binding.btnSubmit.throttleClicks().subscribeAndObserveOnMainThread {
            if (isInputValid())
                otpVerifyAPI()
        }.autoDispose()

        binding.etOtp1.addTextChangedListener(this)
        binding.etOtp2.addTextChangedListener(this)
        binding.etOtp3.addTextChangedListener(this)
        binding.etOtp4.addTextChangedListener(this)

        binding.etOtp1.setOnKeyListener(this)
        binding.etOtp2.setOnKeyListener(this)
        binding.etOtp3.setOnKeyListener(this)
        binding.etOtp4.setOnKeyListener(this)

        observer()
        startSmsRetriever()
    }

    //Observer
    private fun observer() {
        forgotPasswordOTPVerifyViewModel.resendOtpResponse.observe(this) {
            hideLoading()
            showToast(getString(R.string.otp_send_successfully))
        }

        forgotPasswordOTPVerifyViewModel.otpVerifyResponse.observe(this) {
            hideLoading()
            startActivityWithDefaultAnimation(ResetPasswordActivity.getIntent(this))
            finish()
        }

        forgotPasswordOTPVerifyViewModel.onError.observe(this) {
            hideLoading()
            showError(this, binding.root, it)
        }
    }

    //Start SMS Retriever
    private fun startSmsRetriever() {
        registerReceiver()
        // Get an instance of SmsRetrieverClient, used to start listening for a matching
        // SMS message.
        val client = SmsRetriever.getClient(this)
        // Starts SmsRetriever, which waits for ONE matching SMS message until timeout
        // (5 minutes). The matching SMS message will be sent via a Broadcast Intent with
        // action SmsRetriever#SMS_RETRIEVED_ACTION.
        val task = client.startSmsRetriever()
        // Listen for success/failure of the start Task. If in a background thread, this
        // can be made blocking using Tasks.await(task, [timeout]);
        task.addOnSuccessListener {
            Timber.tag("<><><> SMS").e("success")
        }
        task.addOnFailureListener {
            Timber.tag("<><><> SMS").e("failed")
        }
    }

    //Register a Receiver for OTP Receive
    private fun registerReceiver() {
        // filter to receive SMS
        val intentFilter = IntentFilter()
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)

        // receiver to receive and to get otp from SMS
        mBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
                    val extras = intent.extras
                    val status = extras!!.get(SmsRetriever.EXTRA_STATUS) as Status
                    when (status.statusCode) {
                        CommonStatusCodes.SUCCESS -> {
                            // Get SMS message contents
                            val message = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                            // Extract one-time code from the message and complete verification
                            // by sending the code back to your server for SMS authenticity.
                            Timber.tag("<><><> OTP check").e(message)

                            val otp = message.substring(4, 8)
                            binding.etOtp1.setText(otp[0].toString())
                            binding.etOtp2.setText(otp[1].toString())
                            binding.etOtp3.setText(otp[2].toString())
                            binding.etOtp4.setText(otp[3].toString())

                            //verifyOTP(timezone, locale, user_id, keycode, otp)
                            stopSmsReceiver()
                        }
                        CommonStatusCodes.TIMEOUT -> {
                            // Waiting for SMS timed out (5 minutes)
                            stopSmsReceiver()
                        }
                    }
                }
            }
        }
        registerReceiver(mBroadcastReceiver, intentFilter)
    }

    //Stop Sms Receiver
    fun stopSmsReceiver() {
        try {
            unregisterReceiver(mBroadcastReceiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    //Text Changed Function
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    //Otp Box OnClick Event Function
    override fun onKey(v: View?, p1: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN) {
            when (v?.id) {
                R.id.etOtp1 -> {
                    binding.etOtp1.setText("")
                }
                R.id.etOtp2 -> {
                    binding.etOtp2.setText("")
                    binding.etOtp1.requestFocus()
                }
                R.id.etOtp3 -> {
                    binding.etOtp3.setText("")
                    binding.etOtp2.requestFocus()
                }
                R.id.etOtp4 -> {
                    binding.etOtp4.setText("")
                    binding.etOtp3.requestFocus()
                }
            }
        }
        return false
    }

    //Resend Otp Api
    private fun resendOtpApi() {
        showLoading()
        forgotPasswordOTPVerifyViewModel.resendOtpApi(accessToken = accessToken, mobile = mobile)
    }

    //Input Validation Function
    private fun isInputValid(): Boolean {
        val strOTP1: String = binding.etOtp1.getString()
        val strOTP2: String = binding.etOtp2.getString()
        val strOTP3: String = binding.etOtp3.getString()
        val strOTP4: String = binding.etOtp4.getString()

        if (strOTP1.isEmpty() || strOTP2.isEmpty() || strOTP3.isEmpty() || strOTP4.isEmpty()) {
            showToast(getString(R.string.please_enter_4_digit_otp))
            return false
        }
        return true
    }

    //After Text Changed Function
    override fun afterTextChanged(p0: Editable?) {
        if (binding.etOtp1.isFocused) {
            if (binding.etOtp1.text.toString().length == 1) {
                binding.etOtp2.requestFocus()
            }
        }
        if (binding.etOtp2.isFocused) {
            if (binding.etOtp2.text.toString().length == 1) {
                binding.etOtp3.requestFocus()
            }
        }
        if (binding.etOtp3.isFocused) {
            if (binding.etOtp3.text.toString().length == 1) {
                binding.etOtp4.requestFocus()
            }
        }
        if (binding.etOtp4.isFocused) {
            if (binding.etOtp4.text.toString().length == 1) {
                if (!TextUtils.isEmpty(binding.etOtp4.text.toString())) {
                    dismissKeyboard(this)
                }
                otpVerifyAPI()
            }
        }
    }

    //Otp Verify Api
    private fun otpVerifyAPI() {
        showLoading()
        val otp = binding.etOtp1.getString() + binding.etOtp2.getString() + binding.etOtp3.getString() + binding.etOtp4.getString()
        forgotPasswordOTPVerifyViewModel.verifyOtpApi(accessToken = accessToken, mobile = mobile, otp = otp)
    }

    //Before Text Changed Function
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }
}
