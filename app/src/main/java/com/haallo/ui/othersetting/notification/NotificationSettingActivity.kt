package com.haallo.ui.othersetting.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.haallo.api.notification.VibrateSelectState
import com.haallo.base.BaseActivity
import com.haallo.base.extension.subscribeAndObserveOnMainThread
import com.haallo.base.extension.throttleClicks
import com.haallo.databinding.ActivitySettingNotificationBinding

class NotificationSettingActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, NotificationSettingActivity::class.java)
        }
    }

    private lateinit var binding: ActivitySettingNotificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listenToViewEvent()
    }

    private fun listenToViewEvent() {
        binding.ivBack.throttleClicks().subscribeAndObserveOnMainThread {
            onBackPressed()
        }.autoDispose()

        binding.ivConversationTones.throttleClicks().subscribeAndObserveOnMainThread {

        }.autoDispose()

        binding.rlMessageVibrate.throttleClicks().subscribeAndObserveOnMainThread {
            openMessageVibrateSelectDialog()
        }.autoDispose()

        binding.rlGroupVibrate.throttleClicks().subscribeAndObserveOnMainThread {
            openGroupVibrateSelectDialog()
        }.autoDispose()

        binding.rlCallVibrate.throttleClicks().subscribeAndObserveOnMainThread {
            openCallVibrateSelectDialog()
        }.autoDispose()
    }

    private fun openMessageVibrateSelectDialog() {
        val bottomReportSheet = VibrateSelectionDialog()
        bottomReportSheet.vibrateSelectClick.subscribeAndObserveOnMainThread {
            when (it) {
                VibrateSelectState.VibrateOff -> {}
                VibrateSelectState.ShortVibrate -> {}
                VibrateSelectState.LongVibrate -> {}
            }
        }.autoDispose()
        bottomReportSheet.show(supportFragmentManager, VibrateSelectionDialog::class.java.name)
    }

    private fun openGroupVibrateSelectDialog() {
        val bottomReportSheet = VibrateSelectionDialog()
        bottomReportSheet.vibrateSelectClick.subscribeAndObserveOnMainThread {
            when (it) {
                VibrateSelectState.VibrateOff -> {}
                VibrateSelectState.ShortVibrate -> {}
                VibrateSelectState.LongVibrate -> {}
            }
        }.autoDispose()
        bottomReportSheet.show(supportFragmentManager, VibrateSelectionDialog::class.java.name)
    }

    private fun openCallVibrateSelectDialog() {
        val bottomReportSheet = VibrateSelectionDialog()
        bottomReportSheet.vibrateSelectClick.subscribeAndObserveOnMainThread {
            when (it) {
                VibrateSelectState.VibrateOff -> {}
                VibrateSelectState.ShortVibrate -> {}
                VibrateSelectState.LongVibrate -> {}
            }
        }.autoDispose()
        bottomReportSheet.show(supportFragmentManager, VibrateSelectionDialog::class.java.name)
    }
}