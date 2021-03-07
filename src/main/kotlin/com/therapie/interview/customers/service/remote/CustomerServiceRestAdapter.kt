package com.therapie.interview.customers.service.remote

import com.therapie.interview.common.exceptions.NotFoundExeception
import com.therapie.interview.customers.model.Customer
import com.therapie.interview.customers.service.CustomerService
import feign.FeignException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class CustomerServiceRestAdapter(val customerRestClient: CustomerRestClient) : CustomerService {

    @Value("\${app.services.remote.apiKey}")
    lateinit var apiKey: String

    override fun retrieveCustomer(customerId: String): Customer {
        try {
            return customerRestClient.retrieveById(customerId, apiKey)
        } catch (exception: FeignException.NotFound) {
            throw NotFoundExeception("error.customer.not_found",
                    "The customer $customerId doesn't exists in our records",
                    mapOf("customerId" to customerId))
        }


    }

}