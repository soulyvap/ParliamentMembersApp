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
import com.example.parliamentmembersapp.constants.Constants
import com.example.parliamentmembersapp.database.*
import com.example.parliamentmembersapp.databinding.ActivityMainBinding
import com.example.parliamentmembersapp.viewmodels.MainActivityViewModel

/*
* Date:
* Name: Soulyvanh Phetsarath
* ID: 2012208
* Description: Launcher activity which contains the navHost for all the fragments and
* the workManager for updating the Member local database. On first launch, the user
* is redirected to the Welcome Activity.
*/

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        //setting up the work manager to update the data base every time the app is launched
        viewModel.setWorkManager(this, this)

        //verifying if a user name has been setup yet (first launch) then starting the
        //welcome activity if it has not
        if (viewModel.noUsernameSet(this)) {
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        } else {
            //setup binding and navController if a username has already been setup
            binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
            val navController = this.findNavController(R.id.fragCont_navHost)
            NavigationUI.setupActionBarWithNavController(this, navController)
        }
    }

    //inflating the options menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //setting what to do when the menu item is clicked
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_switchUser) {
            val intent = Intent(this, UsernameActivity::class.java)
            startActivity(intent)
            return true
        }
        return false
    }

    //setting up the up button
    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.fragCont_navHost)
        return navController.navigateUp()
    }
}

