package com.haallo.base.extension

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.text.InputType
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.haallo.BuildConfig
import com.haallo.R
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

fun getBaseUrl(): String {
    return if (BuildConfig.DEBUG) {
        "http://3.131.36.176/"
    } else {
        "http://3.131.36.176/"
    }
}

fun Int.prettyCount(): String? {
    val suffix = charArrayOf(' ', 'k', 'M', 'B', 'T', 'P', 'E')
    val numValue: Long = this.toLong()
    val value = floor(log10(numValue.toDouble())).toInt()
    val base = value / 3
    return if (value >= 3 && base < suffix.size) {
        DecimalFormat("#0.00").format(numValue / 10.0.pow(base * 3.toDouble())) + suffix[base]
    } else {
        DecimalFormat().format(numValue)
    }
}

fun getCommonVideoFileName(userId: Int): String {
    return "${userId}_video_android_${System.currentTimeMillis()}"
}

fun getCommonAudioFileName(userId: Int): String {
    return "${userId}_audio_android_${System.currentTimeMillis()}"
}

fun getCommonPhotoFileName(userId: Int): String {
    return "${userId}_img_android_${System.currentTimeMillis()}"
}

val currentTime: Long
    get() = System.nanoTime() / 1000000

// In milliseconds
fun Uri.getMediaDuration(context: Context): Long {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(context, this)
    val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
    retriever.release()
    return duration?.toLongOrNull() ?: 0
}

// In milliseconds
fun File.getMediaDuration(context: Context): Long {
    if (!exists()) return 0
    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(context, Uri.parse(absolutePath))
        val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        retriever.release()
        duration?.toLongOrNull() ?: 0
    } catch (exception: Exception) {
        0
    }
}

fun File.getMediaDurationInSecond(context: Context): Long {
    return TimeUnit.MILLISECONDS.toSeconds(getMediaDuration(context))
}


fun JSONObject.safeJsonObjectParse(key: String): String? {
    return try {
        this.get(key).toString()
    } catch (ex: JSONException) {
        null
    }
}

fun String.toJSONObject(): JSONObject {
    return JSONObject(this)
}

class DeactivatedAccountException(message: String?) : Exception(message)

fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
    val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
    return if (vectorDrawable != null) {
        vectorDrawable.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        BitmapDescriptorFactory.fromBitmap(bitmap)
    } else {
        null
    }
}

fun getChatMessageHeaderDateForGroup(dateString: String?): String {
    var convertTime = ""
    try {
        if (!dateString.isNullOrEmpty()) {
            val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")

            val outputDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
            outputDateFormat.timeZone = TimeZone.getDefault()

            val parsedDate = inputDateFormat.parse(dateString)
            if (parsedDate != null) {
                val formattedDate = outputDateFormat.format(parsedDate)
                if (!formattedDate.isNullOrEmpty()) {
                    convertTime = formattedDate
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return convertTime
}

fun getFormattedDateForChatMessageHeader(context: Context, dateString: String?): String {
    var convertTime = getChatMessageHeaderDateForGroup(dateString)
    try {
        if (!dateString.isNullOrEmpty()) {
            val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")

            val currentDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            currentDateFormat.timeZone = TimeZone.getDefault()

            val parsedDate = inputDateFormat.parse(dateString)
            if (parsedDate != null) {
                val diffInMillis = TimeUnit.DAYS.convert(
                    currentDateFormat.calendar.timeInMillis - parsedDate.time,
                    TimeUnit.MILLISECONDS
                )
                when (diffInMillis) {
                    0L -> {
                        convertTime = context.resources.getString(R.string.today)
                    }
                    1L -> {
                        convertTime = context.resources.getString(R.string.yesterday)
                    }
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return convertTime
}

fun getFormattedTimeForChatMessage(dateString: String?): String {
    var convertTime = ""
    try {
        if (!dateString.isNullOrEmpty()) {
            val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")

            val outputDateFormat = SimpleDateFormat("hh:mm aa", Locale.ENGLISH)
            outputDateFormat.timeZone = TimeZone.getDefault()

            val parsedDate = inputDateFormat.parse(dateString)
            if (parsedDate != null) {
                val formattedDate = outputDateFormat.format(parsedDate)
                if (!formattedDate.isNullOrEmpty()) {
                    convertTime = formattedDate
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return convertTime
}

fun Bitmap.createBitmapWithBorder(borderSize: Float, borderColor: Int): Bitmap {
    val borderOffset = (borderSize * 2).toInt()
    val halfWidth = width / 2
    val halfHeight = height / 2
    val circleRadius = halfWidth.coerceAtMost(halfHeight).toFloat()
    val newBitmap = Bitmap.createBitmap(
        width + borderOffset,
        height + borderOffset,
        Bitmap.Config.ARGB_8888
    )

    // Center coordinates of the image
    val centerX = halfWidth + borderSize
    val centerY = halfHeight + borderSize

    val paint = Paint()
    val canvas = Canvas(newBitmap).apply {
        // Set transparent initial area
        drawARGB(0, 0, 0, 0)
    }

    // Draw the transparent initial area
    paint.isAntiAlias = true
    paint.style = Paint.Style.FILL
    canvas.drawCircle(centerX, centerY, circleRadius, paint)

    // Draw the image
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(this, borderSize, borderSize, paint)

    // Draw the createBitmapWithBorder
    paint.xfermode = null
    paint.style = Paint.Style.STROKE
    paint.color = borderColor
    paint.strokeWidth = borderSize
    canvas.drawCircle(centerX, centerY, circleRadius, paint)
    return newBitmap
}

fun Double.roundDoubleVal(decimals: Int = 2): Double = "%.${decimals}f".format(Locale.ENGLISH, this).toDouble()

fun shareText(context: Context, textToShare: String) {
    ShareCompat.IntentBuilder(context)
        .setType("text/plain")
        .setChooserTitle(context.getString(R.string.app_name))
        .setText(textToShare)
        .startChooser()
}

fun getFormattedDateForEvent(eventDate: String?): String {
    var convertTime = ""
    try {
        if (!eventDate.isNullOrEmpty()) {
            val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            val outputDateFormat = SimpleDateFormat("dd MMM", Locale.ENGLISH)
            val parsedDate = inputDateFormat.parse(eventDate)
            if (parsedDate != null) {
                val formattedDate = outputDateFormat.format(parsedDate)
                if (!formattedDate.isNullOrEmpty()) {
                    convertTime = formattedDate
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return convertTime
}

fun openGoogleMapWithProvidedLatLng(context: Context, latitude: String?, longitude: String?) {
    if (!latitude.isNullOrEmpty() && !longitude.isNullOrEmpty()) {
        val data = "http://maps.google.com/maps?q=loc:$latitude,$longitude"
        val gmmIntentUri = Uri.parse(data)
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        }
    }
}

fun EditText.setMultiLineCapSentencesAndDoneAction() {
    imeOptions = EditorInfo.IME_ACTION_DONE
    setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
}