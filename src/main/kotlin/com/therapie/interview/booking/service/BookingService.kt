package com.therapie.interview.booking.service

import com.therapie.interview.booking.model.dto.Booking
import com.therapie.interview.booking.model.dto.BookingRequest
import java.time.LocalDate
import java.time.LocalTime

interface BookingService {
    fun book(bookingRequest: BookingRequest): Booking
    fun retrieveBookingsByClinic(clinicId: String): List<Booking>
    fun retrieveFreeTimeSlots(clinicId: String, serviceId: String, date: LocalDate): List<LocalTime>

}