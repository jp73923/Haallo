package com.haallo.ui.imagecrop

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.haallo.R
import com.haallo.base.BaseActivity
import com.haallo.base.extension.showToast
import com.haallo.base.extension.subscribeAndObserveOnMainThread
import com.haallo.base.extension.throttleClicks
import com.haallo.base.view.cropper.CropImageView
import com.haallo.databinding.ActivityCropImageBinding
import timber.log.Timber
import java.io.File

class ImageCropActivity : BaseActivity(), CropImageView.OnCropImageCompleteListener {

    companion object {
        const val REQUEST_CODE_CROP_IMAGE = 10001
        const val INTENT_EXTRA_FILE_PATH = "INTENT_EXTRA_FILE_PATH"
        private const val INTENT_EXTRA_ASPECT_RATIO_X = "INTENT_EXTRA_ASPECT_RATIO_X"
        private const val INTENT_EXTRA_ASPECT_RATIO_Y = "INTENT_EXTRA_ASPECT_RATIO_Y"
        fun getIntent(context: Context, filePath: String, aspectRatioX: Int = 1, aspectRatioY: Int = 1): Intent {
            val intent = Intent(context, ImageCropActivity::class.java)
            intent.putExtra(INTENT_EXTRA_FILE_PATH, filePath)
            intent.putExtra(INTENT_EXTRA_ASPECT_RATIO_X, aspectRatioX)
            intent.putExtra(INTENT_EXTRA_ASPECT_RATIO_Y, aspectRatioY)
            return intent
        }
    }

    private lateinit var binding: ActivityCropImageBinding
    private var selectedFilePath = ""
    private var aspectRatioX: Int = 1
    private var aspectRatioY: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCropImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        loadDataFromIntent()
    }

    private fun initViews() {
        binding.ivClose.throttleClicks().subscribeAndObserveOnMainThread {
            onBackPressed()
        }.autoDispose()

        binding.ivDone.throttleClicks().subscribeAndObserveOnMainThread {
            val uri = binding.cropImageView.imageUri
            if (uri != null) {
                if (uri.toString().isNotEmpty()) {
                    val file = File(filesDir, System.currentTimeMillis().toString().plus(File(selectedFilePath).extension))
                    binding.cropImageView.saveCroppedImageAsync(Uri.fromFile(file))
                } else {
                    showToast(getString(R.string.msg_can_not_create_destination_file))
                }
            } else {
                showToast(getString(R.string.msg_can_not_create_destination_file))
            }
        }.autoDispose()

        binding.cropImageView.setOnCropImageCompleteListener(this)
    }

    private fun loadDataFromIntent() {
        intent?.let {
            val filePath = it.getStringExtra(INTENT_EXTRA_FILE_PATH)
            if (!filePath.isNullOrEmpty()) {
                selectedFilePath = filePath
                val aspectRatioX = it.getIntExtra(INTENT_EXTRA_ASPECT_RATIO_X, -1)
                val aspectRatioY = it.getIntExtra(INTENT_EXTRA_ASPECT_RATIO_Y, -1)
                if (aspectRatioX != -1 && aspectRatioX > 0 && aspectRatioY != -1 && aspectRatioY > 0) {
                    this.aspectRatioX = aspectRatioX
                    this.aspectRatioY = aspectRatioY
                }
                loadPhoto()
            } else {
                onBackPressed()
            }
        } ?: onBackPressed()
    }

    private fun loadPhoto() {
        binding.apply {
            cropImageView.clearAspectRatio()
            cropImageView.resetCropRect()
            cropImageView.setAspectRatio(aspectRatioX, aspectRatioY)
            val file = File(selectedFilePath)
            cropImageView.setImageUriAsync(Uri.fromFile(file))
        }
    }

    override fun onCropImageComplete(view: CropImageView?, result: CropImageView.CropResult?) {
        result?.uri?.path?.let {
            val filePath = File(it).path
            Timber.tag("<><>").e(filePath)
            val intent = Intent()
            intent.putExtra(INTENT_EXTRA_FILE_PATH, filePath)
            setResult(Activity.RESULT_OK, intent)
            finish()
        } ?: showToast(getString(R.string.msg_can_not_create_destination_file))
    }
}