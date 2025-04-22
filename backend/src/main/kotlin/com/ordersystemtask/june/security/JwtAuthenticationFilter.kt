package com.ordersystemtask.june.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    private val authenticationManager: AuthenticationManager,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        println("enter JwtAuthenticationFilter from ${request.requestURI}")

        val authorizationValue = request.getHeader("Authorization")?.replace("Bearer ", "")
        println("authorizationValue : ${authorizationValue}")

        if( authorizationValue.isNullOrEmpty() ) {
            filterChain.doFilter(request, response)
            return
        }

        val authResult = authenticationManager.authenticate(
            JwtAuthenticationToken(authorizationValue)
        )

        SecurityContextHolder.getContext().authentication = authResult

        filterChain.doFilter(request, response)
    }


}