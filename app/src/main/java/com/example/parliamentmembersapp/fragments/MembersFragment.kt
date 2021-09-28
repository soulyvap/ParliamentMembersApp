package com.example.parliamentmembersapp.fragments

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.*
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parliamentmembersapp.R
import com.example.parliamentmembersapp.classes.Parties
import com.example.parliamentmembersapp.database.Member
import com.example.parliamentmembersapp.repo.MembersRepo
import com.example.parliamentmembersapp.databinding.MembersFragmentBinding

class MembersFragment : Fragment() {

    private lateinit var viewModel: MembersViewModel
    private lateinit var memberAdapter: MemberAdapter
    private var currentMembers: List<Member> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<MembersFragmentBinding>(
            inflater, R.layout.members_fragment, container, false
        )

        viewModel = ViewModelProvider(this).get(MembersViewModel::class.java)

        arguments?.let { bundle ->
            val partyName = bundle.get("party") as String
            viewModel.setParty(partyName)
        }

        setAdapter(binding)

        viewModel.membersRequired.observe(viewLifecycleOwner, { initialMembers ->

            val initialListSorted = viewModel.sortByFirst(initialMembers)
            var membersDisplayed = viewModel.sortByFirst(initialMembers)
            memberAdapter.updateMembers(membersDisplayed)

            binding.btnFilterFirst.setOnClickListener {
                membersDisplayed = viewModel.sortByFirst(memberAdapter.members)
                memberAdapter.updateMembers(membersDisplayed)
            }

            binding.btnFilterLast.setOnClickListener {
                membersDisplayed = viewModel.sortByLast(memberAdapter.members)
                memberAdapter.updateMembers(membersDisplayed)
            }

            binding.btnFilterAge.setOnClickListener {
                membersDisplayed = viewModel.sortByAge(memberAdapter.members)
                memberAdapter.updateMembers(membersDisplayed)
            }

            binding.btnFilterConstituency.setOnClickListener {
                membersDisplayed = viewModel.sortByConstituency(memberAdapter.members)
                memberAdapter.updateMembers(membersDisplayed)
            }

            binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?) = false
                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText.isNullOrBlank()) {
                        memberAdapter.updateMembers(initialListSorted)
                    } else {
                        val filtered = initialListSorted.filter { member ->
                            member.first.lowercase().contains(newText.lowercase()) ||
                                    member.last.lowercase().contains(newText.lowercase()) ||
                                    member.constituency.lowercase().contains(newText.lowercase())
                        }
                        memberAdapter.updateMembers(filtered)
                    }
                    return false
                }
            })
        })

        return binding.root
    }

    private fun setAdapter(binding: MembersFragmentBinding) {

        memberAdapter = MemberAdapter(currentMembers).apply {
            onItemClick = {
                val bundle = bundleOf("member" to it.personNumber)
                view?.findNavController()
                    ?.navigate(R.id.action_membersFragment_to_detailsFragment, bundle)
            }
        }
        binding.rvMembers.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = memberAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }
}

class MemberAdapter(var members: List<Member>) : RecyclerView.Adapter<MemberAdapter.ViewHolder>() {

    var onItemClick: ((Member) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_item_view_small, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val party = Parties.list.find { it.codeName == members[position].party }
        val fullname = members[position].let { "${it.first} ${it.last}" }
        party?.let { holder.logo.setImageResource(it.logoId)}
        holder.name.text = fullname
    }

    override fun getItemCount() = members.size

    fun updateMembers(newMembers: List<Member>) {
        members = newMembers
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var logo: ImageView = itemView.findViewById(R.id.img_logo)
        var name: TextView = itemView.findViewById(R.id.txt_itemName)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(members[adapterPosition])
            }
        }
    }
}

class MembersViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = MembersRepo
    private val allMembers = repo.membersFromDB
    var party: String? = null
        private set
    private val membersByParty = Transformations.map(repo.membersFromDB)
        { members -> members.filter { it.party == party }}
    val membersRequired: LiveData<List<Member>>
        get() = party?.let { membersByParty } ?: allMembers

    fun setParty(partySelected: String) {
        party = partySelected
    }

    fun sortByFirst(members: List<Member>) = members.sortedBy { it.first }

    fun sortByLast(members: List<Member>) = members.sortedBy { it.last }

    fun sortByAge(members: List<Member>) = members.sortedByDescending { it.bornYear }

    fun sortByConstituency(members: List<Member>) = members.sortedBy { it.constituency }

}