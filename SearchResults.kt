package com.abdullahsajjad.i212477

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.ArrayList

private lateinit var adaptor: MyAdaptor2
private lateinit var recyclerView: RecyclerView
private lateinit var newsArrayList: ArrayList<HomeMentorsClass>

private lateinit var auth: FirebaseAuth
private lateinit var database: FirebaseDatabase
private lateinit var storage: FirebaseStorage

class SearchResults : Fragment() {

    private var query: String? = null

    companion object {
        // Define a function to create a new instance of SearchResults with arguments
        fun newInstance(query: String): SearchResults {
            val fragment = SearchResults()
            val args = Bundle()
            args.putString("query", query)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_search_results, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backtohome = view.findViewById<ImageView>(R.id.backtohomeFromResults)
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        val filterspinner = view.findViewById<Spinner>(R.id.filterid)
        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            view.context,
            R.array.options_array,
            android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filterspinner.adapter = adapter

        val queryy = arguments?.getString("query")
        if(!queryy.isNullOrEmpty()){
            query = queryy
        }

        Log.d("SearchResults", "Query: $query")

        backtohome.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.navfragment, SearchFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        //Populating the recycler list
        dataInitialize(query!!)
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.resultlistview)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)

        //


    }


    private fun dataInitialize(input: String){
        newsArrayList = ArrayList<HomeMentorsClass>()

        val userRef = database.getReference("Mentors")

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("Debug", "Snapshot: $snapshot")

                for (userSnapshot in snapshot.children) {
                    val mentorName = userSnapshot.child("name").getValue(String::class.java)
                    val checkmentor = mentorName?.lowercase()
                    Log.d("Debug", "MentorName: $checkmentor")
                    Log.d("Debug", "Input: $input")
                    if(checkmentor?.contains(input) == false){
                        continue
                    }
                    val mentorId = userSnapshot.key.toString()
                    val mentorAvailability = userSnapshot.child("status").getValue(String::class.java)
                    val mentorImageResId = R.drawable.greyheart // Provide a default image resource ID
                    val mentorPrice = "$10/Session"

                    val mentor = HomeMentorsClass(mentorId,mentorName!!, "Full Stack Developer", mentorAvailability!!, mentorImageResId, mentorPrice)

                    newsArrayList.add(mentor)
                    //val mentorTitle = userSnapshot.child("mentorTitle").getValue(String::class.java)

                }

                adaptor = MyAdaptor2(newsArrayList, this@SearchResults)
                recyclerView.adapter = adaptor
                adaptor.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        userRef.keepSynced(true)

    }
}
