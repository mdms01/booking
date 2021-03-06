package com.therapie.interview.booking.controller

import com.therapie.interview.booking.model.dto.Booking
import com.therapie.interview.booking.model.dto.BookingRequest
import com.therapie.interview.booking.service.BookingService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.LocalTime


@RestController("bookingController")
class BookingController(val bookingService: BookingService) {

    @PostMapping(value = ["/bookings"], produces = ["application/json"])
    fun book(@RequestBody bookingRequest: BookingRequest,
             @RequestHeader("x-idempotent-key", required = false) idempotentKey: String?
    ): ResponseEntity<Booking> {
        val requestWithIdempotentKey = bookingRequest.apply { this.idempotentKey = idempotentKey }
        return ResponseEntity.ok(bookingService.book(requestWithIdempotentKey))
    }

    @GetMapping(value = ["/clinic/{clinicId}/bookings"], produces = ["application/json"])
    fun retrieveBookingsForAClinic(@PathVariable("clinicId") clinicId: String): ResponseEntity<List<Booking>> {
        return ResponseEntity.ok(bookingService.retrieveBookingsByClinic(clinicId))
    }

    @GetMapping(value = ["/clinic/{clinicId}/services/{serviceId}/bookings/available/{date}"],
            produces = ["application/json"])
    fun retrieveFreeBookableTimes(@PathVariable("clinicId") clinicId: String,
                                  @PathVariable("serviceId") serviceId: String,
                                  @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate): ResponseEntity<List<LocalTime>> {
        return ResponseEntity.ok(bookingService.retrieveFreeTimeSlots(clinicId, serviceId, date))
    }

}