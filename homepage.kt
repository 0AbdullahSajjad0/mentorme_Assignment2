package com.abdullahsajjad.i212477

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.abdullahsajjad.i212477.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class homepage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        supportFragmentManager.beginTransaction()
            .replace(R.id.homefragment, HomeFragment())
            .commit()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navigationbar)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.homefragment, HomeFragment())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.search -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.homefragment, SearchFragment())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.addmentor -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.homefragment, AddFragment())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.chats -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.homefragment, ChatsFragment())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.homefragment, ProfileFragment())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                else -> false
            }
        }

    }
}