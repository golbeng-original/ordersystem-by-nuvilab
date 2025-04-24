package com.ordersystemtask.june.controller.order

import java.time.LocalDateTime

// region common ViewModel

enum class OrderStatusViewModelType {
    Pending,
    Accepted,
    Cancelled,
    Completed
}

enum class PaymentMethodViewModelType {
    None,
    Card,
    Cash
}

data class RequestOrderItemInfoViewModel(
    val menuItemId:String,
    val quantity:Int
)

data class OrderedItemInfoViewModel(
    val menuItemId: String,
    val menuName: String,
    val quantity: Int,
    val unitPrice: Long
)

data class PaymentInfoViewModel(
    val paymentMethod: PaymentMethodViewModelType,
)

data class DeliveryInfoViewModel(
    val address: String,
    val receiverName: String,
    val receiverPhoneNumber: String
)

data class OrderViewModel(
    val orderId: String,
    val storeId: Long,
    val orderUserId: Long,
    val orderStatus: OrderStatusViewModelType,
    val orderedAt: LocalDateTime,
    val orderedItems: List<OrderedItemInfoViewModel>,
    val paymentInfo: PaymentInfoViewModel? = null,
    val deliveryInfo: DeliveryInfoViewModel
)
/// endregion


/// region Order
data class OrderRequest(
    val storeId: Long,
    val menuItems:List<RequestOrderItemInfoViewModel>,
    val receiveAddressId:String,
    val paymentMethod: PaymentMethodViewModelType
)

/// endregion

/// region GetOrders
data class GetOrdersResponse(
    val orders: List<OrderViewModel>
)

/// endregion

/// region GetOrder
data class GetOrderResponse(
    val order: OrderViewModel?
)
/// endregion

/// region AcceptOrder
data class AcceptOrderResponse(
    val order: OrderViewModel
)
/// endregion

/// region CancelOrder
data class CancelOrderResponse(
    val order: OrderViewModel
)
/// endregion

/// region AcceptOrder
data class CompleteOrderResponse(
    val order: OrderViewModel
)
/// endregion