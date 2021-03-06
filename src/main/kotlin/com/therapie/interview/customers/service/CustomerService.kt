package com.therapie.interview.customers.service

import com.therapie.interview.customers.model.Customer

interface CustomerService {
    fun retrieveCustomer(customerId: String): Customer
}