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
import com.example.parliamentmembersapp.constants.Constants
import com.example.parliamentmembersapp.databinding.OptionsFragmentBinding
import com.example.parliamentmembersapp.repo.MembersRepo


class OptionsFragment : Fragment() {

    private lateinit var viewModel: OptionsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        viewModel = ViewModelProvider(this).get(OptionsViewModel::class.java)

        val binding = DataBindingUtil.inflate<OptionsFragmentBinding>(
            inflater, R.layout.options_fragment, container, false)

        binding.btnParties.setOnClickListener {
            val bundle = bundleOf(Pair(Constants.KEY_SUBGROUP, Constants.VAL_PARTIES))
            view?.findNavController()?.navigate(
                R.id.action_mainFragment_to_subgroupsFragment, bundle)
        }

        binding.btnConstituencies.setOnClickListener {
            val bundle = bundleOf(Pair(Constants.KEY_SUBGROUP, Constants.VAL_CONSTITUENCIES))
            view?.findNavController()?.navigate(
                R.id.action_mainFragment_to_subgroupsFragment, bundle)
        }

        binding.btnAllMembers.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_mainFragment_to_membersFragment)
        }

        binding.btnRandom.setOnClickListener {
            viewModel.members.observe(viewLifecycleOwner, { memberList ->
                val randomPersonNumber = memberList.map { it.personNumber }.random()
                val bundle = bundleOf(Constants.KEY_PERSON_NUM to randomPersonNumber)
                view?.findNavController()
                    ?.navigate(R.id.action_mainFragment_to_detailsFragment, bundle)
            })
        }
        return binding.root
    }

}

class OptionsViewModel(application: Application): AndroidViewModel(application) {

    private var membersRepo = MembersRepo
    val members = membersRepo.membersFromDB
}