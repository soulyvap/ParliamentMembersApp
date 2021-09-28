package com.example.parliamentmembersapp.activities

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import androidx.navigation.NavAction
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import com.example.parliamentmembersapp.R
import com.example.parliamentmembersapp.database.*
import com.example.parliamentmembersapp.databinding.ActivityMainBinding
import com.example.parliamentmembersapp.repo.MembersRepo
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        if (viewModel.noUsernameSet(this)) {
            startActivity(Intent(this, UsernameActivity::class.java))
        } else {
            binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
            val navController = this.findNavController(R.id.fragCont_navHost)
            NavigationUI.setupActionBarWithNavController(this, navController)
            viewModel.membersFromJson.observe(this, {
                viewModel.updateDB()
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_switchUser) {
            val intent = Intent(this, UsernameActivity::class.java)
            startActivity(intent)
            return true
        }
        return false
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.fragCont_navHost)
        return navController.navigateUp()
    }
}

class MainActivityViewModel(application: Application): AndroidViewModel(application) {

    private val repo = MembersRepo
    var membersFromJson = liveData { emit(repo.getAllFromJson()) }

    fun noUsernameSet(activity: Activity?): Boolean {
        val sharedPref = activity?.getSharedPreferences("userPref", Context.MODE_PRIVATE)
        return sharedPref?.getString("username", "") == ""
    }

    fun updateDB() {
        viewModelScope.launch {
            repo.updateDB()
        }
    }
}