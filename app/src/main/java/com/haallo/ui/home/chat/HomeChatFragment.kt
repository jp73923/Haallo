package com.haallo.ui.home.chat

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.haallo.R
import com.haallo.api.chat.model.RecentMessageModel
import com.haallo.base.BaseFragment
import com.haallo.base.extension.startActivityWithDefaultAnimation
import com.haallo.base.extension.subscribeAndObserveOnMainThread
import com.haallo.base.extension.throttleClicks
import com.haallo.constant.IntentConstant
import com.haallo.databinding.FragmentHomeChatBinding
import com.haallo.ui.archivedchat.ArchivedChatActivity
import com.haallo.ui.chat.activity.ChatActivity
import com.haallo.ui.chat.activity.GroupChatActivity
import com.haallo.ui.group.GroupInfoActivityOld
import com.haallo.ui.home.chat.view.RecentChatListAdapter
import com.haallo.ui.newchat.NewChatActivity
import com.haallo.ui.starredchat.StarredChatActivity
import com.haallo.util.showLongToast
import com.haallo.util.showToast
import com.tbruyelle.rxpermissions2.Permission
import com.tbruyelle.rxpermissions2.RxPermissions

class HomeChatFragment : BaseFragment(), RecentChatListAdapter.RecentChatListListener,
    RecentChatListAdapter.RecentChatListListenerGroup {

    companion object {
        @JvmStatic
        fun newInstance() = HomeChatFragment()
    }

    private var _binding: FragmentHomeChatBinding? = null
    private val binding get() = _binding!!

    private var recentChatListAdapter: RecentChatListAdapter? = null
    private var otherUserIdWithU: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenToViewEvent()
    }

    override fun onResume() {
        super.onResume()
        loadRecentChatList(requireContext())
    }

    private fun listenToViewEvent() {
        otherUserIdWithU = "u_${sharedPreferenceUtil.userId}"

        binding.ivSearch.throttleClicks().subscribeAndObserveOnMainThread {

        }.autoDispose()

        binding.fabNewChat.throttleClicks().subscribeAndObserveOnMainThread {
            checkContactPermission(requireContext())
        }.autoDispose()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.position?.let {
                    when (it) {
                        1 -> {
                            startActivityWithDefaultAnimation(StarredChatActivity.getIntent(requireContext()))
                        }
                        2 -> {
                            startActivityWithDefaultAnimation(ArchivedChatActivity.getIntent(requireContext()))
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
    }

    private fun loadRecentChatList(context: Context) {
        val recentChatList: MutableLiveData<ArrayList<RecentMessageModel>> = MutableLiveData()
        firebaseDbHandler.getRecentMessages(otherUserIdWithU, recentChatList)
        recentChatList.observe(this.requireActivity()) {
            if (it.isNullOrEmpty()) {
                binding.tvNoRecentChat.visibility = View.VISIBLE
            } else {
                binding.tvNoRecentChat.visibility = View.GONE
            }
            recentChatListAdapter = RecentChatListAdapter(context, otherUserIdWithU, it, firebaseDbHandler, this, this)
            binding.rvRecentChat.adapter = recentChatListAdapter
        }
    }

    private fun checkContactPermission(context: Context) {
        RxPermissions(this).requestEachCombined(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
        ).subscribe { permission: Permission ->
            if (permission.granted) {
                startActivityWithDefaultAnimation(NewChatActivity.getIntent(context))
            } else {
                if (permission.shouldShowRequestPermissionRationale) {
                    showLongToast(getString(R.string.msg_please_allow_contact_permission_new_group))
                } else {
                    showLongToast(getString(R.string.msg_allow_permission_from_settings))
                }
            }
        }.autoDispose()
    }

    override fun onChatSelect(otherUserId: String, otherUserName: String, otherUserPic: String, type: String) {
        //First manage proper loading of recent chat list with proper model class and later open chat activity
        return

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
            showToast(getString(R.string.msg_chat_removed))
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