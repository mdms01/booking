package com.therapie.interview.clinics.model

import java.time.LocalDate
import java.time.LocalTime

data class ClinicalServiceTimeSlot(val clinicId: String, val serviceId: String, val date: LocalDate, val startTime: LocalTime, val durationInMinutes: Long) {
    fun getEndTime(): LocalTime = startTime.plusMinutes(durationInMinutes)
}