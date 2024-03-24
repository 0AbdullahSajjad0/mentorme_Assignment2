package com.abdullahsajjad.i212477

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.ArrayList

class ViewBookedSessions : AppCompatActivity() {

    private lateinit var adaptor: BookingsListAdaptor
    private lateinit var recyclerView: RecyclerView
    private lateinit var newsArrayList: ArrayList<Bookings>

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_booked_sessions)

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        dataInitialize()
        val layoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.bookingsRecycler)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)

        var btn = findViewById<ImageView>(R.id.backtoprof)

        btn.setOnClickListener{
            finish()
        }

    }

    private fun dataInitialize(){
        newsArrayList = ArrayList<Bookings>()

        val myRef = database.getReference("Bookings").child(auth.currentUser!!.uid)

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("Debug", "Snapshot: $snapshot")

                for (userSnapshot in snapshot.children) {
                    val userID = userSnapshot.child("userId").getValue(String::class.java)
                    val mentorId = userSnapshot.child("mentorId").getValue(String::class.java)
                    val mentorName = userSnapshot.child("name").getValue(String::class.java)
                    val date = userSnapshot.child("date").getValue(String::class.java)
                    val time = userSnapshot.child("time").getValue(String::class.java)

                    val booking = Bookings(userSnapshot.key.toString(),userID, mentorId ,mentorName, date, time, 0)

                    newsArrayList.add(booking)
                    //val mentorTitle = userSnapshot.child("mentorTitle").getValue(String::class.java)

                }

                adaptor = BookingsListAdaptor(newsArrayList, this@ViewBookedSessions)
                recyclerView.adapter = adaptor
                adaptor.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }
}