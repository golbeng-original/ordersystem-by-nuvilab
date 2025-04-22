package com.ordersystemtask.june.applicationService

import com.ordersystemtask.june.applicationService.clients.GoogleAuthClient
import com.ordersystemtask.june.applicationService.clients.GoogleOAuthConfig
import com.ordersystemtask.june.security.JWTGenerator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationServiceConfiguration {

    @Bean
    fun jwtGenerator(): JWTGenerator {
        return JWTGenerator("12345678901234567890123456789012")
    }

    @Bean
    fun googleAuthClient(config: GoogleOAuthConfig): GoogleAuthClient {
       return GoogleAuthClient(config)
    }

}