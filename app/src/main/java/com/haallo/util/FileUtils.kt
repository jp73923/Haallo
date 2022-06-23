package com.haallo.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File

object FileUtils {
    const val DirRoot = "Outgoer"
    const val DirAudio = "Audio"
    const val DirShort = "Short"
    const val DirFull = "Full"

    private const val FileTypeAllImage = "image/*"
    private const val FileTypeJPG = "image/jpg"
    private const val FileTypeJPEG = "image/jpeg"
    private const val FileTypePNG = "image/png"
    private const val FileTypeWebp = "image/webp"
    private val allImageMimeTypes = arrayOf(FileTypeJPG, FileTypeJPEG, FileTypePNG, FileTypeWebp)

    const val RC_PICK_IMAGE = 1001
    const val RC_OPEN_CAMERA = 1002

    private fun createTempImageFile(context: Context): File {
        var imageFile = File("")
        try {
            val imageFileName = "IMG_"
            val storageDir = getTempDirectoryName(context)
            imageFile = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",   // suffix
                storageDir      // directory
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return imageFile
    }

    private fun getTempDirectoryName(context: Context): File {
        val file = File(context.cacheDir.absolutePath + "/.temp")
        if (!file.exists())
            file.mkdirs()
        return file
    }

    fun openCamera(activity: Activity): String {
        var cardPath = ""
        try {
            val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (pictureIntent.resolveActivity(activity.packageManager) != null) {
                val photoFile = createTempImageFile(activity)
                cardPath = photoFile.absolutePath
                if (photoFile.exists()) {
                    val photoURI: Uri = FileProvider.getUriForFile(activity, activity.packageName + ".file_provider", photoFile)
                    pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    activity.startActivityForResult(pictureIntent, RC_OPEN_CAMERA)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return cardPath
    }

    fun selectPhoto(activity: Activity, isMultiple: Boolean = false) {
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        intent.type = "image/*"
//        activity.startActivityForResult(intent, PICK_IMAGE)
        val intent = if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        } else {
            Intent(Intent.ACTION_PICK, MediaStore.Video.Media.INTERNAL_CONTENT_URI)
        }
        intent.apply {
            type = FileTypeAllImage
            putExtra(Intent.EXTRA_MIME_TYPES, allImageMimeTypes)
            action = Intent.ACTION_GET_CONTENT
            action = Intent.ACTION_OPEN_DOCUMENT
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, isMultiple)
            putExtra("return-data", true)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        activity.startActivityForResult(intent, RC_PICK_IMAGE)
    }
}