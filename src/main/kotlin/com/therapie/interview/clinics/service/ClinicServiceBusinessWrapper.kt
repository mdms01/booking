package com.therapie.interview.clinics.service


import com.therapie.interview.clinics.exception.TimeSlotException
import com.therapie.interview.clinics.model.TimeRange
import com.therapie.interview.clinics.model.TimeSlot
import mu.KLogging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalTime

@Service
@Primary
class ClinicServiceBusinessWrapper(@Qualifier("clinicWrapper") val wrapped: ClinicService) : ClinicService {
    companion object : KLogging() {
        const val SIXTY_SECONDS = 60
    }

    override fun retrieveClinicById(clinicId: String) =
            wrapped.retrieveClinicById(clinicId)


    override fun retrieveTimeSlots(clinicId: String, serviceId: String, date: LocalDate) =
            wrapped.retrieveTimeSlots(clinicId, serviceId, date)


    override fun checkTimeSlotIsValid(timeSlot: TimeSlot) {

        val endTime = timeSlot.getEndTime()
        val serviceShifts = wrapped.retrieveTimeSlots(timeSlot.clinicId, timeSlot.serviceId, timeSlot.date)
        assertThereAreSlots(serviceShifts, timeSlot)

        val shift = getShiftWhereTimeSlotFits(serviceShifts, timeSlot, endTime)

        assertTimeSlotFitsInShift(timeSlot, shift)

    }

    override fun retrieveBookableTimeSlots(clinicId: String, serviceId: String, date: LocalDate, durationInMinutes: Long): List<LocalTime> {
        val timeSlots = retrieveTimeSlots(clinicId, serviceId, date)
        return generateBookableTimeSlots(timeSlots, durationInMinutes)
    }

    private fun getShiftWhereTimeSlotFits(serviceShifts: List<TimeRange>, timeSlot: TimeSlot, endTime: LocalTime): TimeRange {
        return serviceShifts.firstOrNull { it.startTime <= timeSlot.startTime && it.endTime >= endTime }
                ?: throw TimeSlotException("error.time_slot.no_match", "There is no available slots for date ${timeSlot.date} between ${timeSlot.startTime} and $endTime",
                        mapOf<String, Any>("date" to timeSlot.date, "startTime" to timeSlot.startTime, "endTime" to endTime))
    }

    private fun assertTimeSlotFitsInShift(timeSlot: TimeSlot, timeRange: TimeRange) {
        val serviceDurationInSeconds = timeSlot.durationInMinutes * SIXTY_SECONDS
        val timeDifferenceInSeconds = (timeSlot.startTime - timeRange.startTime).toSecondOfDay()

        if (!isTimeSlotFit(timeDifferenceInSeconds, serviceDurationInSeconds)) {
            throw TimeSlotException("error.time_slot.wrong_start_time", "There is no option to book the service at ${timeSlot.startTime}")
        }
    }

    private fun isTimeSlotFit(timeDifferenceInSeconds: Int, serviceDurationInSeconds: Long) =
            (timeDifferenceInSeconds % serviceDurationInSeconds) == 0L

    private fun assertThereAreSlots(retrieveTimeSlots: List<TimeRange>, timeSlot: TimeSlot) {
        if (retrieveTimeSlots.isEmpty()) {
            throw TimeSlotException("error.time_slot.no_available", "There is no available slots for date ${timeSlot.date}",
                    mapOf("date" to timeSlot.date))
        }
    }

    private fun generateBookableTimeSlots(timeSlots: List<TimeRange>, durationInMunites: Long): List<LocalTime> {
        return timeSlots.flatMap { generateBookableTimeSlots(durationInMunites, it) }

    }

    private fun generateBookableTimeSlots(durationInMinutes: Long, timeSlots: TimeRange): List<LocalTime> {
        var startTime = timeSlots.startTime
        val bookableTimeSlots = mutableListOf<LocalTime>()
        while (startTime.plusMinutes(durationInMinutes) <= timeSlots.endTime) {
            bookableTimeSlots.add(startTime)
            startTime = startTime.plusMinutes(durationInMinutes)
        }
        return bookableTimeSlots
    }


    operator fun LocalTime.minus(startTime: LocalTime): LocalTime =
            LocalTime.ofNanoOfDay(this.toNanoOfDay() - startTime.toNanoOfDay())

}