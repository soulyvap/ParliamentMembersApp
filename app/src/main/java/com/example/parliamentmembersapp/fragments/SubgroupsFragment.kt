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
import androidx.lifecycle.LiveData
import androidx.navigation.findNavController
import androidx.recyclerview.widget.*
import com.example.parliamentmembersapp.R
import com.example.parliamentmembersapp.classes.MyApp
import com.example.parliamentmembersapp.constants.Constants
import com.example.parliamentmembersapp.databinding.SubgroupsFragmentBinding
import com.example.parliamentmembersapp.repo.MembersRepo

class SubgroupsFragment : Fragment() {

    private lateinit var viewModel: PartiesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        val binding = DataBindingUtil.inflate<SubgroupsFragmentBinding>(
            inflater, R.layout.subgroups_fragment, container, false)

        viewModel = ViewModelProvider(this).get(PartiesViewModel::class.java)

        val subgroup = arguments?.get(Constants.KEY_SUBGROUP) as String

        if (subgroup == Constants.VAL_PARTIES) {
            setAdapter(binding, subgroup, viewModel.parties)
        } else {
            setAdapter(binding, subgroup, viewModel.constituencies)
        }
        return binding.root
    }

    private fun setAdapter(binding: SubgroupsFragmentBinding, subgroup: String,
                           liveData: LiveData<List<String>>) {
        liveData.observe(viewLifecycleOwner, { list ->
            binding.rvSubgroup.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = SubgroupAdapter(list, subgroup).apply {
                    onItemClick = {
                        val bundle = bundleOf(Constants.KEY_SUBGROUP to it,
                        Constants.KEY_SUBGROUP_TYPE to subgroup)
                        view?.findNavController()
                            ?.navigate(R.id.action_subgroupsFragment_to_membersFragment, bundle)
                    }
                }
            }
        })
    }
}

class SubgroupAdapter(private val subgroupList: List<String>, private val subgroupName: String):
    RecyclerView.Adapter<SubgroupAdapter.ViewHolder>() {
    var onItemClick: ((String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_item_view, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (subgroupName == Constants.VAL_PARTIES) {
            holder.logo.setImageResource(getLogoId(subgroupList[position]))
        } else {
            holder.constituency.text = subgroupList[position]
        }
    }

    override fun getItemCount() = subgroupList.size

    private fun getLogoId(partyName: String): Int {
        return MyApp.appContext.resources.getIdentifier(partyName,
            "drawable", MyApp.appContext.packageName)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var logo: ImageView = itemView.findViewById(R.id.img_partyLogo)
        var constituency: TextView = itemView.findViewById(R.id.txt_subConstituency)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(subgroupList[adapterPosition])
            }
        }
    }
}

class PartiesViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = MembersRepo
    val parties = repo.parties
    val constituencies = repo.constituencies
}

