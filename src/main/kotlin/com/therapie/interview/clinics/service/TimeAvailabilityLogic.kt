package com.therapie.interview.clinics.service

import com.therapie.interview.clinics.exception.TimeSlotException
import com.therapie.interview.clinics.model.TimeAvailability
import com.therapie.interview.clinics.model.TimeSlot
import mu.KLogging
import java.io.InvalidObjectException
import java.time.LocalDate
import java.time.LocalTime


class TimeAvailabilityLogic (val clinicId: String, val serviceId: String, val date: LocalDate, val serviceDurationInMinutes: Long){
    companion object : KLogging() {
        const val SIXTY_SECONDS = 60
    }



    fun assertTimeSlotFitsInOneOfTimeAvailabilities(timeSlot: TimeSlot, serviceShifts: List<TimeAvailability>) {
        assertThereAreSlots(serviceShifts, timeSlot)
        val shift = getShiftWhereTimeSlotFits(serviceShifts, timeSlot)
        assertTimeSlotFitsInShift(timeSlot, shift)
    }

    fun generateBookableTimeSlots(timeSlots: List<TimeAvailability>) =
            timeSlots.flatMap { generateBookableTimeSlotsForTimeRange(it) }

    private fun getShiftWhereTimeSlotFits(serviceShifts: List<TimeAvailability>, timeSlot: TimeSlot): TimeAvailability {
        val endTime = timeSlot.getEndTime()
        return serviceShifts.firstOrNull { it.startTime <= timeSlot.startTime && it.endTime >= endTime }
                ?: throw TimeSlotException("error.time_slot.no_match", "There is no available slots for date ${timeSlot.date} between ${timeSlot.startTime} and $endTime",
                        mapOf<String, Any>("date" to timeSlot.date, "startTime" to timeSlot.startTime, "endTime" to endTime))
    }

    private fun assertTimeSlotFitsInShift(timeSlot: TimeSlot, timeAvailability: TimeAvailability) {
        val serviceDurationInSeconds = timeSlot.durationInMinutes * SIXTY_SECONDS
        val timeDifferenceInSeconds = (timeSlot.startTime - timeAvailability.startTime).toSecondOfDay()

        if (!isTimeSlotFit(timeDifferenceInSeconds, serviceDurationInSeconds)) {
            throw TimeSlotException("error.time_slot.wrong_start_time", "There is no option to book the service at ${timeSlot.startTime}")
        }
    }

    private fun isTimeSlotFit(timeDifferenceInSeconds: Int, serviceDurationInSeconds: Long) =
            (timeDifferenceInSeconds % serviceDurationInSeconds) == 0L

    private fun assertThereAreSlots(retrieveTimeSlots: List<TimeAvailability>, timeSlot: TimeSlot) {
        if (retrieveTimeSlots.isEmpty()) {
            throw TimeSlotException("error.time_slot.no_available", "There is no available slots for date ${timeSlot.date}",
                    mapOf("date" to timeSlot.date))
        }
    }


    private fun generateBookableTimeSlotsForTimeRange(timeSlots: TimeAvailability): List<LocalTime> {
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

    fun validateTimeAvailability(timeAvailabilities: List<TimeAvailability>): List<TimeAvailability> {
        val serviceDurationInSeconds = serviceDurationInMinutes * SIXTY_SECONDS
        return assertNoTimeOverlap(timeAvailabilities.onEach {
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
    private fun assertNoTimeOverlap(timeAvailabilities: List<TimeAvailability>): List<TimeAvailability> {
        if (timeAvailabilities.isNotEmpty()) {
            val sortedAvailabilities = timeAvailabilities.sortedBy { it.startTime }
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

        return timeAvailabilities
    }


}