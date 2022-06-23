package com.haallo.ui.chat.placespicker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.haallo.R
import de.hdodenhof.circleimageview.CircleImageView

class NearbyPlacesAdapter(
    private val context: Context,
    private val places: List<Place>
) : RecyclerView.Adapter<NearbyPlacesAdapter.NearbyPlacesHolder>() {

    var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): NearbyPlacesHolder {
        val row = LayoutInflater.from(parent.context).inflate(R.layout.list_item_place, parent, false)
        return NearbyPlacesHolder(row)
    }

    override fun getItemCount(): Int = places.size

    override fun onBindViewHolder(holder: NearbyPlacesHolder, position: Int) {
        holder.bind(places[position])
    }

    inner class NearbyPlacesHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val ivLocation = view.findViewById(R.id.ivLocation) as CircleImageView
        private val tvPlaceName = view.findViewById(R.id.tvPlaceName) as AppCompatTextView
        private val tvPlaceAddress = view.findViewById(R.id.tvPlaceAddress) as AppCompatTextView

        fun bind(place: Place) {
            tvPlaceName.text = place.name
            tvPlaceAddress.text = place.address
            Glide.with(context).load(place.iconUrl).into(ivLocation)

            itemView.setOnClickListener {
                onClickListener?.onClick(it, place)
            }
        }
    }

    interface OnClickListener {
        fun onClick(view: View, place: Place)
    }
}