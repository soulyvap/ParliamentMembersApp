package com.example.parliamentmembersapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.distinctUntilChanged
import com.example.parliamentmembersapp.constants.Constants
import com.example.parliamentmembersapp.database.Member
import com.example.parliamentmembersapp.repos.MembersRepo

class MembersFragmentViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = MembersRepo
    private val allMembers = repo.membersFromDB
    private var subgroup: String? = null
    private var subGroupType: String? = null
    private val subgroupLiveData = Transformations.map(repo.membersFromDB)
    { members -> members.filter { member ->
        when (subGroupType) {
            Constants.VAL_PARTIES -> member.party == subgroup
            else -> member.constituency == subgroup
        }
    }}.distinctUntilChanged()
    val membersRequired: LiveData<List<Member>>
        get() = subgroup?.let { subgroupLiveData } ?: allMembers

    fun setSubgroup(subgroupSelected: String, type: String) {
        subgroup = subgroupSelected
        subGroupType = type
    }

    //functions to sort the list of members
    fun sortByFirst(members: List<Member>) = members.sortedBy { it.first }
    fun sortByLast(members: List<Member>) = members.sortedBy { it.last }
    fun sortByAge(members: List<Member>) = members.sortedByDescending { it.bornYear }
    fun sortByConstituency(members: List<Member>) = members.sortedBy { it.constituency }
    fun sortByParty(members: List<Member>) = members.sortedBy { it.party }
    fun sortByPosition(members: List<Member>) = members.sortedByDescending { it.minister }
    fun sortBySeat(members: List<Member>) = members.sortedBy { it.seatNumber }
}