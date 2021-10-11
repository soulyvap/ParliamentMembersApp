package com.example.parliamentmembersapp.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.parliamentmembersapp.classes.MyApp

/*
* Date: 22.8.2021
* Name: Soulyvanh Phetsarath
* ID: 2012208
* Description: Room database containing 3 tables: information about MP's, MP ratings and comments
* about MP's
*/

@Database(entities = [Member::class, MemberComment::class, MemberRating::class],
    version = 3, exportSchema = false )
@TypeConverters(Converters::class)
abstract class MemberDB: RoomDatabase() {
    abstract val memberDao: MemberDao
    abstract val ratingDao: RatingDao
    abstract val commentDao: CommentDao

    companion object {
        @Volatile
        private var INSTANCE: MemberDB? = null
        fun getInstance(): MemberDB {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        MyApp.appContext, MemberDB::class.java, "member_database")
                        .fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}