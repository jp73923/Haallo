package com.haallo.ui.chat.adapter

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.haallo.R
import net.alhazmy13.imagefilter.ImageFilter

class FilterItemAdapter(
    private val context: Context,
    private val bitmap: Bitmap,
    private val bitmapArrayList: Array<String>,
    private val filterSelectListener: FilterSelectListener
) : RecyclerView.Adapter<FilterItemAdapter.MyViewHolder>() {

    //Inflate view for recycler
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.filter_item_view, p0, false)
        return MyViewHolder(view)
    }

    //Return size
    override fun getItemCount(): Int {
        return bitmapArrayList.size
    }

    //Bind View Holder
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.apply {
            filterName.text = bitmapArrayList[position]
            filterImage.setImageBitmap(updateFilter(bitmap, position))
            filterImage.setOnClickListener {
                filterSelectListener.filterSelect(position, bitmap)
            }
        }
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val filterName = view.findViewById(R.id.filterName) as TextView
        val filterImage = view.findViewById(R.id.filterImage) as ImageView
    }

    interface FilterSelectListener {
        fun filterSelect(position: Int, bitmap: Bitmap)
    }

    private fun updateFilter(bitmap: Bitmap, value: Int): Bitmap? {
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
                    (width / 2).coerceAtMost(height / 2)
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
}