package com.therapie.interview.clinics.service.remote

import com.therapie.interview.clinics.model.Clinic
import com.therapie.interview.clinics.model.TimeRange
import com.therapie.interview.clinics.model.TimeSlot
import com.therapie.interview.clinics.service.ClinicService
import com.therapie.interview.common.exceptions.NotFoundExeception
import feign.FeignException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Component("clinicWrapper")
class ClinicServiceRestAdapter(val restClient: ClinicRestClient) : ClinicService {

    @Value("\${app.services.clinics.apiKey}")
    lateinit var apiKey: String

    override fun retrieveClinicById(clinicId: String): Clinic {
        try {
            return restClient.retrieveById(clinicId, apiKey)
        } catch (exception: FeignException.NotFound) {
            throw NotFoundExeception("error.clinic.not_found",
                    "The clinic $clinicId doesn't not exists in our records",
                    mapOf("clinicId" to clinicId))
        }
    }

    override fun retrieveTimeSlots(clinicId: String, serviceId: String, date: LocalDate): List<TimeRange> {
        try {
            return restClient.retrieveTimeSlots(clinicId, serviceId, formattedDate(date), apiKey)
        } catch (exception: FeignException.NotFound) {
            throw NotFoundExeception("error.time_slot.not_found",
                    "There is no services available for clinic $clinicId on service $serviceId for date $date",
                    mapOf("clinicId" to clinicId, "serviceId" to serviceId, "date" to date))
        }

    }

    override fun checkTimeSlotIsValid(timeSlot: TimeSlot) {
        throw NotImplementedError("Not to be implemented")
    }

    override fun retrieveBookableTimeSlots(clinicId: String, serviceId: String, date: LocalDate, durationInMinutes: Long): List<LocalTime> {
        throw NotImplementedError("Not to be implemented")
    }

    private fun formattedDate(date: LocalDate) = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

}