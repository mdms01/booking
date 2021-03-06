package com.therapie.interview.clinical_services.service.remote

import com.therapie.interview.clinical_services.model.ClinicalService
import org.springframework.cache.annotation.Cacheable
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@FeignClient(value = "clinicalServicesRestClient", url = "\${app.services.clinics.url}", )
interface ClinicalServicesRestClient {

    @Cacheable(value =["clinical_services"])
    @RequestMapping(method = [RequestMethod.GET], value = ["/services/{serviceId}"])
    fun retrieveById(@PathVariable("serviceId") serviceId: String, @RequestHeader("x-api-key") apikey: String): ClinicalService
}