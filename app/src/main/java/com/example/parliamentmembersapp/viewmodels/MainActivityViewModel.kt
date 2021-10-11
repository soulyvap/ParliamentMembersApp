package com.example.parliamentmembersapp.viewmodels

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.parliamentmembersapp.constants.Constants

/*
* Date: 21.8.2021
* Name: Soulyvanh Phetsarath
* ID: 2012208
* Description: ViewModel for MainActivity. Contains methods to check whether a username has
* been set already
*/

class MainActivityViewModel(application: Application): AndroidViewModel(application) {

    //verifying in sharedPref if username already exists
    fun noUsernameSet(activity: Activity?): Boolean {
        val sharedPref = activity?.getSharedPreferences(Constants.SP_USERNAME, Context.MODE_PRIVATE)
        return sharedPref?.getString(Constants.SP_KEY_USERNAME, "") == ""
    }
}