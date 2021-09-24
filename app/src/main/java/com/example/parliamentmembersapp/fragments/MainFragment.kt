package com.example.parliamentmembersapp.fragments

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.parliamentmembersapp.MainActivity
import com.example.parliamentmembersapp.MainActivityViewModel
import com.example.parliamentmembersapp.R
import com.example.parliamentmembersapp.databinding.FragmentMainBinding
import kotlinx.coroutines.launch


class MainFragment : Fragment() {

    lateinit var viewModel: MainFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        viewModel = ViewModelProvider(this).get(MainFragmentViewModel::class.java)

        val binding = DataBindingUtil.inflate<FragmentMainBinding>(
            inflater, R.layout.fragment_main, container, false)

        binding.btnParties.setOnClickListener {
            if (binding.etxtUsername.text.isBlank()) {
                binding.etxtUsername.error = "Please enter a username"
            } else {
                viewModel.saveUsername(binding.etxtUsername.text.toString(), activity)
                view?.findNavController()?.navigate(R.id.action_mainFragment_to_partiesFragment)
            }
        }

        binding.btnAllMembers.setOnClickListener {
            if (binding.etxtUsername.text.isBlank()) {
                binding.etxtUsername.error = "Please enter a username"
            } else {
                viewModel.saveUsername(binding.etxtUsername.text.toString(), activity)
                view?.findNavController()?.navigate(R.id.action_mainFragment_to_membersFragment)
            }
        }

        return binding.root
    }

}

class MainFragmentViewModel(application: Application): AndroidViewModel(application) {

    fun saveUsername(username: String, activity: FragmentActivity?) {
        viewModelScope.launch {
            val sharedPref = activity?.getSharedPreferences(
                "userPref", Context.MODE_PRIVATE)

            sharedPref?.apply { edit()
                .putString("username", username)
                .apply()
            }
        }

    }
}