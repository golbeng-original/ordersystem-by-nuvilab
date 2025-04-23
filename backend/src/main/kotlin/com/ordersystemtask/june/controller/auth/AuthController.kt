package com.ordersystemtask.june.controller.auth

import com.ordersystemtask.june.applicationService.auth.AuthApplicationService
import com.ordersystemtask.june.security.JWTGenerator
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val jwtGenerator: JWTGenerator,
    private val authApplicationService: AuthApplicationService
) {

    @GetMapping("/login")
    fun getAuthLoginUrl() {
        // TODO("Not yet implemented")
    }

    @PostMapping("/authorize")
    fun authorize(@RequestBody request:AuthorizeRequest): ResponseEntity<AuthorizeResponse> {

        val result = authApplicationService.processAuthByOAuthAuthenticationCode(
            code = request.authorizeCode
        )

        val user = result.user
        val jwtToken = result.jwtToken

        return ResponseEntity
            .ok()
            .body(AuthorizeResponse(
                userId = user.userId,
                email = user.email,
                jwtToken = jwtToken,
            ))
    }

    @GetMapping("/test")
    fun test() : ResponseEntity<Unit> {
        // TODO("Not yet implemented")

        return ResponseEntity
            .ok()
            .build()
    }

}