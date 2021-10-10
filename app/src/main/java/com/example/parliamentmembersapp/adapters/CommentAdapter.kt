package com.example.parliamentmembersapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.parliamentmembersapp.R
import com.example.parliamentmembersapp.database.MemberComment

/*
* Date: 29.9.2021
* Name: Soulyvanh Phetsarath
* ID: 2012208
* Description: RecyclerView adapter for displaying comments about MPs
*/

class CommentAdapter(private var comments: List<MemberComment>):
    RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_comment_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val authorTxt = "${comments[position].author} commented"
        holder.author.text = authorTxt
        holder.comment.text = comments[position].comment
    }

    override fun getItemCount() = comments.size

    //updating the recyclerView with the latest comment at the top
    fun updateList(newComments: List<MemberComment>) {
        comments = newComments
        this.notifyItemInserted(0)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var comment: TextView = itemView.findViewById(R.id.txt_comment)
        var author: TextView = itemView.findViewById(R.id.txt_author)
    }
}