package com.example.parliamentmembersapp.repo

import androidx.lifecycle.Transformations
import com.example.parliamentmembersapp.api.MemberApi
import com.example.parliamentmembersapp.database.Member
import com.example.parliamentmembersapp.database.MemberDB

object MembersRepo {
    private val membersDao = MemberDB.getInstance().memberDao
    private val memberApi = MemberApi
    val membersFromDB = membersDao.getAll()
    val parties = Transformations.map(membersFromDB) { members ->
        members.map { it.party }.toSet().toList().sorted()
    }
    val constituencies = Transformations.map(membersFromDB) { members ->
        members.map { it.constituency }.toSet().toList().sorted()
    }

    private suspend fun getAllFromJson() = memberApi.retrofitService.getMemberRecords()

    private suspend fun insertAllMembers(members: Set<Member>) {
        membersDao.insertAll(members)
    }

    suspend fun updateDB() {
        insertAllMembers(getAllFromJson().toSet())
    }
}