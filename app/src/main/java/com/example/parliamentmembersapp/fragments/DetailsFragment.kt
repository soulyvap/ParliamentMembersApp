package com.example.parliamentmembersapp.fragments

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.parliamentmembersapp.R
import com.example.parliamentmembersapp.adapters.CommentAdapter
import com.example.parliamentmembersapp.database.Member
import com.example.parliamentmembersapp.database.MemberComment
import com.example.parliamentmembersapp.database.MemberRating
import com.example.parliamentmembersapp.databinding.DetailsFragmentBinding
import com.example.parliamentmembersapp.viewmodels.DetailsFragmentViewModel

/*
* Date:
* Name: Soulyvanh Phetsarath
* ID: 2012208
* Description: Fragment that displays the detailed information of an MP. The user can also
* rate the MP's performance or leave a comment about them.
*/

class DetailsFragment : Fragment() {

    private lateinit var viewModel: DetailsFragmentViewModel
    private var fragmentBinding: DetailsFragmentBinding? = null
    private var comments = listOf<MemberComment>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        viewModel = ViewModelProvider(this).get(DetailsFragmentViewModel::class.java)

        val binding = DataBindingUtil.inflate<DetailsFragmentBinding>(
            inflater, R.layout.details_fragment, container, false)
        fragmentBinding = binding

        //retrieving the number of the parliament member to display
        //and the username of the current user
        viewModel.setPersonNumber(arguments)
        viewModel.retrieveUsername(activity)

        //setting up the adapter for the comments recyclerView
        val commentAdapter = CommentAdapter(comments)
        binding.rvComments.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = commentAdapter
        }

        //observing the displayed member's info from the DB and updating the UI
        viewModel.memberDisplayed.observe(viewLifecycleOwner, Observer{ member ->
            member?.let { updateMemberInfo(member, binding) }
        })

        //observing the displayed member's ratings and displaying their average rating
        viewModel.ratings.observe(viewLifecycleOwner, Observer{ ratings ->
            updateAverageRating(ratings, binding)
        })

        //observing the displayed member's comments and updating the comments recyclerView
        viewModel.comments.observe(viewLifecycleOwner, Observer{ comments ->
            updateComments(comments, binding, commentAdapter)
        })

        //adding/modifying a rating to the rating table in DB for the displayed member
        //one user can only have one rating per member, but it can be replaced
        binding.btnSubmitRating.setOnClickListener {
            viewModel.addRating(binding.rtbNewRating.rating.toDouble())
        }

        //adding a new comment for the displayed member to the DB
        binding.btnAddComment.setOnClickListener {
            val comment = binding.etxtComment.text
            addComment(comment)
        }

        return binding.root
    }

    //updating all the member info
    private fun updateMemberInfo(member: Member, binding: DetailsFragmentBinding) {
        val ratingText = "Rate ${member.first}!"
        binding.txtTitleRating.text = ratingText
        binding.imgParty.setImageResource(viewModel.getLogoId(member))
        binding.txtInfo.text = viewModel.getName(member)
        binding.txtMinister.text = viewModel.getMinister(member)
        Glide.with(this)
            .load(viewModel.getPicUrl(member))
            .into(binding.imgProfile)
        binding.txtAge.text = viewModel.getAge(member)
        binding.txtConstituency.text = member.constituency
        binding.txtSeatNumber.text = member.seatNumber.toString()
        binding.txtTwitter.text = viewModel.getTwitter(member)
    }

    //updating the average rating and rating count of the member if there is any rating
    private fun updateAverageRating(ratings: List<MemberRating>,
                                    binding: DetailsFragmentBinding) {
        if (ratings.isNotEmpty()) {
            val average = viewModel.getAverage(ratings)
            val ratingAverageTxt = "(Average: $average, " +
                    "${ratings.size} rating${if (ratings.size > 1) "s" else ""})"
            binding.txtAverageRating.text = ratingAverageTxt
            binding.rtbAverage.rating = average.toFloat()
        }
    }

    //checking new comment EditText for input, then adding comment to DB
    //and clearing EditText if there is an input. displaying a toast if no input
    private fun addComment(comment: Editable) {
        if (comment.isNotEmpty()) {
            viewModel.addComment(comment.toString())
            comment.clear()
        } else {
            Toast.makeText(activity, "Please enter a comment",
                Toast.LENGTH_SHORT).show()
        }
    }

    //updating the comments UI (number of comments and recyclerView)
    private fun updateComments(comments: List<MemberComment>, binding: DetailsFragmentBinding,
                               commentAdapter: CommentAdapter) {
        val txtNumberComments = "Comments ${comments.size}"
        binding.txtNumberOfComments.text = txtNumberComments
        commentAdapter.updateList(comments)
    }

    //making sure username is updated in case username was switched and resetting "current rating"
    override fun onResume() {
        super.onResume()
        viewModel.retrieveUsername(activity)
        fragmentBinding?.rtbNewRating?.rating = 0f
    }
}

