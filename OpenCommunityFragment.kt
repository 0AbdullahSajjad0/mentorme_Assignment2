package com.abdullahsajjad.i212477

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast

class OpenCommunityFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_open_community, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val startAudioCall: ImageView = view.findViewById(R.id.callcommunity)
        val startVideoCall: ImageView = view.findViewById(R.id.videocallcommunity)
        val uploadPhoto: ImageView = view.findViewById(R.id.opencommunitycamera)
        val myButton: ImageView = view.findViewById(R.id.backtoChatsFromCommunity)

        myButton.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.navfragment, ChatsFragment())
            transaction.addToBackStack(null)
            transaction.commit()
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
}
