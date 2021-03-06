package com.therapie.interview.booking.repository

import com.therapie.interview.booking.model.entity.BookingEntity
import com.therapie.interview.booking.model.entity.BookingKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalTime

@Repository
interface BookingRepository : JpaRepository<BookingEntity, BookingKey> {

    @Query("select b from Bookings b where b.clinicId = :clinicId ")
    fun findByClinic(clinicId: String): List<BookingEntity>

    @Modifying
    @Query(value = "insert into bookings (clinic_id, service_id, date, start_time, customer_id) values (:clinicId, :serviceId,:date ,:startTime, :customerId)",
            nativeQuery = true)
    fun insert(
            @Param("clinicId") clinicId: String,
            @Param("serviceId") serviceId: String,
            @Param("date") date: LocalDate,
            @Param("startTime") startTime: LocalTime,
            @Param("customerId") customerId: String)


}