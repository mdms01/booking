package com.therapie.interview.clinical_services.service.remote


import com.therapie.interview.clinical_services.model.ClinicalService
import com.therapie.interview.clinical_services.service.ClinicalServiceTypeService
import com.therapie.interview.common.exceptions.NotFoundExeception
import feign.FeignException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ClinicalServiceTypeServiceRestAdapter(val clinicalServicesRestClient: ClinicalServicesRestClient) : ClinicalServiceTypeService {

    @Value("\${app.services.remote.apiKey}")
    lateinit var apiKey: String

    override fun retrieveById(serviceId: String): ClinicalService {
        try {
            return clinicalServicesRestClient.retrieveById(serviceId, apiKey)
        } catch (exception: FeignException.NotFound) {
            throw NotFoundExeception("error.clinical_service.not_found",
                    "Clinical service $serviceId not found",
                    mapOf("objectId" to serviceId))
        }
    }

}