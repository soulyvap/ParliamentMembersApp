package com.example.parliamentmembersapp.repos

import com.example.parliamentmembersapp.database.MemberDB
import com.example.parliamentmembersapp.database.MemberRating

/*
* Date: 29.9.2021
* Name: Soulyvanh Phetsarath
* ID: 2012208
* Description: Repository to handles queries for ratings in the MemberDB
*/

object RatingsRepo {
    private val ratingDao = MemberDB.getInstance().ratingDao
    val ratings = ratingDao.getAll()

    suspend fun addRating(rating: MemberRating) {
        ratingDao.insertRating(rating)
    }
}