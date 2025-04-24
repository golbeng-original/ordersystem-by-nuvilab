package com.ordersystemtask.june.controller.common

import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


data class ErrorViewModel(
    val message: String
)

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(Exception::class)
    fun handleAllException(e: Exception): ErrorViewModel {
        return ErrorViewModel(
            message = e.message ?: "Unknown error"
        )
    }
}