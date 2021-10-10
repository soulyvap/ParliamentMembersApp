package com.example.parliamentmembersapp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

/*
* Date: 29.9.2021
* Name: Soulyvanh Phetsarath
* ID: 2012208
* Description: Entity class that defines the properties of a Member rating in the Room MemberDB
*/

@Entity
class MemberComment(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    val personNumber: Int,
    val date: Date,
    val comment: String?,
    val author: String
)

/*
* Date:
* Name: Soulyvanh Phetsarath
* ID: 2012208
* Description: DAO for MemberComment instances
*/

@Dao
interface CommentDao {
    @Insert
    suspend fun insertComment(comment: MemberComment)
    @Query("select * from MemberComment")
    fun getAll(): LiveData<List<MemberComment>>
    @Query("select * from MemberComment where personNumber = :personNumber order by date desc")
    fun getAllByNumber(personNumber: Int): LiveData<List<MemberComment>>
}



