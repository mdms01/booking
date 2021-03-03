package com.therapie.interview.customer.service

import com.therapie.interview.customer.model.Customer

interface CustomerService {
    fun retrieveAllCustomers(): List<Customer>
    fun retrieveCustomer(customerId: String): Customer
}