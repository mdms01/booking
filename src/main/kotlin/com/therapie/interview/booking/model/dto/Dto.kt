package com.therapie.interview.booking.model.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

data class BookingRequest(
        val customerId: String,
        val clinicId: String,
        val serviceId: String,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        val date: LocalDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "'T'HH:mm:ss")
        val startTime: LocalTime,
        var idempotentKey: String? = null)

data class Booking(
        val clinicId: String,
        val serviceId: String,
        val date: LocalDate,
        val startTime: LocalTime,
        val customerId: String) : Serializable


data class ErrorInformation(val errorCode: String,
                            val message: String,
                            val parameters: Map<String, Any> = emptyMap(),
                            val id: String = UUID.randomUUID().toString())