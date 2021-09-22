package com.example.parliamentmembersapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Member::class], version = 1, exportSchema = false )
abstract class MemberDB: RoomDatabase() {
    abstract val memberDao: MemberDao
    companion object {

        @Volatile
        private var INSTANCE: MemberDB? = null

        fun getInstance(context: Context): MemberDB {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context, MemberDB::class.java, "member_database")
                        .fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }


}