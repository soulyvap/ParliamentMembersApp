package com.example.parliamentmembersapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.parliamentmembersapp.repos.MembersRepo

/*
* Date: 21.8.2021
* Name: Soulyvanh Phetsarath
* ID: 2012208
* Description: ViewModel for OptionsFragment. Contains references to MembersRepo
* and members livedata
*/

class OptionsFragmentViewModel(application: Application): AndroidViewModel(application) {

    private var membersRepo = MembersRepo
    val members = membersRepo.membersFromDB
}