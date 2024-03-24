package com.abdullahsajjad.i212477

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class login_screen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)

        val email = findViewById<EditText>(R.id.logemail)
        val pass = findViewById<EditText>(R.id.logpass)
        var btn = findViewById<TextView>(R.id.signup)
        var lgnbtn = findViewById<TextView>(R.id.lgnbtn)
        var forgotbtn = findViewById<TextView>(R.id.forgotpasslink)

        val mAuth = FirebaseAuth.getInstance()
        lgnbtn.setBackgroundColor(Color.WHITE)

        /*if(mAuth.currentUser!=null){
            val intent = Intent(this@login_screen, navigation::class.java)
            startActivity(intent)
            finish()
        }*/

        lgnbtn.setOnClickListener() {
            mAuth.signInWithEmailAndPassword(email.text.toString(), pass.text.toString())
                .addOnSuccessListener {

                val userRef = FirebaseDatabase.getInstance().getReference("Users")
                val query = userRef.orderByChild("email").equalTo(email.toString())

                userRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        Log.d("Debug", "Snapshot: $snapshot")

                        for (userSnapshot in snapshot.children) {
                            val user = userSnapshot.getValue(Users::class.java)
                            val userEmail = user?.email

                            // Compare the userEmail with the target email
                            Log.d("Email", "Email Recieved: $userEmail")
                            Log.d("Email", "Email Input: ${email.toString()}")
                            if (userEmail == email.text.toString()) {
                                val userName = user?.name
                                Log.d("Debug", "UserName: $userName")

                                // Now you can use the userName as needed
                                // For example, pass it to the next activity or fragment
                                val intent = Intent(this@login_screen, navigation::class.java)
                                intent.putExtra("userObject", user)
                                startActivity(intent)
                                finish()

                                // Assuming you only want to handle the first match
                                return
                            }
                        }

                        // If the loop completes and no match is found
                        Toast.makeText(this@login_screen, "User not found", Toast.LENGTH_SHORT).show()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle database query cancellation
                        Toast.makeText(this@login_screen, "Failed to query database", Toast.LENGTH_SHORT).show()
                    }
                })
                }
                .addOnFailureListener{
                    Toast.makeText(this, "Failed to SignIn", Toast.LENGTH_SHORT).show()
                }
        }










        btn.setOnClickListener{
            startActivity(
                Intent(this,
                    registeration_screen::class.java)
            );
        }

        forgotbtn.setOnClickListener{
            startActivity(
                Intent(this,
                    forgotpassword::class.java)
            );
        }

    }
}