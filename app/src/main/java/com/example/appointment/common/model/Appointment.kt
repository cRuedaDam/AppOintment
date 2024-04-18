package com.example.appointment.common.model

import java.io.Serializable

class Appointment(
    var appointmentId: String,
    var serviceId: String,
    var commerceId: String,
    var userId: String,
    var employeeId: String,
    var commerceName:String,
    var appointmentDate: String,
    var appointmentTime: String,
    var optionalRequest: String
) : Serializable
