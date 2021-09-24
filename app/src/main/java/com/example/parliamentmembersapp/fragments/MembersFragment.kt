package com.example.parliamentmembersapp.fragments

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
import com.example.parliamentmembersapp.database.MembersRepo
import com.example.parliamentmembersapp.databinding.MembersFragmentBinding
import java.util.*

class MembersFragment : Fragment() {

    companion object {
        fun newInstance() = MembersFragment()
    }

    private lateinit var viewModel: MembersViewModel
    private lateinit var memberAdapter: MemberAdapter
    private var currentMembers: List<Member> = listOf()
    private var refreshCount = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<MembersFragmentBinding>(
            inflater, R.layout.members_fragment, container, false)

        viewModel = ViewModelProvider(this).get(MembersViewModel::class.java)

        setAdapter(binding)

        arguments?.let { bundle ->
            val partyName = bundle.get("party") as String
            setObserver(viewModel.getMembersByParty(partyName))

        } ?: run {
            setObserver(viewModel.members)
        }

        return binding.root
    }

    private fun setObserver(membersLiveData: LiveData<List<Member>>) {
        membersLiveData.observe(viewLifecycleOwner, {
            currentMembers = it
            memberAdapter.updateMembers(currentMembers)
            if (refreshCount > 0) Toast.makeText(activity,
                "List updated"
                , Toast.LENGTH_SHORT).show()
            refreshCount++
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
        this.notifyDataSetChanged()
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