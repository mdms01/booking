package com.therapie.interview.booking.service

import com.therapie.interview.booking.model.dto.BookingRequest
import com.therapie.interview.booking.repository.BookingRepository
import com.therapie.interview.clinical_services.service.ClinicalServiceTypeService
import com.therapie.interview.customers.service.CustomerService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.dao.DataIntegrityViolationException
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

@SpringBootTest
internal class BookingServiceImplTest {
    //@MockBean
    //lateinit var clinicService: ClinicService

    @MockBean
    lateinit var customerService: CustomerService

    //@MockBean
    @Autowired
    lateinit var clinicalServiceTypeService: ClinicalServiceTypeService

    @MockBean
    lateinit var bookingRepository: BookingRepository

    @Autowired
    lateinit var bookingService: BookingService

    @Test
    fun `book - clinic doesnt exists`() {
        clinicalServiceTypeService.retrieveById("234234")
    }

    @Test
    fun `book - clinical service doesnt exists`() {
    }

    @Test
    fun `book - there isnt time slots available `() {
    }

    @Test
    fun `book - appointment conflict`() {
        val date = LocalDate.now()
        val startTime = LocalTime.of(9, 0)
        val clinicId = "345"
        val serviceId = "s345"
/*        `when`(clinicService.retrieveTimeSlot(
                clinicId,
                serviceId,
                date,
                startTime,
                startTime.plusMinutes(30)))
                .thenReturn( TimeRange(LocalTime.of(8, 0), LocalTime.of(12, 0)) )

        `when`(clinicalServiceTypeService.retrieveById(anyString())).thenReturn(ClinicalService(serviceId,"234", BigDecimal.TEN,30))*/
        //`when`(bookingRepository.insert(anyString(),anyString(),any(), anyString())).then.thenThrow(DataIntegrityViolationException(""))
        val idempotentKey1 = UUID.randomUUID().toString()


        val bookingRequest = BookingRequest("c123", clinicId, serviceId, date, startTime, idempotentKey1)
        val idempotentKey2 = UUID.randomUUID().toString()
        val bookingRequest2 = BookingRequest("c567", clinicId, serviceId, date, startTime, idempotentKey2)

        val savedBooking = bookingService.book(bookingRequest)
        // Assertions.assertEquals(bookingRequest,savedBooking)

        assertThrows<DataIntegrityViolationException> {
            bookingService.book(bookingRequest2)
        }
    }

    @Test
    fun `book - successfully completed`() {
        val bookingRequest = BookingRequest("c1235", "clinic100", "ser100", LocalDate.of(2021, 3, 2), LocalTime.of(9, 0), UUID.randomUUID().toString())
        val savedBooking = bookingService.book(bookingRequest)
        // Assertions.assertEquals(bookingRequest,savedBooking)

    }

    @Test
    fun `book - same request twice`() {
        val idempotentKey = "1234"//UUID.randomUUID().toString()
        val bookingRequest = BookingRequest("c123", "clinic100", "ser100", LocalDate.of(2021, 3, 2), LocalTime.of(9, 0), idempotentKey)
        val bookingRequest2 = bookingRequest.copy()

        val savedBooking1 = bookingService.book(bookingRequest)
        val savedBooking2 = bookingService.book(bookingRequest2)
    }

    @Test
    fun `retrieveBookingsByClinic - there is no clinic`() {
    }

    @Test
    fun `retrieveBookingsByClinic - there is no appointments for the clinic`() {
    }

    @Test
    fun `retrieveBookingsByClinic - there are appointments for the clinic`() {
    }

    @Test
    fun retrieveFreeTimeSlots() {
    }
}