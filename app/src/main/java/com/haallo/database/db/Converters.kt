package com.haallo.database.db

import androidx.room.TypeConverter


class Converters {

    @TypeConverter
    fun fromString(value: String): ArrayList<Int> {
        val list = ArrayList<Int>()
        if (value.isNotEmpty()) {
            val mList = value.split(",")
            if (mList.isNotEmpty()) {
                for (str in mList) {
                    list.add(str.toInt())
                }
            }
        }
        return list
    }

    @TypeConverter
    fun toString(list: List<Int>): String {
        return list.joinToString(",") { it.toString() }
    }
}