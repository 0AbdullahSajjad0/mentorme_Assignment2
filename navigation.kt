package com.abdullahsajjad.i212477

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.abdullahsajjad.i212477.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class navigation : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        // Create a fragment instance and set the arguments
        val homeFragment = HomeFragment()
        val profFragment = ProfileFragment()


        val user = intent.getSerializableExtra("userObject") as? Users
        if (user != null) {
            val userName = user.name
            Log.d("Debug", "User Name Received in navigation is: $userName")

            val bundle = Bundle()
            bundle.putString("userName", userName)

            val profbundle = Bundle()
            profbundle.putSerializable("userObject", user)
            homeFragment.arguments = bundle
            profFragment.arguments = profbundle
        }
        else {
            Log.e("Debug", "User object is null")
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.navfragment, homeFragment)
            .commit()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navigationbar)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.navfragment, homeFragment)
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.search -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.navfragment, SearchFragment())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.addmentor -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.navfragment, AddFragment())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.chats -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.navfragment, ChatsFragment())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.navfragment, profFragment)
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                else -> false
            }
        }

    }
}