package com.therapie.interview.customers.service.remote

import com.therapie.interview.customers.model.Customer
import org.springframework.cache.annotation.Cacheable
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@FeignClient(value = "customerRestClient", url = "\${app.services.customer.url}")
interface CustomerRestClient {

    @Cacheable(value = ["customer"])
    @RequestMapping(method = [RequestMethod.GET], value = ["/customers/{customerId}"])
    fun retrieveById(@PathVariable("customerId") customerId: String, @RequestHeader("x-api-key") apikey: String): Customer

}