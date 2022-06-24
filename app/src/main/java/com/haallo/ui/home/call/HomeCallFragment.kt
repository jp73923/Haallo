package com.haallo.ui.home.call

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.haallo.base.OldBaseFragment
import com.haallo.databinding.FragmentHomeCallBinding
import com.haallo.ui.call.CallModel
import com.haallo.util.SharedPreferenceUtil

class HomeCallFragment : OldBaseFragment(), View.OnClickListener {

    companion object {
        @JvmStatic
        fun newInstance() = HomeCallFragment()
    }

    private var _binding: FragmentHomeCallBinding? = null
    private val binding get() = _binding!!

    private var myUserIdwithU: String? = null
    private var myUserId: String? = null
    private val recentCallList: MutableLiveData<ArrayList<CallModel>> = MutableLiveData()
    private val chatList: ArrayList<CallModel> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeCallBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViews()
        initControl()
        observer()
    }

    private fun observer() {
        this.recentCallList.observe(this.requireActivity()) {
            binding.tvNoRecentChat.visibility = View.GONE
            chatList.clear()
            chatList.addAll(it)
            recentMessageRecycler()
        }
    }

    val listener = object : CallHistoryAdapter.OnCallInterface {
        override fun onVideoCall(view: View, item: CallModel) {

        }

        override fun onAudioCall(view: View, item: CallModel) {

        }
    }

    private fun recentMessageRecycler() {
        binding.rvRecentChat.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = CallHistoryAdapter(chatList, listener)
        }
    }

    //All UI Changes From Here
    override fun initViews() {
        binding.rvRecentChat.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = CallHistoryAdapter(chatList, listener)
        }
    }

    override fun onResume() {
        super.onResume()
        myUserId = SharedPreferenceUtil.getInstance(requireContext()).userId
        if (myUserId.toString().contains("u_")) {
            myUserIdwithU = "$myUserId"
        } else {
            myUserIdwithU = "u_$myUserId"
        }

        recentCallList.value?.clear()
        firebaseDbHandler.getRecentCall(
            myUserIdwithU,
            recentCallList
        )
    }

    //All Controls Defines From Here
    override fun initControl() {

    }

    //OnClickListener
    override fun onClick(v: View) {

    }
}