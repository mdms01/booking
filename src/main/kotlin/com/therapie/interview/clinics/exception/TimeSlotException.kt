package com.therapie.interview.clinics.exception

import com.therapie.interview.common.exceptions.TherapieRuntimeException

class TimeSlotException(code: String, message: String?, parameters: Map<String, Any> = emptyMap()) : TherapieRuntimeException(code, message, parameters)
