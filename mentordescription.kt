package com.abdullahsajjad.i212477

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class mentordescription : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mentordescription)

        var DisplayName = findViewById<TextView>(R.id.mentorname)
        var DisplayDescription = findViewById<TextView>(R.id.mentoraboutme)
        var mentorImage = findViewById<ImageView>(R.id.mentorimage)

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        val mentorRef = database.getReference("Mentors")
        val mentorID = intent.getStringExtra("MentorID")
        if (mentorID != null) {
            Log.d("Debug", "MentorID in descripton: $mentorID")
            mentorRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("Debug", "Snapshot: $snapshot")

                    for (userSnapshot in snapshot.children) {
                        if(userSnapshot.key.toString() == mentorID)
                        {
                            val mentorName = userSnapshot.child("name").getValue(String::class.java)
                            val mentordescription = userSnapshot.child("description").getValue(String::class.java)
                            Log.d("Debug", "Mentor Found: $mentorName")
                            DisplayName.text = mentorName
                            DisplayDescription.text = mentordescription
                            break
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

            if(storage.reference.child("profilePics/${mentorID}") != null) {
                val ref = storage.reference.child("profilePics/${mentorID}")
                val localFile = createTempFile("tempImage", "jpeg")
                ref.getFile(localFile).addOnSuccessListener {
                    val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                    mentorImage.setImageBitmap(bitmap)
                }

            }


        } else {

        }

        var gobackbtn = findViewById<ImageView>(R.id.backtohome)
        var reviewbtn = findViewById<CardView>(R.id.review)
        var btn = findViewById<Button>(R.id.booksession)

        btn.setBackgroundColor(Color.parseColor("#0D5995"))

        gobackbtn.setOnClickListener{
            finish()
        }

        reviewbtn.setOnClickListener{
            val intent = Intent(this, reviewpage::class.java)
            intent.putExtra("MentorID", mentorID)
            startActivity(intent)
        }

        btn.setOnClickListener{
            val intent = Intent(this, booksessionpage::class.java)
            intent.putExtra("MentorID", mentorID)
            startActivity(intent)
        }
    }
}