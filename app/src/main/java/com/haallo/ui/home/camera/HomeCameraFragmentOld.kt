package com.haallo.ui.home.camera

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.haallo.base.OldBaseFragment
import com.haallo.databinding.FragmentHomeCameraBinding

class HomeCameraFragmentOld : OldBaseFragment(), View.OnClickListener {

    private var _binding: FragmentHomeCameraBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViews()
        initControl()
    }

    //All UI Changes From Here
    override fun initViews() {
//        camera
    }

    //All Controls Defines From Here
    override fun initControl() {
//        camera.captureImage { p0, p1 -> showToast("Successfully updated") }
//        camera.captureVideo { cameraKitView, any -> {
//            showToast("Successfully updated")
//        }}
    }

    //OnClickListener
    override fun onClick(v: View) {

    }

    override fun onStart() {
        super.onStart()
        binding.camera.onStart();
    }

    override fun onResume() {
        super.onResume()
        binding.camera.onResume()
    }

    override fun onPause() {
        binding. camera.onPause()
        super.onPause()
    }

    override fun onStop() {
        binding. camera.onStop();
        super.onStop()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        binding.camera.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}