package com.example.parliamentmembersapp.viewmodels

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.parliamentmembersapp.constants.Constants
import com.example.parliamentmembersapp.database.DBUpdater

class MainActivityViewModel(application: Application): AndroidViewModel(application) {

    //verifying in sharedPref if username already exists
    fun noUsernameSet(activity: Activity?): Boolean {
        val sharedPref = activity?.getSharedPreferences(Constants.SP_USERNAME, Context.MODE_PRIVATE)
        return sharedPref?.getString(Constants.SP_KEY_USERNAME, "") == ""
    }

    //setting up work manager
    fun setWorkManager(context: Context, lifecycleOwner: LifecycleOwner) {
        WorkManager.getInstance(context)
            .beginUniqueWork("DBUpdater", ExistingWorkPolicy.APPEND_OR_REPLACE,
                OneTimeWorkRequest.from(DBUpdater::class.java)
            ).enqueue().state.observe(lifecycleOwner, { state ->
                Log.d("MainActivity", "DBUpdater: $state")
            })
    }
}