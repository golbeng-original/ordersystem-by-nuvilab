package com.ordersystemtask.june.domain.user.context

import com.ordersystemtask.june.domain.user.context.models.UserModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component

@Component
interface UserDBContext : JpaRepository<UserModel, Long> {

    fun findByUserId(userId: Long): UserModel?

    fun findByEmail(email: String): UserModel?
}