package com.example.parliamentmembersapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.parliamentmembersapp.R
import com.example.parliamentmembersapp.databinding.ActivityUsernameBinding
import com.example.parliamentmembersapp.viewmodels.UsernameActivityViewModel

/*
* Date:
* Name: Soulyvanh Phetsarath
* ID: 2012208
* Description: Activity in which the user can set/change their username
*/

class UsernameActivity : AppCompatActivity() {
    lateinit var binding: ActivityUsernameBinding
    private lateinit var viewModel: UsernameActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(UsernameActivityViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_username)

        val editText = binding.etxtUsername

        //retrieving username if it already exists and displaying it in an EditTextView
        val retrievedUsername = viewModel.retrieveUsername(this)
        editText.setText(retrievedUsername)

        //on click of the save button, making sure some text is input.
        //if it is the first launch, starting main activity.
        //otherwise, simply closing current activity to return to previous fragment/activity
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