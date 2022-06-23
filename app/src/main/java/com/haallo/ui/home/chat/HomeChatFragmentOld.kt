package com.haallo.ui.home.chat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.haallo.R
import com.haallo.base.OldBaseActivity
import com.haallo.base.OldBaseFragment
import com.haallo.constant.IntentConstant
import com.haallo.databinding.FragmentHomeChatBinding
import com.haallo.ui.chat.activity.ChatActivity
import com.haallo.ui.chat.activity.GroupChatActivity
import com.haallo.ui.chat.model.RecentMsgModel
import com.haallo.ui.chat.newChat.NewChatActivityOld
import com.haallo.ui.group.GroupInfoActivityOld
import com.haallo.util.checkContactPermission
import com.haallo.util.showToast

class HomeChatFragmentOld : OldBaseFragment(), View.OnClickListener {

    private var _binding: FragmentHomeChatBinding? = null
    private val binding get() = _binding!!

    //private var isFrom: String = ""
    private var recentChatListAdapter: RecentChatListAdapter? = null
    private val recentChatList: MutableLiveData<ArrayList<RecentMsgModel>> = MutableLiveData()
    private val chatList: ArrayList<RecentMsgModel> = ArrayList()
    private var otherUserIdWithU: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViews()
        initControl()
    }

    //All UI Changes From Here
    override fun initViews() {
        otherUserIdWithU = "u_${sharedPreferenceUtil.userId}"

        this.recentChatList.observe(this.requireActivity()) {
            binding.tvNoRecentChat.visibility = View.GONE
            chatList.clear()
            chatList.addAll(it)
            recentMessageRecycler()
            progressDialog.hide()
        }
    }

    //Recent Message Recycler
    private fun recentMessageRecycler() {
        binding.tvNoRecentChat.visibility = View.GONE
        recentChatListAdapter = RecentChatListAdapter(
            requireContext(),
            activity as OldBaseActivity,
            otherUserIdWithU,
            chatList,
            firebaseDbHandler,
            object : RecentChatListAdapter.RecentChatListListener {
                override fun onChatSelect(
                    otherUserId: String,
                    otherUserName: String,
                    otherUserPic: String,
                    type: String
                ) {
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
            }, object : RecentChatListAdapter.RecentChatListListenerGroup {
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
            })

        hideLoading()
        binding.rvRecentChat.adapter = recentChatListAdapter
    }

    //All Controls Defines From Here
    override fun initControl() {
        binding.ivNewChat.setOnClickListener(this)
    }

    //Onclick Listener
    override fun onClick(v: View) {
        when (v.id) {
            R.id.ivNewChat -> {
                if (checkContactPermission(requireContext())) {
                    startActivity(Intent(context, NewChatActivityOld::class.java))
                }
            }
        }
    }

    fun showActionPrompt(receiverId: String) {
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

    override fun onResume() {
        super.onResume()
        recentChatList.value?.clear()
        firebaseDbHandler.getRecentMessages(
            otherUserIdWithU,
            recentChatList
        )
    }
}