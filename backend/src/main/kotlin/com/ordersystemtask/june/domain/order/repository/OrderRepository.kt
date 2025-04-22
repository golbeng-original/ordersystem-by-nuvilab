package com.ordersystemtask.june.domain.order.repository

import com.ordersystemtask.june.domain.order.entity.OrderEntity
import org.springframework.stereotype.Repository

@Repository
class OrderRepository {

    private var _idGenerator:Long = 1L
    private val _orders = mutableMapOf<Long, OrderEntity>()

    fun findById(orderId:Long) : OrderEntity? {
        return _orders[orderId]
    }

    fun save(orderEntity: OrderEntity) : OrderEntity {

        if( orderEntity.orderId == 0L) {
            val newOrderEntity = OrderEntity(
                orderId = ++_idGenerator,
                storeId = orderEntity.storeId,
                _orderStatus = orderEntity.orderStatus,
                orderedAt = orderEntity.orderedAt,
                _orderedItems = orderEntity.orderedItem.toMutableList(),
                _paymentInfo =  orderEntity.paymentInfo,
                _deliveryInfo = orderEntity.deliveryInfo
            )

            _orders[newOrderEntity.orderId] = newOrderEntity
            return newOrderEntity
        }

        _orders[orderEntity.orderId] = orderEntity
        return orderEntity
    }
}