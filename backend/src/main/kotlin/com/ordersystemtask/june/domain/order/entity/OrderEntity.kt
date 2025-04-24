package com.ordersystemtask.june.domain.order.entity

import org.hibernate.query.Order
import java.time.LocalDateTime
import java.util.UUID

enum class OrderStatus {
    Pending, // 주문 수락 대기중
    Accepted, // 주문 수락
    Cancelled, // 주문 취소
    Completed, // 배달 완료
}

enum class PaymentMethod {
    None,
    Card,
    Cash
}

class OrderEntity(
    val orderId: String,
    val storeId: Long,
    val orderUserId:Long,
    var _orderStatus:OrderStatus = OrderStatus.Pending,
    val orderedAt: LocalDateTime = LocalDateTime.now(),
    val _orderedItemInfos: MutableList<OrderedItemInfo> = mutableListOf(),
    var _paymentInfo: PaymentInfo = PaymentInfo(),
    var _deliveryInfo : DeliveryInfo,
    private var _completedAt: LocalDateTime? = null,
) {
    val orderStatus get() = _orderStatus
    val orderedItems get() = _orderedItemInfos.toList()
    val paymentInfo get() = _paymentInfo
    val deliveryInfo get() = _deliveryInfo

    val totalPrice get() = _orderedItemInfos.sumOf { it.totalPrice }

    val completedAt get() = _completedAt

    fun completePay(method: PaymentMethod) {
        require(_orderStatus == OrderStatus.Pending && _paymentInfo.paid == false) {
            "이미 결제 완료하였음"
        }

        _paymentInfo = _paymentInfo.copy(
            paymentMethod = method,
            paid = true,
            paidAt = LocalDateTime.now()
        )
    }

    fun accept() {
        require(_orderStatus == OrderStatus.Cancelled) {
            "주문 취소된 것을 다시 수락할 수 없음"
        }

        require(_orderStatus == OrderStatus.Pending) {
            "이미 처리 중인 주문"
        }

        require(_paymentInfo.paid == true) {
            "결제가 완료가 안되면 수락하면 안됨"
        }

        _orderStatus = OrderStatus.Accepted
    }

    fun cancel() {
        require(_orderStatus == OrderStatus.Pending || _orderStatus == OrderStatus.Accepted) {
            "주문 취소할 수 없는 상태"
        }

        _orderStatus = OrderStatus.Cancelled
    }

    fun complete() {
        require(_orderStatus != OrderStatus.Cancelled) {
            "취소 된 것을 완료 할 수 없음"
        }

        require(_orderStatus == OrderStatus.Accepted) {
            "주문 수락 후에 배달 완료 처리 가능"
        }

        _orderStatus = OrderStatus.Completed
        _completedAt = LocalDateTime.now()
    }

    fun addOrderItem(newOrderItem: OrderedItemInfo) {
        _orderedItemInfos.add(newOrderItem)
    }

    fun changeDeliveryInfo(deliveryInfo: DeliveryInfo) {
        require(_orderStatus == OrderStatus.Pending) {
            "주문 수락 후에 배송 받을 주소 변경 불가능"
        }

        _deliveryInfo = deliveryInfo
    }

    // TODO(June) : 주문 제거 필요

    // TODO(June) : 주문 수정 필요

    companion object {
        fun new(orderUserId:Long, storeId:Long, deliveryInfo:DeliveryInfo): OrderEntity {
            return OrderEntity(
                orderId = UUID.randomUUID().toString(),
                orderUserId = orderUserId,
                storeId = storeId,
                _deliveryInfo = deliveryInfo
            )
        }
    }
}

data class OrderedItemInfo(
    val menuItemId:String,
    val menuName:String,
    val unitPrice: Long,
    val quantity:Int
) {
    val totalPrice get () = unitPrice * quantity
}

data class PaymentInfo(
    val paymentMethod:PaymentMethod = PaymentMethod.None,
    val paid: Boolean = false,
    val paidAt: LocalDateTime? = null
)

data class DeliveryInfo(
    val address:String,
    val receiverName:String,
    val phoneNumber:String
)