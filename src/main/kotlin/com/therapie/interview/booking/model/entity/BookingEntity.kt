package com.therapie.interview.booking.model.entity

import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.persistence.*

@Entity(name = "Bookings")
@Table(name = "bookings")
@IdClass(BookingKey::class)
data class BookingEntity(
        @Id
        @Column(name = "clinic_id")
        val clinicId: String,

        @Id
        @Column(name = "service_id")
        val serviceId: String,

        @Id
        @Column(name = "date", )
        val date: LocalDate,

        @Id
        @Column(name = "start_time")
        val startTime: LocalTime,

        @Column(name = "customer_id")
        val customerId: String):Serializable

data class BookingKey(
        val clinicId: String = "",
        val serviceId: String = "",
        val date: LocalDate = LocalDate.now(),
        val startTime: LocalTime = LocalTime.now()
) : Serializable