package com.ordersystemtask.june.security

import com.ordersystemtask.june.domain.user.entity.AuthenticationUserEntity
import com.ordersystemtask.june.domain.user.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException


class JwtUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails {
        val userId = username?.toLongOrNull()
        if( userId == null ) {
            throw IllegalArgumentException("Invalid userId")
        }

        val authenticationUser = this.findAuthenticationUser(userId)
        return JwtUserDetails(
            authenticationUser.userId,
            authenticationUser.userTrait.traitName
        )
    }

    private fun findAuthenticationUser(userId:Long) : AuthenticationUserEntity {
        val authenticationUser = userRepository.findAuthenticationUserById(userId)
        if( authenticationUser == null ) {
            throw UsernameNotFoundException("User not found")
        }

        return authenticationUser
    }

}