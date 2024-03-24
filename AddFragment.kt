package com.abdullahsajjad.i212477

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage

class AddFragment : Fragment() {

    private var selectedImage: Uri? = null
    private var uploadedImage: ImageView? = null
    private lateinit var storage: FirebaseStorage
    private lateinit var database: FirebaseDatabase
    private lateinit var dialog: AlertDialog
    private lateinit var MentorId: String
    private lateinit var url: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_addmentor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var back = view.findViewById<ImageView>(R.id.backtonavfromadd)
        back.setOnClickListener{
            val context = view.context
            context.startActivity(
                Intent(context, navigation::class.java)
            )
        }



        // Access the button by its ID
        val myButton: Button = view.findViewById(R.id.uploadbtn)
        val uploadPhoto: LinearLayout = view.findViewById(R.id.uploadmentorphoto)
        val uploadVideo: LinearLayout = view.findViewById(R.id.uploadmentorvid)
        val spinnerst = view.findViewById<Spinner>(R.id.spinnerstatus)
        val mentorName = view.findViewById<EditText>(R.id.mentorname)
        val mentordesc = view.findViewById<EditText>(R.id.mentordescription)
        uploadedImage = view.findViewById<ImageView>(R.id.uploadedmentorphoto)

        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            view.context,
            R.array.status_array,
            android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerst.adapter = adapter

        myButton.setBackgroundColor(Color.parseColor("#0D5995"))

        dialog = AlertDialog.Builder(requireContext())
            .setMessage("Uploading Picture for Profile...")
            .setCancelable(true)
            .create()

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val myRef = database.getReference("Mentors")
        var mentor: Mentors? = null
        MentorId = myRef.push().key!!

        myButton.setOnClickListener {

            Log.d("Completing form", "Form is being completed")

            var selectedstatus = spinnerst.selectedItem.toString()
            mentor = Mentors(MentorId, mentorName.text.toString(), mentordesc.text.toString(), selectedstatus, url)
            myRef.child(MentorId!!).setValue(mentor)
                .addOnCompleteListener {

                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.navfragment, HomeFragment())
                    transaction.addToBackStack(null)
                    transaction.commit()
                }

        }

        uploadPhoto.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        uploadVideo.setOnClickListener {
            startActivity(
                Intent(requireContext(), TakeVideo::class.java)
            );
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            dialog.show()
            val uri = data.data
            val ref = storage.reference.child("profilePics/${MentorId!!}")
            ref.putFile(uri!!).addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    url = it.toString()
                    uploadedImage?.setImageURI(uri)
                    dialog.dismiss()
                    dialog.setMessage("Profile Picture Uploaded Successfully!")
                    dialog.setCancelable(true)
                    dialog.show()
                }
            }

            // Do something with the imageUri
        }
    }
}
