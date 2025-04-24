package com.ordersystemtask.june.controller.auth

import com.ordersystemtask.june.applicationService.auth.AuthApplicationService
import com.ordersystemtask.june.security.JWTGenerator
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authApplicationService: AuthApplicationService
) {
    @PostMapping("/authorize")
    fun authorize(@RequestBody request:AuthorizeRequest): AuthorizeResponse {

        val result = authApplicationService.processAuthByOAuthAuthenticationCode(
            code = request.authorizeCode
        )

        val user = result.user
        val jwtToken = result.jwtToken

        return AuthorizeResponse(
            userId = user.userId,
            email = user.email,
            jwtToken = jwtToken,
        )
    }
}