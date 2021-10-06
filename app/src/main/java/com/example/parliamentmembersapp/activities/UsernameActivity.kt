package com.example.parliamentmembersapp.activities

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.parliamentmembersapp.R
import com.example.parliamentmembersapp.databinding.ActivityUsernameBinding
import kotlinx.coroutines.launch

class UsernameActivity : AppCompatActivity() {
    lateinit var binding: ActivityUsernameBinding
    private lateinit var viewModel: UsernameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(UsernameViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_username)

        val editText = binding.etxtUsername
        val retrievedUsername = viewModel.retrieveUsername(this)
        editText.setText(retrievedUsername)

        binding.btnSave.setOnClickListener {
            if (editText.text.isNotBlank()) {
                viewModel.saveUsername(editText.text.toString(), this)
                if (retrievedUsername.isBlank()) {
                    startActivity(Intent(this, MainActivity::class.java))
                }
                finish()
            } else {
                Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show()
            }
        }

    }
}

class UsernameViewModel(application: Application): AndroidViewModel(application) {
    fun saveUsername(username: String, activity: Activity?) {
        viewModelScope.launch {
            val sharedPref = activity?.getSharedPreferences(
                "userPref", Context.MODE_PRIVATE)
            sharedPref?.apply { edit()
                .putString("username", username)
                .apply()
            }
        }
    }

    fun retrieveUsername(activity: Activity?): String {
        val sharedPref = activity?.getSharedPreferences("userPref", Context.MODE_PRIVATE)
        return sharedPref?.getString("username", "") ?: ""
    }
}