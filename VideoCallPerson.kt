package com.abdullahsajjad.i212477

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView

class VideoCallPerson : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call_person)

        var exitcall = findViewById<CardView>(R.id.endvideocall)

        exitcall.setOnClickListener{
            startActivity(
                Intent(this,
                    navigation::class.java)
            );
        }

    }
}