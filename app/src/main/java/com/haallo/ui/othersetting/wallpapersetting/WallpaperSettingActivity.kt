package com.haallo.ui.othersetting.wallpapersetting

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import br.com.onimur.handlepathoz.HandlePathOz
import br.com.onimur.handlepathoz.HandlePathOzListener
import br.com.onimur.handlepathoz.model.PathOz
import com.haallo.R
import com.haallo.base.BaseActivity
import com.haallo.base.extension.*
import com.haallo.databinding.ActivityWallpaperSettingBinding
import com.haallo.ui.imagecrop.ImageCropActivity
import com.haallo.util.FileUtils
import com.haallo.util.showToast
import com.tbruyelle.rxpermissions2.Permission
import com.tbruyelle.rxpermissions2.RxPermissions

class WallpaperSettingActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, WallpaperSettingActivity::class.java)
        }
    }

    private lateinit var binding: ActivityWallpaperSettingBinding

    private lateinit var handlePathOz: HandlePathOz
    private var captureImagePath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWallpaperSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listenToViewEvent()
    }

    private fun listenToViewEvent() {
        handlePathOz = HandlePathOz(this, listener)

        binding.ivBack.throttleClicks().subscribeAndObserveOnMainThread {
            onBackPressed()
        }.autoDispose()

        binding.tvUploadFromGallery.throttleClicks().subscribeAndObserveOnMainThread {
            checkStoragePermission()
        }.autoDispose()

        binding.tvCaptureWithCamera.throttleClicks().subscribeAndObserveOnMainThread {
            checkCameraPermission()
        }.autoDispose()

        binding.tvRemoveWallpaper.throttleClicks().subscribeAndObserveOnMainThread {
            showRemoveWallpaperConfirmDialog()
        }.autoDispose()
    }

    private fun checkCameraPermission() {
        RxPermissions(this).requestEachCombined(
            Manifest.permission.CAMERA
        ).subscribe { permission: Permission ->
            if (permission.granted) {
                captureImagePath = FileUtils.openCamera(this)
            } else {
                if (permission.shouldShowRequestPermissionRationale) {
                    showLongToast(getString(R.string.msg_please_allow_storage_permission))
                } else {
                    showLongToast(getString(R.string.msg_allow_permission_from_settings))
                }
            }
        }.autoDispose()
    }

    private fun checkStoragePermission() {
        RxPermissions(this).requestEachCombined(
            Manifest.permission.READ_EXTERNAL_STORAGE
        ).subscribe { permission: Permission ->
            if (permission.granted) {
                FileUtils.selectPhoto(this)
            } else {
                if (permission.shouldShowRequestPermissionRationale) {
                    showLongToast(getString(R.string.msg_please_allow_camera_permission))
                } else {
                    showLongToast(getString(R.string.msg_allow_permission_from_settings))
                }
            }
        }.autoDispose()
    }

    private fun showRemoveWallpaperConfirmDialog() {
        val dialogBuilder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        dialogBuilder.setMessage(getString(R.string.msg_are_you_sure_do_you_want_remove_wallpaper))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.label_yes)) { dialog, _ ->
                dialog.dismiss()
                sharedPreference.chatWallpaper = ""
                showToast(getString(R.string.msg_chat_wallpaper_removed))
                onBackPressed()
            }
            .setNegativeButton(getString(R.string.label_no)) { dialog, id ->
                dialog.dismiss()
            }
        val alert = dialogBuilder.create()
        alert.setTitle(getString(R.string.label_confirmation))
        alert.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                FileUtils.RC_OPEN_CAMERA -> {
                    startActivityForResultWithDefaultAnimation(
                        ImageCropActivity.getIntent(
                            this@WallpaperSettingActivity, captureImagePath, getScreenWidth(), getScreenHeight()
                        ),
                        ImageCropActivity.REQUEST_CODE_CROP_IMAGE
                    )
                }
                FileUtils.RC_PICK_IMAGE -> {
                    data?.data?.also {
                        handlePathOz.getRealPath(it)
                    }
                }
                ImageCropActivity.REQUEST_CODE_CROP_IMAGE -> {
                    data?.let {
                        if (it.hasExtra(ImageCropActivity.INTENT_EXTRA_FILE_PATH)) {
                            val croppedImageFilePath = it.getStringExtra(ImageCropActivity.INTENT_EXTRA_FILE_PATH)
                            if (!croppedImageFilePath.isNullOrEmpty()) {
                                sharedPreference.chatWallpaper = croppedImageFilePath
                                showToast(getString(R.string.msg_chat_wallpaper_set_successfully))
                                onBackPressed()
                            }
                        }
                    }
                }
            }
        }
    }

    private val listener = object : HandlePathOzListener.SingleUri {
        override fun onRequestHandlePathOz(pathOz: PathOz, tr: Throwable?) {
            if (tr != null) {
                showToast(getString(R.string.error_in_finding_file_path))
            } else {
                val filePath = pathOz.path
                if (filePath.isNotEmpty()) {
                    startActivityForResultWithDefaultAnimation(
                        ImageCropActivity.getIntent(
                            this@WallpaperSettingActivity, filePath,
                            getScreenWidth(), getScreenHeight()
                        ),
                        ImageCropActivity.REQUEST_CODE_CROP_IMAGE,
                    )
                }
            }
        }
    }
}