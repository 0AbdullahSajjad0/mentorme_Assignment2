package com.abdullahsajjad.i212477

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date

class OpenChatFragment : Fragment(), DisplayMessagesAdaptor.OnItemClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var messagesNodeRef: DatabaseReference
    private lateinit var dialog: AlertDialog

    private lateinit var messagesadaptor: DisplayMessagesAdaptor
    private lateinit var messagesrecyclerView: RecyclerView
    private lateinit var messagesArrayList: ArrayList<Message>
    private lateinit var userTalkingto: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_open_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog = AlertDialog.Builder(view.context)
            .setMessage("Sending Picture...")
            .setCancelable(false)
            .create()

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        userTalkingto = arguments?.getString("userId")!!

        var userTalkingtoName = arguments?.getString("userName")
        Log.d("OpenChatFragment", "I am talking to: $userTalkingto  $userTalkingtoName")

        val uploadPhoto: ImageView = view.findViewById(R.id.opencamera)
        val addFile: ImageView = view.findViewById(R.id.pinfile)
        val startAudioCall: ImageView = view.findViewById(R.id.callperson)
        val startVideoCall: ImageView = view.findViewById(R.id.videocallperson)
        val backButton: ImageView = view.findViewById(R.id.backtoChatsFromChat)
        val sendButton: ImageView = view.findViewById(R.id.sendmessage)
        val chatName: TextView = view.findViewById(R.id.chatpersonname)
        val messageWritten = view.findViewById<TextView>(R.id.writtenmessage)
        chatName.text = userTalkingtoName

        dataInitialize()

        val layoutManager2 = LinearLayoutManager(context)
        messagesrecyclerView = view.findViewById(R.id.completeChatRecycler)
        messagesrecyclerView.layoutManager = layoutManager2
        messagesrecyclerView.setHasFixedSize(true)

        backButton.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.navfragment, ChatsFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        addFile.setOnClickListener {
            val context = view.context
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        sendButton.setOnClickListener {
            val currentUserId = auth.currentUser!!.uid.toString()
            val userTalkingTo = userTalkingto // Assuming User A's ID

            // Sort user IDs alphabetically to ensure consistency
            val sortedUserIds = listOf(currentUserId, userTalkingTo).sorted()

            // Form the chat ID by concatenating sorted user IDs
            val chatId = "${sortedUserIds[0]}_${sortedUserIds[1]}"

            Log.d("OpenChatFragment", "Message you want to send: ${messageWritten.text}")
            val message = Message(messageWritten.text.toString(), "" ,auth.currentUser!!.uid, getCurrentTime())
            val messageId = messagesNodeRef.push().key!!
            messagesNodeRef.child(messageId).setValue(message)
                .addOnCompleteListener {
                    Log.d("OpenChatFragment", "Message sent")
                    messagesArrayList.add(message)
                    messagesadaptor = DisplayMessagesAdaptor(requireContext(),messagesArrayList, this, chatId)
                    messagesrecyclerView.adapter = messagesadaptor
                    messagesrecyclerView.scrollToPosition(messagesArrayList.size - 1)
                    messagesadaptor.notifyDataSetChanged()
                    messagesrecyclerView.scrollToPosition(messagesArrayList.size - 1)
                    messageWritten.text = ""
                }

            messagesNodeRef.keepSynced(true)
        }

        uploadPhoto.setOnClickListener {
            startActivity(
                Intent(requireContext(), TakePicture::class.java)
            );
        }

        startAudioCall.setOnClickListener {
            startActivity(
                Intent(requireContext(), CallPerson::class.java)
            );
        }

        startVideoCall.setOnClickListener {
            startActivity(
                Intent(requireContext(), VideoCallPerson::class.java)
            );
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == -1 && data != null) {

            val currentUserId = auth.currentUser!!.uid.toString()
            val userTalkingTo = userTalkingto // Assuming User A's ID

            // Sort user IDs alphabetically to ensure consistency
            val sortedUserIds = listOf(currentUserId, userTalkingTo).sorted()

            // Form the chat ID by concatenating sorted user IDs
            val chatId = "${sortedUserIds[0]}_${sortedUserIds[1]}"


            dialog.show()
            val uri = data.data
            val messageId = messagesNodeRef.push().key!!
            val ref = storage.reference.child("UserChats/MessageImages/${messageId}")
            ref.putFile(uri!!).addOnSuccessListener {
                dialog.dismiss()
                val message = Message("", messageId ,auth.currentUser!!.uid, getCurrentTime())
                messagesNodeRef.child(messageId).setValue(message)
                    .addOnCompleteListener {
                        Log.d("OpenChatFragment", "Message sent")
                        messagesArrayList.add(message)
                        messagesadaptor = DisplayMessagesAdaptor(requireContext(),messagesArrayList, this, chatId)
                        messagesrecyclerView.adapter = messagesadaptor
                        messagesrecyclerView.scrollToPosition(messagesArrayList.size - 1)
                        messagesadaptor.notifyDataSetChanged()
                        messagesrecyclerView.scrollToPosition(messagesArrayList.size - 1)
                    }
            }
        }
    }

    private fun dataInitialize(){
        messagesArrayList = arrayListOf<Message>()

        val currentUserId = auth.currentUser!!.uid.toString()
        val userTalkingTo = userTalkingto // Assuming User A's ID

        // Sort user IDs alphabetically to ensure consistency
        val sortedUserIds = listOf(currentUserId, userTalkingTo).sorted()

        // Form the chat ID by concatenating sorted user IDs
        val chatId = "${sortedUserIds[0]}_${sortedUserIds[1]}"

        // Now use this chatId to reference the chat node
        val chatRef = database.getReference("UserChats").child(chatId)

        messagesNodeRef = chatRef.child("messages")

        messagesNodeRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Iterate through each child node (each message)
                for (messageSnapshot in dataSnapshot.children) {
                    // Retrieve message data

                    if(messageSnapshot.child("imageUrl").getValue(String::class.java) != "") {

                        if (storage.reference.child("UserChats/MessageImages/${messageSnapshot.key.toString()}") != null) {
                            val senderId =
                                messageSnapshot.child("writtenBy").getValue(String::class.java)
                            val timestamp =
                                messageSnapshot.child("timestamp").getValue(String::class.java)

                            val message =
                                Message("", messageSnapshot.key.toString(), senderId!!, timestamp!!)
                            messagesArrayList.add(message)
                            continue
                        }
                    }
                    val messageContent = messageSnapshot.child("message").getValue(String::class.java)
                    val senderId = messageSnapshot.child("writtenBy").getValue(String::class.java)
                    val timestamp = messageSnapshot.child("timestamp").getValue(String::class.java)
                    Log.d("Message", "Message: $messageContent, Sender: $senderId, Timestamp: $timestamp")

                    val message = Message(messageContent!!, "" ,senderId!!, timestamp!!)
                    messagesArrayList.add(message)
                    // Handle the retrieved message data
                }

                messagesadaptor = DisplayMessagesAdaptor(requireContext(),messagesArrayList, this@OpenChatFragment, chatId)
                messagesrecyclerView.adapter = messagesadaptor
                messagesrecyclerView.scrollToPosition(messagesArrayList.size - 1)
                messagesadaptor.notifyDataSetChanged()
                Log.d("OpenChatFragment", "Data in messagesArrayList: $messagesArrayList")
                messagesrecyclerView.scrollToPosition(messagesArrayList.size - 1)

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                Log.e("Firebase", "Error retrieving messages: ${databaseError.message}")
            }
        })

        messagesNodeRef.keepSynced(true)

    }

    fun getCameraInstance(): Camera? {

        var check = checkCameraHardware(requireContext())

        if (check) {
            return try {
                Camera.open() // attempt to get a Camera instance
            } catch (e: Exception) {
                // Camera is not available (in use or does not exist)
                null // returns null if camera is unavailable
            }
        }
        return null
    }

    private fun checkCameraHardware(context: Context): Boolean {
        if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true
        } else {
            // no camera on this device
            return false
        }
    }

    fun getCurrentTime(): String {
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("HH:mm")
        val timeString = dateFormat.format(Date(currentTime))
        return timeString
    }

    override fun onItemClick(messageContent: String) {
        Log.d("Adapter", "Hello C")
        // Enable editing on click
        //holder.message.inputType = InputType.TYPE_CLASS_TEXT
    }

}
