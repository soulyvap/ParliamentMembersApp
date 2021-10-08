package com.example.parliamentmembersapp.classes

import android.app.Application
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.parliamentmembersapp.database.DBUpdater

/*
* Date:
* Name: Soulyvanh Phetsarath
* ID: 2012208
* Description: Custom application class that contains the app's context. Makes it easier to obtain
* the app's context when it is not an activity or a fragment (e.g. create a DB instance)
*/

class MyApp: Application() {
    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }
}