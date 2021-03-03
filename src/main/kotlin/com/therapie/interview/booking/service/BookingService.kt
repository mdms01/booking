package com.therapie.interview.booking.service

import com.therapie.interview.booking.model.Booking
import com.therapie.interview.booking.model.BookingRequest
import com.therapie.interview.clinics.model.TimeRange
import java.time.LocalDate

interface BookingService {
    fun book(bookingRequest: BookingRequest): Booking
    fun retrieveBookingsByClinic(clinicId: String): List<Booking>
    fun retrieveFreeTimeSlots(clinicId: String, serviceId: String, date: LocalDate): List<TimeRange>
}