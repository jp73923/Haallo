package com.haallo.base

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.haallo.ui.chat.firebaseDb.FirebaseDbHandler
import com.haallo.util.SharedPreferenceUtil
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    //Not_MVVVM
    lateinit var sharedPreference: SharedPreferenceUtil

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Not_MVVVM
        sharedPreference = SharedPreferenceUtil.getInstance(this)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    fun Disposable.autoDispose() {
        compositeDisposable.add(this)
    }

    //Not_MVVVM
    val firebaseDbHandler by lazy {
        FirebaseDbHandler(this)
    }
}