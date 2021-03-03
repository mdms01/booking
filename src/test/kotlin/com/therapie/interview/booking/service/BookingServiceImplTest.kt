package com.therapie.interview.booking.service

import com.therapie.interview.booking.model.BookingRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import java.time.LocalTime

@SpringBootTest
internal class BookingServiceImplTest {

    @Autowired
    lateinit var bookingService: BookingService

    @Test
    fun `book - clinic doesnt exists`() {
    }

    @Test
    fun `book - clinical service doesnt exists`() {
    }

    @Test
    fun `book - there isnt time slots available `() {
    }

    @Test
    fun `book - appointment conflict`() {
        val bookingRequest = BookingRequest("c123", "345", "s345", LocalDate.now(), LocalTime.of(9, 0))
        val bookingRequest2 = BookingRequest("c567", "345", "s345", LocalDate.now(), LocalTime.of(9, 0))
        val b = bookingService.book(bookingRequest)
        val b1 = bookingService.book(bookingRequest2)
        println(bookingService.retrieveBookingsByClinic("345"))
        println(b1)
    }

    @Test
    fun `book - successfully completed`() {
        val bookingRequest = BookingRequest("c123", "345", "s345", LocalDate.now(), LocalTime.of(9, 0))
        val b = bookingService.book(bookingRequest)
        println(b)
    }

    @Test
    fun `retrieveBookingsByClinic - there is no clinic`() {
    }

    @Test
    fun `retrieveBookingsByClinic - there is no appointments for the clinic`() {
    }

    @Test
    fun `retrieveBookingsByClinic - there are appointments for the clinic`() {
    }

    @Test
    fun retrieveFreeTimeSlots() {
    }
}