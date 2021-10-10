package com.example.parliamentmembersapp.database

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.parliamentmembersapp.classes.MyApp
import com.example.parliamentmembersapp.repos.MembersRepo

/*
* Date: 24.8.2021
* Name: Soulyvanh Phetsarath
* ID: 2012208
* Description: WorkManager that updates the local Member database with data from JSON
*/

class DBUpdater(appContext: Context, workerParams: WorkerParameters): CoroutineWorker(appContext, workerParams) {
    private val repo = MembersRepo

    override suspend fun doWork(): Result {
        return try {
            repo.updateDB()
            Log.d("DBUpdater", "DB updated")
            Result.success()
        } catch (error: Throwable) {
            Result.failure()
        }
    }
}