package com.ordersystemtask.june.security

import com.ordersystemtask.june.domain.user.repository.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
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
class SecurityConfiguration(
    private val jwtAuthenticationProvider: JwtAuthenticationProvider
) {

    companion object {
        val corsConfigurationSource: CorsConfigurationSource

        init {
            val corsConfig = CorsConfiguration()
            corsConfig.run {
                allowCredentials = true
                allowedOriginPatterns = listOf(
                    CorsConfiguration.ALL
                )
                allowedHeaders = listOf(
                    CorsConfiguration.ALL
                )
                allowedMethods = listOf(
                    CorsConfiguration.ALL
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
                    "/auth/authorize"
                ).permitAll()

                it.anyRequest().authenticated()
            }

            authenticationManager(authenticationManager)
            addFilterBefore(JwtAuthenticationFilter(authenticationManager), UsernamePasswordAuthenticationFilter::class.java)
        }

        return httpSecurity.build()
    }
}