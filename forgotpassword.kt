package com.abdullahsajjad.i212477

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class forgotpassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotpassword)

        val resetbtn = findViewById<Button>(R.id.resetpassbtn)
        val backlogbtn = findViewById<ImageView>(R.id.backtolgnbtn)
        val backlogbtnlink = findViewById<TextView>(R.id.loginlinkforpass)

        resetbtn.setBackgroundColor(Color.parseColor("#0D5995"))

        resetbtn.setOnClickListener{
            startActivity(
                Intent(this,
                    resetpassword::class.java)
            );
        }

        backlogbtn.setOnClickListener{
            startActivity(
                Intent(this,
                    login_screen::class.java)
            );
        }

        backlogbtnlink.setOnClickListener{
            startActivity(
                Intent(this,
                    login_screen::class.java)
            );
        }


    }
}