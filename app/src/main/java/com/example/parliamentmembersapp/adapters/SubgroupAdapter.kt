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
* Date: 25.9.2021
* Name: Soulyvanh Phetsarath
* ID: 2012208
* Description: RecyclerView adapter for displaying a list of parties or constituencies
*/

class SubgroupAdapter(private val subgroupsList: List<String>, private val subgroupName: String):
    RecyclerView.Adapter<SubgroupAdapter.ViewHolder>() {

    //function type instance to handle recyclerView itemView onClick events
    var onItemClick: ((String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_item_view, parent, false)
        return ViewHolder(v)
    }

    //if parties are displayed, show their logo when it is available, else their name.
    //if constituencies are displayed, show their name
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (subgroupName) {
            Constants.VAL_PARTIES -> {
                val logoID = getLogoIdByName(subgroupsList[position])
                if (logoID == 0) {
                    holder.subgroupName.text = subgroupsList[position].uppercase()
                } else {
                    holder.logo.setImageResource(logoID)
                }
            }
            else -> holder.subgroupName.text = subgroupsList[position]
        }

        //setting onClickListener for onItemClick to invoke the clicked item
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(subgroupsList[position])
        }
    }

    override fun getItemCount() = subgroupsList.size

    //method to get a drawable's id by its file name
    private fun getLogoIdByName(partyName: String): Int {
        return MyApp.appContext.resources.getIdentifier(partyName,
            "drawable", MyApp.appContext.packageName)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var logo: ImageView = itemView.findViewById(R.id.img_partyLogo)
        var subgroupName: TextView = itemView.findViewById(R.id.txt_subgroup)
    }
}