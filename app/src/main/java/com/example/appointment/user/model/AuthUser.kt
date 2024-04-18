package com.example.appointment.user.model

import java.io.Serializable

class AuthUser(
    val userId: String,
    var userName:String,
    var userLastName: String,
    var userEmail:String,
    var userPhoneNumber: String,
): Serializable