package com.therapie.interview.clinical_services.service

import com.therapie.interview.clinical_services.model.ClinicalService

interface ClinicalServiceTypeService {
    fun retrieveById(serviceId: String): ClinicalService
}