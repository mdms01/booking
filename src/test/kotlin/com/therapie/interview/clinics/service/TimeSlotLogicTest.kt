package com.therapie.interview.clinics.service

import com.therapie.interview.clinics.model.TimeSlot
import com.therapie.interview.clinics.model.ClinicalServiceTimeSlot
import org.junit.jupiter.api.Test

import java.time.LocalDate
import java.time.LocalTime

internal class TimeSlotLogicTest {

    @Test
    fun `assertTimeSlot - time slot fits in the time availability`() {
        val serviceDurationInMinutes: Long = 10
        val timeSlotLogic = createTimeAvailabilityLogic(serviceDurationInMinutes)
        val durationInMinutes: Long = 10
        val startTime = LocalTime.now()
        val timeSlot = createTimeSlot(startTime, durationInMinutes)
        val timeRanges = listOf(TimeSlot(LocalTime.now(), LocalTime.now()))
       // timeSlotLogic.assertTimeSlotFitsInOneOfTimeAvailabilities(timeSlot, timeRanges)
    }

    private fun createTimeAvailabilityLogic(serviceDurationInMinutes: Long) =
            TimeAvailabilityLogic("10", "11", LocalDate.now(), serviceDurationInMinutes)

    @Test
    fun `assertTimeSlot - time slot fits in second time availability`() {
    }

    @Test
    fun `assertTimeSlot - time slot fits in the last time availability`() {
    }

    @Test
    fun `assertTimeSlot - service time availability is empty`() {
    }

    @Test
    fun `assertTimeSlot - time slot starts before time availability`() {
    }

    @Test
    fun `assertTimeSlot - time slot starts after time availability`() {
    }

    @Test
    fun `assertTimeSlot - time slot starts after in time availability but ends later`() {
    }

    @Test
    fun `assertTimeSlot - time slot sits between 2 time availabilities `() {
    }

    @Test
    fun `assertTimeSlot - time slot spans to 2 time availabilities `() {
    }

    @Test
    fun `generateBookableTimeSlots - there is no time slots`() {
        //timeSlotLogic.generateBookableTimeSlots(timeSlots)
    }

    @Test
    fun `generateBookableTimeSlots - the duration (20 min) doesnt matches time availability(1h 15min) `() {
        val timeSlotLogic = createTimeAvailabilityLogic(10)
        val timeAvailability = listOf(TimeSlot(LocalTime.now(), LocalTime.now()))
        timeSlotLogic.generateBookableTimeSlots(timeAvailability)
    }

    @Test
    fun `generateBookableTimeSlots - the duration 20 min matches exactly time availability 1h`() {
        val timeSlotLogic = createTimeAvailabilityLogic(10)
        val timeAvailability = listOf(TimeSlot(LocalTime.now(), LocalTime.now()))
        timeSlotLogic.generateBookableTimeSlots(timeAvailability)
    }

    @Test
    fun `assertTimeSlotFitsInOneOfTimeAvailabilities - no time availability`() {
        val timeSlotLogic = createTimeAvailabilityLogic(10)
        val timeSlot = createTimeSlot(LocalTime.now(),10)
       // timeSlotLogic.assertTimeSlotFitsInOneOfTimeAvailabilities(timeSlot,emptyList())
    }

    @Test
    fun `assertTimeSlotFitsInOneOfTimeAvailabilities - time slot at beginning`() {
    }

    @Test
    fun `assertTimeSlotFitsInOneOfTimeAvailabilities - second slot`() {
    }

    @Test
    fun `assertTimeSlotFitsInOneOfTimeAvailabilities - last slot`() {
    }

    @Test
    fun `assertTimeSlotFitsInOneOfTimeAvailabilities - matches second availability`() {
    }

    @Test
    fun `assertTimeSlotFitsInOneOfTimeAvailabilities - matches last availability`() {
    }

    @Test
    fun `assertTimeSlotFitsInOneOfTimeAvailabilities - time slot exceeds end of time availability`() {
    }

    @Test
    fun `assertTimeSlotFitsInOneOfTimeAvailabilities - `() {
    }




    private fun createTimeSlot(startTime: LocalTime, durationInMinutes: Long) =
            ClinicalServiceTimeSlot("10", "11", LocalDate.now(), startTime, durationInMinutes)

}