package com.therapie.interview.booking.common.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.therapie.interview.clinical_services.model.ClinicalService
import com.therapie.interview.clinical_services.service.remote.ClinicalServicesRestClient
import com.therapie.interview.clinics.model.Clinic
import com.therapie.interview.clinics.model.TimeRange
import com.therapie.interview.clinics.service.remote.ClinicRestClient
import com.therapie.interview.common.exceptions.TherapieRuntimeException
import com.therapie.interview.customers.model.Customer
import com.therapie.interview.customers.service.remote.CustomerRestClient
import feign.FeignException
import feign.Request
import feign.RequestTemplate
import feign.RetryableException
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.math.BigDecimal
import java.nio.charset.Charset
import java.time.LocalTime
import java.util.*

open class BaseTest {
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var webApplicationContext: WebApplicationContext

    @MockBean
    lateinit var customerRestClient: CustomerRestClient

    @MockBean
    lateinit var clinicRestClient: ClinicRestClient

    @MockBean
    lateinit var clinicalServicesRestClient: ClinicalServicesRestClient

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
    }

    protected fun mockTimeSlots(amount: Int, startHour: Int = 8, startMinute: Int = 0, sizeInMinutes: Int = 4 * 60, intervalInMinutes: Int = 30) {
        var startTime = LocalTime.of(startHour, startMinute)
        val timeSlots = buildTimeSlots(amount, startTime, sizeInMinutes, intervalInMinutes)
        mockClinicalServicesTimeSlots(timeSlots)
    }

    protected fun mockEmptyTimeSlots() {
        mockClinicalServicesTimeSlots(emptyList())
    }

    private fun buildTimeSlots(amount: Int, startTime: LocalTime, sizeInMinutes: Int, intervalInMinutes: Int): List<TimeRange> {
        var currentTime = startTime
        return (0..amount).map {
            val time = TimeRange(currentTime, currentTime.plusMinutes(sizeInMinutes.toLong()))
            currentTime = currentTime.plusMinutes(sizeInMinutes.toLong() + intervalInMinutes.toLong())
            time
        }
    }

    protected fun mockTimeSlotToRaiseRuntimeException() {
        `when`(clinicRestClient.retrieveTimeSlots(anyString(), anyString(), anyString(), anyString())).thenThrow(RuntimeException("to test unexpected flow"))
    }

    protected fun mockTimeSlotToRaiseTherapieException() {
        `when`(clinicRestClient.retrieveTimeSlots(anyString(), anyString(), anyString(), anyString())).thenThrow(TherapieRuntimeException("error.generic","Generic error"))
    }

    protected fun mockTimeSlotToRaiseNotFoundError() {
        `when`(clinicRestClient.retrieveTimeSlots(anyString(), anyString(), anyString(), anyString())).thenThrow(FeignException.NotFound("", mockFeignRequest(), ByteArray(1)))
    }

    protected fun mockTimeSlotToRaiseCommunicationError() {
        `when`(clinicRestClient.retrieveTimeSlots(anyString(), anyString(), anyString(), anyString())).thenThrow(RetryableException(500, "", Request.HttpMethod.POST, Date(), mockFeignRequest()))
    }

    protected fun mockClinicalService(serviceId: String) {
        val clinicalService = ClinicalService(serviceId, "clinical service $serviceId", BigDecimal.TEN, 30)
        mockClinicalServices(clinicalService)
    }

    protected fun mockClinicalServicesTimeSlots(listOf: List<TimeRange>) {
        `when`(clinicRestClient.retrieveTimeSlots(anyString(), anyString(), anyString(), anyString())).thenReturn(listOf)
    }

    protected fun mockClinic(clinicId: String) {
        `when`(clinicRestClient.retrieveById(anyString(), anyString())).thenReturn(Clinic(clinicId))
    }

    protected fun mockClinicalServices(clinicalService: ClinicalService) {
        `when`(clinicalServicesRestClient.retrieveById(anyString(), anyString())).thenReturn(clinicalService)
    }

    protected fun mockCustomer(id: String = "c123") {
        val customer = Customer(id)
        `when`(customerRestClient.retrieveById(anyString(), anyString())).thenReturn(customer)
    }

    protected fun mockCustomerToRaiseNotFoundError() {
        `when`(customerRestClient.retrieveById(anyString(), anyString())).thenThrow(FeignException.NotFound("", mockFeignRequest(), ByteArray(1)))
    }

    protected fun mockCustomerToRaiseCommunicationError() {
        `when`(customerRestClient.retrieveById(anyString(), anyString())).thenThrow(RetryableException(500, "", Request.HttpMethod.POST, Date(), mockFeignRequest()))
    }

    protected fun mockClinicToRaiseNotFoundError() {
        `when`(clinicRestClient.retrieveById(anyString(), anyString())).thenThrow(FeignException.NotFound("", mockFeignRequest(), ByteArray(1)))
    }

    protected fun mockClinicToRaiseCommunicationError() {
        `when`(clinicRestClient.retrieveById(anyString(), anyString())).thenThrow(RetryableException(500, "", Request.HttpMethod.POST, Date(),
                mockFeignRequest()))
    }

    private fun mockFeignRequest() =
            Request.create(Request.HttpMethod.POST, "", emptyMap(), ByteArray(1), Charset.defaultCharset(), RequestTemplate())


    protected fun mockClinicalServiceToRaiseNotFound() {
        `when`(clinicalServicesRestClient.retrieveById(anyString(), anyString())).thenThrow(FeignException.NotFound("", mockFeignRequest(), ByteArray(1)))
    }

    protected fun mockClinicalServiceToRaiseCommunicationError() {
        `when`(clinicalServicesRestClient.retrieveById(anyString(), anyString())).thenThrow(RetryableException(500, "", Request.HttpMethod.POST, Date(), mockFeignRequest()))
    }

}