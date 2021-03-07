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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
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
internal class BookingControllerCT : BaseTest() {
    companion object {
        const val IDEMPOTENT_KEY_HEADER_NAME = "x-idempotent-key"
        const val BOOKING_URI = "/bookings"
        const val BOOKINGS_FOR_A_CLINIC_URI = "/clinics/{clinicId}/bookings"
        const val TIME_AVALIABLE_URI = "/clinics/{clinicId}/services/{serviceId}/bookings/available/{date}"
        const val DEFAULT_CUSTOMER_ID = "customer123"
        const val DEFAULT_SERVICE_ID = "23423"
        val DEFAULT_DATE = LocalDate.of(2021, 3, 4)
        val DEFAULT_TIME = LocalTime.of(9, 30, 0)
        
    }


    @Test
    fun `book - successful`() {
        val customerId = DEFAULT_CUSTOMER_ID
        val serviceId = DEFAULT_SERVICE_ID
        val clinicId = "c121"

        mockClinic(clinicId)
        mockCustomer(customerId)
        mockClinicalService(serviceId)
        mockTimeSlots(2)
        val idempotentKey = UUID.randomUUID().toString()
        val bookingRequest = BookingRequest(customerId, clinicId, serviceId, DEFAULT_DATE, DEFAULT_TIME)
        bookSuccessfully(bookingRequest, idempotentKey)
    }

    @Test
    fun `book - invalid time range`() {
        
        val customerId = DEFAULT_CUSTOMER_ID
        val serviceId = DEFAULT_SERVICE_ID
        val clinicId = "c121"

        mockClinic(clinicId)
        mockCustomer(customerId)
        mockClinicalService(serviceId)
        mockInvalidTimeSlots(2)
        val idempotentKey = UUID.randomUUID().toString()
        val bookingRequest = BookingRequest(customerId, clinicId, serviceId, DEFAULT_DATE, DEFAULT_TIME)
        book(bookingRequest, idempotentKey, status().isInternalServerError)
    }

    @Test
    fun `book - doesnt match schedule day`() {
        val customerId = DEFAULT_CUSTOMER_ID
        val serviceId = DEFAULT_SERVICE_ID
        val clinicId = "c121"

        mockClinic(clinicId)
        mockCustomer(customerId)
        mockClinicalService(serviceId)
        mockTimeSlots(2)
        val idempotentKey = UUID.randomUUID().toString()
        val bookingRequest = BookingRequest(customerId, clinicId, serviceId, DEFAULT_DATE, LocalTime.of(9, 31, 0))
        book(bookingRequest, idempotentKey, status().isBadRequest)
    }

    @Test
    fun `book - repeated action`() {
        val customerId = DEFAULT_CUSTOMER_ID
        val serviceId = DEFAULT_SERVICE_ID
        val clinicId = "c123"

        mockClinic(clinicId)
        mockCustomer(customerId)
        mockClinicalService(serviceId)
        mockTimeSlots(2)
        val idempotentKey = UUID.randomUUID().toString()
        val bookingRequest = BookingRequest(customerId, clinicId, serviceId, DEFAULT_DATE, DEFAULT_TIME)
        bookSuccessfully(bookingRequest, idempotentKey)
        bookSuccessfully(bookingRequest, idempotentKey)
    }

    @Test
    fun `book - multiple bookings with same idempotent key`() {
        val customerId = DEFAULT_CUSTOMER_ID
        val serviceId = DEFAULT_SERVICE_ID
        val clinicId = "c1238"

        mockClinic(clinicId)
        mockCustomer(customerId)
        mockClinicalService(serviceId)
        mockTimeSlots(2)
        val idempotentKey = UUID.randomUUID().toString()

        val firstBookingRequest = BookingRequest(customerId, clinicId, serviceId, DEFAULT_DATE, DEFAULT_TIME)
        bookSuccessfully(firstBookingRequest, idempotentKey)

        val secondBookingRequest = BookingRequest(customerId, clinicId, serviceId, DEFAULT_DATE, LocalTime.of(9, 0, 0))
        bookSuccessfully(secondBookingRequest, idempotentKey)
    }

    @Test
    fun `book - appointment conflict`() {
        val customerId = DEFAULT_CUSTOMER_ID
        val serviceId = DEFAULT_SERVICE_ID
        val clinicId = "c124"

        mockClinic(clinicId)
        mockCustomer(customerId)
        mockClinicalService(serviceId)
        mockTimeSlots(2)

        val bookingRequest = BookingRequest(customerId, clinicId, serviceId, DEFAULT_DATE, DEFAULT_TIME)
        book(bookingRequest, null, status().isOk)

        book(bookingRequest, null, status().isConflict)

    }

    @Test
    fun `book - wrong structure`() {
        postRequest(BOOKING_URI, "{wrong structure}", null).andExpect(status().isBadRequest)
    }

    @Test
    fun `book - clinic doesnt exists`() {
        val customerId = DEFAULT_CUSTOMER_ID
        val serviceId = DEFAULT_SERVICE_ID
        val clinicId = "c122"

        mockClinicToRaiseNotFoundError()
        mockClinicalService(serviceId)
        mockCustomer(customerId)
        mockTimeSlots(2)
        val idempotentKey = UUID.randomUUID().toString()

        val bookingRequest = BookingRequest(customerId, clinicId, serviceId, DEFAULT_DATE, DEFAULT_TIME)
        book(bookingRequest, idempotentKey, status().isNotFound)
    }

    @Test
    fun `book - network failure when calling clinic system`() {
        val customerId = DEFAULT_CUSTOMER_ID
        val serviceId = DEFAULT_SERVICE_ID
        val clinicId = "c122"

        mockClinicToRaiseCommunicationError()
        mockClinicalService(serviceId)
        mockCustomer(customerId)
        mockTimeSlots(2)
        val idempotentKey = UUID.randomUUID().toString()
        val bookingRequest = BookingRequest(customerId, clinicId, serviceId, DEFAULT_DATE, DEFAULT_TIME)
        book(bookingRequest, idempotentKey, status().isServiceUnavailable)
    }

    @Test
    fun `book - clinical service doesnt exists`() {
        val customerId = DEFAULT_CUSTOMER_ID
        val serviceId = DEFAULT_SERVICE_ID
        val clinicId = "c121"

        mockClinic(clinicId)
        mockCustomer(customerId)
        mockClinicalServiceToRaiseNotFound()
        mockTimeSlots(2)
        val idempotentKey = UUID.randomUUID().toString()
        val bookingRequest = BookingRequest(customerId, clinicId, serviceId, DEFAULT_DATE, DEFAULT_TIME)
        book(bookingRequest, idempotentKey, status().isNotFound)

    }

    @Test
    fun `book - time slots doesnt exists`() {
        val customerId = DEFAULT_CUSTOMER_ID
        val serviceId = DEFAULT_SERVICE_ID
        val clinicId = "c121"

        mockClinic(clinicId)
        mockCustomer(customerId)
        mockClinicalService(serviceId)
        mockTimeSlotToRaiseNotFoundError()
        val idempotentKey = UUID.randomUUID().toString()
        val bookingRequest = BookingRequest(customerId, clinicId, serviceId, DEFAULT_DATE, DEFAULT_TIME)

        book(bookingRequest, idempotentKey, status().isNotFound)
    }

    @Test
    fun `book - time slots for a date are empty`() {
        val customerId = DEFAULT_CUSTOMER_ID
        val serviceId = DEFAULT_SERVICE_ID
        val clinicId = "c121"

        mockClinic(clinicId)
        mockCustomer(customerId)
        mockClinicalService(serviceId)
        mockEmptyTimeSlots()
        val idempotentKey = UUID.randomUUID().toString()
        val bookingRequest = BookingRequest(customerId, clinicId, serviceId, DEFAULT_DATE, DEFAULT_TIME)

        book(bookingRequest, idempotentKey, status().isBadRequest)
    }

    @Test
    fun `book - network failure when calling clinical services type system`() {
        val customerId = DEFAULT_CUSTOMER_ID
        val serviceId = DEFAULT_SERVICE_ID
        val clinicId = "c121"

        mockClinic(clinicId)
        mockCustomer(customerId)
        mockClinicalServiceToRaiseCommunicationError()
        mockTimeSlots(2)
        val idempotentKey = UUID.randomUUID().toString()
        val bookingRequest = BookingRequest(customerId, clinicId, serviceId, DEFAULT_DATE, DEFAULT_TIME)
        book(bookingRequest, idempotentKey, status().isServiceUnavailable)

    }

    @Test
    fun `book - customer doesnt exists`() {
        val customerId = DEFAULT_CUSTOMER_ID
        val serviceId = DEFAULT_SERVICE_ID
        val clinicId = "c021"

        mockClinic(clinicId)
        mockCustomerToRaiseNotFoundError()
        mockClinicalService(serviceId)
        mockTimeSlots(2)
        val idempotentKey = UUID.randomUUID().toString()

        val bookingRequest = BookingRequest(customerId, clinicId, serviceId, DEFAULT_DATE, DEFAULT_TIME)
        book(bookingRequest, idempotentKey, status().isNotFound)

    }

    @Test
    fun `book - network failure when calling customers system `() {
        val customerId = DEFAULT_CUSTOMER_ID
        val serviceId = DEFAULT_SERVICE_ID
        val clinicId = "c121"

        mockClinic(clinicId)
        mockCustomerToRaiseCommunicationError()
        mockClinicalService(serviceId)
        mockTimeSlots(2)
        val idempotentKey = UUID.randomUUID().toString()
        val bookingRequest = BookingRequest(customerId, clinicId, serviceId, DEFAULT_DATE, DEFAULT_TIME)
        book(bookingRequest, idempotentKey, status().isServiceUnavailable)

    }

    @Test
    fun `book - there isn't time slots available`() {
        val customerId = DEFAULT_CUSTOMER_ID
        val serviceId = DEFAULT_SERVICE_ID
        val clinicId = "c121"

        mockClinic(clinicId)
        mockCustomer(customerId)
        mockClinicalService(serviceId)

        mockTimeSlotToRaiseNotFoundError()

        val idempotentKey = UUID.randomUUID().toString()
        val bookingRequest = BookingRequest(customerId, clinicId, serviceId, DEFAULT_DATE, DEFAULT_TIME)
        book(bookingRequest, idempotentKey, status().isNotFound)
    }

    @Test
    fun `book - network failure when retrieving time slots `() {
        val customerId = DEFAULT_CUSTOMER_ID
        val serviceId = DEFAULT_SERVICE_ID
        val clinicId = "c121"

        mockClinic(clinicId)
        mockCustomer(customerId)
        mockClinicalService(serviceId)
        mockTimeSlotToRaiseCommunicationError()
        val idempotentKey = UUID.randomUUID().toString()
        val bookingRequest = BookingRequest(customerId, clinicId, serviceId, DEFAULT_DATE, DEFAULT_TIME)
        book(bookingRequest, idempotentKey, status().isServiceUnavailable)

    }

    @Test
    fun `retrieve bookings - single booking`() {
        val customerId = DEFAULT_CUSTOMER_ID
        val serviceId = DEFAULT_SERVICE_ID
        val clinicId = "c120"

        mockClinic(clinicId)
        mockCustomer(customerId)
        mockClinicalService(serviceId)
        mockTimeSlots(2)
        val idempotentKey = UUID.randomUUID().toString()
        val bookingRequest = BookingRequest(customerId, clinicId, serviceId, DEFAULT_DATE, DEFAULT_TIME)
        bookSuccessfully(bookingRequest, idempotentKey)

        getRequest(BOOKINGS_FOR_A_CLINIC_URI, clinicId)
                .andExpect(status().isOk)
                .andExpect(jsonPath("[0].clinicId", Matchers.`is`(clinicId)))
    }

    @Test
    fun `retrieve bookings - multiple bookings`() {
        val customerId = DEFAULT_CUSTOMER_ID
        val serviceId = DEFAULT_SERVICE_ID
        val clinicId = "c1207"

        mockClinic(clinicId)
        mockCustomer(customerId)
        mockClinicalService(serviceId)
        mockTimeSlots(2)

        var startTime = LocalTime.of(8, 0, 0)
        (0..5).forEach { _ ->
            val idempotentKey = UUID.randomUUID().toString()
            val bookingRequest = BookingRequest(customerId, clinicId, serviceId, DEFAULT_DATE, startTime)
            startTime = startTime.plusMinutes(30)
            bookSuccessfully(bookingRequest, idempotentKey)
        }
        getRequest(BOOKINGS_FOR_A_CLINIC_URI, clinicId)
                .andExpect(status().isOk)
                .andExpect(jsonPath("*", Matchers.hasSize<Any>(6)))
    }

    @Test
    fun `book - unavailable slot`() {
        val customerId = DEFAULT_CUSTOMER_ID
        val serviceId = DEFAULT_SERVICE_ID
        val clinicId = "c1207"

        mockClinic(clinicId)
        mockCustomer(customerId)
        mockClinicalService(serviceId)
        mockTimeSlots(2)

        var startTime = LocalTime.of(12, 0, 0)
        val idempotentKey = UUID.randomUUID().toString()
        val bookingRequest = BookingRequest(customerId, clinicId, serviceId, DEFAULT_DATE, startTime)
        book(bookingRequest, idempotentKey, status().isBadRequest)
    }

    @Test
    fun `book - unexpected exception`() {
        val customerId = DEFAULT_CUSTOMER_ID
        val serviceId = DEFAULT_SERVICE_ID
        val clinicId = "c1207"

        mockClinic(clinicId)
        mockCustomer(customerId)
        mockClinicalService(serviceId)
        mockTimeSlotToRaiseRuntimeException()

        var startTime = LocalTime.of(12, 0, 0)
        val idempotentKey = UUID.randomUUID().toString()
        val bookingRequest = BookingRequest(customerId, clinicId, serviceId, DEFAULT_DATE, startTime)
        book(bookingRequest, idempotentKey, status().isInternalServerError)
    }

    @Test
    fun `book - generic exception`() {
        val customerId = DEFAULT_CUSTOMER_ID
        val serviceId = DEFAULT_SERVICE_ID
        val clinicId = "c1207"

        mockClinic(clinicId)
        mockCustomer(customerId)
        mockClinicalService(serviceId)
        mockTimeSlotToRaiseTherapieException()

        var startTime = LocalTime.of(12, 0, 0)
        val idempotentKey = UUID.randomUUID().toString()
        val bookingRequest = BookingRequest(customerId, clinicId, serviceId, DEFAULT_DATE, startTime)
        book(bookingRequest, idempotentKey, status().isBadRequest)
    }

    @Test
    fun `retrieve bookings - no bookings `() {
        val clinicId = "c1204"
        mockClinic(clinicId)
        getRequest(BOOKINGS_FOR_A_CLINIC_URI, clinicId)
                .andExpect(status().isOk)
                .andExpect(jsonPath("*", Matchers.hasSize<Any>(0)))
    }

    @Test
    fun `retrieve bookings - no clinic`() {
        val clinicId = "c120"
        mockClinicToRaiseNotFoundError()
        getRequest(BOOKINGS_FOR_A_CLINIC_URI, clinicId)
                .andExpect(status().isNotFound)

    }

    @Test
    fun `retrieve avaliable - no bookings`() {
        val customerId = DEFAULT_CUSTOMER_ID
        val serviceId = DEFAULT_SERVICE_ID
        val clinicId = "c1201"

        mockClinic(clinicId)
        mockCustomer(customerId)
        mockClinicalService(serviceId)
        mockTimeSlots(1)
        getRequest(TIME_AVALIABLE_URI, clinicId, serviceId, DEFAULT_DATE)
                .andExpect(status().isOk)
                .andExpect(jsonPath("*", Matchers.hasSize<Any>(16)))

    }

    @Test
    fun `retrieve avaliable - invalid path parameter`() {
        val serviceId = DEFAULT_SERVICE_ID
        val clinicId = "c1201"
        getRequest(TIME_AVALIABLE_URI, clinicId, serviceId, "invalidDate")
                .andExpect(status().isBadRequest)
    }

    @Test
    fun `retrieve avaliable - one booking`() {
        val customerId = DEFAULT_CUSTOMER_ID
        val serviceId = DEFAULT_SERVICE_ID
        val clinicId = "c1202"

        mockClinic(clinicId)
        mockCustomer(customerId)
        mockClinicalService(serviceId)
        mockTimeSlots(1)
        val idempotentKey = UUID.randomUUID().toString()

        val bookingRequest = BookingRequest(customerId, clinicId, serviceId, DEFAULT_DATE, DEFAULT_TIME)
        bookSuccessfully(bookingRequest, idempotentKey)

        getRequest(TIME_AVALIABLE_URI, clinicId, serviceId, DEFAULT_DATE)
                .andExpect(status().isOk)
                .andExpect(jsonPath("*", Matchers.hasSize<Any>(15)))
                .andExpect(jsonPath("*", Matchers.not(Matchers.containsInAnyOrder(DEFAULT_TIME))))

    }


    private fun bookSuccessfully(bookingRequest: BookingRequest, idempotentKey: String) {
        book(bookingRequest, idempotentKey, status().isOk)
    }

    private fun book(bookingRequest: BookingRequest, idempotentKey: String?, expectedStatus: ResultMatcher): ResultActions {

        return postRequest(BOOKING_URI, bookingRequest, idempotentKey).andExpect(expectedStatus)
    }


    private fun postRequest(urlTemplate: String, body: Any, idempotentKey: String?, vararg parameters: Any): ResultActions {
        var request = post(urlTemplate, *parameters).characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(body))

        request = idempotentKey?.let { request.header(IDEMPOTENT_KEY_HEADER_NAME, idempotentKey) } ?: request

        return mockMvc.perform(request).andDo(print())
    }

    private fun getRequest(urlTemplate: String, vararg parameters: Any): ResultActions {
        var request = get(urlTemplate, *parameters).characterEncoding("UTF-8")

        return mockMvc.perform(request).andDo(print())
    }

}