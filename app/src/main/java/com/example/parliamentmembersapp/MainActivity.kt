package com.example.parliamentmembersapp

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.parliamentmembersapp.database.Member
import com.example.parliamentmembersapp.database.MemberDB
import com.example.parliamentmembersapp.database.MemberDbUpdater
import com.example.parliamentmembersapp.database.MyApp
import com.example.parliamentmembersapp.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var updaterVM: MemberDbUpdater
    lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updaterVM = ViewModelProvider(this).get(MemberDbUpdater::class.java)
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        updaterVM.memberCurrentInfo.observe(this, {
            viewModel.updateMembers(updaterVM.getMembers())
        })
    }
}

class MainActivityViewModel(application: Application): AndroidViewModel(application) {
    fun updateMembers(members: List<Member>) {
        val context = getApplication<Application>().applicationContext
        viewModelScope.launch {
            MemberDB.getInstance(context)
                .memberDao
                .insertAll(members)
        }
    }
}