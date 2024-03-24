package com.abdullahsajjad.i212477

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class MyAdaptor(private val newsList: ArrayList<news>) : RecyclerView.Adapter<MyAdaptor.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recentmentorssearch,
        parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = newsList[position]
        holder.titleImage.setImageResource(currentItem.titleImage)
        holder.tvHeading.text = currentItem.heading
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val titleImage : ImageView = itemView.findViewById(R.id.title_image)
        val tvHeading : TextView = itemView.findViewById(R.id.tvHeading)
    }

}


class MyAdaptor2(private val resultsList: ArrayList<HomeMentorsClass>, private val itemClickListener: SearchResults) : RecyclerView.Adapter<MyAdaptor2.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.searchresultslist,
            parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = resultsList[position]
        holder.name.text = currentItem.name
        holder.description.text = currentItem.profession
        holder.status.text = currentItem.status
        holder.cost.text = currentItem.cost
        holder.image.setImageResource(currentItem.image)

        holder.itemView.setOnClickListener {
            val MentorID = currentItem.MentorId
            Log.d("MentorsListAdaptor", "Mentor ID clicked: $MentorID")
            val context = holder.itemView.context
            val intent = Intent(context, mentordescription::class.java)
            intent.putExtra("MentorID", MentorID) // Add MentorID as an extra to the intent
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return resultsList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val image : ImageView = itemView.findViewById(R.id.fav_emoji)
        val name : TextView = itemView.findViewById(R.id.resultname)
        val description : TextView = itemView.findViewById(R.id.prof)
        val status : TextView = itemView.findViewById(R.id.resultstatus)
        val cost : TextView = itemView.findViewById(R.id.resultprice)
    }

}


class CommunityDisplayAdaptor(private val communityList: ArrayList<CommunityDisplayClass>, private val itemClickListener: OnItemClickListener) : RecyclerView.Adapter<CommunityDisplayAdaptor.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.communitydisplay,
            parent,false)
        return MyViewHolder(itemView)
    }

    interface OnItemClickListener {
        fun onCommunityClick(position: Int)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = communityList[position]
        holder.image.setImageResource(currentItem.titleImage)

        holder.itemView.setOnClickListener {
            itemClickListener.onCommunityClick(position)
        }
    }

    override fun getItemCount(): Int {
        return communityList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val image : ImageView = itemView.findViewById(R.id.communitypic)
    }

}




class MentorsListAdaptor(private val mentorsList: ArrayList<HomeMentorsClass>, private val itemClickListener: HomeFragment) : RecyclerView.Adapter<MentorsListAdaptor.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.homepagementors,
            parent,false)
        return MyViewHolder(itemView)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = mentorsList[position]
        holder.favorited.setImageResource(currentItem.image)
        holder.mentorName.text = currentItem.name
        holder.mentorDescription.text = currentItem.profession
        holder.mentorStatus.text = currentItem.status
        holder.mentorCost.text = currentItem.cost


        holder.itemView.setOnClickListener {
            val MentorID = currentItem.MentorId
            Log.d("MentorsListAdaptor", "Mentor ID clicked: $MentorID")
            val context = holder.itemView.context
            val intent = Intent(context, mentordescription::class.java)
            intent.putExtra("MentorID", MentorID) // Add MentorID as an extra to the intent
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return mentorsList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val mentorName : TextView = itemView.findViewById(R.id.mentorname)
        val mentorDescription : TextView = itemView.findViewById(R.id.mentorProfession)
        val mentorStatus : TextView = itemView.findViewById(R.id.mentorStatus)
        val favorited : ImageView = itemView.findViewById(R.id.favouriteIcon)
        val mentorCost : TextView = itemView.findViewById(R.id.mentorPrice)
    }

}

class BookingsListAdaptor(private val bookingsList: ArrayList<Bookings>, private val itemClickListener: ViewBookedSessions) : RecyclerView.Adapter<BookingsListAdaptor.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.bookings,
            parent,false)
        return MyViewHolder(itemView)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = bookingsList[position]

        val storage = FirebaseStorage.getInstance()

        val mentorId = currentItem.mentorId
        val reference = storage.reference.child("profilePics/$mentorId")
        reference.downloadUrl.addOnSuccessListener { uri ->
            // Load image using Picasso
            Picasso.get().load(uri).into(holder.dp)
        }.addOnFailureListener { exception ->
            // Handle any errors that may occur while fetching the image
            Log.e("Adapter", "Error fetching image: ${exception.message}")
        }

        holder.mentorName.text = currentItem.name
        holder.bookingDate.text = currentItem.date
        holder.time.text = currentItem.time

        holder.itemView.setOnClickListener {
            // Handle item click event
        }
    }

    override fun getItemCount(): Int {
        return bookingsList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val mentorName : TextView = itemView.findViewById(R.id.name)
        val bookingDate : TextView = itemView.findViewById(R.id.date)
        val time : TextView = itemView.findViewById(R.id.time)
        val dp : ImageView = itemView.findViewById(R.id.dppicture)
    }

}

class ChatsDisplayAdaptor(private val chatsList: ArrayList<ChatsDisplayClass>, private val itemClickListener: OnItemClickListener) : RecyclerView.Adapter<ChatsDisplayAdaptor.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.chatsdisplay,
            parent,false)
        return MyViewHolder(itemView)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, userId: String, userName: String)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = chatsList[position]

        holder.yourName.text = currentItem.name
        holder.yourMessages.text = currentItem.messages
        holder.yourMessages.setTextColor(ContextCompat.getColor(holder.itemView.context, currentItem.textColor))

        val userId = currentItem.userId
        val storage = FirebaseStorage.getInstance()
        val reference = storage.reference.child("profilePics/$userId")
        reference.downloadUrl.addOnSuccessListener { uri ->
            Picasso.get().load(uri).into(holder.image)
        }.addOnFailureListener { exception ->
            // Handle any errors that may occur while fetching the image
            Log.e("Adapter", "Error fetching image: ${exception.message}")
        }

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(position, currentItem.userId, currentItem.name)
        }
    }

    override fun getItemCount(): Int {
        return chatsList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val image : ImageView = itemView.findViewById(R.id.yourpic)
        val yourName : TextView = itemView.findViewById(R.id.personname)
        val yourMessages : TextView = itemView.findViewById(R.id.ifmessage)
    }

}


class ReviewsListAdaptor(private val reviewsList: ArrayList<Reviews>, private val itemClickListener: ProfileFragment) : RecyclerView.Adapter<ReviewsListAdaptor.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.myreviews,
            parent,false)
        return MyViewHolder(itemView)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = reviewsList[position]
        holder.mentorName.text = currentItem.name
        holder.reviewFeedback.text = currentItem.feedback

        when (currentItem.rating) {
            1 -> {
                holder.oneStar.setImageResource(R.drawable.baseline_star_rate_24)
                holder.twoStar.setImageResource(R.drawable.star_outline_yellow)
                holder.threeStar.setImageResource(R.drawable.star_outline_yellow)
                holder.fourStar.setImageResource(R.drawable.star_outline_yellow)
                holder.fiveStar.setImageResource(R.drawable.star_outline_yellow)
            }
            2 -> {
                holder.oneStar.setImageResource(R.drawable.baseline_star_rate_24)
                holder.twoStar.setImageResource(R.drawable.baseline_star_rate_24)
                holder.threeStar.setImageResource(R.drawable.star_outline_yellow)
                holder.fourStar.setImageResource(R.drawable.star_outline_yellow)
                holder.fiveStar.setImageResource(R.drawable.star_outline_yellow)
            }
            3 -> {
                holder.oneStar.setImageResource(R.drawable.baseline_star_rate_24)
                holder.twoStar.setImageResource(R.drawable.baseline_star_rate_24)
                holder.threeStar.setImageResource(R.drawable.baseline_star_rate_24)
                holder.fourStar.setImageResource(R.drawable.star_outline_yellow)
                holder.fiveStar.setImageResource(R.drawable.star_outline_yellow)
            }
            4 -> {
                holder.oneStar.setImageResource(R.drawable.baseline_star_rate_24)
                holder.twoStar.setImageResource(R.drawable.baseline_star_rate_24)
                holder.threeStar.setImageResource(R.drawable.baseline_star_rate_24)
                holder.fourStar.setImageResource(R.drawable.baseline_star_rate_24)
                holder.fiveStar.setImageResource(R.drawable.star_outline_yellow)
            }
            5 -> {
                holder.oneStar.setImageResource(R.drawable.baseline_star_rate_24)
                holder.twoStar.setImageResource(R.drawable.baseline_star_rate_24)
                holder.threeStar.setImageResource(R.drawable.baseline_star_rate_24)
                holder.fourStar.setImageResource(R.drawable.baseline_star_rate_24)
                holder.fiveStar.setImageResource(R.drawable.baseline_star_rate_24)
            }
        }

        holder.itemView.setOnClickListener {
            // Handle item click event
        }
    }

    override fun getItemCount(): Int {
        return reviewsList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val mentorName : TextView = itemView.findViewById(R.id.reviewName)
        val reviewFeedback : TextView = itemView.findViewById(R.id.reviewFeedback)
        val oneStar : ImageView = itemView.findViewById(R.id.onestarrating)
        val twoStar : ImageView = itemView.findViewById(R.id.twostarrating)
        val threeStar : ImageView = itemView.findViewById(R.id.threestarrating)
        val fourStar : ImageView = itemView.findViewById(R.id.fourstarrating)
        val fiveStar : ImageView = itemView.findViewById(R.id.fivestarrating)

    }

}

class DisplayMessagesAdaptor(private val context: Context, private val chatsList: ArrayList<Message>, private val itemClickListener: OpenChatFragment, private val chatID: String) : RecyclerView.Adapter<DisplayMessagesAdaptor.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            if (viewType == MESSAGE_TYPE_SENT) R.layout.sendmessage else R.layout.recievemessage,
            parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemViewType(position: Int): Int {
        val message = chatsList[position]
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        return if (message.writtenBy == currentUserId) {
            MESSAGE_TYPE_SENT
        } else {
            MESSAGE_TYPE_RECEIVED
        }
    }

    interface OnItemClickListener {

        fun onItemClick(messageContent: String)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val message = chatsList[position]
        Log.d("Adapter", "Message: $message")
        holder.message.text = message.message
        var oldmessageContent = holder.message.text.toString()
        var messageContent = message.message

        holder.message.setOnClickListener(View.OnClickListener {
            // Perform your actions here when the EditText is clicked
            Log.d("Adapter", "Hello A")
            Log.d("Adapter", "Before editing: $messageContent")
            holder.message.setTextIsSelectable(true)
            // Enable editing on click
            holder.message.editableText
            holder.message.inputType = InputType.TYPE_CLASS_TEXT
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(holder.message, InputMethodManager.SHOW_IMPLICIT)
        })

        // Set an OnEditorActionListener to handle input completion (e.g., pressing Enter)
        holder.message.setOnEditorActionListener { _, actionId, _ ->
            holder.message.setTextIsSelectable(false)
            messageContent = holder.message.text.toString()

            val dbRef = FirebaseDatabase.getInstance().getReference("UserChats")

            dbRef.child(chatID).child("messages").orderByChild("message").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        val messageId = snapshot.key
                        val writtenbywhom = snapshot.child("writtenBy").getValue(String::class.java)
                        val messageFound = snapshot.child("message").getValue(String::class.java)
                        // Access the message using the obtained messageId
                        Log.d("Adapter", "Message ID: $messageId")
                        Log.d("Adapter", "Written by: $writtenbywhom")
                        Log.d("Adapter", "Message: $messageFound")

                        if(messageFound == oldmessageContent){
                            dbRef.child(chatID).child("messages").child(messageId!!).child("message").setValue(messageContent)
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors
                }
            })
            Log.d("Adapter", "After editing: $messageContent")
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(holder.message.windowToken, 0)
                return@setOnEditorActionListener true
            }
            false
        }

        if(message.imageUrl == "") {
            Log.d("Adapter", "There was no Image in this message")
            Log.d("Adapter", "Message: ${message.message}")
            holder.time.text = message.timestamp
            holder.image.visibility = View.GONE
        }
        else{
            Log.d("Adapter", "There was Image in this message")
            holder.time.text = message.timestamp
            val storage = FirebaseStorage.getInstance()
            Log.d("Adapter", "Image URL: ${message.imageUrl}")
            val reference = storage.reference.child("UserChats/MessageImages/${message.imageUrl}")
            reference.downloadUrl.addOnSuccessListener { uri ->
                // Load image using Picasso
                Picasso.get().load(uri).into(holder.image)
            }.addOnFailureListener { exception ->
                // Handle any errors that may occur while fetching the image
                Log.e("Adapter", "Error fetching image: ${exception.message}")
            }
        }

        holder.message.setOnLongClickListener {
            Log.d("Adapter", "Long click event detected")
            // Show a confirmation dialog if necessary
            AlertDialog.Builder(context)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Yes") { _, _ ->
                    // Delete the item from the dataset
                    chatsList.removeAt(position)
                    // Notify adapter about item removal
                    val dbRef = FirebaseDatabase.getInstance().getReference("UserChats")

                    dbRef.child(chatID).child("messages").orderByChild("message").addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (snapshot in dataSnapshot.children) {
                                val messageFound = snapshot.child("message").getValue(String::class.java)

                                if(messageFound == oldmessageContent){
                                    snapshot.ref.removeValue()
                                }
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Handle errors
                        }
                    })

                    notifyItemRemoved(position)
                }
                .setNegativeButton("No", null)
                .show()

            true // indicate that the long click event is consumed
        }

        /*holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(message.message)
        }*/

    }

    override fun getItemCount(): Int {
        return chatsList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val message: TextView = itemView.findViewById(R.id.message)
        val time: TextView = itemView.findViewById(R.id.time)
        val image: ImageView = itemView.findViewById(R.id.image)
    }

    companion object {
        const val MESSAGE_TYPE_SENT = 1
        const val MESSAGE_TYPE_RECEIVED = 2
    }

}
