package com.example.parliamentmembersapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.forEach
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parliamentmembersapp.R
import com.example.parliamentmembersapp.adapters.HorizontalAdapter
import com.example.parliamentmembersapp.adapters.MemberAdapter
import com.example.parliamentmembersapp.classes.MyApp
import com.example.parliamentmembersapp.constants.Constants
import com.example.parliamentmembersapp.database.Member
import com.example.parliamentmembersapp.databinding.MembersFragmentBinding
import com.example.parliamentmembersapp.viewmodels.MembersFragmentViewModel
import kotlin.reflect.KFunction1

/*
* Date:
* Name: Soulyvanh Phetsarath
* ID: 2012208
* Description: Fragment in which members of the parliament are listed. Either all of them,
* members of a party or members of a constituency. When a member is clicked, the app navigates
* to the fragment that displays more detailed info of that member
*/

class MembersFragment : Fragment() {

    private lateinit var viewModel: MembersFragmentViewModel
    private lateinit var memberAdapter: MemberAdapter
    private lateinit var orderAdapter: HorizontalAdapter
    private var currentMembers: List<Member> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = DataBindingUtil.inflate<MembersFragmentBinding>(
            inflater, R.layout.members_fragment, container, false
        )

        viewModel = ViewModelProvider(this).get(MembersFragmentViewModel::class.java)

        //if the previous destination was a subgroup fragment, retrieving a string representing
        //the item that was selected (a party or a constituency) and the type ("party"or "constituency")
        arguments?.let { bundle ->
            retrieveSubgroup(bundle)
        }

        //setting adapter for the recyclerView listing the members of parliament
        setMemberAdapter(binding)

        //setting adapter for the recyclerView which contains the sorting buttons
        val orderCriteriaList = Constants.ORDER_LIST
        setOrderAdapter(binding, orderCriteriaList)

        //observing the livedata for the members required (either all the members or
        //the members of a given party/constituency)
        viewModel.membersRequired.observe(viewLifecycleOwner, Observer { initialMembers ->
            val initialListSorted = viewModel.sortByFirst(initialMembers)
            var membersDisplayed = initialListSorted
            memberAdapter.updateMembers(membersDisplayed)

            //setting onClickListener for sorting of member list
            orderAdapter.onItemClick = { orderCriteria, view ->
                //getSorter() returns a sorting function according to the item of the
                //order recyclerView that was clicked
                val sorter: KFunction1<List<Member>, List<Member>> = getSorter(orderCriteria)
                //the list of members is then updated by applying the sorter to it, which returns
                //the sorted list
                membersDisplayed = sortAndUpdate(memberAdapter.members, sorter)
                changeToActiveColor(binding, view)
            }

            //setting searchView to filter member list according to query (first name,
            //last name, constituency, party, birth year, seat number)
            binding.searchView.setOnQueryTextListener(
                OnQueryListener(memberAdapter, initialListSorted)
            )

            //button to scroll to the top of the Member recyclerView
            binding.fabToTop.setOnClickListener {
                binding.rvMembers.scrollToPosition(0)
            }
        })

        return binding.root
    }

    //set the subgroup to be displayed
    private fun retrieveSubgroup(bundle: Bundle) {
        val subgroup = bundle.get(Constants.KEY_SUBGROUP) as String
        val type = bundle.get(Constants.KEY_SUBGROUP_TYPE) as String
        viewModel.setSubgroup(subgroup, type)
    }

    //setting up adapter for Member recyclerView with onClickListener
    private fun setMemberAdapter(binding: MembersFragmentBinding) {
        memberAdapter = MemberAdapter(currentMembers).apply {
            //defining the onItemClick function with the invoked (pre-determined)
            //parameter Member. when an item in the RV is clicked,
            //the app navigates to the details fragment and displays the chosen
            //member's info according to its personNumber
            onItemClick = {
                val bundle = bundleOf(Constants.KEY_PERSON_NUM to it.personNumber)
                view?.findNavController()
                    ?.navigate(R.id.action_membersFragment_to_detailsFragment, bundle)
            }
        }
        binding.rvMembers.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = memberAdapter
        }
    }

    //set adapter for sorting buttons recyclerView
    private fun setOrderAdapter(binding: MembersFragmentBinding, orderCriteriaList: List<String>) {
        orderAdapter = HorizontalAdapter(orderCriteriaList)
        binding.rvOrder.apply {
            layoutManager = LinearLayoutManager(
                activity,
                LinearLayoutManager.HORIZONTAL, false
            )
            adapter = orderAdapter
        }
    }

    //change sorting button color to show it is the active one
    private fun changeToActiveColor(binding: MembersFragmentBinding, view: View?) {
        val defaultBackgroundColor = ContextCompat.getColor(
            MyApp.appContext, R.color.light_grey
        )
        val activeBackgroundColor = ContextCompat.getColor(
            MyApp.appContext, android.R.color.holo_blue_dark
        )
        binding.rvOrder.forEach {
            (it as CardView).setCardBackgroundColor(defaultBackgroundColor)
        }
        (view as CardView).setCardBackgroundColor(activeBackgroundColor)
    }

    //returns the sorting function required according to the string provided
    private fun getSorter(orderCriteria: String) = when (orderCriteria) {
        Constants.ORDER_FIRST -> viewModel::sortByFirst
        Constants.ORDER_LAST -> viewModel::sortByLast
        Constants.ORDER_AGE -> viewModel::sortByAge
        Constants.ORDER_PARTY -> viewModel::sortByParty
        Constants.ORDER_CONSTITUENCY -> viewModel::sortByConstituency
        Constants.ORDER_POSITION -> viewModel::sortByPosition
        else -> viewModel::sortBySeat
    }

    //higher order function to sort members and update the recyclerView
    private fun sortAndUpdate(memberList: List<Member>, sorting: (List<Member>) -> List<Member>)
            : List<Member> {
        val sortedList = sorting(memberList)
        memberAdapter.updateMembers(sortedList)
        return sortedList
    }

/*
* Date:
* Name: Soulyvanh Phetsarath
* ID: 2012208
* Description: DAO for Member instances
*/
    class OnQueryListener(
        private val memberAdapter: MemberAdapter,
        private val initialListSorted: List<Member>
    ) : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?) = false
        override fun onQueryTextChange(newText: String?): Boolean {
            //reset member list if searchView is empty
            if (newText.isNullOrBlank()) {
                memberAdapter.updateMembers(initialListSorted)
            } else {
                //filtering according to searchView input
                val filtered = initialListSorted.filter { member ->
                    member.first.lowercase().contains(newText.lowercase())
                            || member.last.lowercase().contains(newText.lowercase())
                            || member.constituency.lowercase().contains(newText.lowercase())
                            || member.party.lowercase().contains(newText.lowercase())
                            || member.bornYear == newText.toIntOrNull()
                            || member.seatNumber == newText.toIntOrNull()
                }
                memberAdapter.updateMembers(filtered)
            }
            return false
        }
    }
}

