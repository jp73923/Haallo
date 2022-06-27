package com.haallo.ui.splashToHome.chooseLanguage

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import com.haallo.R
import com.haallo.base.OldBaseActivity
import com.haallo.databinding.ActivityChooseLanguageBinding
import com.haallo.ui.signup.SignUpActivity
import com.haallo.util.showToast

class ChooseLanguageActivityOld : OldBaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityChooseLanguageBinding
    private var language: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChooseLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initControl()
    }

    override fun initView() {
        citySpinner()
    }

    //All Control Defines Here
    override fun initControl() {
        binding.btnRegister.setOnClickListener(this)
    }

    //OnClick Listener Function
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnRegister -> {
                if (isLanguageSelected()) {
                    languageApi()
                }
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
        startActivity(Intent(this, SignUpActivity::class.java))
        finish()
    }

    //Spinner Text
    private fun citySpinner() {
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
}