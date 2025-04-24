package com.ordersystemtask.june.security

import com.ordersystemtask.june.clients.GoogleAuthClient
import com.ordersystemtask.june.domain.user.repository.UserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    private val authenticationManager: AuthenticationManager,
    private val userRepository: UserRepository,
    private val googleAuthClient: GoogleAuthClient,
    private val jwtGenerator: JWTGenerator
) : OncePerRequestFilter() {

    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    private val skipPaths = listOf(
        "/auth/authorize"
    )

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI
        return skipPaths.any { path.startsWith(it) }
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authorizationToken = request.getHeader("Authorization")?.replace("Bearer ", "")
        if( authorizationToken.isNullOrEmpty() ) {
            //filterChain.doFilter(request, response)
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Bearer is empty")
            return
        }

        var authentication: Authentication
        try {
            authentication = authenticationManager.authenticate(
                JwtAuthenticationToken(authorizationToken)
            )
        }
        // NOTE : AccessToken이 만료 되었을 때 RefreshToken으로 AccessToken 재발급 필요
        catch (e: JwtTokenExpiredException) {
            val userId = e.claims.subject.toLong()

            val resolveAuthorizationToken = resolveAccessToken(userId)
            response.setHeader("X-NEW-TOKEN", resolveAuthorizationToken)

            authentication = authenticationManager.authenticate(
                JwtAuthenticationToken(resolveAuthorizationToken)
            )
        }
        catch(e: Exception) {
            logger.error(e.message)
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.message)
            return
        }

        SecurityContextHolder.getContext().authentication = authentication
        filterChain.doFilter(request, response)
    }

    private fun resolveAccessToken(userId:Long) : String {
        try {
            println("enter resolveAccessToken")

            val authenticationUser = userRepository.findAuthenticationUserById(userId)
            require(authenticationUser != null) {
                "AuthenticationUser not found"
            }

            val refreshToken = authenticationUser.oauthProvider.refreshToken
            require(refreshToken.isNotEmpty()) {
                "RefreshToken is empty"
            }

            val response = googleAuthClient.resolveAccessToken(refreshToken)

            println("resolveAccessToken : ${response.accessToken}")

            val jwtToken = jwtGenerator.generate(
                userId = userId,
                accessToken = response.accessToken,
                expireSeconds = response.expireSeconds
            )

            return jwtToken
        }
        catch (e: Exception) {
            logger.error(e.message)
            throw e
        }
    }


}