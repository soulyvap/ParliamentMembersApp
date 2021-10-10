package com.example.parliamentmembersapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.parliamentmembersapp.repos.MembersRepo

/*
* Date: 21.8.2021
* Name: Soulyvanh Phetsarath
* ID: 2012208
* Description: ViewModel for SubgroupsFragment. Contains references to MembersRepo,
* parties livedata and constituencies livedata (livedata returning list of strings)
*/

class SubgroupsFragmentViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = MembersRepo
    val parties = repo.parties
    val constituencies = repo.constituencies
}