package com.ordersystemtask.june.applicationService.order

import com.ordersystemtask.june.domain.order.entity.DeliveryInfo
import com.ordersystemtask.june.domain.order.entity.OrderEntity
import com.ordersystemtask.june.domain.order.entity.OrderedItemInfo
import com.ordersystemtask.june.domain.order.entity.PaymentMethod
import com.ordersystemtask.june.domain.order.repository.OrderRepository
import com.ordersystemtask.june.domain.store.entity.StoreStatus
import com.ordersystemtask.june.domain.store.repository.StoreRepository
import com.ordersystemtask.june.domain.user.repository.UserRepository
import org.springframework.stereotype.Service

data class OrderMenuItemUnit(
    val menuItemId:String,
    val quantity:Int
)

data class OrderParam(
    val userId:Long,
    val storeId:Long,
    val receiveAddressId:String,
    val orderMenuItemUnits:List<OrderMenuItemUnit>
)

@Service
class OrderApplicationService(
    private val orderRepository: OrderRepository,
    private val storeRepository: StoreRepository,
    private val userRepository: UserRepository
) {

    /**
     * 주문
     */
    fun order(param:OrderParam) : OrderEntity {
        val ( userId, storeId, receiveAddressId, orderMenuItemUnits ) = param

        val user = userRepository.findUserById(userId)
        require( user != null ) {
            "User not found"
        }

        val store = storeRepository.findStoreById(storeId)
        require( store != null ) {
            "Store not found"
        }

        require(store.storeStatus == StoreStatus.Open) {
            "Store is not open"
        }

        val receiveAddress = user.receiveAddresses.find { it.receiveAddressId == receiveAddressId }
        require( receiveAddress != null ) {
            "Receive address not found"
        }

        val order = OrderEntity.new(
            userId = userId,
            storeId = storeId,
            deliveryInfo = DeliveryInfo(
                address = receiveAddress.address,
                receiverName = receiveAddress.ownerName,
                phoneNumber = receiveAddress.phoneNumber
            )
        )

        val menuItems = store.menus
            .filter {
                orderMenuItemUnits.map { it.menuItemId }.contains(it.menuItemId)
            }

        require( menuItems.isNotEmpty() ) {
            "Menu items not found"
        }

        menuItems.forEach { menuItem ->

            val orderMenuItemUnit = orderMenuItemUnits
                .find {
                    it.menuItemId == menuItem.menuItemId
                }

            val orderMenuItem = OrderedItemInfo(
                menuItemId = menuItem.menuItemId,
                menuName = menuItem.name,
                unitPrice = menuItem.price,
                quantity = orderMenuItemUnit!!.quantity
            )

            order.addOrderItem(orderMenuItem)
        }

        return orderRepository.save(order)
    }

    /**
     * 주문 취소
     */
    fun cancelOrder(orderId:String) : OrderEntity{
        val order = orderRepository.findById(orderId)
        require(order != null) {
            "Order not found"
        }

        order.cancel()

        return orderRepository.save(order)
    }

    /**
     * 결제 완료 하기
     */
    fun tryPay(orderId:String, method:PaymentMethod): OrderEntity {
        val order = orderRepository.findById(orderId)
        require(order != null) {
            "Order not found"
        }

        order.completePay(method)

        return orderRepository.save(order)
    }

    /**
     * 주문 받기
     */
    fun acceptOrder(orderId:String, ownerUserId:Long) : OrderEntity {
        val order = orderRepository.findById(orderId)
        require(order != null) {
            "Order not found"
        }

        val store = storeRepository.findStoreById(order.storeId)
        require(store != null) {
            "Store not found"
        }

        require(store.ownerUserId == ownerUserId) {
            "You are not the owner of this store"
        }

        order.accept()

        return orderRepository.save(order)
    }

    /**
     * 배달 완료
     */
    fun completeDelivery(orderId:String) : OrderEntity {
        val order = orderRepository.findById(orderId)
        require(order != null) {
            "Order not found"
        }

        order.complete()

        return orderRepository.save(order)
    }
}