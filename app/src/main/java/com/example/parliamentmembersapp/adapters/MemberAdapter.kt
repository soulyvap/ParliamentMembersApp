package com.example.parliamentmembersapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.parliamentmembersapp.R
import com.example.parliamentmembersapp.classes.MyApp
import com.example.parliamentmembersapp.database.Member
import java.util.*

/*
* Date: 27.9.2021
* Name: Soulyvanh Phetsarath
* ID: 2012208
* Description: RecyclerView adapter for displaying a list of parliament members with clickable items
*/

class MemberAdapter(var members: List<Member>) : RecyclerView.Adapter<MemberAdapter.ViewHolder>() {

    //function type instance to handle RV itemView click events
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
        val age = "${Calendar.getInstance().get(Calendar.YEAR) - members[position].bornYear} " +
                "years old"
        val picUrl = "https://avoindata.eduskunta.fi/${members[position].picture}"
        val constituency = members[position].constituency
        holder.logo.setImageResource(partyLogoId)
        holder.name.text = fullname
        holder.position.text = minister
        holder.age.text = age
        holder.constituency.text = constituency
        //Glide handles the display and caching of a picture fetched online
        Glide.with(MyApp.appContext)
            .load(picUrl)
            .into(holder.pic)

        //set onClickListener for onItemClick to invoke the clicked member (make it available
        //to use in Fragment)
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(members[position])
        }
    }

    override fun getItemCount() = members.size

    //update RV
    @SuppressLint("NotifyDataSetChanged")
    fun updateMembers(newMembers: List<Member>) {
        members = newMembers
        notifyDataSetChanged()
    }

    //get drawable id by its file name
    private fun getLogoId(partyName: String): Int {
        return MyApp.appContext.resources.getIdentifier(partyName,
            "drawable", MyApp.appContext.packageName)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var logo: ImageView = itemView.findViewById(R.id.img_logo)
        var name: TextView = itemView.findViewById(R.id.txt_itemName)
        var pic: ImageView = itemView.findViewById(R.id.img_profilePic)
        var position: TextView = itemView.findViewById(R.id.txt_itemPosition)
        var age: TextView = itemView.findViewById(R.id.txt_itemAge)
        var constituency: TextView = itemView.findViewById(R.id.txt_itemConstituency)
    }
}