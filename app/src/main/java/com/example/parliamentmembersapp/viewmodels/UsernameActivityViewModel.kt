package com.example.parliamentmembersapp.viewmodels

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.parliamentmembersapp.constants.Constants
import kotlinx.coroutines.launch

/*
* Date: 30.9.2021
* Name: Soulyvanh Phetsarath
* ID: 2012208
* Description: ViewModel for UsernameActivity. Contains methods to retrieve a username from
* shared preferences or save a new one into it.
*/

class UsernameActivityViewModel(application: Application): AndroidViewModel(application) {

    //saving username into shared preferences
    fun saveUsername(username: String, activity: Activity?) {
        viewModelScope.launch {
            val sharedPref = activity?.getSharedPreferences(
                Constants.SP_USERNAME, Context.MODE_PRIVATE)
            sharedPref?.apply { edit()
                .putString(Constants.SP_KEY_USERNAME, username)
                .apply()
            }
        }
    }

    //retrieving username from shared preference if exists or empty string
    fun retrieveUsername(activity: Activity?): String {
        val sharedPref = activity?.getSharedPreferences(Constants.SP_USERNAME, Context.MODE_PRIVATE)
        return sharedPref?.getString(Constants.SP_KEY_USERNAME, "") ?: ""
    }
}