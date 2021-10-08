package com.example.parliamentmembersapp.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.navigation.findNavController
import androidx.recyclerview.widget.*
import com.example.parliamentmembersapp.R
import com.example.parliamentmembersapp.adapters.SubgroupAdapter
import com.example.parliamentmembersapp.constants.Constants
import com.example.parliamentmembersapp.databinding.SubgroupsFragmentBinding
import com.example.parliamentmembersapp.viewmodels.SubgroupsFragmentViewModel

/*
* Date:
* Name: Soulyvanh Phetsarath
* ID: 2012208
* Description: Fragment that displays all parties or constituencies (according to the users choice)
* in a recyclerView
*/

class SubgroupsFragment : Fragment() {

    private lateinit var viewModel: SubgroupsFragmentViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        val binding = DataBindingUtil.inflate<SubgroupsFragmentBinding>(
            inflater, R.layout.subgroups_fragment, container, false)

        viewModel = ViewModelProvider(this).get(SubgroupsFragmentViewModel::class.java)

        //retrieving the name of the subgroup (parties or constituencies) to be listed
        //setting the right list to the recyclerView adapter
        when (val subgroup = arguments?.get(Constants.KEY_SUBGROUP) as String) {
            Constants.VAL_PARTIES -> setAdapter(binding, subgroup, viewModel.parties)
            else -> setAdapter(binding, subgroup, viewModel.constituencies)
        }

        return binding.root
    }

    private fun setAdapter(binding: SubgroupsFragmentBinding, subgroup: String,
                           liveData: LiveData<List<String>>) {
        liveData.observe(viewLifecycleOwner, { list ->
            binding.rvSubgroup.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = SubgroupAdapter(list, subgroup).apply {
                    //defining the onItemClick function with the invoked (pre-determined)
                    //parameter string that represents a party or a constituency
                    //when an item in the RV is clicked, a bundle is sent with 2 pairs:
                    //the name of the party/constituency and the type of subgroup it belongs to
                    onItemClick = { subgroupName ->
                        val bundle = bundleOf(Constants.KEY_SUBGROUP to subgroupName,
                        Constants.KEY_SUBGROUP_TYPE to subgroup)
                        view?.findNavController()
                            ?.navigate(R.id.action_subgroupsFragment_to_membersFragment, bundle)
                    }
                }
            }
        })
    }
}





