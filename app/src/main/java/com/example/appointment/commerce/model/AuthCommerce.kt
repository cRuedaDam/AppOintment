package com.example.appointment.commerce.model

import java.io.Serializable

data class AuthCommerce(
    val commerceId: String,
    val commerceName: String,
    val commerceEmail: String,
    val commercePhoneNumber: String,
    val commerceType: String,
    val commerceStreetType: String,
    val commerceStreetName: String,
    val commerceStreetNumber: String,
    val commerceCity: String,
    val commerceState: String,
    val commercePostalCode: String
) : Serializable