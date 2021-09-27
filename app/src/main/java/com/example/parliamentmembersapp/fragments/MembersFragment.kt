package com.example.parliamentmembersapp.fragments

import android.app.Application
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parliamentmembersapp.R
import com.example.parliamentmembersapp.classes.Parties
import com.example.parliamentmembersapp.database.Member
import com.example.parliamentmembersapp.database.MemberDB
import com.example.parliamentmembersapp.repo.MembersRepo
import com.example.parliamentmembersapp.databinding.MembersFragmentBinding

class MembersFragment : Fragment() {

    companion object {
        fun newInstance() = MembersFragment()
    }

    private lateinit var viewModel: MembersViewModel
    private lateinit var memberAdapter: MemberAdapter
    private var currentMembers: List<Member> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<MembersFragmentBinding>(
            inflater, R.layout.members_fragment, container, false)

        viewModel = ViewModelProvider(this).get(MembersViewModel::class.java)

        val filters = arrayOf("firstname", "lastname", "age", "constituency")
        val spinner = binding.spinnerFilter
        val spinnerAdapter =
            context?.let { ArrayAdapter(it, android.R.layout.simple_spinner_item, filters) }
        spinnerAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        spinner.onItemSelectedListener = object :
        AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?,
                                        position: Int, id: Long) {
                viewModel.members.observe(viewLifecycleOwner, { members ->
                    var sortedList = listOf<Member>()
                    sortedList = when (filters[position]) {
                        "firstname" -> members.sortedBy { it.first }
                        "lastname" -> members.sortedBy { it.last }
                        "age" -> members.sortedByDescending { it.bornYear }
                        else -> members.sortedBy { it.constituency }
                    }
                    memberAdapter.updateMembers(sortedList)
                })

            }
            override fun onNothingSelected(parent: AdapterView<*>?){}
        }

        binding.btnFilter.setOnClickListener { spinner.performClick() }

        setAdapter(binding)

        arguments?.let { bundle ->
            val partyName = bundle.get("party") as String
            setObserver(viewModel.getMembersByParty(partyName))

        } ?: run {
            setObserver(viewModel.members)
        }

         binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
             override fun onQueryTextSubmit(query: String?): Boolean {
                 return false
             }
             override fun onQueryTextChange(newText: String?): Boolean {
                 viewModel.members.observe(viewLifecycleOwner, {
                     if (newText.isNullOrBlank()) {
                         memberAdapter.updateMembers(it)
                     } else {
                         val filteredList = it.filter { member ->
                             member.first.lowercase().contains(newText) ||
                                     member.last.lowercase().contains(newText) }
                         memberAdapter.updateMembers(filteredList)
                     }
                 })
                 return false
             }
         })

        return binding.root
    }

    private fun setObserver(membersLiveData: LiveData<List<Member>>) {
        membersLiveData.observe(viewLifecycleOwner, {
            currentMembers = it
            memberAdapter.updateMembers(currentMembers)
        })
    }

    private fun setAdapter(binding: MembersFragmentBinding) {

        memberAdapter = MemberAdapter(currentMembers ?: listOf()).apply {
            onItemClick = {
                val bundle = bundleOf("member" to it.personNumber)
                view?.findNavController()
                    ?.navigate(R.id.action_membersFragment_to_detailsFragment, bundle)
            }
        }

        binding.rvMembers.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = memberAdapter
            addItemDecoration(
                DividerItemDecoration(
                    context, DividerItemDecoration.VERTICAL
                )
            )
        }
    }

}

class MemberAdapter(var members: List<Member>): RecyclerView.Adapter<MemberAdapter.ViewHolder>() {

    var onItemClick: ((Member) -> Unit)? = null
    val defaultMembers = members

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_item_view_small, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val party = Parties.list.find { it.codeName == members[position].party }
        val fullname = members[position].let { "${it.first} ${it.last}" }

        party?.let {
            holder.logo.setImageResource(it.logoId)
        }
        holder.name.text = fullname
    }

    override fun getItemCount() = members.size

    fun updateMembers(newMembers: List<Member>) {
        members = newMembers
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var logo: ImageView
        var name: TextView

        init {
            logo = itemView.findViewById(R.id.img_logo)
            name = itemView.findViewById(R.id.txt_itemName)

            itemView.setOnClickListener {
                onItemClick?.invoke(members[adapterPosition])
            }
        }
    }
}

class MembersViewModel(application: Application) : AndroidViewModel(application) {

    val repo: MembersRepo
    var members: LiveData<List<Member>>

    init {
        val membersDB = MemberDB.getInstance(application).memberDao
        repo = MembersRepo(membersDB)
        members = repo.getAllFromDB()
    }

    fun getMembersByParty(party: String) = repo.getMembersByParty(party)
}