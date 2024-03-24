package com.abdullahsajjad.i212477

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView

class TakeVideo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_video)

        var btn = findViewById<CardView>(R.id.makevideo)
        var switchtocam = findViewById<CardView>(R.id.switchtocam)

        btn.setOnClickListener{
            startActivity(
                Intent(this,
                    navigation::class.java)
            );
        }

        switchtocam.setOnClickListener{
            startActivity(
                Intent(this,
                    TakePicture::class.java)
            );
        }

    }
}