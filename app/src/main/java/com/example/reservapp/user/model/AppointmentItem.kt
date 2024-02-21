package com.example.reservapp.user.model

data class AppointmentItem (
    var appointmentId: String,
    var serviceId: String,
    var appointmentDate: String,
    var appointmentTime: String,
    var employeeId: String,
    var commerceId: String,
    var userId: String,
    var commerceName: String,
    var optionalRequest: String
)