package com.therapie.interview.clinics.service

import com.therapie.interview.clinics.exception.TimeSlotException
import com.therapie.interview.clinics.model.TimeSlot
import com.therapie.interview.clinics.model.ClinicalServiceTimeSlot
import mu.KLogging
import java.io.InvalidObjectException
import java.time.LocalDate
import java.time.LocalTime


class TimeAvailabilityLogic (val clinicId: String, val serviceId: String, val date: LocalDate, val serviceDurationInMinutes: Long){
    companion object : KLogging() {
        const val SIXTY_SECONDS = 60
    }



    fun assertTimeSlotFitsInOneOfTimeAvailabilities(clinicalServiceTimeSlot: ClinicalServiceTimeSlot, serviceShifts: List<TimeSlot>) {
        assertThereAreSlots(serviceShifts, clinicalServiceTimeSlot)
        val shift = getShiftWhereTimeSlotFits(serviceShifts, clinicalServiceTimeSlot)
        assertTimeSlotFitsInShift(clinicalServiceTimeSlot, shift)
    }

    fun generateBookableTimeSlots(timeSlots: List<TimeSlot>) =
            timeSlots.flatMap { generateBookableTimeSlotsForTimeRange(it) }

    private fun getShiftWhereTimeSlotFits(serviceShifts: List<TimeSlot>, clinicalServiceTimeSlot: ClinicalServiceTimeSlot): TimeSlot {
        val endTime = clinicalServiceTimeSlot.getEndTime()
        return serviceShifts.firstOrNull { it.startTime <= clinicalServiceTimeSlot.startTime && it.endTime >= endTime }
                ?: throw TimeSlotException("error.time_slot.no_match", "There is no available slots for date ${clinicalServiceTimeSlot.date} between ${clinicalServiceTimeSlot.startTime} and $endTime",
                        mapOf<String, Any>("date" to clinicalServiceTimeSlot.date, "startTime" to clinicalServiceTimeSlot.startTime, "endTime" to endTime))
    }

    private fun assertTimeSlotFitsInShift(clinicalServiceTimeSlot: ClinicalServiceTimeSlot, timeSlot: TimeSlot) {
        val serviceDurationInSeconds = clinicalServiceTimeSlot.durationInMinutes * SIXTY_SECONDS
        val timeDifferenceInSeconds = (clinicalServiceTimeSlot.startTime - timeSlot.startTime).toSecondOfDay()

        if (!isTimeSlotFit(timeDifferenceInSeconds, serviceDurationInSeconds)) {
            throw TimeSlotException("error.time_slot.wrong_start_time", "There is no option to book the service at ${clinicalServiceTimeSlot.startTime}")
        }
    }

    private fun isTimeSlotFit(timeDifferenceInSeconds: Int, serviceDurationInSeconds: Long) =
            (timeDifferenceInSeconds % serviceDurationInSeconds) == 0L

    private fun assertThereAreSlots(retrieveTimeSlots: List<TimeSlot>, clinicalServiceTimeSlot: ClinicalServiceTimeSlot) {
        if (retrieveTimeSlots.isEmpty()) {
            throw TimeSlotException("error.time_slot.no_available", "There is no available slots for date ${clinicalServiceTimeSlot.date}",
                    mapOf("date" to clinicalServiceTimeSlot.date))
        }
    }


    private fun generateBookableTimeSlotsForTimeRange(timeSlots: TimeSlot): List<LocalTime> {
        var startTime = timeSlots.startTime
        val bookableTimeSlots = mutableListOf<LocalTime>()
        while (startTime.plusMinutes(serviceDurationInMinutes) <= timeSlots.endTime) {
            bookableTimeSlots.add(startTime)
            startTime = startTime.plusMinutes(serviceDurationInMinutes)
        }
        return bookableTimeSlots
    }

    operator fun LocalTime.minus(startTime: LocalTime): LocalTime =
            LocalTime.ofNanoOfDay(this.toNanoOfDay() - startTime.toNanoOfDay())

    fun validateTimeAvailability(timeSlots: List<TimeSlot>): List<TimeSlot> {
        val serviceDurationInSeconds = serviceDurationInMinutes * SIXTY_SECONDS
        return assertNoTimeOverlap(timeSlots.onEach {
            if (!it.isValid()) {
                throw InvalidObjectException("Time range from for clinic $clinicId and service $serviceId at $date is invalid: $it")
            }
        }.filterNot {
            val isTimeAvailabilityShorter = it.isShorterThan(serviceDurationInSeconds)
            if (isTimeAvailabilityShorter) {
                logger.warn { "Time range in the availability for clinic $clinicId and service $serviceId at date $date is shorter than service duration $serviceDurationInSeconds in seconds: $it" }
            }
            isTimeAvailabilityShorter
        }
        )
    }
    private fun assertNoTimeOverlap(timeSlots: List<TimeSlot>): List<TimeSlot> {
        if (timeSlots.isNotEmpty()) {
            val sortedAvailabilities = timeSlots.sortedBy { it.startTime }
            var previousAvailability = sortedAvailabilities.first()
            var endTime = previousAvailability.startTime
            sortedAvailabilities.forEach {
                if (endTime.isAfter(it.startTime)) {
                    throw InvalidObjectException("The time availabilities for clinic $clinicId and service $serviceId at date $date is an overlap between $previousAvailability and $it")
                }
                endTime = it.endTime
                previousAvailability = it
            }
        }

        return timeSlots
    }


}