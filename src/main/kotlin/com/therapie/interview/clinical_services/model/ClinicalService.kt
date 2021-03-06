package com.therapie.interview.clinical_services.model

import java.math.BigDecimal

data class ClinicalService(val serviceId: String, val name: String, val price: BigDecimal, val durationInMunites: Long)