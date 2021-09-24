package com.example.parliamentmembersapp

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.*
import com.example.parliamentmembersapp.database.*
import com.example.parliamentmembersapp.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        viewModel.membersFromJson.observe(this, {
            viewModel.updateDB()
        })

    }
}

class MainActivityViewModel(application: Application): AndroidViewModel(application) {

    private val repo: MembersRepo
    var membersFromJson: LiveData<List<Member>>
    var membersFromDB: LiveData<List<Member>>
    init {
        val memberDB = MemberDB.getInstance(application).memberDao
        repo = MembersRepo(memberDB)
        membersFromJson = liveData { emit(repo.getAllFromJson()) }
        membersFromDB = repo.getAllFromDB()
    }

    fun updateDB() {
        viewModelScope.launch {
            repo.updateDB()
        }
    }
}