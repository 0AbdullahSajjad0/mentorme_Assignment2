package com.abdullahsajjad.i212477

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class reviewpage : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviewpage)

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        val myRef = database.getReference("Reviews")
        val mentorID = intent.getStringExtra("MentorID")

        var name = findViewById<TextView>(R.id.name)
        var profilePic = findViewById<ImageView>(R.id.profilepic)
        var feedback = findViewById<TextView>(R.id.feedback)
        var OneStarRating = findViewById<ImageView>(R.id.onestarrating)
        var TwoStarRating = findViewById<ImageView>(R.id.twostarrating)
        var ThreeStarRating = findViewById<ImageView>(R.id.threestarrating)
        var FourStarRating = findViewById<ImageView>(R.id.fourstarrating)
        var FiveStarRating = findViewById<ImageView>(R.id.fivestarrating)
        var rating : Int = 0
        var backtodesc = findViewById<ImageView>(R.id.backtodescription)
        var feedbackbtn = findViewById<Button>(R.id.submitfeedback)

        feedbackbtn.setBackgroundColor(Color.parseColor("#0D5995"))


        val mentorRef = database.getReference("Mentors")
        if (mentorID != null) {
            Log.d("Debug", "MentorID in descripton: $mentorID")
            mentorRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("Debug", "Snapshot: $snapshot")

                    for (userSnapshot in snapshot.children) {
                        if (userSnapshot.key.toString() == mentorID) {
                            val mentorName = userSnapshot.child("name").getValue(String::class.java)
                            Log.d("Debug", "Mentor Found: $mentorName")
                            name.text = mentorName
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
                    profilePic.setImageBitmap(bitmap)
                }

            }

        }

        OneStarRating.setOnClickListener{
            if(rating > 1)
            {
                TwoStarRating.setImageResource(R.drawable.baseline_star_outline_24)
                ThreeStarRating.setImageResource(R.drawable.baseline_star_outline_24)
                FourStarRating.setImageResource(R.drawable.baseline_star_outline_24)
                FiveStarRating.setImageResource(R.drawable.baseline_star_outline_24)
            }
            OneStarRating.setImageResource(R.drawable.baseline_star_rate_24)
            rating = 1
        }
        TwoStarRating.setOnClickListener{
            if(rating > 2)
            {
                ThreeStarRating.setImageResource(R.drawable.baseline_star_outline_24)
                FourStarRating.setImageResource(R.drawable.baseline_star_outline_24)
                FiveStarRating.setImageResource(R.drawable.baseline_star_outline_24)
            }
            OneStarRating.setImageResource(R.drawable.baseline_star_rate_24)
            TwoStarRating.setImageResource(R.drawable.baseline_star_rate_24)
            rating = 2
        }
        ThreeStarRating.setOnClickListener{
            if(rating > 3)
            {
                FourStarRating.setImageResource(R.drawable.baseline_star_outline_24)
                FiveStarRating.setImageResource(R.drawable.baseline_star_outline_24)
            }
            OneStarRating.setImageResource(R.drawable.baseline_star_rate_24)
            TwoStarRating.setImageResource(R.drawable.baseline_star_rate_24)
            ThreeStarRating.setImageResource(R.drawable.baseline_star_rate_24)
            rating = 3
        }
        FourStarRating.setOnClickListener{
            if(rating > 3)
            {
                FiveStarRating.setImageResource(R.drawable.baseline_star_outline_24)
            }
            OneStarRating.setImageResource(R.drawable.baseline_star_rate_24)
            TwoStarRating.setImageResource(R.drawable.baseline_star_rate_24)
            ThreeStarRating.setImageResource(R.drawable.baseline_star_rate_24)
            FourStarRating.setImageResource(R.drawable.baseline_star_rate_24)
            rating = 4
        }
        FiveStarRating.setOnClickListener{
            OneStarRating.setImageResource(R.drawable.baseline_star_rate_24)
            TwoStarRating.setImageResource(R.drawable.baseline_star_rate_24)
            ThreeStarRating.setImageResource(R.drawable.baseline_star_rate_24)
            FourStarRating.setImageResource(R.drawable.baseline_star_rate_24)
            FiveStarRating.setImageResource(R.drawable.baseline_star_rate_24)
            rating = 5
        }

        backtodesc.setOnClickListener{
            startActivity(
                Intent(this,
                    mentordescription::class.java)
            );
        }

        feedbackbtn.setOnClickListener{

            var reviewId = myRef.push().key!!
            var review = Reviews(reviewId, mentorID, name.text.toString(), feedback.text.toString(), rating)

            myRef.child(auth.currentUser?.uid!!).child(reviewId).setValue(review)
                .addOnCompleteListener {
                    finish()
                }

        }

    }
}