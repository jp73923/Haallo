package com.haallo.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class SharedPreferenceUtil private constructor(val context: Context) {
    val TAG = SharedPreferenceUtil::class.java.simpleName

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("haallo_pref", Context.MODE_PRIVATE)
    /* private val persistableSharedPreferences: SharedPreferences = context.getSharedPreferences(Constant.PERSISTABLE_PREFRENCE_NAME, Context.MODE_PRIVATE)*/

    private val editor: SharedPreferences.Editor = sharedPreferences.edit()
    /* private val persistableEditor: SharedPreferences.Editor = persistableSharedPreferences.edit()*/

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: SharedPreferenceUtil? = null

        fun getInstance(ctx: Context): SharedPreferenceUtil {
            if (instance == null) {
                instance = SharedPreferenceUtil(ctx)
            }
            return instance!!
        }
    }

    var halloFlag: Int
        get() = sharedPreferences["halloFlag", -1]!!
        set(value) = sharedPreferences.set("halloFlag", value)

    var screenWidth: Int
        get() = sharedPreferences["screenWidth", 0]!!
        set(value) = sharedPreferences.set("screenWidth", value)

    var homeLogin: Int
        get() = sharedPreferences["homeLogin", 0]!!
        set(value) = sharedPreferences.set("homeLogin", value)

    var mobileNumber: String
        get() = sharedPreferences["mobileNumber", ""]!!
        set(value) = sharedPreferences.set("mobileNumber", value)

    var countryCode: String
        get() = sharedPreferences["countryCode", ""]!!
        set(value) = sharedPreferences.set("countryCode", value)

    var isOnCall: Boolean
        get() = sharedPreferences["isOnCall", false]!!
        set(value) = sharedPreferences.set("isOnCall", value)

    var accessToken: String
        get() = sharedPreferences["accessToken", ""]!!
        set(value) = sharedPreferences.set("accessToken", value)

    var chatWallpaper: String
        get() = sharedPreferences["chatWallpaper", ""]!!
        set(value) = sharedPreferences.set("chatWallpaper", value)

    var profilePic: String
        get() = sharedPreferences["profilePic", ""]!!
        set(value) = sharedPreferences.set("profilePic", value)

    var name: String
        get() = sharedPreferences["name", ""]!!
        set(value) = sharedPreferences.set("name", value)

    var about: String
        get() = sharedPreferences["about", ""]!!
        set(value) = sharedPreferences.set("about", value)

    var nightTheme: Boolean
        get() = sharedPreferences["nightTheme", false]!!
        set(value) = sharedPreferences.set("nightTheme", value)

    var chatWallpaperSet: Boolean
        get() = sharedPreferences["chatWallpaperSet", false]!!
        set(value) = sharedPreferences.set("chatWallpaperSet", value)

    var userName: String
        get() = sharedPreferences["userName", ""]!!
        set(value) = sharedPreferences.set("userName", value)

    var userGender: String
        get() = sharedPreferences["userGender", ""]!!
        set(value) = sharedPreferences.set("userGender", value)

    var splashProgress: Int
        get() = sharedPreferences["splashProgress", -1]!!
        set(value) = sharedPreferences.set("splashProgress", value)

    var userId: String
        get() = sharedPreferences["userId", ""] ?: ""
        set(value) = sharedPreferences.set("userId", value)

    var isFirstTime: Int
        get() = sharedPreferences["isFirstTime", -1]!!
        set(value) = sharedPreferences.set("isFirstTime", value)

    var selectedLanguage: String
        get() = sharedPreferences["selectedLanguage", ""]!!
        set(value) = sharedPreferences.set("selectedLanguage", value)

    var deviceToken: String
        get() = sharedPreferences["deviceToken", ""]!!
        set(value) = sharedPreferences.set("deviceToken", value)

    operator fun SharedPreferences.set(key: String, value: Any?) {
        when (value) {
            is Int -> edit { it.putInt(key, value) }
            is String? -> edit { it.putString(key, value) }
            is Boolean -> edit { it.putBoolean(key, value) }
            is Float -> edit { it.putFloat(key, value) }
            is Long -> edit { it.putLong(key, value) }
            else -> Log.e(TAG, "Setting shared pref failed for key: $key and value: $value ")
        }
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = this.edit()
        operation(editor)
        editor.apply()
    }

    inline operator fun <reified T : Any> SharedPreferences.get(
        key: String,
        defaultValue: T? = null
    ): T? {
        return when (T::class) {
            String::class -> getString(key, defaultValue as? String) as T?
            Int::class -> getInt(key, defaultValue as? Int ?: -1) as T?
            Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T?
            Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T?
            Long::class -> getLong(key, defaultValue as? Long ?: -1) as T?
            else -> throw UnsupportedOperationException("Not yet implemented") as Throwable
        }
    }

    fun deletePreferences() {
        editor.clear()
        editor.apply()
    }
}