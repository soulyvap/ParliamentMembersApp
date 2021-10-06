package com.example.parliamentmembersapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.parliamentmembersapp.R
import com.example.parliamentmembersapp.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityWelcomeBinding>(
            this, R.layout.activity_welcome)

        binding.btnStart.setOnClickListener {
            startActivity(Intent(this, UsernameActivity::class.java))
            finish()
        }
    }
}