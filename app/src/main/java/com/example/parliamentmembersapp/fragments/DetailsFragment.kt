package com.example.parliamentmembersapp.fragments

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.parliamentmembersapp.R
import com.example.parliamentmembersapp.activities.MainActivityViewModel
import com.example.parliamentmembersapp.classes.MyApp
import com.example.parliamentmembersapp.constants.Constants
import com.example.parliamentmembersapp.database.Member
import com.example.parliamentmembersapp.database.MemberComment
import com.example.parliamentmembersapp.database.MemberRating
import com.example.parliamentmembersapp.repo.MembersRepo
import com.example.parliamentmembersapp.databinding.DetailsFragmentBinding
import com.example.parliamentmembersapp.repo.CommentsRepo
import com.example.parliamentmembersapp.repo.RatingsRepo
import kotlinx.coroutines.launch
import java.util.*

class DetailsFragment : Fragment() {

    private lateinit var viewModel: DetailsViewModel
    private lateinit var mainVM: MainActivityViewModel
    private var fragmentBinding: DetailsFragmentBinding? = null

    private var comments = listOf<MemberComment>()

    override fun onResume() {
        super.onResume()
        viewModel.retrieveUsername(activity)
        fragmentBinding?.rtbNewRating?.rating = 0f
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        viewModel = ViewModelProvider(this).get(DetailsViewModel::class.java)
        mainVM = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        val binding = DataBindingUtil.inflate<DetailsFragmentBinding>(
            inflater, R.layout.details_fragment, container, false)
        fragmentBinding = binding

        viewModel.setPersonNumber(arguments)
        viewModel.retrieveUsername(activity)

        val commentAdapter = CommentAdapter(comments)
        binding.rvComments.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = commentAdapter
        }

        viewModel.memberDisplayed.observe(viewLifecycleOwner, Observer{ member ->
            member?.let { updateMemberInfo(member, binding) }
        })

        viewModel.ratings.observe(viewLifecycleOwner, Observer{ ratings ->
            updateAverageRating(ratings, binding)
        })

        viewModel.comments.observe(viewLifecycleOwner, Observer{ comments ->
            updateComments(comments, binding, commentAdapter)
        })

        binding.btnSubmitRating.setOnClickListener {
            viewModel.addRating(binding.rtbNewRating.rating.toDouble())
        }

        binding.btnAddComment.setOnClickListener {
            val comment = binding.etxtComment.text
            addComment(comment)
        }

        return binding.root
    }

    private fun addComment(comment: Editable) {
        if (comment.isNotEmpty()) {
            viewModel.addComment(comment.toString())
            comment.clear()
        } else {
            Toast.makeText(
                activity, "Please enter a comment",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun updateComments(
        comments: List<MemberComment>,
        binding: DetailsFragmentBinding,
        commentAdapter: CommentAdapter
    ) {
        val txtNumberComments = "Comments ${comments.size}"
        binding.txtNumberOfComments.text = txtNumberComments
        commentAdapter.updateList(comments)
    }

    private fun updateAverageRating(
        ratings: List<MemberRating>,
        binding: DetailsFragmentBinding
    ) {
        if (ratings.isNotEmpty()) {
            val average = viewModel.getAverage(ratings)
            val ratingAverageTxt = "(Average: $average, " +
                    "${ratings.size} rating${if (ratings.size > 1) "s" else ""})"
            binding.txtAverageRating.text = ratingAverageTxt
            binding.rtbAverage.rating = average.toFloat()
        }
    }

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
        binding.txtConstituency.text = viewModel.getConstituency(member)
    }
}

class CommentAdapter(private var comments: List<MemberComment>):
    RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_comment_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val authorTxt = "${comments[position].author} commented"
        holder.author.text = authorTxt
        holder.comment.text = comments[position].comment
    }

    override fun getItemCount() = comments.size

    fun updateList(newComments: List<MemberComment>) {
        comments = newComments
        this.notifyItemInserted(0)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var comment: TextView = itemView.findViewById(R.id.txt_comment)
        var author: TextView = itemView.findViewById(R.id.txt_author)
    }
}

class DetailsViewModel(application: Application): AndroidViewModel(application){

    private val memberRepo = MembersRepo
    private val ratingRepo = RatingsRepo
    private val commentRepo = CommentsRepo
    private var personNumber: Int = 0
    private var author = ""

    fun setPersonNumber(arguments: Bundle?) {
        personNumber = arguments?.get(Constants.KEY_PERSON_NUM) as Int
    }

    fun retrieveUsername(activity: FragmentActivity?) {
        val sharedPref = activity?.getSharedPreferences("userPref", Context.MODE_PRIVATE)
        author = sharedPref?.getString("username", "") ?: "anonymous"
    }

    //Member LiveData
    val memberDisplayed = Transformations.map(memberRepo.membersFromDB) { members ->
        members.find { it.personNumber == personNumber }}.distinctUntilChanged()

    //Ratings LiveData
    val ratings = Transformations.map(ratingRepo.ratings) { ratings ->
        ratings.filter { it.personNumber == personNumber }}.distinctUntilChanged()
    fun getAverage(ratings: List<MemberRating>) = ratings.sumOf { it.rating } / ratings.size
    fun addRating(rating: Double) {
        viewModelScope.launch {
            ratingRepo.addRating(MemberRating(0, personNumber, rating, author))
        } }

    //Comments LiveData
    val comments = Transformations.map(commentRepo.comments) { comments ->
        comments.filter { it.personNumber == personNumber }
            .sortedByDescending { it.date } }.distinctUntilChanged()
    fun addComment(comment: String) {
        viewModelScope.launch {
            commentRepo.addComment(MemberComment(0,
                personNumber, Calendar.getInstance().time, comment, author
            ))
        } }

    //Update member info
    fun getPicUrl(member: Member) = "https://avoindata.eduskunta.fi/${member.picture}"
    fun getMinister(member: Member) = if (member.minister) "Minister" else "Member Of Parliament"
    fun getName(member: Member) = "${member.first} ${member.last}"
    fun getAge(member: Member) = "${Calendar.getInstance().get(Calendar.YEAR) - member.bornYear}" +
            " years old"
    fun getLogoId(member: Member): Int {
        return MyApp.appContext.resources.getIdentifier(member.party,
            "drawable", MyApp.appContext.packageName)
    }
    fun getConstituency(member: Member) = member.constituency

}