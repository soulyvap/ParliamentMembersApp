package com.example.parliamentmembersapp.database

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.parliamentmembersapp.api.MemberApi


class MembersRepo (private val membersDao: MemberDao) {
    private val memberApi = MemberApi

    suspend fun getAllFromJson() = memberApi.retrofitService.getMemberRecords()

    fun getAllFromDB() = membersDao.getAll()

    fun getMembersByParty(partyName: String) = membersDao.getMembersByParty(partyName)

    fun getMemberByNumber(personNumber: Int) = membersDao.getMemberByNumber(personNumber)

    suspend fun insertAllMembers(members: Set<Member>) {
        membersDao.insertAll(members)
    }

    suspend fun updateDB() {
        insertAllMembers(getAllFromJson().toSet())
    }
}