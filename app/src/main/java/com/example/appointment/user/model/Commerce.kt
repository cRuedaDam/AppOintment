package com.example.appointment.user.model

data class Commerce(
    val commerce_name: String = "",
    val address: Map<String, String> = mapOf(),
    val commerce_opening_time: String = "",
    val commerce_closing_time: String = "",
    val commerce_email: String = "",
    val commerce_phone_number: String = "",
    val commerce_type: String = ""
)