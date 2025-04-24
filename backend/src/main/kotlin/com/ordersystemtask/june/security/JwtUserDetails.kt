package com.ordersystemtask.june.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class JwtUserDetails(
    val userId:Long,
    val userTrait:String
) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(
            SimpleGrantedAuthority("ROLE_${userTrait.uppercase()}")
        )
    }

    override fun getPassword() = ""
    override fun getUsername() = userId.toString()
}