package com.ordersystemtask.june.domain.order.repository

import com.ordersystemtask.june.domain.order.entity.OrderEntity
import org.springframework.stereotype.Repository

@Repository
class OrderRepository {
    private val _orders = mutableMapOf<String, OrderEntity>()

    fun findById(orderId:String) : OrderEntity? {
        return _orders[orderId]
    }

    fun save(orderEntity: OrderEntity) : OrderEntity {
        _orders[orderEntity.orderId] = orderEntity
        return orderEntity
    }
}