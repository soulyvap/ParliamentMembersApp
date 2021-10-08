package com.example.parliamentmembersapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.parliamentmembersapp.R
import com.example.parliamentmembersapp.constants.Constants
import com.example.parliamentmembersapp.databinding.OptionsFragmentBinding
import com.example.parliamentmembersapp.viewmodels.OptionsFragmentViewModel

/*
* Date:
* Name: Soulyvanh Phetsarath
* ID: 2012208
* Description: Fragment where the user selects what they want to see (parties, constituency,
* all members or a random member)
*/

class OptionsFragment : Fragment() {

    private lateinit var viewModel: OptionsFragmentViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        viewModel = ViewModelProvider(this).get(OptionsFragmentViewModel::class.java)

        val binding = DataBindingUtil.inflate<OptionsFragmentBinding>(
            inflater, R.layout.options_fragment, container, false)

        //on click, navigate to the subgroup fragment with a bundle that indicates that
        //parties should be listed in the next destination
        binding.btnParties.setOnClickListener {
            val bundle = bundleOf(Pair(Constants.KEY_SUBGROUP, Constants.VAL_PARTIES))
            view?.findNavController()?.navigate(
                R.id.action_mainFragment_to_subgroupsFragment, bundle)
        }

        //on click, navigate to the subgroup fragment with a bundle that indicates that
        //constituencies should be listed in the next destination
        binding.btnConstituencies.setOnClickListener {
            val bundle = bundleOf(Pair(Constants.KEY_SUBGROUP, Constants.VAL_CONSTITUENCIES))
            view?.findNavController()?.navigate(
                R.id.action_mainFragment_to_subgroupsFragment, bundle)
        }

        //on click, navigate to the members fragment showing all the members of parliament
        binding.btnAllMembers.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_mainFragment_to_membersFragment)
        }

        //on click, navigate to the details fragment showing detailed info of a random member
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