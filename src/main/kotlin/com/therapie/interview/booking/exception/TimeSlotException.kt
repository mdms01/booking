package com.therapie.interview.booking.exception

import com.therapie.interview.common.exceptions.TherapieRuntimeException

class TimeSlotException(code:String, message: String?, parameters:Map<String,Any> ):TherapieRuntimeException(code,message,parameters)
