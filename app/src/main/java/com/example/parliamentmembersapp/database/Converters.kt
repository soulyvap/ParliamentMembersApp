package com.example.parliamentmembersapp.database

import androidx.room.TypeConverter
import java.util.*

/*
* Date: 24.8.2021
* Name: Soulyvanh Phetsarath
* ID: 2012208
* Description: Converters for handling data types that are not supported in a Room DB.
*/

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}