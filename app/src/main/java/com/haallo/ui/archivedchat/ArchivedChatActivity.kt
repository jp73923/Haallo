package com.haallo.ui.archivedchat

import android.content.Context
import android.content.Intent
import com.haallo.base.BaseActivity

class ArchivedChatActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, ArchivedChatActivity::class.java)
        }
    }

}