package com.abdullahsajjad.i212477

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import java.util.ArrayList


class searchpage : AppCompatActivity() {

    private lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searchpage)

    }


}


