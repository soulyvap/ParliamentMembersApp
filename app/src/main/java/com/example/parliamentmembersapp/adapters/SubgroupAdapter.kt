package com.example.parliamentmembersapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.parliamentmembersapp.R
import com.example.parliamentmembersapp.classes.MyApp
import com.example.parliamentmembersapp.constants.Constants

/*
* Date:
* Name: Soulyvanh Phetsarath
* ID: 2012208
* Description: RecyclerView adapter for displaying a list of parties or constituencies
*/

class SubgroupAdapter(private val subgroupList: List<String>, private val subgroupName: String):
    RecyclerView.Adapter<SubgroupAdapter.ViewHolder>() {

    //function type instance to handle recyclerView itemView onClick events
    var onItemClick: ((String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_item_view, parent, false)
        return ViewHolder(v)
    }

    //if parties are displayed, show the logo.
    //if constituencies are displayed, show their name
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (subgroupName) {
            Constants.VAL_PARTIES -> holder.logo
                .setImageResource(getLogoIdByName(subgroupList[position]))
            else -> holder.constituency.text = subgroupList[position]
        }
    }

    override fun getItemCount() = subgroupList.size

    //method to get a drawable's id by its file name
    private fun getLogoIdByName(partyName: String): Int {
        return MyApp.appContext.resources.getIdentifier(partyName,
            "drawable", MyApp.appContext.packageName)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var logo: ImageView = itemView.findViewById(R.id.img_partyLogo)
        var constituency: TextView = itemView.findViewById(R.id.txt_subConstituency)

        init {
            //invoking the string corresponding to the item clicked so that it can
            //be used in the fragment (pre-defining the function parameter)
            itemView.setOnClickListener {
                onItemClick?.invoke(subgroupList[adapterPosition])
            }
        }
    }
}