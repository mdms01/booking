package com.therapie.interview.booking.model

import java.time.LocalDate
import java.time.LocalTime

data class BookingRequest(val customerId: String, val clinicId: String, val serviceId: String, val date:LocalDate, val startTime: LocalTime)