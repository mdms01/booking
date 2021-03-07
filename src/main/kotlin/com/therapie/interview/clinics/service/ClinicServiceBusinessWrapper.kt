package com.therapie.interview.clinics.service


import com.therapie.interview.clinics.model.TimeAvailability
import com.therapie.interview.clinics.model.TimeSlot
import mu.KLogging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalTime

@Service
@Primary
class ClinicServiceBusinessWrapper(@Qualifier("clinicServiceRest") val wrapped: ClinicService) : ClinicService {
    companion object : KLogging()

    override fun retrieveClinicById(clinicId: String) =
            wrapped.retrieveClinicById(clinicId)


    override fun retrieveTimeAvailabilitiesForTimeSlot(clinicId: String, serviceId: String, date: LocalDate, serviceDurationInMinutes: Long): List<TimeAvailability> {
        val timeAvailability = wrapped.retrieveTimeAvailabilitiesForTimeSlot(clinicId, serviceId, date, serviceDurationInMinutes)
        val timeAvailabilityLogic = TimeAvailabilityLogic(clinicId, serviceId, date, serviceDurationInMinutes)
        return timeAvailabilityLogic.validateTimeAvailability(timeAvailability)
    }

    override fun checkTimeSlotIsValid(timeSlot: TimeSlot) {
        val clinicId = timeSlot.clinicId
        val serviceId = timeSlot.serviceId
        val date = timeSlot.date
        val serviceDurationInMinutes = timeSlot.durationInMinutes

        val timeAvailabilities = retrieveTimeAvailabilitiesForTimeSlot(clinicId, serviceId, date, serviceDurationInMinutes)

        val timeAvailabilityLogic = TimeAvailabilityLogic(clinicId, serviceId, date, serviceDurationInMinutes)

        timeAvailabilityLogic.assertTimeSlotFitsInOneOfTimeAvailabilities(timeSlot, timeAvailabilities)
    }

    override fun retrieveBookableTimeSlots(clinicId: String, serviceId: String, date: LocalDate, serviceDurationInMinutes: Long): List<LocalTime> {
        val timeAvailabilities = retrieveTimeAvailabilitiesForTimeSlot(clinicId, serviceId, date, serviceDurationInMinutes)
        val timeAvailabilityLogic = TimeAvailabilityLogic(clinicId, serviceId, date, serviceDurationInMinutes)
        return timeAvailabilityLogic.generateBookableTimeSlots(timeAvailabilities)
    }


}