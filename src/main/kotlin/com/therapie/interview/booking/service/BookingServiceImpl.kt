package com.therapie.interview.booking.service

import com.therapie.interview.booking.exception.BookingException
import com.therapie.interview.booking.model.dto.Booking
import com.therapie.interview.booking.model.dto.BookingRequest
import com.therapie.interview.booking.model.entity.BookingEntity
import com.therapie.interview.booking.repository.BookingRepository
import com.therapie.interview.clinical_services.service.ClinicalServiceTypeService
import com.therapie.interview.clinics.model.ClinicalServiceTimeSlot
import com.therapie.interview.clinics.service.ClinicService
import com.therapie.interview.customers.service.CustomerService
import mu.KLogging
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Service
class BookingServiceImpl(
        val clinicService: ClinicService,
        val customerService: CustomerService,
        val clinicalServiceTypeService: ClinicalServiceTypeService,
        val bookingRepository: BookingRepository) : BookingService {
    companion object : KLogging()

    @Cacheable(value = ["bookings"],
            key = "#bookingRequest.clinicId + '-' + #bookingRequest.serviceId + '-' + #bookingRequest.customerId + '-' + #bookingRequest.date.toString() + '-' + #bookingRequest.startTime.toString() + '-' + #bookingRequest.idempotentKey",
            condition = "#bookingRequest.idempotentKey != null"
    )
    override fun book(bookingRequest: BookingRequest): Booking {
        validateBookingRequest(bookingRequest)
        val booking = bookingRequest.toEntity()
        saveBooking(booking)
        return booking.toDto()
    }

    override fun retrieveBookingsByClinic(clinicId: String): List<Booking> {
        checkClinic(clinicId)
        return bookingRepository.findByClinic(clinicId).map { it.toDto() }
    }

    override fun retrieveFreeTimeSlots(clinicId: String, serviceId: String, date: LocalDate): List<LocalTime> {
        checkClinic(clinicId)
        val clinicalServiceType = clinicalServiceTypeService.retrieveById(serviceId)
        val bookableTimeSlots = clinicService.retrieveBookableTimeSlots(clinicId, serviceId, date, clinicalServiceType.durationInMinutes)
        val bookedTimes = bookingRepository.findByClinicAndServiceAndDate(clinicId, serviceId, date).map { it.startTime }.toSet()
        return bookableTimeSlots.filterNot { bookedTimes.contains(it) }

    }

    private fun validateBookingRequest(bookingRequest: BookingRequest) {
        checkIfBookingIsInTheFuture(bookingRequest)
        checkClinic(bookingRequest.clinicId)
        checkCustomer(bookingRequest)
        val clinicalServiceType = clinicalServiceTypeService.retrieveById(bookingRequest.serviceId)
        val timeSlot = bookingRequest.toTimeSlot(clinicalServiceType.durationInMinutes)
        clinicService.checkTimeSlotIsValid(timeSlot)
    }

    private fun checkIfBookingIsInTheFuture(bookingRequest: BookingRequest) {
        val now = LocalDateTime.now()
        val booking = bookingRequest.date.atTime(bookingRequest.startTime)
        if (now.isAfter(booking)) {
            throw BookingException("error.booking.not_past_booking", "Bookings can't happen in the past", mapOf("currentTime" to now))
        }
    }

    private fun checkCustomer(bookingRequest: BookingRequest) {
        customerService.retrieveCustomer(bookingRequest.customerId)
    }

    private fun checkClinic(clinicId: String) {
        clinicService.retrieveClinicById(clinicId)
    }

    private fun saveBooking(bookingEntity: BookingEntity) {
        bookingRepository.insert(bookingEntity.clinicId, bookingEntity.serviceId, bookingEntity.date, bookingEntity.startTime, bookingEntity.customerId)
    }

}

private fun BookingRequest.toTimeSlot(durationInMinutes: Long): ClinicalServiceTimeSlot =
        ClinicalServiceTimeSlot(clinicId, serviceId, date, startTime, durationInMinutes)

private fun BookingRequest.toEntity(): BookingEntity =
        BookingEntity(clinicId, serviceId, date, startTime, customerId)

private fun BookingEntity.toDto(): Booking =
        Booking(clinicId, serviceId, date, startTime, customerId)
