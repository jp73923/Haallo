package com.haallo.ui.chat.activity

import android.os.Bundle
import com.haallo.base.OldBaseActivity
import com.haallo.constant.IntentConstant
import com.haallo.databinding.ActivityFullScreenImageBinding
import com.squareup.picasso.Picasso

class FullScreenImageActivityOld : OldBaseActivity() {

    private lateinit var binding: ActivityFullScreenImageBinding
    private var pic: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFullScreenImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initControl()
    }

    //All UI changes From Here
    override fun initView() {
        pic = intent.getStringExtra(IntentConstant.PIC)
        if (pic != null && pic != "") {
            Picasso.get().load(pic).into(binding.ivPic)
        }
    }

    //All Controls Defines Here
    override fun initControl() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }
}