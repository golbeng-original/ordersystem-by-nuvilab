package com.ordersystemtask.june.security

import com.ordersystemtask.june.domain.user.repository.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager

@Configuration
class SecurityComponentsConfiguration {

    @Bean
    fun jwtUserDetailsService(
        userRepository: UserRepository
    ): JwtUserDetailsService {
        return JwtUserDetailsService(userRepository)
    }

    @Bean
    fun jwtGenerator(): JWTGenerator {
        return JWTGenerator("12345678901234567890123456789012")
    }

    @Bean
    fun jwtAuthenticationProvider(
        jwtUserDetailsService: JwtUserDetailsService,
        jwtGenerator: JWTGenerator
    ): JwtAuthenticationProvider {
        return JwtAuthenticationProvider(
            jwtUserDetailsService,
            jwtGenerator
        )
    }
}