package com.abdullahsajjad.i212477

import java.io.Serializable

data class Bookings(
    var bookingId: String? = null,
    var userId: String? = null,
    var mentorId: String? = null,
    var name: String? = null,
    var date: String? = null,
    var time: String? = null,
    val profilePic: Int

) : Serializable
