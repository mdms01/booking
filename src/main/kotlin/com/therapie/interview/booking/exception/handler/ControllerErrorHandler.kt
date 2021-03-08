package com.therapie.interview.booking.exception.handler

import com.therapie.interview.booking.model.dto.ErrorInformation
import com.therapie.interview.clinics.exception.TimeSlotException
import com.therapie.interview.common.exceptions.NotFoundExeception
import com.therapie.interview.common.exceptions.TherapieRuntimeException
import feign.RetryableException
import mu.KLogging
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException


@ControllerAdvice(basePackages = ["com.therapie.interview.booking.controller"])
class ControllerErrorHandler {
    companion object : KLogging()

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityException(exception: DataIntegrityViolationException): ResponseEntity<ErrorInformation> {
        val errorInformation = ErrorInformation("error.booking.already_booked",
                "The select time already has been taken")
        logger.warn { "Processing Error id:(${errorInformation.id}) message:${exception.message}" }
        logger.debug(exception) { "Details id:(${errorInformation.id})" }

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(errorInformation)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleUnexpectedDataStructure(exception: HttpMessageNotReadableException) =
            handleGenericDataStructureException(exception)

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleUnexpectedDataStructure(exception: MethodArgumentTypeMismatchException) =
            handleGenericDataStructureException(exception)


    fun handleGenericDataStructureException(exception: Exception): ResponseEntity<ErrorInformation> {
        val errorInformation = ErrorInformation("error.clinic.wrong_structure",
                "Request doesn't contain the expected structure")

        logger.warn { "Malformed Request:(${errorInformation.id}) ${exception.message}" }
        logger.debug(exception) { "Details:(${errorInformation.id})" }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorInformation)
    }


    @ExceptionHandler(RetryableException::class)
    fun handleCommunicationError(exception: RetryableException): ResponseEntity<ErrorInformation> {
        val errorInformation = ErrorInformation("error.server.unavailable",
                "Service is temporarily unavailable ")

        logger.error(exception) { "Communication Error:(${errorInformation.id}) retry after(${exception.retryAfter()})" }

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorInformation)
    }

    @ExceptionHandler(TherapieRuntimeException::class)
    fun handleGenericError(exception: TherapieRuntimeException): ResponseEntity<ErrorInformation> {
        val errorInformation = ErrorInformation(exception.code,
                exception.message ?: "", exception.parameters)

        logger.error(exception) { "Generic Error:(${errorInformation.id}) " }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorInformation)
    }

    @ExceptionHandler(TimeSlotException::class)
    fun handleTimeSlotError(exception: TimeSlotException): ResponseEntity<ErrorInformation> {
        val errorInformation = ErrorInformation(exception.code, exception.message ?: "", exception.parameters)

        logger.warn { "Time Slot Error:(${errorInformation.id}) code:${exception.code} parameters:${exception.parameters} " }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorInformation)
    }

    @ExceptionHandler(NotFoundExeception::class)
    fun handleNotFoundError(exception: NotFoundExeception): ResponseEntity<ErrorInformation> {
        val errorInformation = ErrorInformation(exception.code,
                exception.message ?: "", exception.parameters)

        logger.warn { "Not Found Error:(${errorInformation.id}) ${exception.code} parameters:${exception.parameters} " }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorInformation)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericError(exception: Exception): ResponseEntity<ErrorInformation> {
        val errorInformation = ErrorInformation("error.server.unexpected",
                "An unexpected error happen")

        logger.error(exception) { "Unexpected Error:(${errorInformation.id})" }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorInformation)
    }

}