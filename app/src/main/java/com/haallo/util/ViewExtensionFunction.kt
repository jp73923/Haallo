package com.haallo.util

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationManager
import android.media.MediaMetadataRetriever
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.format.DateFormat
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.haallo.R
import com.haallo.ui.chat.util.RealPathUtil.getRealPathFromURI_API11to18
import com.haallo.ui.chat.util.RealPathUtil.getRealPathFromURI_API19
import com.haallo.ui.chat.util.RealPathUtil.getRealPathFromURI_BelowAPI11
import com.haallo.constant.Constants
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

fun EditText.isValidMobileNumber(): Boolean {
    return this.getString().length in 8..16
}

fun Location.toLatLng(): LatLng = LatLng(this.latitude, this.longitude)
fun LatLng.toLatLngString(): String = "${this.latitude},${this.longitude}"

fun EditText.isValidEmail(): Boolean {
    return if (this.getString().length < 3 || this.getString().length > 265)
        false
    else {
        this.getString().matches(Constants.EMAIL_PATTERN.toRegex())
    }
}

fun getYYYYMMDD(): String? {
    val date = Date()
    val simpleDateFormat = SimpleDateFormat("yyyyMMdd", Locale.ENGLISH)
    return simpleDateFormat.format(date)
}

fun getAudioDuration(context: Context, filePath: String): Int {
    return try {
        //Log.e("MyUtils", "path : $filePath")
        val uri = Uri.parse(filePath)
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(context, uri)
        val durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        mmr.release()
        Integer.parseInt(durationStr)
    } catch (e: Exception) {
        e.printStackTrace()
        0
    }
}

fun View.doVisible() {
    this.visibility = View.VISIBLE
}

fun View.doGone() {
    this.visibility = View.GONE
}

fun View.doInVisible() {
    this.visibility = View.INVISIBLE
}

fun ImageView.setPic(imgUrl: String?) {
    Glide.with(this.context)
        .load(imgUrl)
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .skipMemoryCache(true)
        .error(R.drawable.logo)
        .into(this)
}

fun fullScreenWindow(window: Window) {
    window.setFlags(
        WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN
    )
}

fun findScreenWidth(window: Window): Int {
    val disPlayMetric = DisplayMetrics()
    window.windowManager.defaultDisplay.getMetrics(disPlayMetric)
    return disPlayMetric.widthPixels
}



fun findRequestBody(string: String): RequestBody {
    return string.toRequestBody("text".toMediaTypeOrNull())
}

fun dismissKeyboard(activity: Activity) {
    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    if (null != activity.currentFocus)
        imm.hideSoftInputFromWindow(
            activity.currentFocus!!
                .applicationWindowToken, 0
        )
}

fun openKeyboard(activity: Activity) {
    val inputMethodManager =
        activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.toggleSoftInputFromWindow(
        activity.currentFocus!!.getApplicationWindowToken(),
        InputMethodManager.SHOW_FORCED, 0
    )
}

fun EditText.isValidPassword(): Boolean {
    return if (this.getString().length < 8) {
        false
    } else {
        val matcher: Matcher
        val pattern = Pattern.compile(Constants.PASSWORD_PATTERN)
        matcher = pattern.matcher(this.getString())
        matcher.matches()
        true
    }
}

fun EditText.isANumber(): Boolean {
    return try {
        this.getString().toDouble()
        true
    } catch (e: Exception) {
        false
    }
}

fun EditText.getString(): String {
    return this.text.toString().trim()
}

fun TextView.getString(): String {
    return this.text.toString().trim()
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun AppCompatActivity.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.showLongToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Fragment.showToast(message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.showLongToast(message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

fun showToast(message: String, context: Context) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun showLongToast(message: String, context: Context) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

fun hideKeyboard(context: Context?) {
    if (context is Activity) {
        val focusedView = context.currentFocus
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            focusedView?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}

//Network Connection Available
fun isNetworkConnected(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnectedOrConnecting
}

fun checkStoragePermission(context: Context): Boolean {
    var granted = false
    val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    Permissions.check(context, permissions, null, null, object : PermissionHandler() {
        override fun onGranted() {
            granted = true
        }

        override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>?) {
            granted = false
        }
    })
    return granted
}

//Find Date
fun findTodayDate(): String {
    val date: Date = Date()
    val currentDate = DateFormat.format("dd MMM yyyy", date.time)
    return currentDate.toString()
}

//Find Chat Date
fun getCurrentTimeStamp(): Long {
    val date: Date = Date()
    return date.time
}

fun findTimeFromTimeStamp(timeStamp: Long): String {
    val cal = Calendar.getInstance(Locale.getDefault())
    cal.timeInMillis = timeStamp
    return DateFormat.format("h:mm a", cal).toString()
}
fun findTimeFromTimeStamp12hour(timeStamp: Long?): String {
    val cal = Calendar.getInstance(Locale.getDefault())
    cal.timeInMillis = timeStamp!!
    return DateFormat.format("hh:mm a", cal).toString()
}

fun findDateAndTime(timeStamp: Long): String {
    val cal = Calendar.getInstance(Locale.ENGLISH)
    cal.timeInMillis = timeStamp
    val date = DateFormat.format("dd MMM yyyy", cal).toString()
    val time = DateFormat.format("h:mm a", cal).toString()
    val dateTime = "$date | $time"
    return dateTime
}

//Find Date And Time From String
fun findDateAndTimeFromString(updatedAt: String): String? {
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val date: Date? = simpleDateFormat.parse(updatedAt)
    val time = date?.time
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(time)
}

//Find Date And Time
fun findGroupDateFromTimeStamp(timeStamp: Long): String {
    val cal = Calendar.getInstance(Locale.ENGLISH)
    cal.timeInMillis = timeStamp
    return DateFormat.format("dd MMM yyyy hh:mm", cal).toString()
}



//Find Time In Miles
fun findCurrentTimeInMiles(): Long {
    val date: Date = Date()
    return date.time
}

fun showLog(message: String) {
    Log.e("###", message)
}

fun getRealPath(context: Context, fileUri: Uri): String? {
    return when {
        // SDK < API11
        Build.VERSION.SDK_INT < 11 -> getRealPathFromURI_BelowAPI11(context, fileUri)

        // SDK >= 11 && SDK < 19
        Build.VERSION.SDK_INT < 19 -> getRealPathFromURI_API11to18(context, fileUri)

        // SDK > 19 (Android 4.4) and up
        else -> getRealPathFromURI_API19(context, fileUri)
    }
}

fun createAttachment(image: String): MultipartBody.Part? {


    val file = File(
        image
    )
    val surveyBody = RequestBody.create(
        "image/*".toMediaTypeOrNull(),
        file
    )


    return MultipartBody.Part.createFormData(
        "document_attachment",
        file.name,
        surveyBody
    )
}


fun checkCameraAndStoragePermission(context: Context): Boolean {
    var granted = false
    val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )
    Permissions.check(context, permissions, null, null, object : PermissionHandler() {
        override fun onGranted() {
            granted = true
        }

        override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>?) {
            granted = false
        }
    })
    return granted
}

fun checkContactPermission(context: Context): Boolean {
    var granted = false
    val permissions = arrayOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_CONTACTS
    )
    Permissions.check(context, permissions, null, null, object : PermissionHandler() {
        override fun onGranted() {
            granted = true
        }

        override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>?) {
            granted = false
        }
    })
    return granted
}

fun checkPhonePermission(context: Context): Boolean {
    var granted = false
    Permissions.check(context, Manifest.permission.CALL_PHONE, null,
        object : PermissionHandler() {
            override fun onGranted() {
                granted = true
            }

            override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>?) {
                granted = false
            }
        })
    return granted
}

fun checkAudioPermission(context: Context): Boolean {
    var granted = false
    Permissions.check(context, Manifest.permission.RECORD_AUDIO, null,
        object : PermissionHandler() {
            override fun onGranted() {
                granted = true
            }

            override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>?) {
                granted = false
            }
        })
    return granted
}

fun checkVideoCallPermission(context: Context): Boolean {
    var granted = false
    val permissions = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA
    )
    Permissions.check(context, permissions, null, null, object : PermissionHandler() {
        override fun onGranted() {
            granted = true
        }

        override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>?) {
            granted = false
        }
    })
    return granted
}

fun checkAudioCallPermission(context: Context): Boolean {
    var granted = false
    val permissions = arrayOf(
        Manifest.permission.RECORD_AUDIO,
    )
    Permissions.check(context, permissions, null, null, object : PermissionHandler() {
        override fun onGranted() {
            granted = true
        }

        override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>?) {
            granted = false
        }
    })
    return granted
}

@Throws(Throwable::class)
fun retriveVideoFrameFromVideo(videoPath: String?): Bitmap? {
    var bitmap: Bitmap? = null
    var mediaMetadataRetriever: MediaMetadataRetriever? = null
    try {
        mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(videoPath, HashMap())
        //   mediaMetadataRetriever.setDataSource(videoPath);
        bitmap = mediaMetadataRetriever.frameAtTime
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
        throw Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.message)
    } finally {
        mediaMetadataRetriever?.release()
    }
    return bitmap
}

//Check Location Permission
fun checkLocationPermission(context: Context): Boolean {
    var granted = false
    val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    Permissions.check(context, permissions, null, null, object : PermissionHandler() {
        override fun onGranted() {
            granted = true
        }

        override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>?) {
            granted = false
            super.onDenied(context, deniedPermissions)
        }
    })
    return granted
}

//Check Gps and Network Permission
fun checkGpsPermission(context: Context): Boolean {
    val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    var gpsEnabled = false
    var networkEnabled = false
    try {
        gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    try {
        networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }

    var gpsAndNetworkEnable = false
    if (gpsEnabled && networkEnabled) {
        gpsAndNetworkEnable = true
    }
    return gpsAndNetworkEnable
}

//Enable Gps
fun enableDeviceLocation(context: Context) {
    val alertDialog = AlertDialog.Builder(context).setMessage("GPS Enable")
        .setPositiveButton("Settings") { dialog, which ->
            context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
        .setNegativeButton("Cancel") { dialog, which ->
            showToast("Please open your phone setting and enable your location", context)
        }
    alertDialog.show()
}
fun bitmapToFile(bitmap: Bitmap, context: Context?): Uri {
    // Get the context wrapper
    val wrapper = ContextWrapper(context)

    // Initialize a new file instance to save bitmap object
    var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
    file = File(file, "${UUID.randomUUID()}.jpg")

    try {
        // Compress the bitmap and save in jpg format
        val stream: OutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.flush()
        stream.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }

    // Return the saved bitmap uri
    return Uri.parse(file.absolutePath)
}
fun storeFirebaseToken(context: Context) {


    FirebaseMessaging.getInstance().token.addOnCompleteListener {
        if (it.isComplete) {
            var firebaseToken = it.result.toString()
            if (firebaseToken != null)
                SharedPreferenceUtil.getInstance(context).deviceToken = firebaseToken
        }
    }
}
fun Context.getResource(name:String): Drawable? {
    val resID = resources.getIdentifier(name , "drawable", this.packageName)
    return getDrawable(resID)
}
