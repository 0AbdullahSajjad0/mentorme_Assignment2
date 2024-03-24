package com.abdullahsajjad.i212477

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import com.google.firebase.database.FirebaseDatabase

class EditProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        var name = findViewById<EditText>(R.id.updateName)
        var email = findViewById<EditText>(R.id.updateEmail)
        var pNum = findViewById<EditText>(R.id.updateNumber)
        var update = findViewById<Button>(R.id.updateprofbtn)
        val spinnerct = findViewById<Spinner>(R.id.EPspinnercity)
        val spinnercnt = findViewById<Spinner>(R.id.EPspinnercountry)

        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            this,
            R.array.city_array,
            android.R.layout.simple_spinner_item
        )

        val adapter2: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            this,
            R.array.country_array,
            android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerct.adapter = adapter
        spinnercnt.adapter = adapter2

        val user = intent.getSerializableExtra("userObject") as? Users

        update.setBackgroundColor(Color.parseColor("#0D5995"))
        update.setOnClickListener{
            if (user != null) {
                val selectedcity = spinnerct.selectedItem.toString()
                val selectedcountry = spinnercnt.selectedItem.toString()
                val dbRef = FirebaseDatabase.getInstance().getReference("Users").child(user.userId.toString())
                val tempuser = Users(user.userId, name.text.toString(), email.text.toString(),
                    pNum.text.toString(), selectedcountry, selectedcity)
                dbRef.setValue(tempuser)
                startActivity(
                    Intent(this,
                        navigation::class.java)
                );
            }
            else {
                Log.e("Debug", "User object is null in EditProfile activity")
                startActivity(
                    Intent(this,
                        navigation::class.java)
                );
            }

        }

        var btn = findViewById<ImageView>(R.id.gobacktoproffromedit)
        btn.setOnClickListener{
            startActivity(
                Intent(this,
                    navigation::class.java)
            );
        }

    }
}