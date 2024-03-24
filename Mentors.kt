package com.abdullahsajjad.i212477

import java.io.Serializable

data class Mentors(
    var mentorId: String? = null,
    var name: String? = null,
    var description: String? = null,
    var status: String? = null,
    val profilePic: String? = null

) : Serializable
