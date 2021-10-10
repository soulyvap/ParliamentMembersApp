package com.example.parliamentmembersapp.repos

import android.util.Log
import androidx.lifecycle.Transformations
import androidx.lifecycle.distinctUntilChanged
import com.example.parliamentmembersapp.api.MemberApi
import com.example.parliamentmembersapp.database.Member
import com.example.parliamentmembersapp.database.MemberDB

/*
* Date: 24.8.2021
* Name: Soulyvanh Phetsarath
* ID: 2012208
* Description: Repository to handles queries for Member info in the MemberDB
*/

object MembersRepo {
    private val membersDao = MemberDB.getInstance().memberDao
    private val memberApi = MemberApi
    private const val TAG = "MembersRepo"

    //fetching data from local member DB
    val membersFromDB = membersDao.getAll().distinctUntilChanged()
    val parties = Transformations.map(membersFromDB) { members ->
        members.map { it.party }.toSet().toList().sorted()}.distinctUntilChanged()
    val constituencies = Transformations.map(membersFromDB) { members ->
        members.map { it.constituency }.toSet().toList().sorted()}.distinctUntilChanged()
    //inserting into DB and making sure there is no duplicate
    private suspend fun insertAllMembers(members: Set<Member>) {
        membersDao.insertAll(members)
    }

    //fetching all data online from JSON
    private suspend fun getAllFromJson() = memberApi.retrofitService.getMemberRecords()

    //updating local database with online data from JSON
    suspend fun updateDB(): Boolean {
        return try {
            insertAllMembers(getAllFromJson().toSet())
            true
        } catch (error: Throwable) {
            Log.e(TAG, error.toString())
            false
        }
    }
}