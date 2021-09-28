package com.example.parliamentmembersapp.repo

import com.example.parliamentmembersapp.database.MemberDB
import com.example.parliamentmembersapp.database.MemberRating
import com.example.parliamentmembersapp.database.RatingDao

object RatingsRepo {
    private val ratingDao = MemberDB.getInstance().ratingDao
    val ratings = ratingDao.getAll()

    suspend fun addRating(rating: MemberRating) {
        ratingDao.insertRating(rating)
    }

}