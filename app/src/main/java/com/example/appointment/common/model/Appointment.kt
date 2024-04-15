package com.example.appointment.common.model

import java.io.Serializable

class Appointment(
    var serviceId: String = "",
    var employeeId: String = "",
    var commerceId: String = "",
    var userId: String = "",
    var appointmentDate: String = "",
    var appointmentTime: String = "",
    var optionalRequest: String = ""
) : Serializable
