package com.example.parliamentmembersapp.viewmodels

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Transformations
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.example.parliamentmembersapp.classes.MyApp
import com.example.parliamentmembersapp.constants.Constants
import com.example.parliamentmembersapp.database.Member
import com.example.parliamentmembersapp.database.MemberComment
import com.example.parliamentmembersapp.database.MemberRating
import com.example.parliamentmembersapp.repos.CommentsRepo
import com.example.parliamentmembersapp.repos.MembersRepo
import com.example.parliamentmembersapp.repos.RatingsRepo
import kotlinx.coroutines.launch
import java.util.*

/*
* Date: 21.8.2021
* Name: Soulyvanh Phetsarath
* ID: 2012208
* Description: ViewModel for DetailsFragment. Contains references to repos, livedata as well as
* the personNumber of the member to be displayed and the current user's username.
*/

class DetailsFragmentViewModel(application: Application): AndroidViewModel(application){

    private val memberRepo = MembersRepo
    private val ratingRepo = RatingsRepo
    private val commentRepo = CommentsRepo
    private var personNumber: Int = 0
    private var author = ""

    //retrieving the person number of the member to be displayed
    fun setPersonNumber(arguments: Bundle?) {
        personNumber = arguments?.get(Constants.KEY_PERSON_NUM) as Int
    }

    //retrieving username from shared preferences
    fun retrieveUsername(activity: FragmentActivity?) {
        val sharedPref = activity?.getSharedPreferences(Constants.SP_USERNAME, Context.MODE_PRIVATE)
        author = sharedPref?.getString(Constants.SP_KEY_USERNAME, "") ?: "anonymous"
    }

    //LiveData for the displayed member from member repo
    val memberDisplayed = Transformations.map(memberRepo.membersFromDB) { members ->
        members.find { it.personNumber == personNumber }}.distinctUntilChanged()

    //Ratings LiveData for the displayed member from ratings repo
    val ratings = Transformations.map(ratingRepo.ratings) { ratings ->
        ratings.filter { it.personNumber == personNumber }}.distinctUntilChanged()
    fun getAverage(ratings: List<MemberRating>) = ratings.sumOf { it.rating } / ratings.size
    fun addRating(rating: Double) {
        viewModelScope.launch {
            ratingRepo.addRating(MemberRating(0, personNumber, rating, author))
        } }

    //Comments LiveData for the displayed member from the comments repo
    val comments = Transformations.map(commentRepo.comments) { comments ->
        comments.filter { it.personNumber == personNumber }
            .sortedByDescending { it.date } }.distinctUntilChanged()
    fun addComment(comment: String) {
        viewModelScope.launch {
            commentRepo.addComment(
                MemberComment(0,
                personNumber, Calendar.getInstance().time, comment, author
            )
            )
        } }

    //all the functions to get the data needed to update the member info UI
    fun getPicUrl(member: Member) = "https://avoindata.eduskunta.fi/${member.picture}"
    fun getMinister(member: Member) = if (member.minister) "Minister" else "Member Of Parliament"
    fun getName(member: Member) = "${member.first} ${member.last}"
    fun getAge(member: Member) = "${Calendar.getInstance().get(Calendar.YEAR) - member.bornYear}" +
            " years old"
    fun getTwitter(member: Member) = if (member.twitter.isBlank()) "None" else member.twitter
    fun getLogoId(member: Member): Int {
        return MyApp.appContext.resources.getIdentifier(member.party,
            "drawable", MyApp.appContext.packageName)
    }
}