package com.example.parliamentmembersapp.repos

import com.example.parliamentmembersapp.database.MemberComment
import com.example.parliamentmembersapp.database.MemberDB

/*
* Date:
* Name: Soulyvanh Phetsarath
* ID: 2012208
* Description: Repository to handles queries for comments in the MemberDB
*/

object CommentsRepo {
    private val commentDao = MemberDB.getInstance().commentDao
    val comments = commentDao.getAll()

    suspend fun addComment(comment: MemberComment) {
        commentDao.insertComment(comment)
    }
}