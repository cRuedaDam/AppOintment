package com.example.reservapp.commerce.model

import com.example.reservapp.commerce.viewModel.FireBaseManager
import java.io.Serializable

class Employee(
    var name: String = "",
    var lastName: String = "",
    var specialities: String? = null,
    var checkInTime: String? = null,
    var checkOutTime: String? = null
) : Serializable
