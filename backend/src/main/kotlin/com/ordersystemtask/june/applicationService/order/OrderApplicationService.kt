package com.ordersystemtask.june.applicationService.order

import com.ordersystemtask.june.domain.order.entity.DeliveryInfo
import com.ordersystemtask.june.domain.order.entity.OrderEntity
import com.ordersystemtask.june.domain.order.entity.OrderStatus
import com.ordersystemtask.june.domain.order.entity.OrderedItemInfo
import com.ordersystemtask.june.domain.order.entity.PaymentMethod
import com.ordersystemtask.june.domain.order.repository.OrderRepository
import com.ordersystemtask.june.domain.store.entity.StoreStatus
import com.ordersystemtask.june.domain.store.repository.StoreRepository
import com.ordersystemtask.june.domain.user.entity.UserTraitType
import com.ordersystemtask.june.domain.user.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

data class OrderMenuItemUnit(
    val menuItemId:String,
    val quantity:Int
)

data class OrderParam(
    val userId:Long,
    val storeId:Long,
    val receiveAddressId:String,
    val orderMenuItemUnits:List<OrderMenuItemUnit>,
    val paymentMethod: PaymentMethod
)

@Service
class OrderApplicationService(
    private val orderRepository: OrderRepository,
    private val storeRepository: StoreRepository,
    private val userRepository: UserRepository
) {

    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    @Transactional(readOnly = true)
    fun getOrderByOrderId(
        orderId:String,
        orderedUserId:Long
    ) : OrderEntity? {
        val order = orderRepository.findById(orderId)
        if( order == null ) {
            return null
        }

        if( order.orderUserId != orderedUserId) {
            return null
        }

        return order
    }

    @Transactional(readOnly = true)
    fun getOrdersByStoreId(
        ownerUserId: Long,
        storeId:Long,
        orderStatus: OrderStatus? = null
    ) : List<OrderEntity> {
        try {
            val ownerUser = userRepository.findUserById(ownerUserId)
            require(ownerUser != null) {
                "Owner user not found"
            }

            require(ownerUser.userTrait == UserTraitType.Seller) {
                "Owner user not a seller"
            }

            val store = storeRepository.findStoreById(storeId)
            require(store != null ) {
                "Store not found"
            }

            require(store.ownerUserId == ownerUserId) {
                "Owner User is not the owner of this store"
            }

            var orders = orderRepository.findByStoreId(storeId)
            if( orderStatus != null ) {
                orders = orders.filter {
                    it.orderStatus == orderStatus
                }
            }

            return orders
        }
        catch (e: Exception) {
            logger.error(e.message)
            throw e
        }
    }

    @Transactional(readOnly = true)
    fun getLatestOrderByUserId(
        userId:Long
    ) : OrderEntity? {
        try {
            val order = orderRepository.findLatestByOrderUserId(userId)
            require(order != null) {
                "Order not found"
            }

            return order
        }
        catch (e: Exception) {
            logger.error(e.message)
            throw e
        }
    }

    /**
     * 주문
     * - NOTE : 시간 관계상 결제 완료까지 처리 된 상태로 한다.
     */
    @Transactional
    fun order(param:OrderParam) : OrderEntity {
        val ( userId, storeId, receiveAddressId, orderMenuItemUnits ) = param

        try {
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
                orderUserId = userId,
                storeId = storeId,
                deliveryInfo = DeliveryInfo(
                    address = receiveAddress.address,
                    receiverName = receiveAddress.ownerName,
                    phoneNumber = receiveAddress.phoneNumber
                )
            )

            order.completePay(param.paymentMethod)

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
        catch (e: Exception) {
            logger.error(e.message)
            throw e
        }

    }

    /**
     * 주문 취소
     */
    @Transactional
    fun cancelOrder(orderId:String, orderUserId:Long) : OrderEntity{
        try {
            val order = orderRepository.findById(orderId)
            require(order != null) {
                "Order not found"
            }

            require(order.orderUserId == orderUserId) {
                "OrderUser is not the owner of this order"
            }

            order.cancel()

            return orderRepository.save(order)
        }
        catch (e:Exception) {
            logger.error(e.message)
            throw e
        }

    }

    /**
     * 결제 완료 하기
     */
    @Transactional
    fun tryPay(orderId:String, method:PaymentMethod): OrderEntity {
        try {
            val order = orderRepository.findById(orderId)
            require(order != null) {
                "Order not found"
            }

            order.completePay(method)

            return orderRepository.save(order)
        }
        catch (e: Exception) {
            logger.error(e.message)
            throw e
        }

    }

    /**
     * 주문 받기
     */
    @Transactional
    fun acceptOrder(orderId:String, ownerUserId:Long) : OrderEntity {

        try {
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
        catch (e: Exception) {
            logger.error(e.message)
            throw e
        }

    }

    /**
     * 배달 완료
     */
    @Transactional
    fun completeDelivery(orderId:String, orderUserId:Long) : OrderEntity {
        try {
            val order = orderRepository.findById(orderId)
            require(order != null) {
                "Order not found"
            }

            require(order.orderUserId == orderUserId) {
                "OrderUser is not the owner of this order"
            }

            order.complete()

            return orderRepository.save(order)
        }
        catch (e:Exception) {
            logger.error(e.message)
            throw e
        }
    }
}
