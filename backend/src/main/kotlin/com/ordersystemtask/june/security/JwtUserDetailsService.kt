package com.ordersystemtask.june.security

import com.ordersystemtask.june.domain.user.entity.AuthenticationUserEntity
import com.ordersystemtask.june.domain.user.entity.UserEntity
import com.ordersystemtask.june.domain.user.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

class JwtUserDetails(
    val user:AuthenticationUserEntity
) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(
            SimpleGrantedAuthority("ROLE_${user.userTrait.traitName.uppercase()}")
        )
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

        val entity = this.findAuthenticationUser(userId)
        return JwtUserDetails(entity)
    }

    private fun findAuthenticationUser(userId:Long) : AuthenticationUserEntity {
        val entity = userRepository.findAuthenticationUserById(userId)
        if( entity == null ) {
            throw UsernameNotFoundException("User not found")
        }

        return entity
    }

}