package com.example.parliamentmembersapp.fragments

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.bumptech.glide.Glide
import com.example.parliamentmembersapp.R
import com.example.parliamentmembersapp.classes.Parties
import com.example.parliamentmembersapp.database.Member
import com.example.parliamentmembersapp.database.MemberDB
import com.example.parliamentmembersapp.database.MembersRepo
import com.example.parliamentmembersapp.databinding.DetailsFragmentBinding
import java.util.*

class DetailsFragment : Fragment() {

    companion object {
        fun newInstance() = DetailsFragment()
    }

    private lateinit var viewModel: DetailsViewModel
    private var refreshCount = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this).get(DetailsViewModel::class.java)

        val binding = DataBindingUtil.inflate<DetailsFragmentBinding>(
            inflater, R.layout.details_fragment, container, false)

        val personNumber = arguments?.get("member") as Int

        viewModel.getMemberByNumber(personNumber).observe(viewLifecycleOwner, { member ->
            binding.imgParty.setImageResource(viewModel.getLogoId(member))
            binding.txtInfo.text = viewModel.getInfo(member)
            Glide.with(this)
                .load(viewModel.getPicUrl(member))
                .into(binding.imgProfile)
            binding.txtAge.text = viewModel.getAge(member)
            binding.txtConstituency.text = viewModel.getConstituency(member)
            if (refreshCount > 0)
                Toast.makeText(activity, "info refreshed", Toast.LENGTH_SHORT).show()
        })

        return binding.root
    }


}

class DetailsViewModel(application: Application): AndroidViewModel(application){

    private val repo: MembersRepo

    init {
        val membersDB = MemberDB.getInstance(application).memberDao
        repo = MembersRepo(membersDB)
    }

    fun getMemberByNumber(personNumber: Int) = repo.getMemberByNumber(personNumber)

    fun getPicUrl(member: Member) = "https://avoindata.eduskunta.fi/${member.picture}"

    fun getInfo(member: Member) = (if (member.minister) "Minister" else "Member Of Parliament") +
            " ${member.first} ${member.last}"

    fun getAge(member: Member) = "Age: " +
            "${Calendar.getInstance().get(Calendar.YEAR) - member.bornYear}"

    fun getLogoId(member: Member) = Parties.list.find { it.codeName == member.party }?.logoId ?: 0

    fun getConstituency(member: Member) = "Constituency: ${member.constituency}"


}