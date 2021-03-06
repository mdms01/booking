package com.therapie.interview.clinics.service.remote

import com.therapie.interview.clinics.model.Clinic
import com.therapie.interview.clinics.model.TimeRange
import com.therapie.interview.clinics.service.ClinicService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class ClinicServiceRestAdapter (val restClient:ClinicRestClient): ClinicService {

    @Value("\${app.services.clinics.apiKey}")
    lateinit var apiKey:String



    override fun retrieveAllClinics(): List<Clinic> {
        TODO("Not yet implemented")
    }

    override fun retrieveClinicById(clinicId: String): Clinic {
        return restClient.retrieveById(clinicId,apiKey)
    }

    override fun retrieveTimeSlotsByClinicAndServiceAndDate(clinicId: String, serviceId: String, date: LocalDate): List<TimeRange> {
        TODO("Not yet implemented")
    }

    override fun retrieveTimeSlot(clinicId: String, serviceId: String, date: LocalDate, startTime: LocalTime, endTime: LocalTime): TimeRange {
        //return TimeRange(LocalTime.of(8, 0), LocalTime.of(12, 0))
        val a = restClient.retrieveTimeSlots(clinicId,serviceId,date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),apiKey)
        return a.get(0)
    }
}