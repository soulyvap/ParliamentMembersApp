package com.example.parliamentmembersapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.parliamentmembersapp.R
import com.example.parliamentmembersapp.classes.MyApp

/*
* Date: 7.10.2021
* Name: Soulyvanh Phetsarath
* ID: 2012208
* Description: RecyclerView adapter for displaying members list ordering buttons horizontally
*/

class HorizontalAdapter(private val orderCriteria: List<String>)
    : RecyclerView.Adapter<HorizontalAdapter.ViewHolder>() {

    var onItemClick: ((String) -> Unit)? = null
    private val cardViewList = mutableListOf<CardView>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HorizontalAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_horizontal_item, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: HorizontalAdapter.ViewHolder, position: Int) {
        if (!cardViewList.contains(holder.itemView as CardView)) {
            cardViewList.add(holder.itemView)
        }

        holder.orderCriterion.text = orderCriteria[position]

        //change the color of the first element to show that it is active
        if (position == 0) {
            holder.itemView.setCardBackgroundColor(
                ContextCompat.getColor(MyApp.appContext, android.R.color.holo_blue_dark))
        }

        //setting list item onClickListener, invoking the String that was clicked
        //switching the color of the clicked element to show which one was clicked last
        holder.itemView.setOnClickListener { view ->
            onItemClick?.invoke(orderCriteria[position])
            switchActiveItemColor(view)
        }
    }

    private fun switchActiveItemColor(view: View?) {
        cardViewList.forEach {
            it.setCardBackgroundColor(ContextCompat.getColor(
                    MyApp.appContext, R.color.light_grey))
        }
        (view as CardView).setCardBackgroundColor(ContextCompat.getColor(
                MyApp.appContext, android.R.color.holo_blue_dark))
    }

    override fun getItemCount() = orderCriteria.size

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val orderCriterion: TextView = itemView.findViewById(R.id.txt_filterCriteria)
    }
}