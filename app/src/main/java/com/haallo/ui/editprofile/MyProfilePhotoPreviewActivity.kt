package com.haallo.ui.editprofile

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import br.com.onimur.handlepathoz.HandlePathOz
import br.com.onimur.handlepathoz.HandlePathOzListener
import br.com.onimur.handlepathoz.model.PathOz
import com.bumptech.glide.Glide
import com.haallo.R
import com.haallo.api.profile.model.EditProfilePhotoState
import com.haallo.base.BaseActivity
import com.haallo.base.extension.*
import com.haallo.databinding.ActivityMyProfilePhotoPreviewBinding
import com.haallo.ui.imagecrop.ImageCropActivity
import com.haallo.util.FileUtils
import com.tbruyelle.rxpermissions2.Permission
import com.tbruyelle.rxpermissions2.RxPermissions

class MyProfilePhotoPreviewActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, MyProfilePhotoPreviewActivity::class.java)
        }
    }

    private lateinit var binding: ActivityMyProfilePhotoPreviewBinding

    private lateinit var handlePathOz: HandlePathOz
    private var captureImagePath = ""
    private var selectedImagePath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyProfilePhotoPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listenToViewEvent()
    }

    private fun listenToViewEvent() {
        handlePathOz = HandlePathOz(this, listener)

        binding.ivBack.throttleClicks().subscribeAndObserveOnMainThread {
            onBackPressed()
        }.autoDispose()

        binding.ivEdit.throttleClicks().subscribeAndObserveOnMainThread {
            openEditProfilePhotoBottomSheet()
        }.autoDispose()

        binding.ivShare.throttleClicks().subscribeAndObserveOnMainThread {

        }.autoDispose()
    }

    override fun onResume() {
        super.onResume()
        loadProfile()
    }

    private fun loadProfile() {
        selectedImagePath = sharedPreference.profilePic

        Glide.with(this)
            .load(selectedImagePath)
            .centerCrop()
            .placeholder(R.drawable.outline_account_circle_24_profile)
            .into(binding.ivUserProfile)
    }

    private fun openEditProfilePhotoBottomSheet() {
        val bottomReportSheet = EditProfilePhotoBottomSheet()
        bottomReportSheet.editProfilePhotoClick.subscribeAndObserveOnMainThread {
            bottomReportSheet.dismissBottomSheet()
            when (it) {
                EditProfilePhotoState.OpenCamera -> {
                    checkCameraPermission()
                }
                EditProfilePhotoState.OpenGallery -> {
                    checkStoragePermission()
                }
                EditProfilePhotoState.DeletePhoto -> {}
            }
        }.autoDispose()
        bottomReportSheet.show(supportFragmentManager, EditProfilePhotoBottomSheet::class.java.name)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                FileUtils.RC_OPEN_CAMERA -> {
                    startActivityForResultWithDefaultAnimation(
                        ImageCropActivity.getIntent(this@MyProfilePhotoPreviewActivity, captureImagePath),
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
                                sharedPreference.profilePic = croppedImageFilePath
                                Glide.with(this@MyProfilePhotoPreviewActivity)
                                    .load(croppedImageFilePath)
                                    .circleCrop()
                                    .placeholder(R.drawable.outline_account_circle_24_profile)
                                    .into(binding.ivUserProfile)
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
                        ImageCropActivity.getIntent(this@MyProfilePhotoPreviewActivity, filePath),
                        ImageCropActivity.REQUEST_CODE_CROP_IMAGE
                    )
                }
            }
        }
    }
}