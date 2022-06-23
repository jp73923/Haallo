package com.haallo.ui.call

import android.content.Intent
import android.os.Bundle
import android.util.Log

object BundleUtils {
    val TAG = BundleUtils::class.simpleName!!

    fun printIntentData(tag: String?, intent: Intent?) {
        Log.d(tag ?: TAG, "printIntentData intent?.data: ${intent?.data}")
        printBundleData(tag ?: TAG, intent?.extras)
    }

    fun printBundleData(tag: String?, bundle: Bundle?) {
        Log.d(tag ?: TAG, "printBundleData bundle: $bundle")
    }
}