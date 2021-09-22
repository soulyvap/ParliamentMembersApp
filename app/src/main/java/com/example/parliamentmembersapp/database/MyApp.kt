package com.example.parliamentmembersapp.database

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider

class MyApp: Application() {
    companion object {
        lateinit var appContext: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }
}