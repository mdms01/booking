package com.therapie.interview.booking.service

import com.therapie.interview.booking.model.Booking
import com.therapie.interview.booking.model.BookingKey
import com.therapie.interview.booking.model.BookingRequest
import com.therapie.interview.booking.repository.BookingRepository
import com.therapie.interview.clinics.model.TimeRange
import com.therapie.interview.clinics.service.ClinicService
import com.therapie.interview.customer.service.CustomerService
import com.therapie.interview.services.service.ClinicalServiceTypeService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime

@Service
class BookingServiceImpl(
        val clinicService: ClinicService,
        val customerService: CustomerService,
        val clinicalServiceTypeService: ClinicalServiceTypeService,
        val bookingRepository: BookingRepository) : BookingService {

    @Transactional
    override fun book(bookingRequest: BookingRequest): Booking {
        val customer = customerService.retrieveCustomer(bookingRequest.customerId)
        val clinicalServiceType = clinicalServiceTypeService.retrieveById(bookingRequest.serviceId)
        val bookingEndTime = clinicalServiceType.calculateFinishTime(bookingRequest.startTime)
        val timeSlot = clinicService.timeSlotExists(bookingRequest.clinicId, bookingRequest.serviceId, bookingRequest.date, bookingRequest.startTime, bookingEndTime)
        val matchesAnIntervalSpot = ( (bookingRequest.startTime - timeSlot.startTime).toSecondOfDay() % clinicalServiceType.durationInMunites * 60) == 0
        if (!matchesAnIntervalSpot){
            throw RuntimeException("There is no spot")
        }

        val booking = bookingRequest.toAppointment(bookingEndTime)
         bookingRepository.insert(booking.clinicId, booking.serviceId, booking.startTime, booking.customerId)
        return booking

    }

    override fun retrieveBookingsByClinic(clinicId: String): List<Booking> =
            bookingRepository.findByClinic(clinicId)

    override fun retrieveFreeTimeSlots(clinicId: String, serviceId: String, date: LocalDate): List<TimeRange> {
        TODO("Not yet implemented")
    }
}

private fun BookingRequest.toBookingKey(): BookingKey =
        BookingKey(clinicId, serviceId, date.atTime(startTime))

private operator fun LocalTime.minus(startTime: LocalTime): LocalTime =
        LocalTime.ofNanoOfDay(this.toNanoOfDay() - startTime.toNanoOfDay())


private fun BookingRequest.toAppointment(endTime: LocalTime): Booking =
        Booking(clinicId, serviceId, date.atTime(startTime),customerId )

