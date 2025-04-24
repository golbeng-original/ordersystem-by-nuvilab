package com.ordersystemtask.june.controller.order

import com.ordersystemtask.june.applicationService.order.OrderApplicationService
import com.ordersystemtask.june.applicationService.order.OrderMenuItemUnit
import com.ordersystemtask.june.applicationService.order.OrderParam
import com.ordersystemtask.june.domain.order.entity.OrderEntity
import com.ordersystemtask.june.domain.order.entity.OrderStatus
import com.ordersystemtask.june.domain.order.entity.PaymentMethod
import com.ordersystemtask.june.security.JwtUserDetails
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/orders")
class OrderController(
    private val orderApplicationService: OrderApplicationService
) {

    @PostMapping("")
    fun order(
        @AuthenticationPrincipal userDetails: JwtUserDetails,
        @RequestBody request: OrderRequest
    ) : ResponseEntity<OrderViewModel> {

        val order = orderApplicationService.order(
            OrderParam(
                userId = userDetails.userId,
                storeId = request.storeId,
                receiveAddressId = request.receiveAddressId,
                orderMenuItemUnits = request.menuItems.map {
                    OrderMenuItemUnit(
                        menuItemId = it.menuItemId,
                        quantity = it.quantity
                    )
                },
                paymentMethod = when(request.paymentMethod) {
                    PaymentMethodViewModelType.None -> PaymentMethod.None
                    PaymentMethodViewModelType.Card -> PaymentMethod.Card
                    PaymentMethodViewModelType.Cash -> PaymentMethod.Cash
                }
            )
        )

        val orderViewModel = convertOrderToModel(order)

        return ResponseEntity
            .created(URI.create("/orders/${order.orderId}"))
            .body(
                orderViewModel
            )
    }

    @GetMapping("")
    @PreAuthorize("hasRole('SELLER')")
    fun getOrders(
        @AuthenticationPrincipal userDetails: JwtUserDetails,
        @RequestParam(required = true, name = "store") storeId:Long,
        @RequestParam(required = false, name = "status") orderStatus:OrderStatusViewModelType?,
    ) : GetOrdersResponse {
        val orderStatus = when(orderStatus) {
            OrderStatusViewModelType.Pending -> OrderStatus.Pending
            OrderStatusViewModelType.Accepted -> OrderStatus.Accepted
            OrderStatusViewModelType.Cancelled -> OrderStatus.Cancelled
            OrderStatusViewModelType.Completed -> OrderStatus.Completed
            null -> null
        }

        val orders = orderApplicationService.getOrdersByStoreId(
            userDetails.userId,
            storeId,
            orderStatus
        )

        val orderViewModels = orders.map {
            convertOrderToModel(it)
        }

        return GetOrdersResponse(orderViewModels)
    }

    @GetMapping("/latest")
    fun getLatestOrder(
        @AuthenticationPrincipal userDetails: JwtUserDetails
    ) : GetOrderResponse {
        val order = orderApplicationService.getLatestOrderByUserId(userDetails.userId)
        if( order == null ) {
            return GetOrderResponse(null)
        }

        return GetOrderResponse(
            convertOrderToModel(order)
        )
    }

    @GetMapping("/{orderId}")
    fun getOrder(
        @AuthenticationPrincipal userDetails: JwtUserDetails,
        @PathVariable orderId:String
    ) : GetOrderResponse {
        val order = orderApplicationService.getOrderByOrderId(
            orderId,
            userDetails.userId,

        )
        if( order == null ) {
            return GetOrderResponse(null)
        }

        return GetOrderResponse(
            convertOrderToModel(order)
        )
    }

    @PostMapping("/{orderId}")
    @PreAuthorize("hasRole('SELLER')")
    fun acceptOrder(
        @AuthenticationPrincipal userDetails: JwtUserDetails,
        @PathVariable orderId:String
    ) : AcceptOrderResponse {
        val order = orderApplicationService.acceptOrder(
            orderId,
            userDetails.userId
        )

        return AcceptOrderResponse(
            convertOrderToModel(order)
        )
    }

    @PostMapping("/{orderId}/cancel")
    fun cancelOrder(
        @AuthenticationPrincipal userDetails: JwtUserDetails,
        @PathVariable orderId:String
    ) : CancelOrderResponse {
        val order = orderApplicationService.cancelOrder(
            orderId,
            userDetails.userId
        )

        return CancelOrderResponse(
            convertOrderToModel(order)
        )
    }

    @PostMapping("/{orderId}/complete")
    fun completeReceive(
        @AuthenticationPrincipal userDetails: JwtUserDetails,
        @PathVariable orderId:String
    ) : CompleteOrderResponse {
        val order = orderApplicationService.completeDelivery(
            orderId,
            userDetails.userId
        )

        return CompleteOrderResponse(
            convertOrderToModel(order)
        )
    }

    private fun convertOrderToModel(order: OrderEntity) : OrderViewModel {
        return OrderViewModel(
            orderId = order.orderId,
            storeId = order.storeId,
            orderUserId = order.orderUserId,
            orderStatus = when(order.orderStatus) {
                OrderStatus.Pending -> OrderStatusViewModelType.Pending
                OrderStatus.Accepted -> OrderStatusViewModelType.Accepted
                OrderStatus.Cancelled -> OrderStatusViewModelType.Cancelled
                OrderStatus.Completed -> OrderStatusViewModelType.Completed
            },
            orderedAt = order.orderedAt,
            orderedItems = order.orderedItems.map {
                OrderedItemInfoViewModel(
                    menuItemId = it.menuItemId,
                    menuName = it.menuName,
                    quantity = it.quantity,
                    unitPrice = it.unitPrice
                )
            },
            paymentInfo = PaymentInfoViewModel(
                paymentMethod = when(order.paymentInfo.paymentMethod) {
                    PaymentMethod.None -> PaymentMethodViewModelType.None
                    PaymentMethod.Card -> PaymentMethodViewModelType.Card
                    PaymentMethod.Cash -> PaymentMethodViewModelType.Cash
                }
            ),
            deliveryInfo = DeliveryInfoViewModel(
                address = order.deliveryInfo.address,
                receiverName = order.deliveryInfo.receiverName,
                receiverPhoneNumber = order.deliveryInfo.phoneNumber
            )
        )

    }
}