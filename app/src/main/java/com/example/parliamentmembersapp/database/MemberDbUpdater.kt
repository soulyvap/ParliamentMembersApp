package com.example.parliamentmembersapp.database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parliamentmembersapp.api.MemberApi
import com.example.parliamentmembersapp.api.MemberApiService
import com.example.parliamentmembersapp.api.MemberInfo
import kotlinx.coroutines.launch
import java.lang.Exception

class MemberDbUpdater: ViewModel() {
    private val _memberCurrentInfo = MutableLiveData<List<MemberInfo>>()
    val memberCurrentInfo: LiveData<List<MemberInfo>>
        get() = _memberCurrentInfo

    init {
        getMemberInfo()
    }

    fun getMemberInfo() {
        viewModelScope.launch {
            try {
                _memberCurrentInfo.value = MemberApi
                    .retrofitService
                    .getMemberRecords()
            } catch (e: Exception) {
                Log.d("***", e.toString())
                _memberCurrentInfo.value = ArrayList()
            }
        }
    }

    fun getMembers(): List<Member> {
        val members = mutableListOf<Member>()
        for (memberInfo in memberCurrentInfo.value ?: listOf()){
            val (personNumber,
            seatNumber,
            last,
            first,
            party,
            minister,
            picture,
            twitter,
            bornYear,
            constituency) = memberInfo
            members.add(Member(0, personNumber, seatNumber, last, first, party,
                minister, picture, twitter, bornYear, constituency))
        }
        return members
    }

}