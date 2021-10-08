package com.example.parliamentmembersapp.database

import androidx.lifecycle.LiveData
import androidx.room.*

/*
* Date:
* Name: Soulyvanh Phetsarath
* ID: 2012208
* Description: Entity class that defines the properties of a comment about a Member
* in the Room MemberDB
*/

//making sure one user can only rate a parliament member once by making the combination of
//columns personNumber and author unique
@Entity(indices = [Index(value = ["personNumber", "author"], unique = true)])
class MemberRating(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    val personNumber: Int,
    val rating: Double,
    val author: String
)

/*
* Date:
* Name: Soulyvanh Phetsarath
* ID: 2012208
* Description: DAO for MemberRating instances
*/

@Dao
interface RatingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRating(rating: MemberRating)
    @Query("select * from MemberRating")
    fun getAll(): LiveData<List<MemberRating>>
    @Query("select * from MemberRating where personNumber = :personNumber")
    fun getAllByNumber(personNumber: Int): LiveData<List<MemberRating>>
    @Query("select * from MemberRating " +
            "where personNumber = :personNumber and author = :author")
    fun getRatingByNumberAndAuthor(personNumber: Int, author: String): LiveData<MemberRating>
}




