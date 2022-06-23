package com.haallo.ui.chat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.haallo.R
import com.wafflecopter.multicontactpicker.RxContacts.PhoneNumber

class PhoneNumberAdapter(
    private val context: Context,
    private val phoneNumber: ArrayList<PhoneNumber>,
    private val phoneNumberListener: PhoneNumberListener
) : RecyclerView.Adapter<PhoneNumberAdapter.MyViewHolder>() {

    //Inflate view for recycler
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item_phone, p0, false)
        return MyViewHolder(view)
    }

    //Return size
    override fun getItemCount(): Int {
        return phoneNumber.size
    }

    //Bind View Holder
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.apply {
            val type: String? = phoneNumber[position].typeLabel
            if (type != null && type != "") {
                tvType.text = type
            } else {
                tvType.text = context.getString(R.string.not_available)
            }
            val number: String? = phoneNumber[position].number
            if (number != null && number != "") {
                tvType.text = number
            } else {
                tvNumber.text = context.getString(R.string.not_available)
            }
            rlPhone.setOnClickListener {
                phoneNumberListener.phoneSelect(position)
            }
        }
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rlPhone = view.findViewById(R.id.rlPhone) as RelativeLayout
        val tvType = view.findViewById(R.id.tvType) as AppCompatTextView
        val tvNumber = view.findViewById(R.id.tvNumber) as AppCompatTextView
    }

    interface PhoneNumberListener {
        fun phoneSelect(position: Int)
    }
}