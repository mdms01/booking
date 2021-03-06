package com.therapie.interview.clinics.service.remote

import com.therapie.interview.clinics.model.Clinic
import com.therapie.interview.clinics.model.TimeRange
import org.springframework.cache.annotation.Cacheable
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import java.time.LocalDate

@FeignClient(value = "clinicRestClient", url = "\${app.services.clinics.url}", )
interface ClinicRestClient {

    @Cacheable(value =["clinics"])
    @RequestMapping(method = [RequestMethod.GET], value = ["/clinics/{clinicId}"])
    fun retrieveById(@PathVariable("clinicId") clinicId: String, @RequestHeader("x-api-key") apikey: String): Clinic

    @Cacheable(value =["clinics_time_slots"])
    @RequestMapping(method = [RequestMethod.GET], value = ["/clinics/{clinicId}/services/{serviceId}/timeslots/{date}"])
    fun retrieveTimeSlots(@PathVariable("clinicId") clinicId: String,
                          @PathVariable("serviceId") serviceId: String,
                          @PathVariable("date") date: String,
                          @RequestHeader("x-api-key") apikey: String): List<TimeRange>

}