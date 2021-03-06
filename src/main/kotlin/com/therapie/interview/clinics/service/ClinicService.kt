package com.therapie.interview.clinics.service

import com.therapie.interview.clinics.model.Clinic
import com.therapie.interview.clinics.model.TimeRange
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

interface ClinicService {
    fun retrieveClinicById(clinicId: String): Clinic
    fun retrieveTimeSlots(clinicId: String, serviceId: String, date: LocalDate): List<TimeRange>
    fun retrieveTimeSlotForTimeInterval(clinicId: String, serviceId: String, date: LocalDate, startTime: LocalTime, endTime: LocalTime): TimeRange
}