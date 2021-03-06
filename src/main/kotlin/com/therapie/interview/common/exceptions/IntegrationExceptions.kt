package com.therapie.interview.common.exceptions

class NotFoundExeception(code: String, message: String?, parameters: Map<String, Any>) : TherapieRuntimeException(code, message, parameters)