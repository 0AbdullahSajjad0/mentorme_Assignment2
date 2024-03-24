package com.abdullahsajjad.i212477

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class resetpassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resetpassword)

        val resetbtn = findViewById<Button>(R.id.mainresetpassbtn)
        val backlogbtn = findViewById<ImageView>(R.id.backtolgnbtn)
        val backlogbtnlink = findViewById<TextView>(R.id.loginlinkrespass)

        resetbtn.setBackgroundColor(Color.parseColor("#0D5995"))

        resetbtn.setOnClickListener{
            startActivity(
                Intent(this,
                    login_screen::class.java)
            );
        }

        backlogbtn.setOnClickListener{
            startActivity(
                Intent(this,
                    forgotpassword::class.java)
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