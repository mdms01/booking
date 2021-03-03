package com.therapie.interview.services.model

import java.math.BigDecimal
import java.time.LocalTime

data class ClinicalService(val serviceId: String, val name: String, val price: BigDecimal, val durationInMunites: Int) {

    fun calculateFinishTime(startTime: LocalTime): LocalTime = startTime.plusMinutes(durationInMunites.toLong())

}