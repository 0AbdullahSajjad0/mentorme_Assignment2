package com.abdullahsajjad.i212477

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.ArrayList

private lateinit var communityadaptor: CommunityDisplayAdaptor
private lateinit var communityrecyclerView: RecyclerView
private lateinit var comunityArrayList: ArrayList<CommunityDisplayClass>

private lateinit var chatsadaptor: ChatsDisplayAdaptor
private lateinit var chatsrecyclerView: RecyclerView
private lateinit var chatsArrayList: ArrayList<ChatsDisplayClass>

lateinit var communityImages: Array<Int>
lateinit var chatsProfileImages: Array<Int>
lateinit var chatsMessagesColor: Array<Int>
lateinit var chatsNames: Array<String>
lateinit var chatsMessages: Array<String>

class ChatsFragment : Fragment(), ChatsDisplayAdaptor.OnItemClickListener, CommunityDisplayAdaptor.OnItemClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_chatspage, container, false)
    }

    override fun onCommunityClick(position: Int) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.navfragment, OpenCommunityFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onItemClick(position: Int, userId: String, userName: String) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        val fragment = OpenChatFragment().apply {
            arguments = Bundle().apply {
                putString("userId", userId)
                putString("userName", userName)
            }
        }
        transaction.replace(R.id.navfragment, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        var back = view.findViewById<ImageView>(R.id.backtonavfromchats)
        back.setOnClickListener{
            val context = view.context
            context.startActivity(
                Intent(context, navigation::class.java)
            )
        }


        //Populating the recycler list
        dataInitialize()

        val layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        communityrecyclerView = view.findViewById(R.id.communityrecyclerView)
        communityrecyclerView.layoutManager = layoutManager
        communityrecyclerView.setHasFixedSize(true)
        communityadaptor = CommunityDisplayAdaptor(comunityArrayList, this)
        communityrecyclerView.adapter = communityadaptor


        val layoutManager2 = LinearLayoutManager(context)
        chatsrecyclerView = view.findViewById(R.id.messagesrecyclerView)
        chatsrecyclerView.layoutManager = layoutManager2
        chatsrecyclerView.setHasFixedSize(true)

    }

    private fun dataInitialize(){
        comunityArrayList = arrayListOf<CommunityDisplayClass>()
        chatsArrayList = arrayListOf<ChatsDisplayClass>()

        val userRef = database.getReference("Users")

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("Debug", "Snapshot: $snapshot")

                for (userSnapshot in snapshot.children) {
                    val userId = userSnapshot.key.toString()

                    if(userId == auth.currentUser!!.uid){
                        continue
                    }

                    val userName = userSnapshot.child("name").getValue(String::class.java)
                    val userDefaultImage = R.drawable.baseline_person_24 // Provide a default image resource ID
                    val messages = "No New Messages"
                    val color = R.color.grey

                    val chat = ChatsDisplayClass(userId, userDefaultImage, userName!!, messages, color)

                    chatsArrayList.add(chat)
                }

                chatsadaptor = ChatsDisplayAdaptor(chatsArrayList, this@ChatsFragment)
                chatsrecyclerView.adapter = chatsadaptor
                chatsadaptor.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        userRef.keepSynced(true)

        communityImages = arrayOf(
            R.drawable.baseline_person_24,
            R.drawable.baseline_person_24,
            R.drawable.baseline_person_24,
            R.drawable.baseline_person_24,
            R.drawable.baseline_person_24
        )

        for (i in communityImages.indices){
            val n = CommunityDisplayClass(communityImages[i])
            comunityArrayList.add(n)
        }

    }

}
