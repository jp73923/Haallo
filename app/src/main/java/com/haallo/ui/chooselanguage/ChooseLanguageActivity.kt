package com.haallo.ui.chooselanguage

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import com.haallo.R
import com.haallo.base.BaseActivity
import com.haallo.base.extension.startActivityWithDefaultAnimation
import com.haallo.base.extension.subscribeAndObserveOnMainThread
import com.haallo.base.extension.throttleClicks
import com.haallo.databinding.ActivityChooseLanguageBinding
import com.haallo.ui.signup.SignUpActivity
import com.haallo.util.showToast

class ChooseLanguageActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, ChooseLanguageActivity::class.java)
        }
    }

    private lateinit var binding: ActivityChooseLanguageBinding
    private var language: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChooseLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listenToViewEvent()
    }

    private fun listenToViewEvent() {
        binding.btnRegister.throttleClicks().subscribeAndObserveOnMainThread {
            if (isLanguageSelected()) {
                languageApi()
            }
        }.autoDispose()

        binding.spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @SuppressLint("ResourceAsColor")
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long) {
                val selectedText = parentView.getChildAt(0) as TextView
                language = selectedText.text.toString()
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {

            }
        }
    }

    //Language Selection Validation
    private fun isLanguageSelected(): Boolean {
        if (binding.spinnerLanguage.selectedItem.toString() == "Select Language") {
            showToast(getString(R.string.please_select_language))
            return false
        }
        return true
    }

    //Language Api
    private fun languageApi() {
        sharedPreference.selectedLanguage = binding.spinnerLanguage.selectedItem.toString()

        startActivityWithDefaultAnimation(SignUpActivity.getIntent(this))
        finish()
    }
}