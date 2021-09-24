package com.example.parliamentmembersapp.repo

import com.example.parliamentmembersapp.database.CommentDao
import com.example.parliamentmembersapp.database.MemberComment


class CommentsRepo(private val commentDao: CommentDao) {

    suspend fun addComment(comment: MemberComment) {
        commentDao.insertComment(comment)
    }

    fun getAll() = commentDao.getAll()

    fun getAllByPersonNumber(personNumber: Int) = commentDao.getAllByNumber(personNumber)

}