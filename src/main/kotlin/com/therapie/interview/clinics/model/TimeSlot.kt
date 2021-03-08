package com.therapie.interview.clinics.model

import com.fasterxml.jackson.annotation.JsonFormat
import java.io.Serializable
import java.time.LocalTime
import javax.validation.constraints.AssertTrue

data class TimeSlot(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "'T'HH:mm:ss")
        val startTime: LocalTime,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "'T'HH:mm:ss")
        val endTime: LocalTime) : Serializable {

    @AssertTrue
    fun isValid() = startTime.isBefore(endTime)

    fun isShorterThan(seconds:Long) = seconds >= (endTime.toSecondOfDay() - startTime.toSecondOfDay())
}