package com.example.parliamentmembersapp.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(indices = [Index(value = ["personNumber", "author"], unique = true)])
class MemberRating(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    val personNumber: Int,
    val rating: Double,
    val author: String
)

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




