package com.ordersystemtask.june.controller.auth

import com.ordersystemtask.june.controller.auth.specification.AuthSpecification
import com.ordersystemtask.june.security.JWTGenerator
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val jwtGenerator: JWTGenerator,
    private val authSpecification: AuthSpecification
) {

    @GetMapping("/login")
    fun getAuthLoginUrl() {
        // TODO("Not yet implemented")
    }

    @PostMapping("/authorize")
    fun authorize(@RequestBody request:AuthorizeRequest): ResponseEntity<AuthorizeResponse> {

        val result = authSpecification.processAuthByOAuthAuthenticationCode(
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