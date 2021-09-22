package com.example.parliamentmembersapp.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity
class Member(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
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

@Dao
interface MemberDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(member: Member)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(members: List<Member>)
    @Query("select * from Member order by party")
    fun getAll(): LiveData<List<Member>>
    @Query("select * from Member where party = :party")
    fun getMembersByParty(party: String): LiveData<List<Member>>
}