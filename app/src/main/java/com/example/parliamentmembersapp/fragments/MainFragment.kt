package com.example.parliamentmembersapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.parliamentmembersapp.R
import com.example.parliamentmembersapp.databinding.FragmentMainBinding


class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val binding = DataBindingUtil.inflate<FragmentMainBinding>(
            inflater, R.layout.fragment_main, container, false)

        binding.btnParties.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_mainFragment_to_partiesFragment)
        }

        binding.btnAllMembers.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_mainFragment_to_membersFragment)
        }

        return binding.root
    }

}