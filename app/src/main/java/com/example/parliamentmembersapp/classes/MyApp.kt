package com.example.parliamentmembersapp.classes

import android.app.Application
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.work.*
import com.example.parliamentmembersapp.database.DBUpdater
import java.util.concurrent.TimeUnit

/*
* Date: 22.8.2021
* Name: Soulyvanh Phetsarath
* ID: 2012208
* Description: Custom application class that contains the app's context. Makes it easier to obtain
* the app's context when it is not an activity or a fragment (e.g. create a DB instance).
* Initiates WorkManager to update MemberDB
*/

class MyApp: Application() {
    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext

        //WorkManager that updates the MemberDB every 15 min
        val updateDbWork = PeriodicWorkRequestBuilder<DBUpdater>(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "DBUpdater", ExistingPeriodicWorkPolicy.KEEP,
            updateDbWork)
    }
}