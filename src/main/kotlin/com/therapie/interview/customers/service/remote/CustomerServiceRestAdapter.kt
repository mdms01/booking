package com.therapie.interview.customer.service.remote

import com.therapie.interview.customer.model.Customer
import com.therapie.interview.customer.service.CustomerService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class CustomerServiceRestAdapter (val customerRestClient: CustomerRestClient): CustomerService {
    @Value("\${app.services.clinics.apiKey}")
    lateinit var apiKey:String

    override fun retrieveAllCustomers(): List<Customer> {
        TODO("Not yet implemented")
    }

    override fun retrieveCustomer(customerId: String): Customer {
        return customerRestClient.retrieveById(customerId,apiKey)
    }
}