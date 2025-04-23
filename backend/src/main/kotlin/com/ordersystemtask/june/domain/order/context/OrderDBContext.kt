package com.ordersystemtask.june.domain.order.context

import com.ordersystemtask.june.domain.order.context.models.OrderModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component

@Component
interface OrderDBContext : JpaRepository<OrderModel, String> {
    fun findByOrderId(orderId: String): OrderModel?

    fun findFirstByOrderUserIdOrderByOrderedAtDesc(orderUserId: Long): OrderModel?
}