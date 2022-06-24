package com.haallo.ui.home.chat.broadcast

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.haallo.R
import com.haallo.api.chat.model.RecentMessageModel
import com.haallo.base.OldBaseFragment
import com.haallo.constant.IntentConstant
import com.haallo.databinding.FragmentBroadcastBinding
import com.haallo.ui.chat.activity.ChatActivity
import com.haallo.ui.chat.activity.GroupChatActivity
import com.haallo.ui.chat.newChat.NewChatActivityOld
import com.haallo.ui.group.GroupInfoActivityOld
import com.haallo.ui.home.chat.broadcast.view.RecentChatListAdapter
import com.haallo.util.checkContactPermission
import com.haallo.util.showToast

class BroadCastFragment : OldBaseFragment(), View.OnClickListener,
    RecentChatListAdapter.RecentChatListListener,
    RecentChatListAdapter.RecentChatListListenerGroup {

    companion object {
        @JvmStatic
        fun newInstance() = BroadCastFragment()
    }

    private var _binding: FragmentBroadcastBinding? = null
    private val binding get() = _binding!!

    private var recentChatListAdapter: RecentChatListAdapter? = null
    private val recentChatList: MutableLiveData<ArrayList<RecentMessageModel>> = MutableLiveData()
    private var chatList: ArrayList<RecentMessageModel> = ArrayList()
    private var otherUserIdWithU: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentBroadcastBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViews()
        initControl()
    }

    override fun onResume() {
        super.onResume()
        recentChatList.value?.clear()
        firebaseDbHandler.getRecentMessages(otherUserIdWithU, recentChatList)
    }

    override fun initViews() {
        otherUserIdWithU = "u_${sharedPreferenceUtil.userId}"

        recentChatList.observe(this.requireActivity()) {
            binding.tvNoRecentChat.visibility = View.GONE
            chatList.clear()
            chatList = it
            recentChatListAdapter = RecentChatListAdapter(requireContext(), otherUserIdWithU, chatList, firebaseDbHandler, this, this)
            binding.rvRecentChat.adapter = recentChatListAdapter

            hideLoading()
            progressDialog.hide()
        }
    }

    override fun initControl() {
        binding.fabNewChat.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fabNewChat -> {
                if (checkContactPermission(requireContext())) {
                    startActivity(Intent(context, NewChatActivityOld::class.java))
                }
            }
        }
    }

    override fun onChatSelect(otherUserId: String, otherUserName: String, otherUserPic: String, type: String) {
        if (type == "click") {
            startActivity(
                Intent(context, ChatActivity::class.java)
                    .putExtra(IntentConstant.OTHER_USER_ID, otherUserId)
                    .putExtra(IntentConstant.OTHER_USER_NAME, otherUserName)
                    .putExtra(IntentConstant.OTHER_USER_IMAGE, otherUserPic)
            )
        } else {
            showActionPrompt(otherUserId)
        }
    }

    override fun onChatSelect(
        otherUserId: String,
        otherUserName: String,
        otherUserPic: String,
        leaved: String?, type: String
    ) {
        if (type == "click") {
            startActivity(
                Intent(context, GroupChatActivity::class.java)
                    .putExtra(IntentConstant.OTHER_USER_ID, otherUserId)
                    .putExtra(IntentConstant.LEAVED, leaved)
                    .putExtra(IntentConstant.OTHER_USER_NAME, otherUserName)
                    .putExtra(IntentConstant.OTHER_USER_IMAGE, otherUserPic)
            )
        } else {
            showActionPrompt(otherUserId)
            //  firebaseDbHandler.deleteChat(otherUserId)
            // showToast(otherUserName)
        }
    }

    private fun showActionPrompt(receiverId: String) {
        val view = layoutInflater.inflate(R.layout.action_prompt_chat_list, null)
        val dialog = BottomSheetDialog(this.requireActivity())
        dialog.setContentView(view)

        val clear = dialog.findViewById<TextView>(R.id.clear)
        val info = dialog.findViewById<TextView>(R.id.info)

        clear?.setOnClickListener {
            firebaseDbHandler.deleteChat("u_" + sharedPreferenceUtil.userId, receiverId)
            showToast("Chat Removed !", requireContext())
            recentChatListAdapter?.notifyDataSetChanged()
            dialog.dismiss()
        }
        info?.setOnClickListener {
            startActivity(
                Intent(this.requireActivity(), GroupInfoActivityOld::class.java)
                    .putExtra(IntentConstant.GROUP_ID, receiverId)
            )
            dialog.dismiss()
        }
        dialog.show()
    }
}