package com.example.parliamentmembersapp.database

import androidx.lifecycle.LiveData
import androidx.room.*

/*
* Date: 22.8.2021
* Name: Soulyvanh Phetsarath
* ID: 2012208
* Description: Entity class that defines the properties of a Member in the Room MemberDB
*/

@Entity
class Member(
    @PrimaryKey
    val personNumber: Int,
    val seatNumber: Int,
    val last: String,
    val first: String,
    val party: String,
    val minister: Boolean,
    val picture:String,
    val twitter: String,
    val bornYear: Int,
    val constituency: String
)

/*
* Date:
* Name: Soulyvanh Phetsarath
* ID: 2012208
* Description: DAO for Member instances
*/

@Dao
interface MemberDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(member: Member)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(members: Set<Member>)
    @Query("select * from Member order by first")
    fun getAll(): LiveData<List<Member>>
    @Query("select * from Member where party = :party")
    fun getMembersByParty(party: String): LiveData<List<Member>>
    @Query("select * from Member where personNumber = :personNumber")
    fun getMemberByNumber(personNumber: Int): LiveData<Member>
}