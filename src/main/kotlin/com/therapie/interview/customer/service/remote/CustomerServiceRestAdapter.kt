package com.therapie.interview.customer.service.remote

import com.therapie.interview.customer.model.Customer
import com.therapie.interview.customer.service.CustomerService
import org.springframework.stereotype.Service

@Service
class CustomerServiceRestAdapter:CustomerService {
    override fun retrieveAllCustomers(): List<Customer> {
        TODO("Not yet implemented")
    }

    override fun retrieveCustomer(customerId: String): Customer {
        return Customer(customerId)
    }
}