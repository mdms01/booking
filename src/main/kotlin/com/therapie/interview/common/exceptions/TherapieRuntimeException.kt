package com.therapie.interview.common.exceptions

open class TherapieRuntimeException(val code:String,
                                    message: String?,
                                    val parameters:Map<String,Any> = HashMap() ) : RuntimeException(message)