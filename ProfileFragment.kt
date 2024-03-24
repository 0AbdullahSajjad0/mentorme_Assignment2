package com.abdullahsajjad.i212477

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abdullahsajjad.i212477.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.ArrayList


class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage

    private lateinit var reviewsArrayList: ArrayList<Reviews>
    private lateinit var reviewsAdaptor: ReviewsListAdaptor
    private lateinit var reviewsRecyclerView: RecyclerView
    private lateinit var selectedImage: Uri
    private lateinit var dialog: AlertDialog
    private lateinit var dialog2: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_profile, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var back = view.findViewById<ImageView>(R.id.backtonavfromprof)
        val user = arguments?.getSerializable("userObject") as? Users
        val dp = view.findViewById<ImageView>(R.id.dppicture)
        val editcover = view.findViewById<CardView>(R.id.editCover)
        val profileCoverImage = view.findViewById<ImageView>(R.id.backgroundimageofprofile)
        val profilename = view.findViewById<TextView>(R.id.profilename)
        val profilelocation = view.findViewById<TextView>(R.id.profilelocation)

        dialog = AlertDialog.Builder(view.context)
            .setMessage("Updating Profile Picture...")
            .setCancelable(false)
            .create()

        dialog2 = AlertDialog.Builder(view.context)
            .setMessage("Updating Cover Picture...")
            .setCancelable(false)
            .create()

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        database.getReference("Users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(Users::class.java)
                    val currentUser = auth.currentUser?.uid

                    if (user?.userId == currentUser) {
                        profilename.text = user?.name
                        profilelocation.text = user?.city
                        if(storage.reference.child("profilePics/${auth.currentUser?.uid}") != null) {
                            val ref = storage.reference.child("profilePics/${auth.currentUser?.uid}")
                            val localFile = createTempFile("tempImage", "jpeg")
                            ref.getFile(localFile).addOnSuccessListener {
                                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                                dp.setImageBitmap(bitmap)
                            }

                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Debug", "Error: $error")
            }
        })

        if(storage.reference.child("profilePics/${auth.currentUser?.uid}") != null) {
            val ref = storage.reference.child("profilePics/${auth.currentUser?.uid}")
            val localFile = createTempFile("tempImage", "jpeg")
            ref.getFile(localFile).addOnSuccessListener {
                // Load the image using Picasso
                Picasso.get().load(localFile).into(dp)
            }.addOnFailureListener { exception ->
                // Handle any errors
                Log.e("FirebaseStorage", "Error downloading image: $exception")
            }
        }

        if(storage.reference.child("profileCovers/${auth.currentUser?.uid}") != null) {
            Log.d("Debug", "Cover Image Found")
            val ref = storage.reference.child("profileCovers/${auth.currentUser?.uid}")
            val localFile = createTempFile("tempImage", "jpeg")
            ref.getFile(localFile).addOnSuccessListener {
                Picasso.get().load(localFile).into(profileCoverImage)
            }.addOnFailureListener { exception ->
                // Handle any errors that may occur while fetching the image
                Log.e("Adapter", "Error fetching image: ${exception.message}")
            }
        }

        dp.setOnClickListener{
            val context = view.context
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }


        editcover.setOnClickListener{
            val context = view.context
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }


        dataInitialize()
        val layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        reviewsRecyclerView = view.findViewById(R.id.reviewsRecycle)
        reviewsRecyclerView.layoutManager = layoutManager
        reviewsRecyclerView.setHasFixedSize(true)




        /*if (user != null) {
            if(user.profilePic.isNullOrEmpty()) {
                val profilePic = view.findViewById<ImageView>(R.id.dppicture)
                profilePic.setImageBitmap(user.profilePic)
            }
        }*/

        back.setOnClickListener{
            val context = view.context
            context.startActivity(
                Intent(context, navigation::class.java)
            )
        }


        var edit = view.findViewById<CardView>(R.id.editProfile)
        edit.setOnClickListener{
            val context = view.context

            if (user != null) {
                val intent = Intent(context, EditProfile::class.java)
                intent.putExtra("userObject", user)
                context.startActivity(intent)
            }
            else {
                Log.e("Debug", "User object is null")
                context.startActivity(
                    Intent(context, EditProfile::class.java)
                )
            }
        }

        var btn = view.findViewById<Button>(R.id.viewBookedSessions)

        btn.setBackgroundColor(Color.parseColor("#FFC107"))

        btn.setOnClickListener{
            val context = view.context
            context.startActivity(
                Intent(context, ViewBookedSessions::class.java)
            )
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == -1 && data != null) {
            dialog.show()
            val dp = view?.findViewById<ImageView>(R.id.dppicture)
            val uri = data.data
            val ref = storage.reference.child("profilePics/${auth.currentUser?.uid}")
            ref.putFile(uri!!).addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    val url = it.toString()
                    database.getReference("Users").child(auth.currentUser?.uid!!).child("profilePic").setValue(url)
                    dp?.setImageURI(uri)
                    dialog.dismiss()
                    dialog.setMessage("Profile Picture Updated")
                    dialog.setCancelable(true)
                }
            }
        }
        if(requestCode == 1 && resultCode == -1 && data != null) {
            dialog2.show()
            val cover = view?.findViewById<ImageView>(R.id.backgroundimageofprofile)
            val uri = data.data
            val ref = storage.reference.child("profileCovers/${auth.currentUser?.uid}")
            ref.putFile(uri!!).addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    val url = it.toString()
                    Log.d("Debug", "URL: $url")

                    Picasso.get().load(uri).into(cover)
                    //cover?.setImageURI(uri)
                    dialog2.dismiss()
                    dialog2.setMessage("Cover Picture Updated")
                    dialog2.setCancelable(true)
                }
            }
        }
    }

    private fun dataInitialize() {
        reviewsArrayList = ArrayList<Reviews>()

        val userRef = database.getReference("Reviews")

        userRef.child(auth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("Debug", "Snapshot: $snapshot")

                for (userSnapshot in snapshot.children) {
                    val reviewId = userSnapshot.key.toString()
                    val mentorId = userSnapshot.child("mentorId").getValue(String::class.java)
                    val mentorName = userSnapshot.child("name").getValue(String::class.java)
                    val feedback = userSnapshot.child("feedback").getValue(String::class.java)
                    val rating = userSnapshot.child("rating").getValue(Int::class.java)

                    val review = Reviews(reviewId,mentorId, mentorName, feedback, rating)

                    reviewsArrayList.add(review)
                }

                reviewsAdaptor = ReviewsListAdaptor(reviewsArrayList, this@ProfileFragment)
                reviewsRecyclerView.adapter = reviewsAdaptor
                reviewsAdaptor.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        userRef.keepSynced(true)

    }
}
