package com.haallo.ui.editprofile

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import br.com.onimur.handlepathoz.HandlePathOz
import br.com.onimur.handlepathoz.HandlePathOzListener
import br.com.onimur.handlepathoz.model.PathOz
import com.bumptech.glide.Glide
import com.haallo.R
import com.haallo.api.profile.model.EditProfilePhotoState
import com.haallo.base.BaseActivity
import com.haallo.base.extension.*
import com.haallo.databinding.ActivityEditProfileBinding
import com.haallo.ui.imagecrop.ImageCropActivity
import com.haallo.util.FileUtils
import com.tbruyelle.rxpermissions2.Permission
import com.tbruyelle.rxpermissions2.RxPermissions

class EditProfileActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, EditProfileActivity::class.java)
        }
    }

    private lateinit var binding: ActivityEditProfileBinding

    private lateinit var handlePathOz: HandlePathOz
    private var captureImagePath = ""
    private var selectedImagePath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listenToViewEvent()
    }

    private fun listenToViewEvent() {
        handlePathOz = HandlePathOz(this, listener)

        binding.ivBack.throttleClicks().subscribeAndObserveOnMainThread {
            onBackPressed()
        }.autoDispose()

        binding.ivUserProfile.throttleClicks().subscribeAndObserveOnMainThread {
            startActivityWithFadeInAnimation(MyProfilePhotoPreviewActivity.getIntent(this))
        }.autoDispose()

        binding.ivCamera.throttleClicks().subscribeAndObserveOnMainThread {
            openEditProfilePhotoBottomSheet()
        }.autoDispose()

        binding.ivEditName.throttleClicks().subscribeAndObserveOnMainThread {
            openEditNameBottomSheet()
        }.autoDispose()

        binding.ivEditAbout.throttleClicks().subscribeAndObserveOnMainThread {
            openEditAboutBottomSheet()
        }.autoDispose()

        binding.btnSave.throttleClicks().subscribeAndObserveOnMainThread {

        }.autoDispose()
    }

    override fun onResume() {
        super.onResume()
        loadProfile()
    }

    private fun loadProfile() {
        binding.tvName.text = sharedPreference.name
        binding.tvAbout.text = sharedPreference.about

        selectedImagePath = sharedPreference.profilePic

        Glide.with(this)
            .load(selectedImagePath)
            .circleCrop()
            .placeholder(R.drawable.outline_account_circle_24_profile)
            .into(binding.ivUserProfile)
    }

    private fun openEditNameBottomSheet() {
        val bottomReportSheet = EditNameBottomSheet(binding.tvName.text.toString())
        bottomReportSheet.saveNameClick.subscribeAndObserveOnMainThread {
            bottomReportSheet.dismissBottomSheet()
            binding.tvName.text = it

            binding.rlFooter.visibility = View.VISIBLE
            binding.progressBar.visibility = View.INVISIBLE
            binding.btnSave.visibility = View.VISIBLE
        }.autoDispose()
        bottomReportSheet.show(supportFragmentManager, EditNameBottomSheet::class.java.name)
    }

    private fun openEditAboutBottomSheet() {
        val bottomReportSheet = EditAboutBottomSheet(binding.tvAbout.text.toString())
        bottomReportSheet.saveAboutClick.subscribeAndObserveOnMainThread {
            bottomReportSheet.dismissBottomSheet()
            binding.tvAbout.text = it

            binding.rlFooter.visibility = View.VISIBLE
            binding.progressBar.visibility = View.INVISIBLE
            binding.btnSave.visibility = View.VISIBLE
        }.autoDispose()
        bottomReportSheet.show(supportFragmentManager, EditAboutBottomSheet::class.java.name)
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
                        ImageCropActivity.getIntent(this@EditProfileActivity, captureImagePath),
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
                                Glide.with(this@EditProfileActivity)
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
                        ImageCropActivity.getIntent(this@EditProfileActivity, filePath),
                        ImageCropActivity.REQUEST_CODE_CROP_IMAGE
                    )
                }
            }
        }
    }
}