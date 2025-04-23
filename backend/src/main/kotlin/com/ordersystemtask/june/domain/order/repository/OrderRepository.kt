package com.ordersystemtask.june.domain.order.repository

import com.ordersystemtask.june.domain.order.context.OrderDBContext
import com.ordersystemtask.june.domain.order.context.models.DeliveryInfoModel
import com.ordersystemtask.june.domain.order.context.models.OrderModel
import com.ordersystemtask.june.domain.order.context.models.OrderedItemId
import com.ordersystemtask.june.domain.order.context.models.OrderedItemModel
import com.ordersystemtask.june.domain.order.context.models.PaymentInfoModel
import com.ordersystemtask.june.domain.order.entity.DeliveryInfo
import com.ordersystemtask.june.domain.order.entity.OrderEntity
import com.ordersystemtask.june.domain.order.entity.OrderedItemInfo
import com.ordersystemtask.june.domain.order.entity.PaymentInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class OrderRepository @Autowired constructor(
    private val context: OrderDBContext
) {
    fun findById(orderId:String) : OrderEntity? {
        val model = context.findByOrderId(orderId)
        if( model == null ) {
            return null
        }

        return convertToEntity(model)
    }

    fun findLatestByOrderUserId(orderUserId:Long) : OrderEntity? {
        val model = context.findFirstByOrderUserIdOrderByOrderedAtDesc(orderUserId)
        if( model == null ) {
            return null
        }

        return convertToEntity(model)
    }

    fun save(orderEntity: OrderEntity) : OrderEntity {
        val model = convertToModel(orderEntity)

        val savedModel = context.save(model)

        return convertToEntity(savedModel)
    }

    fun convertToEntity(model: OrderModel) : OrderEntity{
        return OrderEntity(
            orderId = model.orderId,
            storeId = model.storeId,
            orderUserId = model.orderUserId,
            _orderStatus = model.orderStatus,
            orderedAt = model.orderedAt,
            _orderedItemInfos = model.orderedItems.map {
                OrderedItemInfo(
                    menuItemId = it.id.menuItemId,
                    menuName = it.menuName,
                    unitPrice = it.unitPrice,
                    quantity = it.quantity,
                )
            }.toMutableList(),
            _paymentInfo = PaymentInfo(
                paymentMethod = model.paymentInfo.paymentMethod,
                paid = model.paymentInfo.paid,
                paidAt = model.paymentInfo.paidAt
            ),
            _deliveryInfo = DeliveryInfo(
                address = model.deliveryInfo.address,
                receiverName = model.deliveryInfo.receiverName,
                phoneNumber = model.deliveryInfo.phoneNumber
            ),
            _completedAt = model.completedAt
        )
    }

    fun convertToModel(entity: OrderEntity) : OrderModel {
        val model = OrderModel(
            orderId = entity.orderId,
            storeId = entity.storeId,
            orderUserId = entity.orderUserId,
            orderStatus = entity.orderStatus,
            orderedAt = entity.orderedAt,
            completedAt = entity.completedAt,
            paymentInfo = PaymentInfoModel(
                paymentMethod = entity.paymentInfo.paymentMethod,
                paid = entity.paymentInfo.paid,
                paidAt = entity.paymentInfo.paidAt
            ),
            deliveryInfo = DeliveryInfoModel(
                address = entity.deliveryInfo.address,
                receiverName = entity.deliveryInfo.receiverName,
                phoneNumber = entity.deliveryInfo.phoneNumber
            )
        )

        entity.orderedItems.forEach {
            val orderedItemModel = OrderedItemModel(
                id = OrderedItemId(
                    orderId = entity.orderId,
                    menuItemId = it.menuItemId
                ),
                order = model,
                menuName = it.menuName,
                unitPrice = it.unitPrice,
                quantity = it.quantity
            )
            model.orderedItems.add(orderedItemModel)
        }

        return model
    }
}