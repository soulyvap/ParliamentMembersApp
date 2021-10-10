package com.example.parliamentmembersapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.parliamentmembersapp.R
import com.example.parliamentmembersapp.databinding.ActivityWelcomeBinding

/*
* Date: 30.9.2021
* Name: Soulyvanh Phetsarath
* ID: 2012208
* Description: Activity that welcomes the user and gives a short description of the app.
* Only shown on first launch
*/

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityWelcomeBinding>(
            this, R.layout.activity_welcome)

        //starting username activity on click
        binding.btnStart.setOnClickListener {
            startActivity(Intent(this, UsernameActivity::class.java))
            finish()
        }
    }
}