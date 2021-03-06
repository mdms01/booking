package com.therapie.interview.clinics.model

import com.fasterxml.jackson.annotation.JsonFormat
import java.io.Serializable
import java.time.LocalTime

data class TimeRange(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "'T'HH:mm:ss")
        val startTime: LocalTime,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "'T'HH:mm:ss")
        val endTime: LocalTime):Serializable