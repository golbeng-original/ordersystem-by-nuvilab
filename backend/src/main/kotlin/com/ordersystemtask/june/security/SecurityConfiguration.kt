package com.ordersystemtask.june.security

import com.ordersystemtask.june.clients.GoogleAuthClient
import com.ordersystemtask.june.domain.user.repository.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfiguration(
    private val jwtAuthenticationProvider: JwtAuthenticationProvider,
    private val userRepository: UserRepository,
    private val googleAuthClient: GoogleAuthClient,
    private val jwtGenerator: JWTGenerator
) {

    companion object {
        val corsConfigurationSource: CorsConfigurationSource

        init {
            val corsConfig = CorsConfiguration()
            corsConfig.run {
                allowCredentials = true
                allowedOriginPatterns = listOf(
                    CorsConfiguration.ALL
                    //"http://localhost:8080",
                    //"http://127.0.0.1:8080",
                    //"http://ordersystem-nuvilab.frontend.s3-website.ap-northeast-2.amazonaws.com"
                )
                allowedHeaders = listOf(
                    CorsConfiguration.ALL
                )
                allowedMethods = listOf(
                    CorsConfiguration.ALL
                )
                exposedHeaders = listOf(
                    "X-NEW-TOKEN"
                )
            }

            corsConfigurationSource = UrlBasedCorsConfigurationSource()
            corsConfigurationSource.registerCorsConfiguration(
                "/**",
                corsConfig
            )
        }
    }

    @Bean
    fun filterChain(
        httpSecurity: HttpSecurity
    ) : SecurityFilterChain {

        val authenticationManager = httpSecurity
            .getSharedObject(AuthenticationManagerBuilder::class.java)
            .authenticationProvider(jwtAuthenticationProvider)
            .build()


        httpSecurity.run {
            httpBasic { it.disable() }
            formLogin { it.disable() }
            logout { it.disable() }

            csrf { it.disable() }
            cors { it.configurationSource(corsConfigurationSource)}

            sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }

            authorizeHttpRequests {
                it.requestMatchers(
                    "/auth/**"
                ).permitAll()

                it.anyRequest().authenticated()
            }

            authenticationManager(authenticationManager)

            addFilterBefore(
                JwtAuthenticationFilter(
                    authenticationManager,
                    userRepository,
                    googleAuthClient,
                    jwtGenerator
                ),
                UsernamePasswordAuthenticationFilter::class.java
            )
        }

        return httpSecurity.build()
    }
}