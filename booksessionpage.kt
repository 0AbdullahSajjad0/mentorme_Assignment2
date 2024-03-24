package com.abdullahsajjad.i212477

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

class booksessionpage : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage

    fun getDayWithSuffix(day: Int): String {
        return when {
            day in 11..13 -> "${day}th"
            day % 10 == 1 -> "${day}st"
            day % 10 == 2 -> "${day}nd"
            day % 10 == 3 -> "${day}rd"
            else -> "${day}th"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booksessionpage)

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        val myRef = database.getReference("Bookings")

        var backtodesc = findViewById<ImageView>(R.id.backtodescription)
        var slot1btn = findViewById<Button>(R.id.timeslot1)
        var slot2btn = findViewById<Button>(R.id.timeslot2)
        var slot3btn = findViewById<Button>(R.id.timeslot3)
        var calender = findViewById<CalendarView>(R.id.date)
        var bookbtn = findViewById<Button>(R.id.submitbooking)
        var MentorName = findViewById<TextView>(R.id.name)
        var mentorImage = findViewById<ImageView>(R.id.mentorimage)

        val currentDate = LocalDate.now()
        var date_day = currentDate.dayOfMonth
        var date_month = currentDate.monthValue
        var date_year = currentDate.year
        var date = "$date_day/$date_month/$date_year"
        var time = "11:00 AM"

        bookbtn.setBackgroundColor(Color.parseColor("#0D5995"))
        slot1btn.setBackgroundColor(Color.parseColor("#DDDEDF"))
        slot2btn.setBackgroundColor(Color.parseColor("#0D5995"))
        slot3btn.setBackgroundColor(Color.parseColor("#DDDEDF"))

        slot1btn.setOnClickListener{
            slot1btn.setBackgroundColor(Color.parseColor("#0D5995"))
            slot2btn.setBackgroundColor(Color.parseColor("#DDDEDF"))
            slot3btn.setBackgroundColor(Color.parseColor("#DDDEDF"))
            slot1btn.setTextColor(Color.WHITE)
            slot2btn.setTextColor(Color.BLACK)
            slot3btn.setTextColor(Color.BLACK)
            time = "10:00 AM"
        }
        slot2btn.setOnClickListener{
            slot1btn.setBackgroundColor(Color.parseColor("#DDDEDF"))
            slot2btn.setBackgroundColor(Color.parseColor("#0D5995"))
            slot3btn.setBackgroundColor(Color.parseColor("#DDDEDF"))
            slot1btn.setTextColor(Color.BLACK)
            slot2btn.setTextColor(Color.WHITE)
            slot3btn.setTextColor(Color.BLACK)
            time = "11:00 AM"
        }
        slot3btn.setOnClickListener{
            slot1btn.setBackgroundColor(Color.parseColor("#DDDEDF"))
            slot2btn.setBackgroundColor(Color.parseColor("#DDDEDF"))
            slot3btn.setBackgroundColor(Color.parseColor("#0D5995"))
            slot1btn.setTextColor(Color.BLACK)
            slot2btn.setTextColor(Color.BLACK)
            slot3btn.setTextColor(Color.WHITE)
            time = "12:00 PM"
        }

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
                            MentorName.text = mentorName
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

        calender.setOnDateChangeListener { view, year, month, dayOfMonth ->
            date_year = year
            date_month = month + 1
            date_day = dayOfMonth
            date = "$dayOfMonth/$month/$year"
            Log.d("Debug", "Date Selected: $date")
        }

        backtodesc.setOnClickListener{
            finish()
        }

        bookbtn.setOnClickListener{
            /*startActivity(
                Intent(this,
                    navigation::class.java)
            );*/
            var formattedDay = getDayWithSuffix(date_day)
            var nameMonth = Month.of(date_month).getDisplayName(TextStyle.SHORT, Locale.getDefault())
            var formattedDate = "$formattedDay $nameMonth $date_year"

            var bookingId = myRef.push().key!!
            var booking = Bookings(bookingId, auth.currentUser?.uid, mentorID, MentorName.text.toString(), formattedDate, time, R.drawable.baseline_person_24)
            myRef.child(auth.currentUser!!.uid).child(booking.bookingId!!).setValue(booking)
                .addOnCompleteListener {
                    val intent = Intent(this@booksessionpage, navigation::class.java)
                    startActivity(intent)
                    finish()
                }

            Log.d("Debug", "Date Selected: $formattedDate")
        }

    }
}