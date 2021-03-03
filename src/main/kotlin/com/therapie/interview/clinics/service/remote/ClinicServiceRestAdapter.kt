package com.therapie.interview.clinics.service.remote

import com.therapie.interview.clinics.model.Clinic
import com.therapie.interview.clinics.model.TimeRange
import com.therapie.interview.clinics.service.ClinicService
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalTime

@Service
class ClinicServiceRestAdapter:ClinicService {
    override fun retrieveAllClinics(): List<Clinic> {
        TODO("Not yet implemented")
    }

    override fun retrieveClinicById(clinicId: String): Clinic {
        return Clinic(clinicId)
    }

    override fun retrieveTimeSlotsByClinicAndServiceAndDate(clinicId: String, serviceId: String, date: LocalDate): List<TimeRange> {
        TODO("Not yet implemented")
    }

    override fun timeSlotExists(clinicId: String, serviceId: String, date: LocalDate, startTime: LocalTime, endTime: LocalTime): TimeRange {
        return TimeRange(LocalTime.of(8,0),LocalTime.of(12,0))
    }
}