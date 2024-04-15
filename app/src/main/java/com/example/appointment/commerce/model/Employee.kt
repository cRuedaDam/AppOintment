package com.example.appointment.commerce.model

import java.io.Serializable

class Employee(
    var name: String = "",
    var lastName: String = "",
    var specialities: String? = null,
    var checkInTime: String? = null,
    var checkOutTime: String? = null
) : Serializable
