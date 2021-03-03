package com.therapie.interview.booking.repository

import com.therapie.interview.booking.model.Booking
import com.therapie.interview.booking.model.BookingKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface BookingRepository : JpaRepository<Booking, BookingKey> {

    @Query("select b from Bookings b where b.clinicId = :clinicId ")
    fun findByClinic(clinicId: String): List<Booking>

    @Modifying
    @Query(value = "insert into bookings (clinic_id, service_id, start_time, customer_id) values (:clientId, :serviceId, :startTime, :customerId)",
            nativeQuery = true)
    fun insert(
            @Param("clientId") clientId: String,
            @Param("serviceId") serviceId: String,
            @Param("startTime") startTime: LocalDateTime,
            @Param("customerId") customerId: String)


}