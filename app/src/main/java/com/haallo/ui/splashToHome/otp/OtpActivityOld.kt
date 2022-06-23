package com.haallo.ui.splashToHome.otp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.haallo.R
import com.haallo.base.OldBaseActivity
import com.haallo.constant.IntentConstant
import com.haallo.databinding.ActivityOtpBinding
import com.haallo.ui.splashToHome.SignInToHomeViewModelOld
import com.haallo.ui.splashToHome.forgotAndResetPassword.ResetPasswordActivityOld
import com.haallo.ui.splashToHome.profile.CreateProfileActivityOld
import com.haallo.util.dismissKeyboard
import com.haallo.util.getString
import com.haallo.util.openKeyboard
import com.haallo.util.showToast

class OtpActivityOld : OldBaseActivity(), View.OnClickListener, TextWatcher, View.OnKeyListener {

    private lateinit var binding: ActivityOtpBinding
    private lateinit var signInToHomeViewModel: SignInToHomeViewModelOld
    private var isFrom: String? = ""
    private var accessToken: String = ""
    private var mobile: String = ""
    private var mBroadcastReceiver: BroadcastReceiver? = null
    private var isFromLogin: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initControl()
    }

    override fun initView() {
        signInToHomeViewModel = ViewModelProvider(this).get(SignInToHomeViewModelOld::class.java)
        checkTheme()
        getDataFromSharedPreferences()
        getDataFromIntent()
        observer()
        hitResentOtp()
        setHeading()
        startSmsRetriever()
        otpAddTextChangedListener()
        otpSetOnKeyListener()
    }

    //Check App Theme
    private fun checkTheme() {
        if (sharedPreference.nightTheme) {
            binding.rootLayoutOtp.setBackgroundColor(ContextCompat.getColor(this, R.color.appNightModeBackground))
        } else if (!sharedPreference.nightTheme) {
            binding.rootLayoutOtp.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        }
    }

    //Find Data From the SharedPreferences
    private fun getDataFromSharedPreferences() {
        accessToken = sharedPreference.accessToken
        mobile = sharedPreference.mobileNumber
    }

    //Get Data From Intent
    private fun getDataFromIntent() {
        isFrom = intent.getStringExtra(IntentConstant.IS_FROM)
    }

    //Observer
    private fun observer() {
        signInToHomeViewModel.resendOtpResponse.observe(this) {
            hideLoading()
            showToast(getString(R.string.otp_send_successfully))
        }

        signInToHomeViewModel.otpVerifyResponse.observe(this) {
            hideLoading()
            when (isFrom) {
                IntentConstant.REGISTRATION -> {
                    startActivity(Intent(this, CreateProfileActivityOld::class.java))
                    finish()
                }

                IntentConstant.SIGN_IN -> {
                    startActivity(Intent(this, CreateProfileActivityOld::class.java))
                    finish()
                }

                IntentConstant.FORGOT_PASSWORD -> {
                    startActivity(Intent(this, ResetPasswordActivityOld::class.java))
                    finish()
                }
            }
        }

        signInToHomeViewModel.onError.observe(this) {
            hideLoading()
            showError(this, binding.rootLayoutOtp, it)
        }
    }

    //Hit Resend Otp Api If From is From Login Activity
    private fun hitResentOtp() {
        if (isFromLogin)
            resendOtpApi()
    }

    //Set Heading Text
    private fun setHeading() {
        binding.tvHeading.text = getString(R.string.otp_verification)
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
        task.addOnSuccessListener { Log.e("SMSRE", "success") }

        task.addOnFailureListener { Log.e("SMSRE", "failed") }

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
                            Log.e("OTP check", "message : $message")

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

    //Add Changed Listener
    private fun otpAddTextChangedListener() {
        binding.etOtp1.addTextChangedListener(this)
        binding.etOtp2.addTextChangedListener(this)
        binding.etOtp3.addTextChangedListener(this)
        binding.etOtp4.addTextChangedListener(this)
        binding.etOtp5.addTextChangedListener(this)
    }

    //Text Changed Function
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    //Set Onclick Listener
    private fun otpSetOnKeyListener() {
        binding.etOtp1.setOnKeyListener(this)
        binding.etOtp2.setOnKeyListener(this)
        binding.etOtp3.setOnKeyListener(this)
        binding.etOtp4.setOnKeyListener(this)
        binding.etOtp5.setOnKeyListener(this)
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
                R.id.etOtp5 -> {
                    binding.etOtp5.setText("")
                    binding.etOtp4.requestFocus()
                }
            }
        }
        return false
    }

    //All Control Defines Here
    override fun initControl() {
        binding.btnBack.setOnClickListener(this)
        binding.tvResend.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
    }

    //OnClick Listener Function
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnBack -> {
                onBackPressed()
            }

            R.id.tvResend -> {
                resendOtpApi()
                binding.etOtp1.setText("")
                binding.etOtp2.setText("")
                binding.etOtp3.setText("")
                binding.etOtp4.setText("")
                binding.etOtp1.requestFocus()
                openKeyboard(this)
            }

            R.id.btnSubmit -> {
                if (isInputValid())
                    otpVerifyAPI()
            }
        }
    }

    //Resend Otp Api
    private fun resendOtpApi() {
        showLoading()
        signInToHomeViewModel.resendOtpApi(accessToken = accessToken, mobile = mobile)
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
                binding.etOtp5.requestFocus()
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
        signInToHomeViewModel.verifyOtpApi(accessToken = accessToken, mobile = mobile, otp = otp)
    }

    //Before Text Changed Function
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }
}
