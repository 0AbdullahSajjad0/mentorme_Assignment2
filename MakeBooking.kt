package com.abdullahsajjad.i212477

import java.io.Serializable

data class MakeBooking(
    var userId: String? = null,
    var mentorId: String? = null,
    var mentorName: String? = null,
    var email: String? = null,
    var pNum: String? = null,
    var country: String? = null,
    var city: String? = null,
    val profilePic: String? = null

) : Serializable
