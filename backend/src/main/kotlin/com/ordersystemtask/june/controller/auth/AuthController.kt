package com.ordersystemtask.june.controller.auth

import com.ordersystemtask.june.applicationService.auth.AuthApplicationService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authApplicationService: AuthApplicationService
) {
    @GetMapping("/login")
    fun getLoginUrl() : GetLoginUrlResponse {
        val loginUrl = authApplicationService.getGoogleOauthLoginUrl()

        return GetLoginUrlResponse(
            loginUrl = loginUrl.toURL().toString()
        )
    }

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