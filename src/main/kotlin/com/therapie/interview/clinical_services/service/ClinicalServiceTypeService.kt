package com.therapie.interview.services.service

import com.therapie.interview.services.model.ClinicalService

interface ClinicalServiceTypeService {
    fun retrieveAll(): List<ClinicalService>
    fun retrieveById(serviceId: String): ClinicalService
}