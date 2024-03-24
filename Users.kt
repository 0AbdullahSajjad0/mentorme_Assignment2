package com.abdullahsajjad.i212477

import java.io.Serializable

data class Users(
    var userId: String? = null,
    var name: String? = null,
    var email: String? = null,
    var pNum: String? = null,
    var country: String? = null,
    var city: String? = null,
    val profilePic: String? = null

) : Serializable
