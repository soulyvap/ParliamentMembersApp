package com.example.parliamentmembersapp.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.parliamentmembersapp.constants.Constants
import com.example.parliamentmembersapp.database.DBUpdater
import com.example.parliamentmembersapp.database.Member
import com.example.parliamentmembersapp.repos.MembersRepo
import kotlinx.coroutines.launch

/*
* Date: 21.8.2021
* Name: Soulyvanh Phetsarath
* ID: 2012208
* Description: ViewModel for MembersFragment. Contains references to Members repo, livedata
* as well as data about the subgroup of members to be displayed (party or constituency).
* Also contains methods to sort the members list.
*/

class MembersFragmentViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = MembersRepo
    private val allMembers = repo.membersFromDB

    //variables to indicate which member subgroup was chosen to be displayed
    private var subgroup: String? = null
    private var subGroupType: String? = null
    //according to the selected subgroup the livedata changes
    private val subgroupLiveData = Transformations.map(repo.membersFromDB) { members ->
        members.filter { member -> when (subGroupType) {
            Constants.VAL_PARTIES -> member.party == subgroup
            else -> member.constituency == subgroup
        }}}.distinctUntilChanged()

    //if a subgroup was selected to be displayed (party or constituency) then the
    //subgroup livedata is the one to be observed, else all members will be observed
    val membersRequired: LiveData<List<Member>>
        get() = subgroup?.let { subgroupLiveData } ?: allMembers

    //livedata to control the refreshing animation when the recyclerView is pulled down
    private val _refreshed = MutableLiveData<Boolean>()
    val refreshed: LiveData<Boolean>
        get() = _refreshed
    fun setRefreshed (boolean: Boolean) {
        _refreshed.value = boolean
    }
    init {
        setRefreshed(false)
    }

    //set the variables for the selected subgroup
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

    //update the database from JSON and stop the refreshing animation when it is done
    fun updateDB() {
        viewModelScope.launch {
            if (repo.updateDB()) {
                setRefreshed(true)
            }
        }
    }
}