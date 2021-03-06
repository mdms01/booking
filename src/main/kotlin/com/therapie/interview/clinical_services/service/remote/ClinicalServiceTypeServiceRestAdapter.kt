package com.therapie.interview.services.service.remote

import com.therapie.interview.services.model.ClinicalService
import com.therapie.interview.services.service.ClinicalServiceTypeService
import java.math.BigDecimal

@org.springframework.stereotype.Service
class ClinicalServiceTypeServiceRestAdapter : ClinicalServiceTypeService {
    override fun retrieveAll(): List<ClinicalService> {
        TODO("Not yet implemented")
    }

    override fun retrieveById(serviceId: String): ClinicalService {
        return ClinicalService(serviceId, "mock", BigDecimal.TEN, 30)
    }
}