package com.example.parliamentmembersapp.repo

import com.example.parliamentmembersapp.database.MemberComment
import com.example.parliamentmembersapp.database.MemberDB


object CommentsRepo {
    private val commentDao = MemberDB.getInstance().commentDao
    val comments = commentDao.getAll()

    suspend fun addComment(comment: MemberComment) {
        commentDao.insertComment(comment)
    }
}