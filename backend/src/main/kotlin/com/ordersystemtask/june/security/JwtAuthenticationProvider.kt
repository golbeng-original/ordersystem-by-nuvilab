package com.ordersystemtask.june.security

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

class JwtAuthenticationToken(
    val token:String
) : AbstractAuthenticationToken(null) {
    override fun getCredentials(): Any {
        return token
    }

    override fun getPrincipal(): Any? {
        return null
    }

}

@Component
class JwtAuthenticationProvider(
    private val jwtUserDetailsService: JwtUserDetailsService,
    private val jwtGenerator: JWTGenerator
) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication?): Authentication {
        val token = (authentication as JwtAuthenticationToken).token

        var claims: JwtClaims? = null
        try {
            claims = jwtGenerator.deserialize(token)
        }
        // Filter에서 accessToken 재발급 할 수 있도록 예외 전파
        catch (e: JwtTokenExpiredException) {
            throw e
            // TODO : AccessToken이 만료 되었나? 체크 필요
            // TODO : RefershToken으로 AccessToken 재발급 필요
        }


        if( claims == null ) {
            throw BadCredentialsException("Invalid JWT token")
        }

        // userDetailsService에서 실제 검증 유저 정보 찾기
        val userDetails = jwtUserDetailsService.loadUserByUsername(claims.userId.toString())

        return UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.authorities
        )
    }

    override fun supports(authentication: Class<*>?): Boolean {
        if( authentication == null ) {
            return false
        }

        return JwtAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}