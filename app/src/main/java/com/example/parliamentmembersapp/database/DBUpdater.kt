package com.example.parliamentmembersapp.database

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.parliamentmembersapp.repo.MembersRepo

class DBUpdater(appContext: Context, workerParams: WorkerParameters): CoroutineWorker(appContext, workerParams) {
    private val repo = MembersRepo

    override suspend fun doWork(): Result {
        return try {
            repo.updateDB()
            Result.success()
        } catch (error: Throwable) {
            Result.failure()
        }
    }
}