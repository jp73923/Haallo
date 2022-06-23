package com.haallo.ui.home.status

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.haallo.R
import com.haallo.base.OldBaseFragment
import com.haallo.constant.Constants
import com.haallo.databinding.FragmentHomeStatusBinding
import com.haallo.ui.chat.activity.ImageDetailActivity
import com.haallo.ui.chat.adapter.FilterItemAdapter
import com.haallo.ui.chat.model.StatusModel
import com.haallo.util.bitmapToFile
import com.imagepicker.FilePickUtils
import net.alhazmy13.imagefilter.ImageFilter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.util.*

class HomeStatusFragmentOld : OldBaseFragment() {

    private var _binding: FragmentHomeStatusBinding? = null
    private val binding get() = _binding!!

    private lateinit var chatViewModel: com.haallo.ui.chat.ChatViewModelOld
    private val statusModel = StatusModel()
    private lateinit var firebaseDatabaseReference: DatabaseReference

    var imageUrl = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViews()
        initControl()
        observers()
        getAllStories("u_" + sharedPreferenceUtil.userId)
    }

    //All UI Changes From Here
    override fun initViews() {
        chatViewModel = ViewModelProvider(this).get(com.haallo.ui.chat.ChatViewModelOld::class.java)
        firebaseDatabaseReference = FirebaseDatabase.getInstance().reference
    }

    //All Controls Defines From Here
    override fun initControl() {
        binding.ivStoryImage.setOnClickListener {
            val intent = Intent(this.requireActivity(), ImageDetailActivity::class.java)
            intent.putExtra("image", imageUrl)
            startActivity(intent)
        }
        binding.edit.setOnClickListener {
            val items = arrayOf(getString(R.string.pick_image), getString(R.string.camera))
            val builder = AlertDialog.Builder(this.context)
            builder.setTitle(Html.fromHtml(getString(R.string.complete_action_using)))
            builder.setItems(items) { _, position ->
                when (position) {
                    0 -> chooseImageFromGallery()
                    1 -> checkPermissionCamera()
                    else -> return@setItems
                }
            }.show()
        }
    }

    private fun checkPermissionCamera() {
        if (ActivityCompat.checkSelfPermission(this.requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                FilePickUtils.CAMERA_PERMISSION
            )
        } else {
            takePhotoFromCamera()
        }
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, Constants.CAMERA)
    }

    fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager()
        } else {
            val readCheck = ContextCompat.checkSelfPermission(this.requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
            val writeCheck = ContextCompat.checkSelfPermission(this.requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
            return readCheck == PackageManager.PERMISSION_GRANTED && writeCheck == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun chooseImageFromGallery() {
        /*   filePickUtils.requestImageGallery(
               FilePickUtils.STORAGE_PERMISSION_IMAGE,
               true,
               false
           )*/
        ImagePicker.with(this)
            .crop()                    //Crop image(Optional), Check Customization for more option
            //Final image size will be less than 1 MB(Optional)
            .maxResultSize(
                1080,
                1080
            )    //Final image resolution will be less than 1080 x 1080(Optional)
            .galleryOnly()
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.CAMERA) {
            if (data != null) {
                val thumbnail = data.extras!!.get("data") as Bitmap
                filterImagePrompt(thumbnail)
                /*  Log.e("fdfs", thumbnail.toString())
                  val bStream = ByteArrayOutputStream()
                  thumbnail.compress(Bitmap.CompressFormat.PNG, 100, bStream)
                  val byteArray: ByteArray = bStream.toByteArray()*/
                /* progressDialog.show()
                 chatViewModel.getUrlApi(sharedPreferenceUtil.accessToken, image)*/
            }
        } else if (requestCode == ImagePicker.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val fileUri = data!!.data
            val currentImage: File?
            currentImage = File(fileUri?.path)
            val image = MultipartBody.Part.createFormData("image", currentImage.name, currentImage.asRequestBody("image/*".toMediaTypeOrNull()))
            filterImagePrompt(getThumbnail(fileUri)!!)
            /* val bStream = ByteArrayOutputStream()
             thumbnail.compress(Bitmap.CompressFormat.PNG, 100, bStream)
             val byteArray: ByteArray = bStream.toByteArray()
             val intent = Intent(this.requireActivity(), FilterActivity::class.java)
             intent.putExtra("image", byteArray)
             this.requireActivity().startActivityForResult(intent, 200)*/
            /* progressDialog.show()
             chatViewModel.getUrlApi(sharedPreferenceUtil.accessToken, image)
 */
        } else {
            Log.e("Fdsfsdf", data.toString())
        }
    }

    fun observers() {
        chatViewModel.getFileToUrlResponse.observe(this.requireActivity(), Observer {
            progressDialog.hide()
            statusModel.content = it.result
            statusModel.duration = "0"
            statusModel.mobile = sharedPreferenceUtil.mobileNumber
            statusModel.name = sharedPreferenceUtil.name
            statusModel.profile_image = sharedPreferenceUtil.profilePic
            statusModel.thumbImg = it.result
            statusModel.type = "1"

            val instance = Calendar.getInstance()
            statusModel.timeStamp = instance.timeInMillis.toString()
            firebaseDbHandler.createStatus(statusModel, "u_" + sharedPreferenceUtil.userId)

        })
        chatViewModel.error.observe(this.requireActivity()) {
            progressDialog.hide()
        }
    }


    fun createStoryView(uris: ArrayList<StatusModel?>) {
        //storyView.resetStoryVisits()
        binding.ivStoryImage.visibility = View.VISIBLE
        binding.ivGroupIcon.visibility = View.GONE
        imageUrl = uris[uris.size - 1]!!.thumbImg!!
        Glide.with(this.requireActivity()).load(uris[uris.size - 1]!!.thumbImg)
            .placeholder(R.drawable.logo_haallo).into(binding.ivStoryImage)

        val difference: Long = Calendar.getInstance().timeInMillis - uris[uris.size - 1]!!.timeStamp!!.toLong()
        val days = (difference / (1000 * 60 * 60 * 24)).toInt()
        var hours = ((difference - 1000 * 60 * 60 * 24 * days) / (1000 * 60 * 60)) as Long
        val min = (difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours) as Long / (1000 * 60)
        hours = if (hours < 0) -hours else hours
        Log.i("======= Hours", " :: $hours")
        when {
            days > 0 -> {
                binding.time.text = "$days day ago"
            }
            hours > 0 -> {
                binding.time.text = "$hours hour ago"
            }
            min > 0 -> {
                binding.time.text = "$min min ago"
            }
            else -> {
                binding.time.text = "Just now"
            }
        }
    }

    private fun getAllStories(userId: String) {
        var statusData: MutableLiveData<ArrayList<StatusModel?>> = MutableLiveData<ArrayList<StatusModel?>>()
        firebaseDbHandler.getAllStories(userId, statusData)
        statusData.observe(this.requireActivity(), Observer {
            //showToast(it.size.toString())
            if (it.size > 0) {
                createStoryView(it)
            } else {
                binding.ivStoryImage.visibility = View.GONE
                binding.ivGroupIcon.visibility = View.VISIBLE
            }
        })
    }

    private fun filterImagePrompt(thumbnail: Bitmap) {
        var filteredImageBitmap: Bitmap? = null
        val view = layoutInflater.inflate(R.layout.activity_filter, null)
        val dialog = BottomSheetDialog(this.requireActivity())
        dialog.setContentView(view)

        val filtered_image = dialog.findViewById<ImageView>(R.id.filtered_image)
        val recyclerView = dialog.findViewById<RecyclerView>(R.id.recyclerView)
        val done = dialog.findViewById<TextView>(R.id.done)
        val back = dialog.findViewById<ImageView>(R.id.back)
        filteredImageBitmap = ImageFilter.applyFilter(thumbnail, ImageFilter.Filter.AVERAGE_BLUR)
        filtered_image!!.setImageBitmap(ImageFilter.applyFilter(thumbnail, ImageFilter.Filter.AVERAGE_BLUR))
        recyclerView!!.layoutManager = LinearLayoutManager(this.requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter =
            FilterItemAdapter(this.requireContext(), thumbnail!!, resources.getStringArray(R.array.style), object : FilterItemAdapter.FilterSelectListener {
                override fun filterSelect(position: Int, bitmap: Bitmap) {
                    filteredImageBitmap = updateFilter(bitmap, position)
                    filtered_image.setImageBitmap(filteredImageBitmap)
                }
            })

        back!!.setOnClickListener {
            dialog.dismiss()
        }

        done!!.setOnClickListener {
            val fileUri = bitmapToFile(filteredImageBitmap!!, this.requireActivity())
            val currentImage: File?
            currentImage = File(fileUri.path)

            val image = MultipartBody.Part.createFormData("image", currentImage!!.name, currentImage.asRequestBody("image/*".toMediaTypeOrNull()))

            progressDialog.show()
            chatViewModel.getUrlApi(sharedPreferenceUtil.accessToken, image)

            dialog.dismiss()
        }
        dialog.setCancelable(false)
        dialog.show()

    }

    fun updateFilter(bitmap: Bitmap, value: Int): Bitmap? {
        return when (value) {
            1 -> ImageFilter.applyFilter(bitmap, ImageFilter.Filter.GRAY)
            2 -> ImageFilter.applyFilter(bitmap, ImageFilter.Filter.RELIEF)
            3 -> ImageFilter.applyFilter(bitmap, ImageFilter.Filter.AVERAGE_BLUR, 9)
            4 -> ImageFilter.applyFilter(bitmap, ImageFilter.Filter.OIL, 10)
            5 -> ImageFilter.applyFilter(bitmap, ImageFilter.Filter.NEON, 200, 50, 100)
            6 -> ImageFilter.applyFilter(bitmap, ImageFilter.Filter.PIXELATE, 9)
            7 -> ImageFilter.applyFilter(bitmap, ImageFilter.Filter.TV)
            8 -> ImageFilter.applyFilter(bitmap, ImageFilter.Filter.INVERT)
            9 -> ImageFilter.applyFilter(bitmap, ImageFilter.Filter.BLOCK)
            10 -> ImageFilter.applyFilter(bitmap, ImageFilter.Filter.OLD)
            11 -> ImageFilter.applyFilter(bitmap, ImageFilter.Filter.SHARPEN)
            12 -> {
                val width = bitmap.width
                val height = bitmap.height
                ImageFilter.applyFilter(
                    bitmap,
                    ImageFilter.Filter.LIGHT,
                    width / 2,
                    height / 2,
                    Math.min(width / 2, height / 2)
                )
            }
            13 -> {
                val radius = (bitmap.width / 2 * 95 / 100).toDouble()
                ImageFilter.applyFilter(bitmap, ImageFilter.Filter.LOMO, radius)
            }
            14 -> ImageFilter.applyFilter(bitmap, ImageFilter.Filter.HDR)
            15 -> ImageFilter.applyFilter(bitmap, ImageFilter.Filter.GAUSSIAN_BLUR, 1.2)
            16 -> ImageFilter.applyFilter(bitmap, ImageFilter.Filter.SOFT_GLOW, 0.6)
            17 -> ImageFilter.applyFilter(bitmap, ImageFilter.Filter.SKETCH)
            18 -> ImageFilter.applyFilter(bitmap, ImageFilter.Filter.MOTION_BLUR, 5, 1)
            19 -> ImageFilter.applyFilter(bitmap, ImageFilter.Filter.GOTHAM)
            else -> bitmap
        }
    }

    @Throws(FileNotFoundException::class, IOException::class)
    fun getThumbnail(uri: Uri?): Bitmap? {
        var input: InputStream = context!!.contentResolver.openInputStream(uri!!)!!
        val onlyBoundsOptions = BitmapFactory.Options()
        onlyBoundsOptions.inJustDecodeBounds = true
        onlyBoundsOptions.inDither = true //optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888 //optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions)
        input.close()
        if (onlyBoundsOptions.outWidth == -1 || onlyBoundsOptions.outHeight == -1) {
            return null
        }
        val originalSize =
            if (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) onlyBoundsOptions.outHeight else onlyBoundsOptions.outWidth
        /* val ratio = if (originalSize > THUMBNAIL_SIZE) originalSize / THUMBNAIL_SIZE else 1.0

         bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio)*/
        val bitmapOptions = BitmapFactory.Options()
        bitmapOptions.inDither = true //optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888 //
        input = context!!.contentResolver.openInputStream(uri)!!
        val bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions)
        input.close()
        return bitmap
    }

    private fun getPowerOfTwoForSampleRatio(ratio: Double): Int {
        val k = Integer.highestOneBit(Math.floor(ratio).toInt())
        return if (k == 0) 1 else k
    }
}