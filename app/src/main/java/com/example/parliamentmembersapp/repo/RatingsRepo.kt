package com.example.parliamentmembersapp.repo

import com.example.parliamentmembersapp.database.MemberRating
import com.example.parliamentmembersapp.database.RatingDao

class RatingsRepo(private val ratingDao: RatingDao) {

    suspend fun addRating(rating: MemberRating) {
        ratingDao.insertRating(rating)
    }

    fun getAll() = ratingDao.getAll()

    fun getAllByPersonNumber(personNumber: Int) = ratingDao.getAllByNumber(personNumber)

    fun getRatingByNumberAndAuthor(personNumber: Int, author: String) =
        ratingDao.getRatingByNumberAndAuthor(personNumber, author)
}