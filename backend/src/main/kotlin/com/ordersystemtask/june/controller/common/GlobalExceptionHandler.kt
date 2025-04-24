package com.ordersystemtask.june.controller.common

import org.springframework.http.ResponseEntity
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


data class ErrorViewModel(
    val message: String
)

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(AuthorizationDeniedException::class)
    fun handleAuthorizationDeniedException(e: AuthorizationDeniedException): ResponseEntity<ErrorViewModel> {
        return ResponseEntity
            .status(403)
            .body(
                ErrorViewModel(
                    message = e.message ?: "Authorization denied"
                )
            )
    }

    @ExceptionHandler(Exception::class)
    fun handleAllException(e: Exception): ResponseEntity<ErrorViewModel> {
        return ResponseEntity
            .badRequest()
            .body(
                ErrorViewModel(
                    message = e.message ?: "Unknown error"
                )
            )
    }
}