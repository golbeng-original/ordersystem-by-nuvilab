package com.ordersystemtask.june.domain.order.entity

import java.time.LocalDateTime

enum class OrderStatus {
    Pending, // 주문 수락 대기중
    Accepted, // 주문 수락
    Canceled, // 주문 취소
    Completed, // 배달 완료
}

enum class PaymentMethod {
    Card,
    Cash
}

class OrderEntity(
    val orderId: Long,
    val storeId: Long,
    var _orderStatus:OrderStatus = OrderStatus.Pending,
    val orderedAt: LocalDateTime = LocalDateTime.now(),
    val _orderedItems: MutableList<OrderedItem> = mutableListOf(),
    var _paymentInfo: PaymentInfo,
    var _deliveryInfo : DeliveryInfo
) {
    val orderStatus get() = _orderStatus
    val orderedItem get() = _orderedItems.toList()
    val paymentInfo get() = _paymentInfo
    val deliveryInfo get() = _deliveryInfo

    fun accept() {
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

        _orderStatus = OrderStatus.Canceled
    }
    fun complete() {
        require(_orderStatus == OrderStatus.Canceled) {
            "취소 된 것을 완료 할 수 없음"
        }

        _orderStatus = OrderStatus.Completed
    }

    fun addOrderItem(newOrderItem: OrderedItem) {
        _orderedItems.add(newOrderItem)
    }

    fun completePay() {
        _paymentInfo = _paymentInfo.copy(
            paid = true,
            paidAt = LocalDateTime.now()
        )
    }

    fun changeDeliveryInfo(deliveryInfo: DeliveryInfo) {
        require(_orderStatus == OrderStatus.Pending) {
            "주문 수락 후에 배송 받을 주소 변경 불가능"
        }

        _deliveryInfo = deliveryInfo
    }

    // TODO(June) : 주문 제거 필요

    // TODO(June) : 주문 수정 필요
}

class OrderedItem(
    val menuItemId:Long,
    val menuName:String,
    val unitPrice: Long,
    val quantity:Int
) {
    val totalPrice get () = unitPrice * quantity
}

data class PaymentInfo(
    val paymentMethod:PaymentMethod,
    val paid: Boolean = false,
    val paidAt: LocalDateTime? = null
)

data class DeliveryInfo(
    val address:String,
    val receiverName:String,
    val phoneNumber:String
)