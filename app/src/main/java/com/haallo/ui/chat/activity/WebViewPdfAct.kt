package com.haallo.ui.chat.activity

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.haallo.base.OldBaseActivity
import com.haallo.databinding.ActivityWebViewPdfBinding

class WebViewPdfAct : OldBaseActivity() {

    private lateinit var binding: ActivityWebViewPdfBinding

    private val pdf: String?
        get() = intent.getStringExtra("pdf")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWebViewPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initControl()
    }

    //All UI changes From Here
    override fun initView() {
//        webView.webViewClient = WebViewClient()
//        webView.settings.javaScriptEnabled = true
//        webView.settings.setSupportZoom(true)
//        webView.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=$pdf")
        binding.webView.webViewClient = AppWebViewClients()
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.useWideViewPort = true
        binding.webView.loadUrl("http://docs.google.com/gview?embedded=true&url=$pdf")
    }

    //All Controls Defines Here
    override fun initControl() {
        binding.btnBack.setOnClickListener { onBackPressed() }
    }

    class AppWebViewClients() : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
            return true
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
        }
    }
}