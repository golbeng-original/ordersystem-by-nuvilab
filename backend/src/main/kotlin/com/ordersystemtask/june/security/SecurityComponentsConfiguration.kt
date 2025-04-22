package com.ordersystemtask.june.security

import com.ordersystemtask.june.domain.user.repository.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SecurityComponentsConfiguration {

    @Bean
    fun jwtUserDetailsService(
        userRepository: UserRepository
    ): JwtUserDetailsService {
        return JwtUserDetailsService(userRepository)
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