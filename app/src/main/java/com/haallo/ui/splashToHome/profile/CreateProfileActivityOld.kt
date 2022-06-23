package com.haallo.ui.splashToHome.profile

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.haallo.R
import com.haallo.base.OldBaseActivity
import com.haallo.base.extension.startActivityWithDefaultAnimation
import com.haallo.databinding.ActivityCreateProfileBinding
import com.haallo.ui.chat.model.UserModel
import com.haallo.ui.home.HomeActivity
import com.haallo.ui.splashToHome.SignInToHomeViewModelOld
import com.haallo.util.findRequestBody
import com.haallo.util.getString
import com.haallo.util.showToast
import com.imagepicker.FilePickUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class CreateProfileActivityOld : OldBaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityCreateProfileBinding

    private lateinit var signInToHomeViewModel: SignInToHomeViewModelOld
    private var profilePic: MultipartBody.Part? = null
    private var accessToken: String = ""
    private var countryCode: String = ""
    private var mobile: String = ""
    private var gender: String = ""
    private val placePickerRequest = 999
    private var selectCameraOrGalleryDialog: Dialog? = null
    private var ref: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initControl()
    }

    override fun initView() {
        signInToHomeViewModel = ViewModelProvider(this).get(SignInToHomeViewModelOld::class.java)
        checkTheme()
        fireBaseReference()
        getDataFromSharedPreferences()
        hideBackButton()
        setHeading()
        getDialog()
        observer()
    }

    //Check App Theme
    private fun checkTheme() {
        if (sharedPreference.nightTheme) {
            binding.rootLayoutProfileCreation.setBackgroundColor(ContextCompat.getColor(this, R.color.appNightModeBackground))
        } else if (!sharedPreference.nightTheme) {
            binding.rootLayoutProfileCreation.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        }
    }

    //FireBase Reference
    private fun fireBaseReference() {
        ref = FirebaseDatabase.getInstance().getReference("users")
    }

    //Find Data From the SharedPreferences
    private fun getDataFromSharedPreferences() {
        accessToken = sharedPreference.accessToken
        countryCode = sharedPreference.countryCode
        mobile = sharedPreference.mobileNumber
    }

    //Hide Back Button
    private fun hideBackButton() {
        binding.btnBack.visibility = View.GONE
    }

    //Set Heading
    @SuppressLint("SetTextI18n")
    private fun setHeading() {
        binding.tvHeading.text = getString(R.string.profile_creation)
        binding.tvMobile.text = "$countryCode-$mobile"
    }

    private val onFileChoose = FilePickUtils.OnFileChoose { fileUri, requestCode ->
        //  showLoading()
        val currentImage = File(fileUri)
        val bitmap = BitmapFactory.decodeFile(fileUri)
        binding.ivProfilePic.setImageBitmap(bitmap)

        profilePic = MultipartBody.Part.createFormData(
            "image",
            currentImage.name,
            currentImage.asRequestBody("image/*".toMediaTypeOrNull())
        )
    }

    private var filePickUtils = FilePickUtils(this, onFileChoose)
    private var lifeCycleCallBackManager = filePickUtils.callBackManager

    //Get Dialog
    private fun getDialog() {
        selectCameraOrGalleryDialog = SelectCameraOrGalleryDialog(this, sharedPreference.nightTheme,
            object : SelectCameraOrGalleryDialog.SelectCameraOrGalleryDialogListener {
                override fun onGalleryClick() {
                    chooseFromGallery()
                }

                override fun onCameraClick() {
                    takePhotoFromCamera()
                }
            })
        selectCameraOrGalleryDialog!!.setCanceledOnTouchOutside(true)
    }

    private fun chooseFromGallery() {
        filePickUtils.requestImageGallery(FilePickUtils.STORAGE_PERMISSION_IMAGE, true, false)
    }

    private fun takePhotoFromCamera() {
        filePickUtils.requestImageCamera(FilePickUtils.CAMERA_PERMISSION, true, false)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (lifeCycleCallBackManager != null) {
            lifeCycleCallBackManager.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (lifeCycleCallBackManager != null) {
            lifeCycleCallBackManager.onActivityResult(requestCode, resultCode, data)
        }

        if (requestCode == placePickerRequest) {
            if (resultCode == RESULT_OK) {
                val place = PlacePicker.getPlace(data, this)
                val placeAddress = String.format("%s", place.address)
                if (place.name == "")
                    binding.etAddress.setText(placeAddress)
                else
                    binding.etAddress.setText(place.name)
                binding.etAddress.requestFocus()
            }
        }
    }

    //Observer
    private fun observer() {
        // Profile response
        signInToHomeViewModel.createProfileResponse.observe(this) {
            if ((it.result.image != null) && (it.result.image != "")) {
                sharedPreference.profilePic = it.result.image
            }

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
            userModel.uid = sharedPreference.userId.toString()
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

        signInToHomeViewModel.onError.observe(this) {
            hideLoading()
            showError(this, binding.rootLayoutProfileCreation, it)
        }
    }

    //All Control Defines Here
    override fun initControl() {
        binding.tvEditProfilePic.setOnClickListener(this)
        binding.ivMap.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)
        binding.ivMap.setOnClickListener(this)
    }

    //OnClick Listener Function
    override fun onClick(v: View) {
        when (v.id) {
            R.id.tvEditProfilePic -> {
                selectCameraOrGalleryDialog!!.show()
            }

            R.id.ivMap -> {
                placePicker()
            }

            R.id.btnSave -> {
                if (isValidInput()) {
                    createProfileApi()
                }
            }
        }
    }

    //Address Picker
    private fun placePicker() {
        val builder: PlacePicker.IntentBuilder = PlacePicker.IntentBuilder()
        startActivityForResult(builder.build(this), placePickerRequest)
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
        findGender()

        signInToHomeViewModel.createProfileApi(
            accessToken = sharedPreference.accessToken,
            userName = findRequestBody(binding.etName.getString()),
            mobile = findRequestBody(mobile),
            address = findRequestBody(binding.etAddress.getString()),
            gender = findRequestBody(gender),
            image = profilePic
        )
    }

    //Find Gender
    private fun findGender() {
        gender = if (binding.rbFemale.isSelected)
            "Female"
        else
            "Male"
    }
}