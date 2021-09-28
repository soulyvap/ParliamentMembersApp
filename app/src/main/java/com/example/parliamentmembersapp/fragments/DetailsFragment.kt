package com.example.parliamentmembersapp.fragments

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.parliamentmembersapp.R
import com.example.parliamentmembersapp.classes.Parties
import com.example.parliamentmembersapp.database.Member
import com.example.parliamentmembersapp.database.MemberComment
import com.example.parliamentmembersapp.database.MemberDB
import com.example.parliamentmembersapp.database.MemberRating
import com.example.parliamentmembersapp.repo.MembersRepo
import com.example.parliamentmembersapp.databinding.DetailsFragmentBinding
import com.example.parliamentmembersapp.repo.CommentsRepo
import com.example.parliamentmembersapp.repo.RatingsRepo
import kotlinx.coroutines.launch
import java.util.*

class DetailsFragment : Fragment() {

    private lateinit var viewModel: DetailsViewModel
    private var fragmentBinding: DetailsFragmentBinding? = null

    var comments = listOf<MemberComment>()

    override fun onResume() {
        super.onResume()
        viewModel.retrieveUsername(activity)
        fragmentBinding?.rtbNewRating?.rating = 0f
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this).get(DetailsViewModel::class.java)

        val binding = DataBindingUtil.inflate<DetailsFragmentBinding>(
            inflater, R.layout.details_fragment, container, false)
        fragmentBinding = binding

        viewModel.setPersonNumber(arguments)

        viewModel.retrieveUsername(activity)

        viewModel.currentGivenRating.observe(viewLifecycleOwner, {
            binding.rtbNewRating.rating = it?.rating?.toFloat() ?: 0F
        })

        val commentAdapter = CommentAdapter(comments)

        viewModel.ratings.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                val average = viewModel.getAverage(it)
                val ratingAverageTxt = "(Average rating: $average, " +
                        "Number of ratings: ${it.size})"
                binding.txtAverageRating.text = ratingAverageTxt
                binding.rtbAverage.rating = average.toFloat()
            }
        })

        binding.rvComments.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = commentAdapter
            addItemDecoration(
                DividerItemDecoration(
                    context, DividerItemDecoration.VERTICAL
                )
            )
        }

        viewModel.memberDisplayed.observe(viewLifecycleOwner, { member ->
            member?.let {
                val ratingText = "Rate ${member.first}!"
                binding.txtTitleRating.text = ratingText
                binding.imgParty.setImageResource(viewModel.getLogoId(member))
                binding.txtInfo.text = viewModel.getInfo(member)
                Glide.with(this)
                    .load(viewModel.getPicUrl(member))
                    .into(binding.imgProfile)
                binding.txtAge.text = viewModel.getAge(member)
                binding.txtConstituency.text = viewModel.getConstituency(member)
            }
        })

        viewModel.comments.observe(viewLifecycleOwner, {
            val txtNumberComments = "Comments ${it.size}"
            binding.txtNumberOfComments.text = txtNumberComments
            commentAdapter.updateList(it)
        })

        binding.btnSubmitRating.setOnClickListener {
            viewModel.addRating(binding.rtbNewRating.rating.toDouble())
        }

        binding.btnAddComment.setOnClickListener {
            val comment = binding.etxtComment.text
            if (comment.isNotEmpty()) {
                viewModel.addComment(comment.toString())
                comment.clear()
            } else {
                Toast.makeText(activity, "Please enter comment",
                    Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }
}

class CommentAdapter(var comments: List<MemberComment>):
    RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_comment_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.comment.text = comments[position].comment
        holder.author.text = comments[position].author
    }

    override fun getItemCount() = comments.size

    fun updateList(newComments: List<MemberComment>) {
        comments = newComments
        this.notifyItemInserted(0)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var comment: TextView
        var author: TextView

        init {
            comment = itemView.findViewById(R.id.txt_comment)
            author = itemView.findViewById(R.id.txt_author)
        }
    }
}

class DetailsViewModel(application: Application): AndroidViewModel(application){

    private val memberRepo = MembersRepo
    private val ratingRepo = RatingsRepo
    private val commentRepo = CommentsRepo
    private var personNumber: Int = 0
    private var author = ""

    fun setPersonNumber(arguments: Bundle?) {
        personNumber = arguments?.get("member") as Int
    }

    fun retrieveUsername(activity: FragmentActivity?) {
        val sharedPref = activity?.getSharedPreferences("userPref", Context.MODE_PRIVATE)
        author = sharedPref?.getString("username", "") ?: "anonymous"
    }

    //Member LiveData
    val memberDisplayed = Transformations.distinctUntilChanged(
        Transformations.map(memberRepo.membersFromDB) { members ->
        members.find { it.personNumber == personNumber }})

    //Ratings LiveData
    val currentGivenRating = Transformations.map(ratingRepo.ratings) { ratings ->
        ratings.find { it.personNumber == personNumber && it.author == author } }
    val ratings = Transformations.distinctUntilChanged(
        Transformations.map(ratingRepo.ratings) { ratings ->
        ratings.filter { it.personNumber == personNumber }})
    fun getAverage(ratings: List<MemberRating>) = ratings.sumOf { it.rating } / ratings.size
    fun addRating(rating: Double) {
        viewModelScope.launch {
            ratingRepo.addRating(MemberRating(0, personNumber, rating, author.toString()))
        } }

    //Comments LiveData
    val comments = Transformations.map(commentRepo.comments) { comments ->
        comments.filter { it.personNumber == personNumber }
            .sortedByDescending { it.date } }
    fun addComment(comment: String) {
        viewModelScope.launch {
            commentRepo.addComment(MemberComment(0,
                personNumber, Calendar.getInstance().time, comment, author.toString()))
        } }

    //Update member info
    fun getPicUrl(member: Member) = "https://avoindata.eduskunta.fi/${member.picture}"
    fun getInfo(member: Member) = (if (member.minister) "Minister" else "Member Of Parliament") +
            " ${member.first} ${member.last}"
    fun getAge(member: Member) = "Age: " +
            "${Calendar.getInstance().get(Calendar.YEAR) - member.bornYear}"
    fun getLogoId(member: Member) = Parties.list.find { it.codeName == member.party }?.logoId ?: 0
    fun getConstituency(member: Member) = "Constituency: ${member.constituency}"

}