package com.example.appointment.commerce.model

import java.io.Serializable

data class Speciality(
    var name: String? = "",
    var timeRequired: String? = "",
    var price: String? = "0.00"
) : Serializable
