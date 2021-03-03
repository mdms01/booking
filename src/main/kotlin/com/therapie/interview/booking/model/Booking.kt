package com.therapie.interview.booking.model

import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.*

@Entity(name = "Bookings")
@Table(name = "bookings")
@IdClass(BookingKey::class)
data class Booking(
        @Id
        @Column(name = "clinic_id")
        val clinicId: String,

        @Id
        @Column(name = "service_id")
        val serviceId: String,

        @Id
        @Column(name = "start_time")
        val startTime: LocalDateTime,

        @Column(name = "customer_id")
        val customerId: String)

data class BookingKey(
        val clinicId: String = "",
        val serviceId: String = "",
        val startTime: LocalDateTime = LocalDateTime.now()
) : Serializable