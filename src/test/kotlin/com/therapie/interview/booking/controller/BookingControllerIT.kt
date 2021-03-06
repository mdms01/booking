package com.therapie.interview.booking.controller

import com.therapie.interview.BookingApplication
import com.therapie.interview.booking.common.util.BaseTest
import com.therapie.interview.booking.model.dto.BookingRequest
import com.therapie.interview.common.config.CacheConfiguration
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.ResultMatcher
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

@SpringBootTest(classes = [BookingApplication::class, CacheConfiguration::class])
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [BookingApplication::class, CacheConfiguration::class])
@WebAppConfiguration
internal class BookingControllerIT : BaseTest() {
    companion object {
        const val IDEMPOTENT_KEY_HEADER_NAME = "x-idempotent-key"
        const val BOOKING_URI = "/bookings"
        const val BOOKINGS_FOR_A_CLINIC_URI = "/clinic/{clinicId}/bookings"
    }


    @Test
    fun `book - successful`() {
        val customerId = "123"
        val serviceId = "23423"
        val clinicId = "c121"

        mockClinic(clinicId)
        mockCustomer(customerId)
        mockClinicalService(serviceId)
        mockTimeSlots(2)
        val idempotentKey = UUID.randomUUID().toString()
        val bookingRequest = BookingRequest(customerId, clinicId, serviceId, LocalDate.of(2021, 3, 4), LocalTime.of(9, 30, 0))
        bookSuccessfully(bookingRequest, idempotentKey)
    }

    @Test
    fun `book - doesnt match schedule day`() {
        val customerId = "123"
        val serviceId = "23423"
        val clinicId = "c121"

        mockClinic(clinicId)
        mockCustomer(customerId)
        mockClinicalService(serviceId)
        mockTimeSlots(2)
        val idempotentKey = UUID.randomUUID().toString()
        val bookingRequest = BookingRequest(customerId, clinicId, serviceId, LocalDate.of(2021, 3, 4), LocalTime.of(9, 31, 0))
        book(bookingRequest, idempotentKey,status().isBadRequest)
    }

    @Test
    fun `book - repeated action`() {
        val customerId = "123"
        val serviceId = "23423"
        val clinicId = "c123"

        mockClinic(clinicId)
        mockCustomer(customerId)
        mockClinicalService(serviceId)
        mockTimeSlots(2)
        val idempotentKey = UUID.randomUUID().toString()
        val bookingRequest = BookingRequest(customerId, clinicId, serviceId, LocalDate.of(2021, 3, 4), LocalTime.of(9, 30, 0))
        bookSuccessfully(bookingRequest, idempotentKey)
        bookSuccessfully(bookingRequest, idempotentKey)
    }

    @Test
    fun `book - multiple bookings with same idempotent key`() {
        val customerId = "123"
        val serviceId = "23423"
        val clinicId = "c1238"

        mockClinic(clinicId)
        mockCustomer(customerId)
        mockClinicalService(serviceId)
        mockTimeSlots(2)
        val idempotentKey = UUID.randomUUID().toString()

        val firstBookingRequest = BookingRequest(customerId, clinicId, serviceId, LocalDate.of(2021, 3, 4), LocalTime.of(9, 30, 0))
        bookSuccessfully(firstBookingRequest, idempotentKey)

        val secondBookingRequest = BookingRequest(customerId, clinicId, serviceId, LocalDate.of(2021, 3, 4), LocalTime.of(9, 0, 0))
        bookSuccessfully(secondBookingRequest, idempotentKey)
    }

    @Test
    fun `book - appointment conflict`() {
        val customerId = "123"
        val serviceId = "23423"
        val clinicId = "c124"

        mockClinic(clinicId)
        mockCustomer(customerId)
        mockClinicalService(serviceId)
        mockTimeSlots(2)

        val bookingRequest = BookingRequest(customerId, clinicId, serviceId, LocalDate.of(2021, 3, 4), LocalTime.of(9, 30, 0))
        book(bookingRequest, null, status().isOk)

        book(bookingRequest, null, status().isConflict)

    }

    @Test
    fun `book - wrong structure`() {
        postRequest(BOOKING_URI,"{wrong structure}",null ).andExpect(status().isBadRequest)
    }

    @Test
    fun `book - clinic doesnt exists`() {
        val customerId = "123"
        val serviceId = "23423"
        val clinicId = "c122"

        mockClinicNotFoundError()
        mockClinicalService(serviceId)
        mockCustomer(customerId)
        mockTimeSlots(2)
        val idempotentKey = UUID.randomUUID().toString()
        val bookingRequest = BookingRequest(customerId, clinicId, serviceId, LocalDate.of(2021, 3, 4), LocalTime.of(9, 30, 0))
        book(bookingRequest, idempotentKey, status().isNotFound)
    }

    @Test
    fun `book - network failure when calling clinic system`() {
        val customerId = "123"
        val serviceId = "23423"
        val clinicId = "c122"

        mockClinicCommunicationError()
        mockClinicalService(serviceId)
        mockCustomer(customerId)
        mockTimeSlots(2)
        val idempotentKey = UUID.randomUUID().toString()
        val bookingRequest = BookingRequest(customerId, clinicId, serviceId, LocalDate.of(2021, 3, 4), LocalTime.of(9, 30, 0))
        book(bookingRequest, idempotentKey, status().isServiceUnavailable)
    }

    @Test
    fun `book - clinical service doesnt exists`() {
        val customerId = "123"
        val serviceId = "23423"
        val clinicId = "c121"

        mockClinic(clinicId)
        mockCustomer(customerId)
        mockClinicalServiceNotFound()
        mockTimeSlots(2)
        val idempotentKey = UUID.randomUUID().toString()
        val bookingRequest = BookingRequest(customerId, clinicId, serviceId, LocalDate.of(2021, 3, 4), LocalTime.of(9, 30, 0))
        book(bookingRequest, idempotentKey, status().isNotFound)

    }

    @Test
    fun `book - network failure when calling clinical services type system`() {
        val customerId = "123"
        val serviceId = "23423"
        val clinicId = "c121"

        mockClinic(clinicId)
        mockCustomer(customerId)
        mockClinicalServiceCommunicationError()
        mockTimeSlots(2)
        val idempotentKey = UUID.randomUUID().toString()
        val bookingRequest = BookingRequest(customerId, clinicId, serviceId, LocalDate.of(2021, 3, 4), LocalTime.of(9, 30, 0))
        book(bookingRequest, idempotentKey, status().isServiceUnavailable)

    }

    @Test
    fun `book - customer doesnt exists`() {
        val customerId = "123"
        val serviceId = "23423"
        val clinicId = "c121"

        mockClinic(clinicId)
        mockCustomerNotFoundError()
        mockClinicalService(serviceId)
        mockTimeSlots(2)
        val idempotentKey = UUID.randomUUID().toString()
        val bookingRequest = BookingRequest(customerId, clinicId, serviceId, LocalDate.of(2021, 3, 4), LocalTime.of(9, 30, 0))
        book(bookingRequest, idempotentKey, status().isNotFound)

    }

    @Test
    fun `book - network failure when calling customers system `() {
        val customerId = "123"
        val serviceId = "23423"
        val clinicId = "c121"

        mockClinic(clinicId)
        mockCustomerCommunicationError()
        mockClinicalService(serviceId)
        mockTimeSlots(2)
        val idempotentKey = UUID.randomUUID().toString()
        val bookingRequest = BookingRequest(customerId, clinicId, serviceId, LocalDate.of(2021, 3, 4), LocalTime.of(9, 30, 0))
        book(bookingRequest, idempotentKey, status().isServiceUnavailable)

    }

    @Test
    fun `book - there isnt time slots available `() {
        val customerId = "123"
        val serviceId = "23423"
        val clinicId = "c121"

        mockClinic(clinicId)
        mockCustomer(customerId)
        mockClinicalService(serviceId)
        mockTimeSlotNotFoundError()
        val idempotentKey = UUID.randomUUID().toString()
        val bookingRequest = BookingRequest(customerId, clinicId, serviceId, LocalDate.of(2021, 3, 4), LocalTime.of(9, 30, 0))
        book(bookingRequest, idempotentKey, status().isNotFound)
    }

    @Test
    fun `book - network failure when retrieving time slots `() {
        val customerId = "123"
        val serviceId = "23423"
        val clinicId = "c121"

        mockClinic(clinicId)
        mockCustomer(customerId)
        mockClinicalService(serviceId)
        mockTimeSlotCommunicationError()
        val idempotentKey = UUID.randomUUID().toString()
        val bookingRequest = BookingRequest(customerId, clinicId, serviceId, LocalDate.of(2021, 3, 4), LocalTime.of(9, 30, 0))
        book(bookingRequest, idempotentKey, status().isServiceUnavailable)

    }

    @Test
    fun `retrieve bookings - single booking`() {
        val customerId = "123"
        val serviceId = "23423"
        val clinicId = "c120"

        mockClinic(clinicId)
        mockCustomer(customerId)
        mockClinicalService(serviceId)
        mockTimeSlots(2)
        val idempotentKey = UUID.randomUUID().toString()
        val bookingRequest = BookingRequest(customerId, clinicId, serviceId, LocalDate.of(2021, 3, 4), LocalTime.of(9, 30, 0))
        bookSuccessfully(bookingRequest,idempotentKey)

        getRequest(BOOKINGS_FOR_A_CLINIC_URI,clinicId)
                .andExpect(status().isOk)
                .andExpect(jsonPath("[0].clinicId", Matchers.`is`(clinicId)))
    }

    @Test
    fun `retrieve bookings - multiple bookings`() {
        val customerId = "123"
        val serviceId = "23423"
        val clinicId = "c1207"

        mockClinic(clinicId)
        mockCustomer(customerId)
        mockClinicalService(serviceId)
        mockTimeSlots(2)

        var startTime = LocalTime.of(9, 30, 0)
        (0..10).forEach{ _ ->
            val idempotentKey = UUID.randomUUID().toString()
            val bookingRequest = BookingRequest(customerId, clinicId, serviceId, LocalDate.of(2021, 3, 4), startTime)
            startTime = startTime.plusMinutes(30)
            bookSuccessfully(bookingRequest, idempotentKey)
        }
        getRequest(BOOKINGS_FOR_A_CLINIC_URI,clinicId)
                .andExpect(status().isOk)
                .andExpect(jsonPath("*", Matchers.hasSize<Any>(11)))
    }

    @Test
    fun `retrieveBookingsForAClinic - no bookings `() {
        val clinicId = "c1204"
        mockClinic(clinicId)
        getRequest(BOOKINGS_FOR_A_CLINIC_URI,clinicId)
                .andExpect(status().isOk)
                .andExpect(jsonPath("*", Matchers.hasSize<Any>(0)))
    }

    @Test
    fun `retrieve bookings - no clinic`() {
        val clinicId = "c120"
        mockClinicNotFoundError()
        getRequest(BOOKINGS_FOR_A_CLINIC_URI, clinicId)
                .andExpect(status().isNotFound)

    }

    private fun bookSuccessfully(bookingRequest: BookingRequest, idempotentKey: String) {
        book(bookingRequest, idempotentKey, status().isOk)
    }

    private fun book(bookingRequest: BookingRequest, idempotentKey: String?, expectedStatus: ResultMatcher): ResultActions {

        return postRequest(BOOKING_URI, bookingRequest, idempotentKey).andExpect(expectedStatus)
    }


    private fun postRequest(urlTemplate: String, body: Any, idempotentKey: String?,vararg parameters:Any): ResultActions {
        var request = post(urlTemplate,*parameters).characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(body))

        request = idempotentKey?.let { request.header(IDEMPOTENT_KEY_HEADER_NAME, idempotentKey) } ?: request

        return mockMvc.perform(request).andDo(print())
    }

    private fun getRequest(urlTemplate: String, vararg parameters:Any): ResultActions {
        var request = get(urlTemplate,*parameters).characterEncoding("UTF-8")

        return mockMvc.perform(request).andDo(print())
    }

}