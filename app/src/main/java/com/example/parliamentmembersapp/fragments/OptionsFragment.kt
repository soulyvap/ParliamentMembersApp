package com.example.parliamentmembersapp.fragments

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.parliamentmembersapp.R
import com.example.parliamentmembersapp.database.MemberDB
import com.example.parliamentmembersapp.databinding.FragmentOptionsBinding
import com.example.parliamentmembersapp.repo.MembersRepo


class MainFragment : Fragment() {

    lateinit var viewModel: OptionsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        viewModel = ViewModelProvider(this).get(OptionsViewModel::class.java)

        val binding = DataBindingUtil.inflate<FragmentOptionsBinding>(
            inflater, R.layout.fragment_options, container, false)

        binding.btnParties.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_mainFragment_to_partiesFragment)
        }

        binding.btnAllMembers.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_mainFragment_to_membersFragment)
        }

        binding.btnRandom.setOnClickListener {
            viewModel.getAllMembers().observe(viewLifecycleOwner, { memberList ->
                val randomPersonNumber = memberList.map { it.personNumber }.random()
                val bundle = bundleOf("member" to randomPersonNumber)
                view?.findNavController()
                    ?.navigate(R.id.action_mainFragment_to_detailsFragment, bundle)
            })
        }
        return binding.root
    }

}

class OptionsViewModel(application: Application): AndroidViewModel(application) {

    private var membersRepo: MembersRepo

    init {
        val membersDao = MemberDB.getInstance(application).memberDao
        membersRepo = MembersRepo(membersDao)
    }

    fun getAllMembers() = membersRepo.getAllFromDB()
}