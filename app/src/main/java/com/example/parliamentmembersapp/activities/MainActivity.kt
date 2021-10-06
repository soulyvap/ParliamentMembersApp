package com.example.parliamentmembersapp.activities

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.*
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.parliamentmembersapp.R
import com.example.parliamentmembersapp.database.*
import com.example.parliamentmembersapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        viewModel.setWorkManager(this, this)

        if (viewModel.noUsernameSet(this)) {
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        } else {
            binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
            val navController = this.findNavController(R.id.fragCont_navHost)
            NavigationUI.setupActionBarWithNavController(this, navController)
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

    fun noUsernameSet(activity: Activity?): Boolean {
        val sharedPref = activity?.getSharedPreferences("userPref", Context.MODE_PRIVATE)
        return sharedPref?.getString("username", "") == ""
    }

    fun setWorkManager(context: Context, lifecycleOwner: LifecycleOwner) {
        WorkManager.getInstance(context)
            .beginUniqueWork("DBUpdater", ExistingWorkPolicy.APPEND_OR_REPLACE,
                OneTimeWorkRequest.from(DBUpdater::class.java)
            ).enqueue().state.observe(lifecycleOwner, Observer { state ->
                Log.d("MainActivity", "DBUpdater: $state")
            })
    }
}