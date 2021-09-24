package com.example.parliamentmembersapp.fragments

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.findNavController
import androidx.recyclerview.widget.*
import com.example.parliamentmembersapp.R
import com.example.parliamentmembersapp.classes.Parties
import com.example.parliamentmembersapp.classes.Party
import com.example.parliamentmembersapp.databinding.PartiesFragmentBinding

class PartiesFragment : Fragment() {

    private lateinit var viewModel: PartiesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<PartiesFragmentBinding>(
            inflater, R.layout.parties_fragment, container, false)

        viewModel = ViewModelProvider(this).get(PartiesViewModel::class.java)

        binding.rvParties.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = PartyAdapter().apply {
                onItemClick = {
                    val bundle = bundleOf("party" to it.codeName)
                    view?.findNavController()
                        ?.navigate(R.id.action_partiesFragment_to_membersFragment, bundle)
                }
            }
            addItemDecoration(DividerItemDecoration(
                context, DividerItemDecoration.VERTICAL))
        }
        return binding.root
    }
}

class PartyAdapter: RecyclerView.Adapter<PartyAdapter.ViewHolder>() {

    val parties = Parties.list
    var onItemClick: ((Party) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_item_view, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.logo.setImageResource(parties[position].logoId)
        holder.name.text = parties[position].name
    }

    override fun getItemCount() = parties.size

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var logo: ImageView
        var name: TextView

        init {
            logo = itemView.findViewById(R.id.img_logo)
            name = itemView.findViewById(R.id.txt_itemName)

            itemView.setOnClickListener {
                onItemClick?.invoke(parties[adapterPosition])
            }
        }
    }
}

class PartiesViewModel(application: Application) : AndroidViewModel(application) {

}

