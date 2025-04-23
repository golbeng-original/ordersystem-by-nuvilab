package com.ordersystemtask.june.clients

import com.ordersystemtask.june.security.JWTGenerator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ClientsConfiguration {

    @Bean
    fun googleAuthClient(config: GoogleOAuthConfig): GoogleAuthClient {
       return GoogleAuthClient(config)
    }

}