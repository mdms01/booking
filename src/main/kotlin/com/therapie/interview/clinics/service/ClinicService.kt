package com.therapie.interview.clinics.service

import com.therapie.interview.clinics.model.Clinic
import com.therapie.interview.clinics.model.TimeSlot
import com.therapie.interview.clinics.model.ClinicalServiceTimeSlot
import java.time.LocalDate
import java.time.LocalTime

interface ClinicService {
    fun retrieveClinicById(clinicId: String): Clinic
    fun retrieveTimeAvailabilitiesForTimeSlot(clinicId: String, serviceId: String, date: LocalDate, durationInMinutes: Long): List<TimeSlot>
    fun checkTimeSlotIsValid(clinicalServiceTimeSlot: ClinicalServiceTimeSlot)
    fun retrieveBookableTimeSlots(clinicId: String, serviceId: String, date: LocalDate, durationInMinutes: Long): List<LocalTime>


}