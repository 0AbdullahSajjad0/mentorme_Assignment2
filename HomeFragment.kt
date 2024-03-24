package com.abdullahsajjad.i212477

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.ArrayList

class HomeFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage

    private lateinit var mentorsArrayList: ArrayList<HomeMentorsClass>
    private lateinit var mentorsAdaptor: MentorsListAdaptor
    private lateinit var topmentorRecyclerView: RecyclerView
    private lateinit var educationmentorRecyclerView: RecyclerView
    private lateinit var recentmentorsRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_homepage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("Users")
        var userName = arguments?.getString("userName")
        auth = FirebaseAuth.getInstance()
        val userNameTextView = view.findViewById<TextView>(R.id.homedisplayName)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("Debug", "Snapshot: $snapshot")

                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(Users::class.java)
                    val currentUser = auth.currentUser?.uid

                    if (user?.userId == currentUser) {
                        userName = user?.name
                        userNameTextView.text = userName
                        Log.d("Debug", "UserName: $userName")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        userRef.keepSynced(true)

        userNameTextView.text = userName

        dataInitialize()
        val layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        val layoutManager2 = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        val layoutManager3 = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        topmentorRecyclerView = view.findViewById(R.id.topmentorsrecyclerView)
        topmentorRecyclerView.layoutManager = layoutManager
        topmentorRecyclerView.setHasFixedSize(true)

        educationmentorRecyclerView = view.findViewById(R.id.educationalmentorsrecyclerView)
        educationmentorRecyclerView.layoutManager = layoutManager2
        educationmentorRecyclerView.setHasFixedSize(true)

        recentmentorsRecyclerView = view.findViewById(R.id.recentmentorsrecyclerView)
        recentmentorsRecyclerView.layoutManager = layoutManager3
        recentmentorsRecyclerView.setHasFixedSize(true)

        var checknotif = view.findViewById<CardView>(R.id.checknotifications)
        checknotif.setOnClickListener{
            val context = view.context
            context.startActivity(
                Intent(context, Notifications::class.java)
            )
        }

    }

    private fun dataInitialize() {
        mentorsArrayList = ArrayList<HomeMentorsClass>()

        val userRef = database.getReference("Mentors")

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("Debug", "Snapshot: $snapshot")

                for (userSnapshot in snapshot.children) {
                    val mentorId = userSnapshot.key.toString()
                    val mentorName = userSnapshot.child("name").getValue(String::class.java)
                    //val mentorTitle = userSnapshot.child("mentorTitle").getValue(String::class.java)
                    val mentorAvailability = userSnapshot.child("status").getValue(String::class.java)
                    val mentorImageResId = R.drawable.greyheart // Provide a default image resource ID
                    val mentorPrice = "$10/Session"

                    val mentor = HomeMentorsClass(mentorId,mentorName!!, "Full Stack Developer", mentorAvailability!!, mentorImageResId, mentorPrice)

                    mentorsArrayList.add(mentor)
                }

                mentorsAdaptor = MentorsListAdaptor(mentorsArrayList, this@HomeFragment)
                topmentorRecyclerView.adapter = mentorsAdaptor
                educationmentorRecyclerView.adapter = mentorsAdaptor
                recentmentorsRecyclerView.adapter = mentorsAdaptor
                mentorsAdaptor.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        userRef.keepSynced(true)

    }
}
