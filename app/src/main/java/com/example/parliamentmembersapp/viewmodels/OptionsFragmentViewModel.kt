package com.example.parliamentmembersapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.parliamentmembersapp.repos.MembersRepo

class OptionsFragmentViewModel(application: Application): AndroidViewModel(application) {

    private var membersRepo = MembersRepo
    val members = membersRepo.membersFromDB
}