package com.ordersystemtask.june.security

import com.ordersystemtask.june.domain.user.entity.AuthenticationUserEntity
import com.ordersystemtask.june.domain.user.repository.UserRepository
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

class JwtUserDetails(
    private val user:AuthenticationUserEntity
) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mapOf(
            "ROLE_USER" to SimpleGrantedAuthority(user.userTrait.traitName),
        ).values.toMutableList()
    }

    override fun getPassword() = ""
    override fun getUsername() = user.userId.toString()
}

class JwtUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails {
        val userId = username?.toLongOrNull()
        if( userId == null ) {
            throw IllegalArgumentException("Invalid userId")
        }

        val entity = userRepository.findAuthenticationUserById(userId)
        if( entity == null ) {
            throw UsernameNotFoundException("User not found")
        }

        return JwtUserDetails(entity)
    }

}