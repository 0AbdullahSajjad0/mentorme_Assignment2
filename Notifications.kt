package com.abdullahsajjad.i212477

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class Notifications : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        var back = findViewById<ImageView>(R.id.backtohomeFromNotif)
        back.setOnClickListener{
            startActivity(
                Intent(this, navigation::class.java)
            )
        }

    }
}