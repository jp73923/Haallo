package com.haallo.ui.createprofile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import br.com.onimur.handlepathoz.HandlePathOz
import br.com.onimur.handlepathoz.HandlePathOzListener
import br.com.onimur.handlepathoz.model.PathOz
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.ui.PlacePicker
import com.haallo.R
import com.haallo.api.profile.model.EditProfilePhotoState
import com.haallo.base.BaseActivity
import com.haallo.base.extension.*
import com.haallo.databinding.ActivityCreateProfileBinding
import com.haallo.ui.chat.model.UserModel
import com.haallo.ui.createprofile.viewmodel.CreateProfileViewModel
import com.haallo.ui.editprofile.EditProfilePhotoBottomSheet
import com.haallo.ui.home.HomeActivity
import com.haallo.ui.imagecrop.ImageCropActivity
import com.haallo.util.FileUtils
import com.haallo.util.findRequestBody
import com.haallo.util.getString
import com.haallo.util.showToast
import com.tbruyelle.rxpermissions2.Permission
import com.tbruyelle.rxpermissions2.RxPermissions
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*

class CreateProfileActivity : BaseActivity() {

    companion object {
        private const val RC_GPS_SETTINGS = 1001
        private const val RC_PLACE_PICKER = 1002
        fun getIntent(context: Context): Intent {
            return Intent(context, CreateProfileActivity::class.java)
        }
    }

    private lateinit var binding: ActivityCreateProfileBinding

    private lateinit var createProfileViewModel: CreateProfileViewModel

    private var accessToken: String = ""
    private var countryCode: String = ""
    private var mobile: String = ""

    private var gender: String = ""

    private lateinit var handlePathOz: HandlePathOz
    private var captureImagePath = ""
    private var profilePic: MultipartBody.Part? = null

    private lateinit var locationManager: LocationManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listenToViewEvent()
        observer()
    }

    private fun listenToViewEvent() {
        handlePathOz = HandlePathOz(this, listener)
        createProfileViewModel = ViewModelProvider(this).get(CreateProfileViewModel::class.java)

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        accessToken = sharedPreference.accessToken
        countryCode = sharedPreference.countryCode
        mobile = sharedPreference.mobileNumber

        gender = getString(R.string.male)

        binding.etMobile.setText(countryCode.plus("-").plus(mobile))

        binding.tvEditProfilePic.throttleClicks().subscribeAndObserveOnMainThread {
            openEditProfilePhotoBottomSheet()
        }.autoDispose()

        binding.tvGender.throttleClicks().subscribeAndObserveOnMainThread {
            openSelectGenderBottomSheet()
        }.autoDispose()

        binding.ivMap.throttleClicks().subscribeAndObserveOnMainThread {
            checkLocationPermission()
//            val builder: PlacePicker.IntentBuilder = PlacePicker.IntentBuilder()
//            startActivityForResult(builder.build(this), RC_PLACE_PICKER)
        }.autoDispose()

        binding.btnSave.throttleClicks().subscribeAndObserveOnMainThread {
            if (isValidInput()) {
                createProfileApi()
            }
        }.autoDispose()
    }

    private fun openEditProfilePhotoBottomSheet() {
        val bottomReportSheet = EditProfilePhotoBottomSheet(true)
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
                        ImageCropActivity.getIntent(this@CreateProfileActivity, captureImagePath),
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

                                Glide.with(this@CreateProfileActivity)
                                    .load(croppedImageFilePath)
                                    .circleCrop()
                                    .placeholder(R.drawable.dp_default)
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
                RC_PLACE_PICKER -> {
                    if (data != null) {
                        val place = PlacePicker.getPlace(data, this)
                        val placeAddress = String.format("%s", place.address)
                        if (place.name == "") {
                            binding.etAddress.setText(placeAddress)
                        } else {
                            binding.etAddress.setText(place.name)
                        }
                    }
                }
            }
        } else if (requestCode == RC_GPS_SETTINGS) {
            val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (isGPSEnabled) {
                detectCurrentLocation()
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
                        ImageCropActivity.getIntent(this@CreateProfileActivity, filePath),
                        ImageCropActivity.REQUEST_CODE_CROP_IMAGE
                    )
                }
            }
        }
    }

    private fun openSelectGenderBottomSheet() {
        val bottomReportSheet = SelectGenderBottomSheet(gender)
        bottomReportSheet.editProfilePhotoClick.subscribeAndObserveOnMainThread {
            bottomReportSheet.dismissBottomSheet()
            this.gender = it
            binding.tvGender.text = it
        }.autoDispose()
        bottomReportSheet.show(supportFragmentManager, SelectGenderBottomSheet::class.java.name)
    }

    private fun checkLocationPermission() {
        RxPermissions(this).requestEachCombined(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ).subscribe { permission: Permission ->
            if (permission.granted) {
                val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                if (isGPSEnabled) {
                    detectCurrentLocation()
                } else {
                    showGPSSettingsAlert()
                }
            } else {
                if (permission.shouldShowRequestPermissionRationale) {
                    showLongToast(getString(R.string.msg_please_allow_location_permission))
                } else {
                    showLongToast(getString(R.string.msg_allow_permission_from_settings))
                }
            }
        }.autoDispose()
    }

    private fun showGPSSettingsAlert() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle(getString(R.string.msg_gps_settings))
        alertDialog.setMessage(getString(R.string.msg_gps_settings_confirmation))
        alertDialog.setPositiveButton(getString(R.string.label_settings)) { _, _ ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(intent, RC_GPS_SETTINGS)
        }
        alertDialog.setNegativeButton(getString(R.string.label_cancel)) { dialog, _ ->
            dialog.cancel()
            showLongToast(getString(R.string.msg_getting_current_location_failed))
        }
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    @SuppressLint("MissingPermission")
    private fun detectCurrentLocation() {
        showLongToast(getString(R.string.msg_getting_current_location_please_wait))
        val locationResult = fusedLocationClient.lastLocation
        locationResult.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Set the map's camera position to the current location of the device.
                val lastKnownLocation = task.result
                if (lastKnownLocation != null) {
                    lastKnownLocation.let {
                        getAddressFromLocation(it.latitude, it.longitude)
                    }
                } else {
                    showLongToast(getString(R.string.msg_getting_current_location_failed))
                }
            } else {
                showLongToast(getString(R.string.msg_getting_current_location_failed))
            }
        }
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double) {
        var location = ""
        try {
            if (latitude != 0.toDouble() && longitude != 0.toDouble()) {
                Handler().post {
                    val geocoder = Geocoder(this, Locale.ENGLISH)
                    val addressList = geocoder.getFromLocation(latitude, longitude, 1)
                    runOnUiThread {
                        if (!addressList.isNullOrEmpty()) {
                            val address = addressList.firstOrNull()
                            if (address != null) {
                                var mainAddress = ""

                                val subLocality = address.subLocality
                                val locality = address.locality
                                val subAdminArea = address.subAdminArea
                                val adminArea = address.adminArea
                                val countryName = address.countryName

                                if (!subLocality.isNullOrEmpty()) {
                                    mainAddress = subLocality.plus(" ")
                                } else if (!locality.isNullOrEmpty()) {
                                    mainAddress = mainAddress.plus(locality).plus(" ")
                                } else if (!subAdminArea.isNullOrEmpty()) {
                                    mainAddress = mainAddress.plus(subAdminArea).plus(" ")
                                } else if (!adminArea.isNullOrEmpty()) {
                                    mainAddress = mainAddress.plus(adminArea).plus(" ")
                                } else if (!countryName.isNullOrEmpty()) {
                                    mainAddress = mainAddress.plus(countryName).plus(" ")
                                }

                                location = mainAddress.ifEmpty {
                                    val maxAddressLineIndex = address.maxAddressLineIndex
                                    if (maxAddressLineIndex >= 0) {
                                        val fullAddress = address.getAddressLine(0)
                                        if (!fullAddress.isNullOrEmpty()) {
                                            fullAddress
                                        } else {
                                            ""
                                        }
                                    } else {
                                        ""
                                    }
                                }
                            } else {
                                location = ""
                            }
                        } else {
                            location = ""
                        }
                        setAddress(location)
                    }
                }
            } else {
                location = ""
                setAddress(location)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setAddress(location: String) {
        if (location.isNotEmpty()) {
            binding.etAddress.setText(location)
        } else {
            showLongToast(getString(R.string.label_address_not_found))
        }
    }

    //Input Validation Function
    private fun isValidInput(): Boolean {
        if (binding.etName.getString().isEmpty()) {
            binding.etName.requestFocus()
            showToast(getString(R.string.please_enter_name))
            return false
        }
        return true
    }

    //Create Profile Api
    private fun createProfileApi() {
        showLoading()
        createProfileViewModel.createProfileApi(
            accessToken = sharedPreference.accessToken,
            userName = findRequestBody(binding.etName.getString()),
            mobile = findRequestBody(mobile),
            address = findRequestBody(binding.etAddress.getString()),
            gender = findRequestBody(gender),
            image = profilePic
        )
    }

    //Observer
    private fun observer() {
        // Profile response
        createProfileViewModel.createProfileResponse.observe(this) {

            if ((it.result.user_name != null) && (it.result.user_name != "")) {
                sharedPreference.userName = it.result.user_name
                sharedPreference.name = it.result.user_name
            }

            if ((it.result.gender != null) && (it.result.gender != "")) {
                sharedPreference.userGender = it.result.gender
            }

            if ((it.result.image != null) && (it.result.image != "")) {
                sharedPreference.profilePic = it.result.image
            }

            sharedPreference.halloFlag = 1
            sharedPreference.about = "Hey I am using Haallo!"

            val userModel = UserModel()
            userModel.countryCode = sharedPreference.countryCode
            userModel.name = sharedPreference.name
            userModel.uid = sharedPreference.userId
            userModel.phone = sharedPreference.mobileNumber
            userModel.photo = sharedPreference.profilePic
            userModel.status = "Hey I am using Haallo!!"
            userModel.userName = sharedPreference.userName
            userModel.ver = "1.0"
            val userId = "u_${sharedPreference.userId}"
            firebaseDbHandler.saveUser(userId, userModel)

            showToast(getString(R.string.profile_created_successfully))
            hideLoading()

            startActivityWithDefaultAnimation(HomeActivity.getIntent(this))
            finish()
        }

        createProfileViewModel.onError.observe(this) {
            hideLoading()
            showError(this, binding.root, it)
        }
    }
}