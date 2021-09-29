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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.parliamentmembersapp.R
import com.example.parliamentmembersapp.classes.MyApp
import com.example.parliamentmembersapp.database.Member
import com.example.parliamentmembersapp.repo.MembersRepo
import com.example.parliamentmembersapp.databinding.MembersFragmentBinding
import com.example.parliamentmembersapp.databinding.RvCardviewMemberBinding
import java.util.*

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
        }
    }
}

class MemberAdapter(var members: List<Member>) : RecyclerView.Adapter<MemberAdapter.ViewHolder>() {

    var onItemClick: ((Member) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_cardview_member, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val partyLogoId = getLogoId(members[position].party)
        val minister = if (members[position].minister) "Minister" else "Member of Parliament"
        val fullname = members[position].let { "${it.first} ${it.last}" }
        val memberInfo = "$minister $fullname"
        val age = "${Calendar.getInstance().get(Calendar.YEAR) - members[position].bornYear} " +
                "years old"
        val picUrl = "https://avoindata.eduskunta.fi/${members[position].picture}"
        val constituency = members[position].constituency
        holder.logo.setImageResource(partyLogoId)
        holder.name.text = memberInfo
        holder.age.text = age
        holder.constituency.text = constituency
        Glide.with(MyApp.appContext)
            .load(picUrl)
            .into(holder.pic)
    }

    override fun getItemCount() = members.size

    fun updateMembers(newMembers: List<Member>) {
        members = newMembers
        notifyDataSetChanged()
    }

    fun getLogoId(partyName: String): Int {
        return MyApp.appContext.resources.getIdentifier(partyName,
            "drawable", MyApp.appContext.packageName)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var logo: ImageView = itemView.findViewById(R.id.img_logo)
        var name: TextView = itemView.findViewById(R.id.txt_itemName)
        var pic: ImageView = itemView.findViewById(R.id.img_profilePic)
        var age: TextView = itemView.findViewById(R.id.txt_itemAge)
        var constituency: TextView = itemView.findViewById(R.id.img_itemConstituency)

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