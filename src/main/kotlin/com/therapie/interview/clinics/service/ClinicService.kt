package com.therapie.interview.clinics.service

import com.therapie.interview.clinics.model.Clinic
import com.therapie.interview.clinics.model.TimeRange
import java.time.LocalDate
import java.time.LocalTime

interface ClinicService {
    fun retrieveAllClinics(): List<Clinic>
    fun retrieveClinicById(clinicId: String): Clinic
    fun retrieveTimeSlotsByClinicAndServiceAndDate(clinicId: String, serviceId: String, date: LocalDate): List<TimeRange>
    fun timeSlotExists(clinicId: String, serviceId: String, date: LocalDate, startTime: LocalTime, endTime: LocalTime): TimeRange
}