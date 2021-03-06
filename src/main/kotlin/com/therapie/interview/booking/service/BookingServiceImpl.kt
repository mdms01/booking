package com.therapie.interview.booking.service

import com.therapie.interview.booking.exception.TimeSlotException
import com.therapie.interview.booking.model.dto.Booking
import com.therapie.interview.booking.model.dto.BookingRequest
import com.therapie.interview.booking.model.entity.BookingEntity
import com.therapie.interview.booking.repository.BookingRepository

import com.therapie.interview.clinics.model.TimeRange
import com.therapie.interview.clinics.service.ClinicService
import com.therapie.interview.customers.service.CustomerService
import com.therapie.interview.clinical_services.model.ClinicalService
import com.therapie.interview.clinical_services.service.ClinicalServiceTypeService
import mu.KLogging
import org.springframework.cache.annotation.Cacheable
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
    companion object: KLogging() {
        const val SIXTY_SECONDS = 60
    }

    @Transactional
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
        clinicService.retrieveClinicById(clinicId)
        return bookingRepository.findByClinic(clinicId).map { it.toDto() }
    }

    override fun retrieveFreeTimeSlots(clinicId: String, serviceId: String, date: LocalDate): List<TimeRange> {
        TODO("Not yet implemented")
    }

    private fun validateBookingRequest(bookingRequest: BookingRequest) {
        checkClinic(bookingRequest)
        checkCustomer(bookingRequest)
        val clinicalServiceType = clinicalServiceTypeService.retrieveById(bookingRequest.serviceId)
        val timeSlot = retrieveTimeSlot(bookingRequest, clinicalServiceType)
        checkIfBookingRequestMatchesStartTimeSlots(bookingRequest, timeSlot, clinicalServiceType)
    }

    private fun checkCustomer(bookingRequest: BookingRequest) {
        customerService.retrieveCustomer(bookingRequest.customerId)
    }

    private fun checkClinic(bookingRequest: BookingRequest) {
        clinicService.retrieveClinicById(bookingRequest.clinicId)
    }

    private fun checkIfBookingRequestMatchesStartTimeSlots(bookingRequest: BookingRequest, timeSlot: TimeRange, clinicalServiceType: ClinicalService) {
        val serviceDurationInSeconds = clinicalServiceType.durationInMunites * SIXTY_SECONDS
        val timeDifferenceInSeconds = (bookingRequest.startTime - timeSlot.startTime).toSecondOfDay()
        if ((timeDifferenceInSeconds % serviceDurationInSeconds) != 0) {
            val previousTimeSlot = timeSlot.startTime.plusSeconds((timeDifferenceInSeconds / serviceDurationInSeconds).toLong() * serviceDurationInSeconds)
            throw TimeSlotException("error.time_slot.wrong_start_time", "There is no option to book the service at ${bookingRequest.startTime} the previous slot is $previousTimeSlot", mapOf("lastSlot" to previousTimeSlot));
        }
    }


    private fun saveBooking(bookingEntity: BookingEntity) {
        bookingRepository.insert(bookingEntity.clinicId, bookingEntity.serviceId, bookingEntity.date, bookingEntity.startTime, bookingEntity.customerId)
    }

    private fun retrieveTimeSlot(bookingRequest: BookingRequest, clinicalServiceType: ClinicalService): TimeRange {
        val bookingEndTime = clinicalServiceType.calculateFinishTime(bookingRequest.startTime)

        return clinicService.retrieveTimeSlotForTimeInterval(bookingRequest.clinicId,
                bookingRequest.serviceId, bookingRequest.date, bookingRequest.startTime, bookingEndTime)
    }

}

private operator fun LocalTime.minus(startTime: LocalTime): LocalTime =
        LocalTime.ofNanoOfDay(this.toNanoOfDay() - startTime.toNanoOfDay())

private fun BookingRequest.toEntity(): BookingEntity =
        BookingEntity(clinicId, serviceId, date, startTime, customerId)

private fun BookingEntity.toDto(): Booking =
        Booking(clinicId, serviceId, date, startTime, customerId)
