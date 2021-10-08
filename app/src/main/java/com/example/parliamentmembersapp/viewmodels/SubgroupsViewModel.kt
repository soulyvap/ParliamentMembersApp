package com.example.parliamentmembersapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.parliamentmembersapp.repos.MembersRepo

class SubgroupsFragmentViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = MembersRepo
    val parties = repo.parties
    val constituencies = repo.constituencies
}