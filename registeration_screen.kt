package com.abdullahsajjad.i212477

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.database
import java.util.concurrent.TimeUnit

class registeration_screen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registeration_screen)

        val name = findViewById<EditText>(R.id.fullname)
        val email = findViewById<EditText>(R.id.regemail)
        val pass = findViewById<EditText>(R.id.pass)
        val num = findViewById<EditText>(R.id.phoneNum)
        val spinnerct = findViewById<Spinner>(R.id.spinnercity)
        val spinnercnt = findViewById<Spinner>(R.id.spinnercountry)

        val cities = arrayOf(R.array.city_array)
        val countries = arrayOf(R.array.country_array)

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

        val registerbtn = findViewById<Button>(R.id.signupbtn)
        val lgnlink = findViewById<TextView>(R.id.loginlink)

        val mAuth = FirebaseAuth.getInstance()
        val database = Firebase.database
        val myRef = database.getReference("Users")

        registerbtn.setBackgroundColor(Color.WHITE)

        var user: Users? = null
        val Uname: String? = null
        val Uemail: String? = null
        val Unum: String? = null
        val selectedcity: String? = null
        val selectedcountry: String? = null
        var UserId: String? = null

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                mAuth.createUserWithEmailAndPassword(email.text.toString(), pass.text.toString())
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful) {
                            // Registration successful
                            myRef.child(UserId!!).setValue(user)
                                .addOnCompleteListener {
                                    val intent = Intent(this@registeration_screen, navigation::class.java)
                                    intent.putExtra("userObject", user)
                                    startActivity(intent)
                                    finish()
                                }
                        } else {
                            // Registration failed, check if it's due to an existing email
                            if (task.exception is FirebaseAuthUserCollisionException) {
                                // Email is already registered
                                email.error = "This email is already registered"
                            } else {
                                // Other registration errors
                                Toast.makeText(this@registeration_screen, "Failed to SignUp", Toast.LENGTH_SHORT).show()
                            }
                        }

                    }
                    .addOnFailureListener{
                        Toast.makeText(this@registeration_screen, "Failed to SignUp", Toast.LENGTH_SHORT).show()
                    }
            }

            override fun onVerificationFailed(p0: com.google.firebase.FirebaseException) {
                Toast.makeText(this@registeration_screen, "Failed to Verify", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                Log.d("Debug", "Code Sent")

                val i = Intent(this@registeration_screen, verfiyphone::class.java)
                i.putExtra("userObject", user)
                i.putExtra("token",p0)
                i.putExtra("password",pass.text.toString())
                i.putExtra("email",email.text.toString())
                startActivity(i)
            }
        }


        registerbtn.setOnClickListener() {

            val Uname = name.text.toString()
            val Uemail = email.text.toString()
            val Unum = num.text.toString()
            val selectedcity = spinnerct.selectedItem.toString()
            val selectedcountry = spinnercnt.selectedItem.toString()

            if(Uname.isNullOrEmpty()){
                name.error = "Please Enter Name"
            }
            else if(Uemail.isNullOrEmpty()){
                email.error = "Please Enter Email"
            }
            else if(Unum.isNullOrEmpty()){
                num.error = "Please Enter Phone Number"
            }
            else if(selectedcity.isNullOrEmpty() or (selectedcity == "Select City")){
                Toast.makeText(this@registeration_screen, "Please Select a City", Toast.LENGTH_SHORT).show()
            }
            else if(selectedcountry.isNullOrEmpty() or (selectedcountry == "Select Country")){
                Toast.makeText(this@registeration_screen, "Please Select a Country", Toast.LENGTH_SHORT).show()
            }
            else {
                UserId = myRef.push().key!!
                user = Users(UserId, Uname, Uemail, Unum, selectedcountry, selectedcity, "")

                Log.d("UserObject", "User ID: ${user?.userId}")
                Log.d("UserObject", "Name: ${user?.name}")
                Log.d("UserObject", "Email: ${user?.email}")
                Log.d("UserObject", "Phone Number: ${user?.pNum}")
                Log.d("UserObject", "Country: ${user?.country}")
                Log.d("UserObject", "City: ${user?.city}")
                val options = PhoneAuthOptions.newBuilder()
                    .setPhoneNumber("+92" + num.text.toString())
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(this)
                    .setCallbacks(callbacks)
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)

            }

        }

        lgnlink.setOnClickListener{
            startActivity(
                Intent(this,
                    login_screen::class.java)
            );
        }


    }
}