package com.therapie.interview.clinics.service

import com.therapie.interview.clinics.model.Clinic
import com.therapie.interview.clinics.model.TimeAvailability
import com.therapie.interview.clinics.model.TimeSlot
import java.time.LocalDate
import java.time.LocalTime

interface ClinicService {
    fun retrieveClinicById(clinicId: String): Clinic
    fun retrieveTimeAvailabilitiesForTimeSlot(clinicId: String, serviceId: String, date: LocalDate, durationInMinutes: Long): List<TimeAvailability>
    fun checkTimeSlotIsValid(timeSlot: TimeSlot)
    fun retrieveBookableTimeSlots(clinicId: String, serviceId: String, date: LocalDate, durationInMinutes: Long): List<LocalTime>


}