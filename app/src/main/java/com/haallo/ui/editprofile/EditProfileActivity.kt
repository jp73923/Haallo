package com.haallo.ui.editprofile

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import br.com.onimur.handlepathoz.HandlePathOz
import br.com.onimur.handlepathoz.HandlePathOzListener
import br.com.onimur.handlepathoz.model.PathOz
import com.bumptech.glide.Glide
import com.haallo.R
import com.haallo.api.fbrtdb.model.FirebaseUser
import com.haallo.api.profile.model.EditProfilePhotoState
import com.haallo.base.BaseActivity
import com.haallo.base.extension.*
import com.haallo.databinding.ActivityEditProfileBinding
import com.haallo.ui.editprofile.viewmodel.EditProfileViewModel
import com.haallo.ui.imagecrop.ImageCropActivity
import com.haallo.util.FileUtils
import com.haallo.util.findRequestBody
import com.haallo.util.getString
import com.tbruyelle.rxpermissions2.Permission
import com.tbruyelle.rxpermissions2.RxPermissions
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class EditProfileActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, EditProfileActivity::class.java)
        }
    }

    private lateinit var binding: ActivityEditProfileBinding

    private lateinit var editProfileViewModel: EditProfileViewModel

    private lateinit var handlePathOz: HandlePathOz
    private var captureImagePath = ""
    private var profilePic: MultipartBody.Part? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listenToViewEvent()
        observer()
        loadProfile()
    }

    private fun listenToViewEvent() {
        handlePathOz = HandlePathOz(this, listener)
        editProfileViewModel = ViewModelProvider(this).get(EditProfileViewModel::class.java)

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

        binding.btnDone.throttleClicks().subscribeAndObserveOnMainThread {
            updateProfile()
        }.autoDispose()
    }

    private fun loadProfile() {
        binding.tvName.text = sharedPreference.name
        binding.tvAbout.text = sharedPreference.about

        Glide.with(this)
            .load(sharedPreference.profilePic)
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
            binding.btnDone.visibility = View.VISIBLE
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
            binding.btnDone.visibility = View.VISIBLE
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

                                Glide.with(this@EditProfileActivity)
                                    .load(croppedImageFilePath)
                                    .circleCrop()
                                    .placeholder(R.drawable.outline_account_circle_24_profile)
                                    .into(binding.ivUserProfile)

                                val croppedImageFile = File(croppedImageFilePath)
                                profilePic = MultipartBody.Part.createFormData(
                                    "image",
                                    System.currentTimeMillis().toString().plus(croppedImageFile.extension),
                                    croppedImageFile.asRequestBody("image/*".toMediaTypeOrNull())
                                )
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

    //Update Profile Api
    private fun updateProfile() {
        showLoading()
        editProfileViewModel.updateProfile(
            accessToken = sharedPreference.accessToken,
            name = findRequestBody(binding.tvName.getString()),
            about = findRequestBody(binding.tvAbout.getString()),
            image = profilePic
        )
    }

    //Observer
    private fun observer() {
        // Profile response
        editProfileViewModel.updateProfileResponse.observe(this) {
            if ((it.result.name != null) && (it.result.name != "")) {
                sharedPreference.name = it.result.name
            }

            if ((it.result.about != null) && (it.result.about != "")) {
                sharedPreference.about = it.result.about
            }

            if ((it.result.image != null) && (it.result.image != "")) {
                sharedPreference.profilePic = it.result.image
            }

            val firebaseUser = FirebaseUser()
            firebaseUser.countryCode = sharedPreference.countryCode
            firebaseUser.name = sharedPreference.name
            firebaseUser.uid = sharedPreference.userId.toLong()
            firebaseUser.phone = sharedPreference.mobileNumber
            firebaseUser.photo = sharedPreference.profilePic
            firebaseUser.status = "Hey I am using Haallo!!"
            firebaseUser.userName = sharedPreference.userName
            firebaseUser.ver = "1.0"
            val userId = "u_${sharedPreference.userId}"
            firebaseDbHandler.saveUser(userId, firebaseUser)

            showToast(getString(R.string.msg_profile_updated_successfully))
            hideLoading()

            binding.rlFooter.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.INVISIBLE
            binding.btnDone.visibility = View.INVISIBLE

            loadProfile()
        }

        editProfileViewModel.onError.observe(this) {
            hideLoading()
            showError(this, binding.root, it)
        }
    }
}