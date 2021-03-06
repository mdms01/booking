package com.therapie.interview.booking.controller

import com.therapie.interview.booking.model.dto.Booking
import com.therapie.interview.booking.model.dto.BookingRequest
import com.therapie.interview.booking.service.BookingService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController("bookingController")
class BookingController(val bookingService: BookingService) {

    @PostMapping(value = ["/bookings"], produces = ["application/json"])
    fun book(@RequestBody bookingRequest: BookingRequest,
             @RequestHeader("x-idempotent-key", required = false) idempotentKey:String?
    ): ResponseEntity<Booking>{
        bookingRequest.idempotentKey = idempotentKey
        return ResponseEntity.ok(bookingService.book(bookingRequest))
    }

    @GetMapping(value = ["/clinic/{clinicId}/bookings"], produces = ["application/json"])
    fun retrieveBookingsForAClinic(@PathVariable("clinicId") clinicId:String): ResponseEntity<List<Booking>>{
        return ResponseEntity.ok(bookingService.retrieveBookingsByClinic(clinicId))
    }

}